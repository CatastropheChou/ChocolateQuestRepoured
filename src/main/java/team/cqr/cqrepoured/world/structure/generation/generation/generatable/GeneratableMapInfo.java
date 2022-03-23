package team.cqr.cqrepoured.world.structure.generation.generation.generatable;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.storage.MapData;
import team.cqr.cqrepoured.util.BlockPlacingHelper;
import team.cqr.cqrepoured.util.DungeonGenUtils;
import team.cqr.cqrepoured.world.structure.generation.generation.GeneratableDungeon;

public class GeneratableMapInfo extends GeneratablePosInfo {

	private final ItemFrameEntity entity;
	private final int mapOriginX;
	private final int mapOriginZ;
	private final int mapX;
	private final int mapZ;
	private final byte scale;
	private final boolean fillMap;
	private final int fillRadius;

	public GeneratableMapInfo(int x, int y, int z, ItemFrameEntity entity, int mapOriginX, int mapOriginZ, int mapX, int mapZ, byte scale, boolean fillMap, int fillRadius) {
		super(x, y, z);
		this.entity = entity;
		this.mapOriginX = mapOriginX;
		this.mapOriginZ = mapOriginZ;
		this.mapX = mapX;
		this.mapZ = mapZ;
		this.scale = scale;
		this.fillMap = fillMap;
		this.fillRadius = fillRadius;
	}

	public GeneratableMapInfo(BlockPos pos, ItemFrameEntity entity, int mapOriginX, int mapOriginZ, int mapX, int mapZ, byte scale, boolean fillMap, int fillRadius) {
		this(pos.getX(), pos.getY(), pos.getZ(), entity, mapOriginX, mapOriginZ, mapX, mapZ, scale, fillMap, fillRadius);
	}

	@Override
	public boolean place(World world, Chunk chunk, ChunkSection blockStorage, BlockPos pos, GeneratableDungeon dungeon) {
		BlockState state = blockStorage.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
		int light = state.getLightValue(world, pos);
		if (light > 0) {
			dungeon.markRemovedLight(pos.getX(), pos.getY(), pos.getZ(), light);
		}
		boolean flag = BlockPlacingHelper.setBlockState(world, chunk, blockStorage, pos, Blocks.AIR.defaultBlockState(), null, 16);
		ItemStack stack = FilledMapItem.create(world, this.mapX, this.mapZ, this.scale, true, true);
		if (this.fillMap) {
			updateMapData(world, this.mapOriginX, this.mapOriginZ, this.fillRadius, FilledMapItem.getSavedData(stack, world));
		}
		this.entity.setItem(stack);
		world.addFreshEntity(this.entity);
		return flag;
	}

	private static void updateMapData(World worldIn, int x, int z, int radius, MapData data) {
		if (worldIn.dimension() != data.dimension) {
			return;
		}

		for (int ix = 0; ix < 128; ix++) {
			int worldX = data.x + (ix - 64) * (1 << data.scale);

			for (int iz = 0; iz < 128; iz++) {
				int worldZ = data.z + (iz - 64) * (1 << data.scale);

				if (!DungeonGenUtils.isInsideCircle(worldX - x, worldZ - z, radius)) {
					continue;
				}

				updateMapColorAt(worldIn, ix, iz, data);
			}
		}
	}

	private static void updateMapColorAt(World world, int mapX, int mapZ, MapData data) {
		int i = 1 << data.scale;
		double d0 = 0.0D;
		int k2 = (data.x / i + mapX - 64) * i;
		int l2 = (data.z / i + mapZ - 64) * i;
		Multiset<MaterialColor> multiset = HashMultiset.create();
		Chunk chunk = world.getChunk(k2 >> 4, l2 >> 4);
		int i3 = k2 & 15;
		int j3 = l2 & 15;
		int k3 = 0;
		double d1 = 0.0D;

		if (world.dimension().equals(World.NETHER)) {
			int l3 = k2 + l2 * 231_871;
			l3 = l3 * l3 * 31_287_121 + l3 * 11;

			if ((l3 >> 20 & 1) == 0) {
				multiset.add(Blocks.DIRT.defaultBlockState().getMapColor(world, BlockPos.ZERO), 10);
			} else {
				multiset.add(Blocks.STONE.defaultBlockState().getMapColor(world, BlockPos.ZERO), 100);
			}

			d1 = 100.0D;
		} else {
			Mutable pos = new Mutable();

			for (int i4 = 0; i4 < i; ++i4) {
				for (int j4 = 0; j4 < i; ++j4) {
					int k4 = chunk.getHeight(Type.WORLD_SURFACE, i4 + i3, j4 + j3) + 1; //Correct replacement?
					BlockState state = Blocks.AIR.defaultBlockState();

					if (k4 <= 1) {
						state = Blocks.BEDROCK.defaultBlockState();
					} else {
						label175: {
							while (true) {
								--k4;
								state = chunk.getBlockState(new BlockPos(i4 + i3, k4, j4 + j3));
								pos.set((chunk.getPos().x << 4) + i4 + i3, k4, (chunk.getPos().z << 4) + j4 + j3);

								if (state.getMapColor(world, pos) != MaterialColor.NONE || k4 <= 0) {
									break;
								}
							}

							if (k4 > 0 && state.getMaterial().isLiquid()) {
								int l4 = k4 - 1;

								while (true) {
									BlockState iblockstate1 = chunk.getBlockState(new BlockPos(i4 + i3, l4--, j4 + j3));
									++k3;

									if (l4 <= 0 || !iblockstate1.getMaterial().isLiquid()) {
										break label175;
									}
								}
							}
						}
					}

					d1 += (double) k4 / (double) (i * i);
					multiset.add(state.getMapColor(world, pos));
				}
			}
		}

		k3 = k3 / (i * i);
		double d2 = (d1 - d0) * 4.0D / (i + 4) + ((mapX + mapZ & 1) - 0.5D) * 0.4D;
		int i5 = 1;

		if (d2 > 0.6D) {
			i5 = 2;
		}

		if (d2 < -0.6D) {
			i5 = 0;
		}

		MaterialColor mapcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.NONE);

		if (mapcolor == MaterialColor.WATER) {
			d2 = k3 * 0.1D + (mapX + mapZ & 1) * 0.2D;
			i5 = 1;

			if (d2 < 0.5D) {
				i5 = 2;
			}

			if (d2 > 0.9D) {
				i5 = 0;
			}
		}

		byte b0 = data.colors[mapX + mapZ * 128];
		byte b1 = (byte) (mapcolor.col * 4 + i5); //Correct replacement?

		if (b0 != b1) {
			data.colors[mapX + mapZ * 128] = b1;
			data.setDirty(mapX, mapZ);
		}
	}

	public ItemFrameEntity getEntity() {
		return this.entity;
	}

	public int getMapX() {
		return this.mapX;
	}

	public int getMapZ() {
		return this.mapZ;
	}

	public byte getScale() {
		return this.scale;
	}

	public boolean isFillMap() {
		return this.fillMap;
	}

	public int getFillRadius() {
		return this.fillRadius;
	}

}
