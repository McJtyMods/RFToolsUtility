package mcjty.rftoolsutility.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandCleanupReceivers implements Command<CommandSourceStack> {

    private static final CommandCleanupReceivers CMD = new CommandCleanupReceivers();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("cleanupreceivers")
                .requires(cs -> cs.hasPermission(2))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TeleportDestinations.get(context.getSource().getLevel()).cleanupInvalid();
        return 0;
    }
}
