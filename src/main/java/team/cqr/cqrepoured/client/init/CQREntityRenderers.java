package team.cqr.cqrepoured.client.init;

import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import team.cqr.cqrepoured.client.model.entity.boss.ModelLich;
import team.cqr.cqrepoured.client.model.entity.boss.ModelNecromancer;
import team.cqr.cqrepoured.client.model.entity.boss.ModelPigMage;
import team.cqr.cqrepoured.client.render.entity.RenderBubble;
import team.cqr.cqrepoured.client.render.entity.RenderCQRWasp;
import team.cqr.cqrepoured.client.render.entity.RenderCalamityCrystal;
import team.cqr.cqrepoured.client.render.entity.RenderChungus;
import team.cqr.cqrepoured.client.render.entity.RenderColoredLightningBolt;
import team.cqr.cqrepoured.client.render.entity.RenderElectricFieldEntity;
import team.cqr.cqrepoured.client.render.entity.RenderFlyingSkull;
import team.cqr.cqrepoured.client.render.entity.RenderIceSpike;
import team.cqr.cqrepoured.client.render.entity.RenderLaser;
import team.cqr.cqrepoured.client.render.entity.RenderMultiPartPart;
import team.cqr.cqrepoured.client.render.entity.RenderPirateParrot;
import team.cqr.cqrepoured.client.render.entity.RenderSpiderEgg;
import team.cqr.cqrepoured.client.render.entity.RenderSummoningCircle;
import team.cqr.cqrepoured.client.render.entity.RenderWalkerTornado;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRGiantSpider;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRGiantTortoiseGecko;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRMage;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRNecromancer;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRNetherDragon;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRNetherDragonSegment;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRPirateCaptain;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRWalkerKing;
import team.cqr.cqrepoured.client.render.entity.boss.RenderCQRWalkerKingIllusion;
import team.cqr.cqrepoured.client.render.entity.boss.endercalamity.RenderCQREnderCalamity;
import team.cqr.cqrepoured.client.render.entity.boss.endercalamity.RenderCQREnderKing;
import team.cqr.cqrepoured.client.render.entity.boss.endercalamity.RenderEndLaser;
import team.cqr.cqrepoured.client.render.entity.boss.exterminator.RenderCQRExterminator;
import team.cqr.cqrepoured.client.render.entity.boss.exterminator.RenderExterminatorBackpackPart;
import team.cqr.cqrepoured.client.render.entity.boss.spectrelord.RenderCQRSpectreLord;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRBoarman;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRDummy;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRDwarf;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQREnderman;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRGoblin;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRGolem;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRGremlin;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRHuman;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRIllager;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRMandril;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRMinotaur;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRMummy;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRNPC;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQROgre;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQROrc;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRPirate;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRSkeleton;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRSpectre;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRTriton;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRWalker;
import team.cqr.cqrepoured.client.render.entity.mobs.RenderCQRZombie;
import team.cqr.cqrepoured.client.render.entity.mounts.RenderGiantEndermite;
import team.cqr.cqrepoured.client.render.entity.mounts.RenderGiantSilverfish;
import team.cqr.cqrepoured.client.render.entity.mounts.RenderGiantSilverfishGreen;
import team.cqr.cqrepoured.client.render.entity.mounts.RenderGiantSilverfishRed;
import team.cqr.cqrepoured.client.render.entity.mounts.RenderPollo;
import team.cqr.cqrepoured.client.render.projectile.RenderEnergyOrb;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileBubble;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileBullet;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileCannonBall;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileEarthQuake;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileFirewallPart;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileHomingEnderEye;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileHookShotHook;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileHotFireball;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectilePoisonSpell;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileSpiderBall;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileSpiderHook;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileThrownBlock;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileVampiricSpell;
import team.cqr.cqrepoured.client.render.projectile.RenderProjectileWeb;
import team.cqr.cqrepoured.client.render.tileentity.TileEntityExporterChestRenderer;
import team.cqr.cqrepoured.client.render.tileentity.TileEntityExporterRenderer;
import team.cqr.cqrepoured.client.render.tileentity.TileEntityForceFieldNexusRenderer;
import team.cqr.cqrepoured.client.render.tileentity.TileEntityMapPlaceHolderRenderer;
import team.cqr.cqrepoured.client.render.tileentity.TileEntityTableRenderer;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.entity.boss.AbstractEntityLaser;
import team.cqr.cqrepoured.entity.boss.EntityCQRBoarmage;
import team.cqr.cqrepoured.entity.boss.EntityCQRGiantSpider;
import team.cqr.cqrepoured.entity.boss.EntityCQRLich;
import team.cqr.cqrepoured.entity.boss.EntityCQRNecromancer;
import team.cqr.cqrepoured.entity.boss.EntityCQRPirateCaptain;
import team.cqr.cqrepoured.entity.boss.EntityCQRPirateParrot;
import team.cqr.cqrepoured.entity.boss.EntityCQRWalkerKing;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderCalamity;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderKing;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCalamityCrystal;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityEndLaser;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityEndLaserTargeting;
import team.cqr.cqrepoured.entity.boss.exterminator.EntityCQRExterminator;
import team.cqr.cqrepoured.entity.boss.exterminator.SubEntityExterminatorFieldEmitter;
import team.cqr.cqrepoured.entity.boss.gianttortoise.EntityCQRGiantTortoise;
import team.cqr.cqrepoured.entity.boss.netherdragon.EntityCQRNetherDragon;
import team.cqr.cqrepoured.entity.boss.netherdragon.SubEntityNetherDragonSegment;
import team.cqr.cqrepoured.entity.boss.spectrelord.EntityCQRSpectreLord;
import team.cqr.cqrepoured.entity.misc.EntityBubble;
import team.cqr.cqrepoured.entity.misc.EntityColoredLightningBolt;
import team.cqr.cqrepoured.entity.misc.EntityElectricField;
import team.cqr.cqrepoured.entity.misc.EntityFlyingSkullMinion;
import team.cqr.cqrepoured.entity.misc.EntityIceSpike;
import team.cqr.cqrepoured.entity.misc.EntitySpiderEgg;
import team.cqr.cqrepoured.entity.misc.EntitySummoningCircle;
import team.cqr.cqrepoured.entity.misc.EntityWalkerKingIllusion;
import team.cqr.cqrepoured.entity.misc.EntityWalkerTornado;
import team.cqr.cqrepoured.entity.mobs.EntityCQRBoarman;
import team.cqr.cqrepoured.entity.mobs.EntityCQRDummy;
import team.cqr.cqrepoured.entity.mobs.EntityCQRDwarf;
import team.cqr.cqrepoured.entity.mobs.EntityCQREnderman;
import team.cqr.cqrepoured.entity.mobs.EntityCQRGoblin;
import team.cqr.cqrepoured.entity.mobs.EntityCQRGolem;
import team.cqr.cqrepoured.entity.mobs.EntityCQRGremlin;
import team.cqr.cqrepoured.entity.mobs.EntityCQRHuman;
import team.cqr.cqrepoured.entity.mobs.EntityCQRIllager;
import team.cqr.cqrepoured.entity.mobs.EntityCQRMandril;
import team.cqr.cqrepoured.entity.mobs.EntityCQRMinotaur;
import team.cqr.cqrepoured.entity.mobs.EntityCQRMummy;
import team.cqr.cqrepoured.entity.mobs.EntityCQRNPC;
import team.cqr.cqrepoured.entity.mobs.EntityCQROgre;
import team.cqr.cqrepoured.entity.mobs.EntityCQROrc;
import team.cqr.cqrepoured.entity.mobs.EntityCQRPirate;
import team.cqr.cqrepoured.entity.mobs.EntityCQRSkeleton;
import team.cqr.cqrepoured.entity.mobs.EntityCQRSpectre;
import team.cqr.cqrepoured.entity.mobs.EntityCQRTriton;
import team.cqr.cqrepoured.entity.mobs.EntityCQRWalker;
import team.cqr.cqrepoured.entity.mobs.EntityCQRZombie;
import team.cqr.cqrepoured.entity.mount.EntityGiantEndermite;
import team.cqr.cqrepoured.entity.mount.EntityGiantSilverfishGreen;
import team.cqr.cqrepoured.entity.mount.EntityGiantSilverfishNormal;
import team.cqr.cqrepoured.entity.mount.EntityGiantSilverfishRed;
import team.cqr.cqrepoured.entity.mount.EntityPollo;
import team.cqr.cqrepoured.entity.projectiles.ProjectileBullet;
import team.cqr.cqrepoured.entity.projectiles.ProjectileCannonBall;
import team.cqr.cqrepoured.entity.projectiles.ProjectileEarthQuake;
import team.cqr.cqrepoured.entity.projectiles.ProjectileEnergyOrb;
import team.cqr.cqrepoured.entity.projectiles.ProjectileFireWallPart;
import team.cqr.cqrepoured.entity.projectiles.ProjectileHomingEnderEye;
import team.cqr.cqrepoured.entity.projectiles.ProjectileHookShotHook;
import team.cqr.cqrepoured.entity.projectiles.ProjectileHotFireball;
import team.cqr.cqrepoured.entity.projectiles.ProjectilePoisonSpell;
import team.cqr.cqrepoured.entity.projectiles.ProjectileSpiderBall;
import team.cqr.cqrepoured.entity.projectiles.ProjectileSpiderHook;
import team.cqr.cqrepoured.entity.projectiles.ProjectileThrownBlock;
import team.cqr.cqrepoured.entity.projectiles.ProjectileVampiricSpell;
import team.cqr.cqrepoured.entity.projectiles.ProjectileWeb;
import team.cqr.cqrepoured.init.CQREntityTypes;
import team.cqr.cqrepoured.tileentity.TileEntityExporter;
import team.cqr.cqrepoured.tileentity.TileEntityExporterChest;
import team.cqr.cqrepoured.tileentity.TileEntityForceFieldNexus;
import team.cqr.cqrepoured.tileentity.TileEntityMap;
import team.cqr.cqrepoured.tileentity.TileEntityTable;

