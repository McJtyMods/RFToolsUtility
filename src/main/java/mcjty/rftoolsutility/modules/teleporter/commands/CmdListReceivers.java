package mcjty.rftoolsutility.modules.teleporter.commands;

import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.commands.AbstractRfToolsCommand;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

import java.util.Collection;

public class CmdListReceivers extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public void execute(Player sender, String[] args) {
        TeleportDestinations destinations = TeleportDestinations.get(sender.getCommandSenderWorld());

        Collection<TeleportDestinationClientInfo> validDestinations = destinations.getValidDestinations(sender.getCommandSenderWorld(), null);
        for (TeleportDestinationClientInfo clientInfo : validDestinations) {
            ResourceKey<Level> type = clientInfo.getDimension();
            Component component = new TextComponent("    Receiver: dimension=" + type.location().getPath() + ", location=" + BlockPosTools.toString(clientInfo.getCoordinate()));
            sender.displayClientMessage(component, false);
        }
    }
}
