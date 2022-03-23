package team.cqr.cqrepoured.world.structure.protection;

import net.minecraft.util.math.BlockPos;
import team.cqr.cqrepoured.CQRMain;

import javax.annotation.Nullable;
import java.util.*;

public class ClientProtectedRegionManager implements IProtectedRegionManager {

	private final Map<UUID, ProtectedRegion> protectedRegions = new HashMap<>();

	@Override
	@Nullable
	public ProtectedRegion getProtectedRegion(UUID uuid) {
		return this.protectedRegions.get(uuid);
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

		this.protectedRegions.put(protectedRegion.getUuid(), protectedRegion);
	}

	@Override
	public void removeProtectedRegion(ProtectedRegion protectedRegion) {
		this.removeProtectedRegion(protectedRegion.getUuid());
	}

	@Override
	public void removeProtectedRegion(UUID uuid) {
		this.protectedRegions.remove(uuid);
	}

	@Override
	public Iterable<ProtectedRegion> getProtectedRegions() {
		return Collections.unmodifiableCollection(this.protectedRegions.values());
	}

	@Override
	public List<ProtectedRegion> getProtectedRegionsAt(BlockPos pos) {
		List<ProtectedRegion> list = new ArrayList<>();
		for (ProtectedRegion protectedRegion : this.protectedRegions.values()) {
			if (protectedRegion.isInsideProtectedRegion(pos)) {
				list.add(protectedRegion);
			}
		}
		return list;
	}

	@Override
	public void clearProtectedRegions() {
		this.protectedRegions.clear();
	}

}
