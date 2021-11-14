package team.cqr.cqrepoured.structuregen.grid;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Predicates;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.integration.IntegrationInformation;
import team.cqr.cqrepoured.structuregen.DungeonRegistry;
import team.cqr.cqrepoured.structuregen.WorldDungeonGenerator;
import team.cqr.cqrepoured.structuregen.dungeons.DungeonBase;
import team.cqr.cqrepoured.util.CQRWeightedRandom;
import team.cqr.cqrepoured.util.DungeonGenUtils;
import team.cqr.cqrepoured.util.PropertyFileHelper;
import team.cqr.cqrepoured.util.VanillaStructureHelper;

public class DungeonGrid {

	private final String name;
	
	private final List<DungeonBase> dungeons;
	private int distance;
	private int spread;
	private double rarityFactor;
	private int checkRadiusInChunks;

	// chance to generate a dungeon at a grid position
	private int chance;
	// TODO used to determine which grid comes first
	private int priority;
	// TODO the index of all grids which is used to calculate the seed
	private int id;
	// different seed for each grid (used to calculate dungeon spread)
	private int seed = new Random(id).nextInt();
	// TODO Maybe also include grid offset based on id?
	private int offset;
	
	public String getName() {
		return this.name;
	}
	
	public DungeonGrid(final String name, Properties properties) {
		this.name = name;
		this.distance = PropertyFileHelper.getIntProperty(properties, "distance", CQRConfig.general.dungeonSeparation);
		this.spread = PropertyFileHelper.getIntProperty(properties, "spread", CQRConfig.general.dungeonSpread);
		this.rarityFactor = PropertyFileHelper.getDoubleProperty(properties, "rarityFactor", CQRConfig.general.dungeonRarityFactor);
		this.chance = PropertyFileHelper.getIntProperty(properties, "chance", CQRConfig.general.overallDungeonChance);
		this.priority = PropertyFileHelper.getIntProperty(properties, "priority", 10);
		this.checkRadiusInChunks = PropertyFileHelper.getIntProperty(properties, "checkRadius", 4);
		this.offset = PropertyFileHelper.getIntProperty(properties, "offset", 0);
		this.dungeons = Arrays.stream(PropertyFileHelper.getStringArrayProperty(properties, "dungeons", new String[0], true))
				.map(s -> DungeonRegistry.getInstance().getDungeon(s)).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	public List<DungeonBase> getDungeons() {
		return this.dungeons;
	}
	
	@Nullable
	public DungeonBase getDungeonAt(World world, int chunkX, int chunkZ) {
		return this.getDungeonAt(world, chunkX, chunkZ, Predicates.alwaysTrue());
	}

	@Nullable
	public DungeonBase getDungeonAt(World world, int chunkX, int chunkZ, Predicate<DungeonBase> dungeonPredicate) {
		Random random = WorldDungeonGenerator.getRandomForCoords(world, chunkX, chunkZ);
		if (!this.canSpawnDungeonAtCoords(world, chunkX, chunkZ, random)) {
			return null;
		}

		Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8));
		CQRWeightedRandom<DungeonBase> possibleDungeons = this.getDungeonsForPos(world, biome, chunkX, chunkZ);
		DungeonBase dungeon = possibleDungeons.next(random);
		if (dungeon == null) {
			log(world, chunkX, chunkZ, "Could not find any dungeon for biome: %s (%s)", biome, BiomeDictionary.getTypes(biome));
			return null;
		}
		if (!dungeonPredicate.test(dungeon)) {
			return null;
		}

		int weight = dungeon.getWeight();
		int totalWeight = possibleDungeons.getTotalWeight();
		double chanceModifier = 1.0D / Math.pow((double) weight / (double) totalWeight, rarityFactor);
		if (!DungeonGenUtils.percentageRandom((double) dungeon.getChance() / 100.0D * chanceModifier, random)) {
			log(world, chunkX, chunkZ, "Specific dungeon generation chance check failed for dungeon: %s", dungeon);
			return null;
		}

