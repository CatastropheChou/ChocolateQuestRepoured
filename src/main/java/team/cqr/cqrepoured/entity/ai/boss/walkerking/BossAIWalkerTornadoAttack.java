package team.cqr.cqrepoured.entity.ai.boss.walkerking;

import net.minecraft.util.math.vector.Vector3d;
import team.cqr.cqrepoured.entity.ai.AbstractCQREntityAI;
import team.cqr.cqrepoured.entity.boss.EntityCQRWalkerKing;
import team.cqr.cqrepoured.entity.misc.EntityWalkerTornado;
import team.cqr.cqrepoured.util.DungeonGenUtils;
import team.cqr.cqrepoured.util.VectorUtil;

public class BossAIWalkerTornadoAttack extends AbstractCQREntityAI<EntityCQRWalkerKing> {

	protected static final int MIN_TORNADOES = 3;
	protected static final int MAX_TORNADOES = 6;
	protected static final int MIN_COOLDOWN = 120;
	protected static final int MAX_COOLDOWN = 240;

	protected int cooldown = MIN_COOLDOWN + (MAX_COOLDOWN - MIN_COOLDOWN) / 2;

	public BossAIWalkerTornadoAttack(EntityCQRWalkerKing entity) {
		super(entity);
	}

	@Override
	public boolean canUse() {
		if (this.entity != null && this.entity.getTarget() != null && !this.entity.getTarget().isDeadOrDying()) {
			this.cooldown--;
			return this.cooldown <= 0;
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return super.canContinueToUse() && this.canUse();
	}

	@Override
	public void start() {
		super.start();
		this.spawnTornadoes(DungeonGenUtils.randomBetween(MIN_TORNADOES, MAX_TORNADOES + 1, this.entity.getRandom()));
		this.cooldown = DungeonGenUtils.randomBetween(MIN_COOLDOWN, MAX_COOLDOWN, this.entity.getRandom());
	}

	private void spawnTornadoes(int count) {
		// System.out.println("Executing");
		double angle = 90 / (count - 1);
		Vector3d velocity = this.entity.getTarget().position().subtract(this.entity.position());
		velocity = VectorUtil.rotateVectorAroundY(velocity, -45);
		for (int i = 0; i < count; i++) {
			Vector3d v = VectorUtil.rotateVectorAroundY(velocity, angle * i);
			Vector3d p = this.entity.position().add(v.normalize().scale(0.5));
			v = v.normalize().scale(0.25);
			// System.out.println("V=" + v.toString());
			EntityWalkerTornado tornado = new EntityWalkerTornado(this.entity.level);
			tornado.setOwner(this.entity.getUUID());
			tornado.setPos(p.x, p.y, p.z);
			tornado.setVelocity(v);
			this.entity.level.addFreshEntity(tornado);
		}
	}

}
