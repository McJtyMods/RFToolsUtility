package mcjty.rftoolsutility.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> commands = dispatcher.register(
                Commands.literal(RFToolsUtility.MODID)
                        .then(CommandSetBuffs.register(dispatcher))
                        .then(CommandCleanupReceivers.register(dispatcher))
        );

        dispatcher.register(Commands.literal("rfutil").redirect(commands));
    }

}
