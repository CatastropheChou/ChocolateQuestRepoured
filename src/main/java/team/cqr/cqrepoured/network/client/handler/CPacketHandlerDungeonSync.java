package team.cqr.cqrepoured.network.client.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import team.cqr.cqrepoured.item.ItemDungeonPlacer;
import team.cqr.cqrepoured.network.AbstractPacketHandler;
import team.cqr.cqrepoured.network.server.packet.SPacketDungeonSync;

import java.util.function.Supplier;

public class CPacketHandlerDungeonSync extends AbstractPacketHandler<SPacketDungeonSync> {

	@Override
	protected void execHandlePacket(SPacketDungeonSync message, Supplier<Context> context, World world, PlayerEntity player) {
		ItemDungeonPlacer.updateClientDungeonList(message.getFakeDungeonList());
	}

}
