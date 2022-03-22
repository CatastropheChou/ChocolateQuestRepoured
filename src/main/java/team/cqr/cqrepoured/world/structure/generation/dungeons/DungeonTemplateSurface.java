package team.cqr.cqrepoured.world.structure.generation.dungeons;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.util.PropertyFileHelper;
import team.cqr.cqrepoured.world.structure.generation.DungeonDataManager;
import team.cqr.cqrepoured.world.structure.generation.generators.AbstractDungeonGenerator;
import team.cqr.cqrepoured.world.structure.generation.generators.GeneratorTemplateSurface;

import java.io.File;
import java.util.Properties;
import java.util.Random;

/**
 * Copyright (c) 29.04.2019 Developed by DerToaster98 GitHub: https://github.com/DerToaster98
 */
public class DungeonTemplateSurface extends DungeonBase {

	protected File structureFolderPath = new File(CQRMain.CQ_STRUCTURE_FILES_FOLDER, "test");
	protected boolean rotateDungeon = true;

	public DungeonTemplateSurface(String name, Properties prop) {
		super(name, prop);

		this.structureFolderPath = PropertyFileHelper.getStructureFolderProperty(prop, "structureFolder", "test");
		this.rotateDungeon = PropertyFileHelper.getBooleanProperty(prop, "rotateDungeon", this.rotateDungeon);
	}

	@Override
	public AbstractDungeonGenerator<DungeonTemplateSurface> createDungeonGenerator(World world, int x, int y, int z, Random rand, DungeonDataManager.DungeonSpawnType spawnType) {
		return new GeneratorTemplateSurface(world, new BlockPos(x, y, z), this, rand);
	}

	public File getStructureFolderPath() {
		return this.structureFolderPath;
	}

	public boolean rotateDungeon() {
		return this.rotateDungeon;
	}

}