		return dungeon;
	}

	/**
	 * @return true when the passed chunk coords are on the dungeon grid
	 */
	public boolean canSpawnStructureAtCoords(World world, int chunkX, int chunkZ) {
		int dungeonSeparation = this.getDistance();
		// Check whether this chunk is farther north than the wall
		if (CQRConfig.wall.enabled && chunkZ < -CQRConfig.wall.distance && CQRConfig.general.moreDungeonsBehindWall) {
			dungeonSeparation = MathHelper.ceil((double) dungeonSeparation / CQRConfig.general.densityBehindWallFactor);
		}
		int dungeonSpread = Math.min(this.getSpread() + 1, dungeonSeparation);

		int cx = chunkX + offset - (DungeonGenUtils.getSpawnX(world) >> 4);
		int cz = chunkZ + offset - (DungeonGenUtils.getSpawnZ(world) >> 4);
		if (dungeonSpread <= 1) {
			return cx % dungeonSeparation == 0 && cz % dungeonSeparation == 0;
		}

		int x = MathHelper.intFloorDiv(cx, dungeonSeparation);
		int z = MathHelper.intFloorDiv(cz, dungeonSeparation);
		Random random = world.setRandomSeed(x, z, seed);
		x *= dungeonSeparation;
		z *= dungeonSeparation;
		x += random.nextInt(dungeonSpread);
		z += random.nextInt(dungeonSpread);
		return x == cx && z == cz;
	}
	
	/**
	 * Checks if<br>
	 * - the chunk coords are on the dungeon grid<br>
	 * - this chunk is far away enough from the spawn<br>
	 * - grid chance is fulfilled<br>
	 * - other structures are far away enough
	 * 
	 * @return true when dungeon can be spawned in this chunk
	 */
	public boolean canSpawnDungeonAtCoords(World world, int chunkX, int chunkZ, Random random) {
		// Check if the chunk is on the grid
		if (!canSpawnStructureAtCoords(world, chunkX, chunkZ)) {
			return false;
		}

		if (!DungeonGenUtils.isFarAwayEnoughFromSpawn(world, chunkX, chunkZ)) {
			log(world, chunkX, chunkZ, "Too near to spawn point");
			return false;
		}

		if (!DungeonGenUtils.percentageRandom(this.chance, random)) {
			log(world, chunkX, chunkZ, "Grid dungeon generation chance check failed");
			return false;
		}

		return !isOtherStructureNearby(world, chunkX, chunkZ);
	}

	/**
	 * @return true when a location specific dungeon, a vanilla structure or a aw2 structure is nearby
	 */
	public boolean isOtherStructureNearby(World world, int chunkX, int chunkZ) {
		// Checks if this chunk is in the "wall zone", if yes, abort
		if (DungeonGenUtils.isInWallRange(world, chunkX, chunkZ)) {
			log(world, chunkX, chunkZ, "Nearby wall in the north structure was found");
			return true;
		}

		if (!DungeonGenUtils.isFarAwayEnoughFromLocationSpecifics(world, chunkX, chunkZ, 4)) {
			log(world, chunkX, chunkZ, "Nearby location specific structure was found");
			return true;
		}

		BlockPos pos = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);
		if (CQRConfig.advanced.generationRespectOtherStructures) {
			// Vanilla Structures
			if (VanillaStructureHelper.isStructureInRange(world, pos, MathHelper.ceil(CQRConfig.advanced.generationMinDistanceToOtherStructure / 16.0D))) {
				log(world, chunkX, chunkZ, "Nearby vanilla structure was found");
				return true;
			}
			// AW2-Structures
			if (IntegrationInformation.isAW2StructureAlreadyThere(world, pos)) {
				log(world, chunkX, chunkZ, "Nearby ancient warfare 2 structure was found");
				return true;
			}
		}

		// check for nearby CQR dungeon generated by grid with higher priority
		// DONE make range a grid property?
		for (int x = -this.checkRadiusInChunks; x <= this.checkRadiusInChunks; x++) {
			for (int z = -this.checkRadiusInChunks; z <= this.checkRadiusInChunks; z++) {
				if (WorldDungeonGenerator.getDungeonAt(world, chunkX + x, chunkZ + z, grid -> grid.priority < this.priority, Predicates.alwaysTrue()) != null) {
					if (x * x + z * z > this.checkRadiusInChunks * this.checkRadiusInChunks) {
						continue;
					}
					log(world, chunkX, chunkZ, "Nearby cqrepoured structure was found");
					return true;
				}
			}
		}

		return false;
	}
	
	private static void log(World world, int chunkX, int chunkZ, String message, Object... params) {
		if (!CQRConfig.advanced.debugDungeonGen) {
			return;
		}
		CQRMain.logger.info("Failed to generate structure at x={} z={} dim={}: {}", (chunkX << 4) + 8, (chunkZ << 4) + 8, world.provider.getDimension(),
				String.format(message, params));
	}
	
	public CQRWeightedRandom<DungeonBase> getDungeonsForPos(World world, Biome biome, int chunkX, int chunkZ) {
		CQRWeightedRandom<DungeonBase> dungeonsForChunk = new CQRWeightedRandom<>();

		for (DungeonBase dungeon : this.dungeons) {
			if (dungeon.canSpawnAt(world, biome, chunkX, chunkZ)) {
				dungeonsForChunk.add(dungeon, dungeon.getWeight());
			}
		}

		return dungeonsForChunk;
	}

	public int getDistance() {
		return this.distance;
	}

	public int getSpread() {
		return this.spread;
	}
	
	public double getRarityFactor() {
		return this.rarityFactor;
	}

	public int getPriority() {
		return priority;
	}

	public void setId(int id) {
		this.id = id;
		ThreadLocalRandom.current().setSeed(id);
		this.seed = ThreadLocalRandom.current().nextInt();
	}

}
