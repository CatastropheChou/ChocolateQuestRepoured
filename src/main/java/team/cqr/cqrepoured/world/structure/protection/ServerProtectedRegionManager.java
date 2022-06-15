package team.cqr.cqrepoured.world.structure.protection;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.capability.protectedregions.CapabilityProtectedRegionData;
import team.cqr.cqrepoured.capability.protectedregions.CapabilityProtectedRegionDataProvider;
import team.cqr.cqrepoured.network.server.packet.SPacketUnloadProtectedRegion;
import team.cqr.cqrepoured.network.server.packet.SPacketUpdateProtectedRegion;
import team.cqr.cqrepoured.util.data.FileIOUtil;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class ServerProtectedRegionManager implements IProtectedRegionManager {

	private final Map<UUID, ProtectedRegionContainer> protectedRegions = new HashMap<>();
	private final World world;
	private final File folder;

	public static class ProtectedRegionContainer {
		public final ProtectedRegion protectedRegion;
		public long lastTickForceLoaded;
		public final Set<Chunk> chunkSet = new HashSet<>();

		public ProtectedRegionContainer(ProtectedRegion protectedRegion, boolean loadChunks) {
			this.protectedRegion = protectedRegion;
			this.lastTickForceLoaded = protectedRegion.getWorld().getGameTime();

			boolean logCascadingWorldGeneration = ForgeModContainer.logCascadingWorldGeneration;
			ForgeModContainer.logCascadingWorldGeneration = false;
			((ServerWorld) protectedRegion.getWorld()).addScheduledTask(() -> {
				BlockPos p1 = protectedRegion.getStartPos();
				BlockPos p2 = protectedRegion.getEndPos();
				for (int x = p1.getX() >> 4; x <= p2.getX() >> 4; x++) {
					for (int z = p1.getZ() >> 4; z <= p2.getZ() >> 4; z++) {
						Chunk chunk;
						if (loadChunks) {
							chunk = protectedRegion.getWorld().getChunk(x, z);
						} else {
							chunk = protectedRegion.getWorld().getChunkProvider().getLoadedChunk(x, z);
						}
						if (chunk != null) {
							this.chunkSet.add(chunk);
							CapabilityProtectedRegionData capProtectedRegionData = chunk.getCapability(CapabilityProtectedRegionDataProvider.PROTECTED_REGION_DATA, null);
							capProtectedRegionData.addProtectedRegionUuid(protectedRegion.getUuid());
						}
					}
				}
			});
			ForgeModContainer.logCascadingWorldGeneration = logCascadingWorldGeneration;
		}
	}

	public ServerProtectedRegionManager(World world) {
		this.world = world;
		int dim = world.provider.getDimension();
		if (dim == 0) {
			this.folder = new File(world.getSaveHandler().getWorldDirectory(), "data/CQR/protected_regions");
		} else {
			this.folder = new File(world.getSaveHandler().getWorldDirectory(), "DIM" + dim + "/data/CQR/protected_regions");
		}
	}

	public void handleChunkLoad(Chunk chunk) {
		LazyOptional<CapabilityProtectedRegionData> capabilityProtectedRegionData = chunk.getCapability(CapabilityProtectedRegionDataProvider.PROTECTED_REGION_DATA, null);
		capabilityProtectedRegionData.ifPresent(cap -> cap.removeIf(uuid -> this.provideProtectedRegion(uuid) == null));
	}

	public void handleChunkUnload(Chunk chunk) {
		for (ProtectedRegionContainer container : this.protectedRegions.values()) {
			container.chunkSet.remove(chunk);
		}
	}

	public void handleWorldTick() {
		long time = this.world.getGameTime(); //Correct replacement?
		for (Iterator<ProtectedRegionContainer> iterator = this.protectedRegions.values().iterator(); iterator.hasNext();) {
			ProtectedRegionContainer container = iterator.next();
			if (!container.chunkSet.isEmpty()) {
				container.lastTickForceLoaded = time;
			} else if (time - container.lastTickForceLoaded > 600) {
				if (container.protectedRegion.needsSaving()) {
					this.saveProtectedRegionToFile(container.protectedRegion);
				}
				iterator.remove();
				CQRMain.NETWORK.send(PacketDistributor.DIMENSION.with(this.world::dimension), new SPacketUnloadProtectedRegion(container.protectedRegion.getUuid()));
				//CQRMain.NETWORK.sendToDimension(new SPacketUnloadProtectedRegion(container.protectedRegion.getUuid()), this.world.provider.getDimension());
				continue;
			}
			if (container.protectedRegion.needsSyncing()) {
				CQRMain.NETWORK.send(PacketDistributor.DIMENSION.with(this.world::dimension), new SPacketUpdateProtectedRegion(container.protectedRegion));
				//CQRMain.NETWORK.sendToDimension(new SPacketUpdateProtectedRegion(container.protectedRegion), this.world.provider.getDimension());
				container.protectedRegion.clearNeedsSyncing();
			}
		}
	}

	@Nullable
	public ProtectedRegion provideProtectedRegion(UUID uuid) {
		ProtectedRegionContainer container = this.protectedRegions.get(uuid);
		if (container != null) {
			return container.protectedRegion;
		}
		ProtectedRegion protectedRegion = this.createProtectedRegionFromFile(uuid);
		if (protectedRegion != null) {
			this.protectedRegions.put(uuid, new ProtectedRegionContainer(protectedRegion, false));
			
			CQRMain.NETWORK.send(PacketDistributor.DIMENSION.with(this.world::dimension), new SPacketUpdateProtectedRegion(protectedRegion));
			//CQRMain.NETWORK.sendToDimension(new SPacketUpdateProtectedRegion(protectedRegion), this.world.provider.getDimension());
			protectedRegion.clearNeedsSyncing();
		}
		return protectedRegion;
	}

	@Override
	@Nullable
	public ProtectedRegion getProtectedRegion(UUID uuid) {
		ProtectedRegionContainer container = this.protectedRegions.get(uuid);
		return container != null ? container.protectedRegion : null;
	}

	@Override
	public void addProtectedRegion(ProtectedRegion protectedRegion) {
		if (!protectedRegion.isValid()) {
			return;
		}

		if (this.protectedRegions.containsKey(protectedRegion.getUuid())) {
			CQRMain.logger.warn("Protected region with uuid {} already exists.", protectedRegion.getUuid());
			return;
		}

		this.protectedRegions.put(protectedRegion.getUuid(), new ProtectedRegionContainer(protectedRegion, true));
		CQRMain.NETWORK.send(PacketDistributor.DIMENSION.with(this.world::dimension), new SPacketUpdateProtectedRegion(protectedRegion));
		//CQRMain.NETWORK.sendToDimension(new SPacketUpdateProtectedRegion(protectedRegion), this.world.provider.getDimension());
		protectedRegion.clearNeedsSyncing();
	}

	@Override
	public void removeProtectedRegion(ProtectedRegion protectedRegion) {
		this.removeProtectedRegion(protectedRegion.getUuid());
	}

	@Override
	public void removeProtectedRegion(UUID uuid) {
		ProtectedRegionContainer container = this.protectedRegions.remove(uuid);

		File file = new File(this.folder, uuid.toString() + ".nbt");
		if (file.exists()) {
			file.delete();
		}

		if (container != null) {
			for (Chunk chunk : container.chunkSet) {
				LazyOptional<CapabilityProtectedRegionData> lOpCapProtectedRegionData = chunk.getCapability(CapabilityProtectedRegionDataProvider.PROTECTED_REGION_DATA, null);
				lOpCapProtectedRegionData.ifPresent((cap) -> {
					cap.removeProtectedRegionUuid(uuid);
				});
			}
			CQRMain.NETWORK.send(PacketDistributor.DIMENSION.with(this.world::dimension), new SPacketUnloadProtectedRegion(uuid));
			//CQRMain.NETWORK.sendToDimension(new SPacketUnloadProtectedRegion(uuid), this.world.provider.getDimension());
		}
	}

	@Override
	public Iterable<ProtectedRegion> getProtectedRegions() {
		List<ProtectedRegion> regionsTmp = new ArrayList<>(this.protectedRegions.values().size());
		this.protectedRegions.values().forEach(prc -> {
			regionsTmp.add(prc.protectedRegion);
		});
		return regionsTmp;
		/*return () -> new Iterator<ProtectedRegion>() {
			private final Iterator<ProtectedRegionContainer> iterator = Collections.unmodifiableCollection(ServerProtectedRegionManager.this.protectedRegions.values()).iterator();

			@Override
			public boolean hasNext() {
				return this.iterator.hasNext();
			}

			@Override
			public ProtectedRegion next() {
				return this.iterator.next().protectedRegion;
			}
		};*/
	}

	@Override
	public List<ProtectedRegion> getProtectedRegionsAt(BlockPos pos) {
		// load chunk which also loads all associated protected regions
		IChunk ichunk = this.world.getChunk(pos);
		if(!(ichunk instanceof Chunk)) {
			return Collections.emptyList();
		}
		Chunk chunk = (Chunk)ichunk;
		LazyOptional<CapabilityProtectedRegionData> lOpCap = chunk.getCapability(CapabilityProtectedRegionDataProvider.PROTECTED_REGION_DATA, null);
		if (!lOpCap.isPresent()) {
			return Collections.emptyList();
		}
		List<ProtectedRegion> list = new ArrayList<>();
		lOpCap.ifPresent((cap) -> {
			cap.removeIf(uuid -> {
				ProtectedRegionContainer container = this.protectedRegions.get(uuid);
				if (container != null && container.protectedRegion.isInsideProtectedRegion(pos)) {
					list.add(container.protectedRegion);
				}
				return container == null;
			});
		});
		return list;
	}

	@Override
	public void clearProtectedRegions() {
		for (UUID uuid : new ArrayList<>(this.protectedRegions.keySet())) {
			this.removeProtectedRegion(uuid);
		}
	}

	public void saveProtectedRegions() {
		for (Iterator<ProtectedRegionContainer> iterator = this.protectedRegions.values().iterator(); iterator.hasNext();) {
			ProtectedRegionContainer container = iterator.next();
			if (!container.protectedRegion.isValid()) {
				File file = new File(this.folder, container.protectedRegion.getUuid() + ".nbt");
				if (file.exists()) {
					file.delete();
				}
				iterator.remove();
			} else if (container.protectedRegion.needsSaving()) {
				this.saveProtectedRegionToFile(container.protectedRegion);
			}
		}
	}

	private void saveProtectedRegionToFile(ProtectedRegion protectedRegion) {
		File file = new File(this.folder, protectedRegion.getUuid().toString() + ".nbt");
		FileIOUtil.writeNBTToFile(protectedRegion.writeToNBT(), file);
	}

	@Nullable
	private ProtectedRegion createProtectedRegionFromFile(UUID uuid) {
		File file = new File(this.folder, uuid.toString() + ".nbt");
		if (!file.exists()) {
			return null;
		}

		CompoundNBT compound = FileIOUtil.readNBTFromFile(file);
		ProtectedRegion protectedRegion = new ProtectedRegion(this.world, compound);

		if (!compound.getString("version").equals(ProtectedRegion.PROTECTED_REGION_VERSION)) {
			this.saveProtectedRegionToFile(protectedRegion);
		}

		return protectedRegion;
	}

}
