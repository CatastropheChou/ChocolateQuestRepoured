package team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.decoration;

import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.decoration.objects.*;
import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.decoration.paintings.RoomDecorPainting;

public class RoomDecorTypes {
	public static final IRoomDecor ANVIL = new RoomDecorAnvil();
	public static final IRoomDecor ARMOR_STAND = new RoomDecorArmorStand();
	public static final IRoomDecor BED = new RoomDecorBed();
	public static final IRoomDecor BREW_STAND = new RoomDecorBrewingStand();
	public static final IRoomDecor CAULDRON = new RoomDecorCauldron();
	public static final IRoomDecor CHEST = new RoomDecorChest();
	public static final IRoomDecor CRAFTING_TABLE = new RoomDecorCraftingTable();
	public static final IRoomDecor FIREPLACE = new RoomDecorFireplace();
	public static final IRoomDecor FURNACE = new RoomDecorFurnace();
	public static final IRoomDecor JUKEBOX = new RoomDecorJukebox();
	public static final IRoomDecor NONE = new RoomDecorNone();
	public static final IRoomDecor PLANKS = new RoomDecorPlanks();
	public static final IRoomDecor SHELF = new RoomDecorShelf();
	public static final IRoomDecor PILLAR = new RoomDecorPillar();
	public static final IRoomDecor TABLE_1x1 = new RoomDecorTableSmall();
	public static final IRoomDecor TABLE_2x2 = new RoomDecorTableMedium();
	public static final IRoomDecor TORCH = new RoomDecorTorch();
	public static final IRoomDecor UNLIT_TORCH = new RoomDecorUnlitTorch();
	public static final IRoomDecor WATER_BASIN = new RoomDecorWaterBasin();

	public static final RoomDecorPainting PAINTING = new RoomDecorPainting();
}
