package mcjty.rftoolsutility.modules.teleporter.commands;

import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.commands.AbstractRfToolsCommand;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

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
        TeleportDestinations destinations = TeleportDestinations.get(sender.getEntityWorld());

        Collection<TeleportDestinationClientInfo> validDestinations = destinations.getValidDestinations(sender.getEntityWorld(), null);
        for (TeleportDestinationClientInfo clientInfo : validDestinations) {
            DimensionType type = clientInfo.getDimension();
            ITextComponent component = new StringTextComponent("    Receiver: dimension=" + type.getRegistryName() + ", location=" + BlockPosTools.toString(clientInfo.getCoordinate()));
            if (sender instanceof PlayerEntity) {
                sender.sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
    }
}
