package com.teamcqr.chocolatequestrepoured.network.client.handler;

import com.teamcqr.chocolatequestrepoured.customtextures.ClientPacketHandler;
import com.teamcqr.chocolatequestrepoured.network.server.packet.SPacketCustomTextures;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketHandlerSyncTextureSets implements IMessageHandler<SPacketCustomTextures, IMessage> {

	public CPacketHandlerSyncTextureSets() {
	}

	@Override
	public IMessage onMessage(SPacketCustomTextures message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (ctx.side.isClient()) {
				ClientPacketHandler.handleCTPacketClientside(message);
			}
		});

		return null;
	}

}
