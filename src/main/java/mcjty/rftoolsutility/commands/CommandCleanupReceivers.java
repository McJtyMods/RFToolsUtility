package mcjty.rftoolsutility.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandCleanupReceivers implements Command<CommandSource> {

    private static final CommandCleanupReceivers CMD = new CommandCleanupReceivers();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("cleanupreceivers")
                .requires(cs -> cs.hasPermission(2))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        TeleportDestinations.get(context.getSource().getLevel()).cleanupInvalid();
        return 0;
    }
}
