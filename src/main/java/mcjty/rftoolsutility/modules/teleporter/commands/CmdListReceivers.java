package mcjty.rftoolsutility.modules.teleporter.commands;

import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.commands.AbstractRfToolsCommand;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

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
    public void execute(PlayerEntity sender, String[] args) {
        TeleportDestinations destinations = TeleportDestinations.get(sender.getCommandSenderWorld());

        Collection<TeleportDestinationClientInfo> validDestinations = destinations.getValidDestinations(sender.getCommandSenderWorld(), null);
        for (TeleportDestinationClientInfo clientInfo : validDestinations) {
            RegistryKey<World> type = clientInfo.getDimension();
            ITextComponent component = new StringTextComponent("    Receiver: dimension=" + type.location().getPath() + ", location=" + BlockPosTools.toString(clientInfo.getCoordinate()));
            sender.displayClientMessage(component, false);
        }
    }
}