public class CQREntityRenderers
{
	private CQREntityRenderers() {}

	public static void registerRenderers() {
		registerTileRenderers();
		registerProjectileAndMiscRenderers();
		if (CQRConfig.isAprilFoolsEnabled()) {
			registerAprilFoolsRenderer();
		} else {
			registerEntityRenderers();
		}
		registerBossRenderers();
		registerMountRenderers();
	}

	// Registers a big chungus renderer that renders on april the first
	protected static void registerAprilFoolsRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(AbstractEntityCQR.class, RenderChungus::new);
	}

	protected static void registerTileRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTable.class, new TileEntityTableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExporter.class, new TileEntityExporterRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForceFieldNexus.class, new TileEntityForceFieldNexusRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExporterChest.class, new TileEntityExporterChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMap.class, new TileEntityMapPlaceHolderRenderer());
	}

	protected static void registerProjectileAndMiscRenderers() {
		// Projectiles
		RenderingRegistry.registerEntityRenderingHandler(CQREntityTypes.PROJECTILE_BULLET.get(), RenderProjectileBullet::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileCannonBall.class, RenderProjectileCannonBall::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileEarthQuake.class, RenderProjectileEarthQuake::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectilePoisonSpell.class, RenderProjectilePoisonSpell::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileSpiderBall.class, RenderProjectileSpiderBall::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileVampiricSpell.class, RenderProjectileVampiricSpell::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileFireWallPart.class, RenderProjectileFirewallPart::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileHookShotHook.class, RenderProjectileHookShotHook::new);
		RenderingRegistry.registerEntityRenderingHandler(CQREntityTypes.PROJECTILE_BUBBLE.get(), RenderProjectileBubble::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileHotFireball.class, RenderProjectileHotFireball::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileWeb.class, RenderProjectileWeb::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileSpiderHook.class, RenderProjectileSpiderHook::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileThrownBlock.class, RenderProjectileThrownBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileHomingEnderEye.class, RenderProjectileHomingEnderEye::new);

		// Miscs
		RenderingRegistry.registerEntityRenderingHandler(EntitySummoningCircle.class, RenderSummoningCircle::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingSkullMinion.class, RenderFlyingSkull::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBubble.class, RenderBubble::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWalkerKingIllusion.class, RenderCQRWalkerKingIllusion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRWasp.class, RenderCQRWasp::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWalkerTornado.class, RenderWalkerTornado::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRPirateParrot.class, RenderPirateParrot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityIceSpike.class, RenderIceSpike::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiderEgg.class, RenderSpiderEgg::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCalamityCrystal.class, RenderCalamityCrystal::new);
		RenderingRegistry.registerEntityRenderingHandler(ProjectileEnergyOrb.class, RenderEnergyOrb::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityElectricField.class, RenderElectricFieldEntity::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityColoredLightningBolt.class, RenderColoredLightningBolt::new);

		// Multipart parts
		RenderingRegistry.registerEntityRenderingHandler(PartEntity.class, RenderMultiPartPart::new);
	}

	protected static void registerEntityRenderers() {
		// Mobs
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRBoarman.class, RenderCQRBoarman::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRDummy.class, RenderCQRDummy::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRDwarf.class, RenderCQRDwarf::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQREnderman.class, RenderCQREnderman::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRGolem.class, RenderCQRGolem::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRGremlin.class, RenderCQRGremlin::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRHuman.class, RenderCQRHuman::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRIllager.class, RenderCQRIllager::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRMandril.class, RenderCQRMandril::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRMinotaur.class, RenderCQRMinotaur::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRMummy.class, RenderCQRMummy::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRNPC.class, RenderCQRNPC::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQROgre.class, RenderCQROgre::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQROrc.class, RenderCQROrc::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRPirate.class, RenderCQRPirate::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRSkeleton.class, RenderCQRSkeleton::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRSpectre.class, RenderCQRSpectre::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRTriton.class, RenderCQRTriton::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRWalker.class, RenderCQRWalker::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRZombie.class, RenderCQRZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRGoblin.class, RenderCQRGoblin::new);
	}

	protected static void registerMountRenderers() {
		// Mounts
		RenderingRegistry.registerEntityRenderingHandler(EntityGiantEndermite.class, RenderGiantEndermite::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGiantSilverfishNormal.class, RenderGiantSilverfish::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGiantSilverfishGreen.class, RenderGiantSilverfishGreen::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGiantSilverfishRed.class, RenderGiantSilverfishRed::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPollo.class, RenderPollo::new);
	}

	protected static void registerBossRenderers() {
		// Nether Dragon
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRNetherDragon.class, RenderCQRNetherDragon::new);
		RenderingRegistry.registerEntityRenderingHandler(SubEntityNetherDragonSegment.class, RenderCQRNetherDragonSegment::new);

		// Giant Tortoise
		// RenderingRegistry.registerEntityRenderingHandler(EntityCQRGiantTortoise.class, RenderCQRGiantTortoise::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRGiantTortoise.class, RenderCQRGiantTortoiseGecko::new);

		// Lich
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRLich.class, renderManager -> new RenderCQRMage<>(renderManager, new ModelLich(), "boss/lich"));

		// Boar Mage
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRBoarmage.class, renderManager -> new RenderCQRMage<>(renderManager, new ModelPigMage(), "boss/pig_mage"));

		// Necromancer
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRNecromancer.class, renderManager -> new RenderCQRNecromancer(renderManager, new ModelNecromancer(), "boss/necromancer"));

		// Walker King
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRWalkerKing.class, RenderCQRWalkerKing::new);

		// Pirate Captain
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRPirateCaptain.class, RenderCQRPirateCaptain::new);

		// Shelob
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRGiantSpider.class, RenderCQRGiantSpider::new);

		// Spectre Lord
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRSpectreLord.class, RenderCQRSpectreLord::new);
		// RenderingRegistry.registerEntityRenderingHandler(EntitySpectreLordIllusion.class, RenderSpectreLordIllusion::new);
		// RenderingRegistry.registerEntityRenderingHandler(EntitySpectreLordCurse.class, RenderSpectreLordCurse::new);
		// RenderingRegistry.registerEntityRenderingHandler(EntitySpectreLordExplosion.class, RenderSpectreLordExplosion::new);
		RenderingRegistry.registerEntityRenderingHandler(AbstractEntityLaser.class, RenderLaser<AbstractEntityLaser>::new);

		// Ender King
		RenderingRegistry.registerEntityRenderingHandler(EntityCQREnderKing.class, RenderCQREnderKing::new);

		// Geckolib
		// Ender Calamity
		RenderingRegistry.registerEntityRenderingHandler(EntityCQREnderCalamity.class, RenderCQREnderCalamity::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEndLaserTargeting.class, RenderEndLaser<AbstractEntityLaser>::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEndLaser.class, RenderEndLaser<AbstractEntityLaser>::new);

		// GeckoLib
		// Exterminator
		RenderingRegistry.registerEntityRenderingHandler(EntityCQRExterminator.class, RenderCQRExterminator::new);
		RenderingRegistry.registerEntityRenderingHandler(SubEntityExterminatorFieldEmitter.class, RenderExterminatorBackpackPart::new);
	}

}
