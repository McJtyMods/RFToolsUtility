package mcjty.rftoolsutility.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.McJtyLib;
import mcjty.lib.gui.BuffStyle;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TextComponent;

public class CommandSetBuffs implements Command<CommandSourceStack> {

    private static final CommandSetBuffs CMD = new CommandSetBuffs();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("setbuffs")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("style", StringArgumentType.string())
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                        .executes(CMD))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        String styleS = context.getArgument("style", String.class);
        BuffStyle buffStyle = BuffStyle.getStyle(styleS);
        if (buffStyle == null) {
            context.getSource().sendFailure(new TextComponent("Unknown style '" + styleS + "'! Use one of 'off', 'topleft', 'topright', 'botleft', 'botright'"));
            return 0;
        }
        int x = context.getArgument("x", Integer.class);
        int y = context.getArgument("y", Integer.class);
        Player playerEntity = context.getSource().getPlayerOrException();
        McJtyLib.getPreferencesProperties(playerEntity).ifPresent(p -> p.setBuffXY(buffStyle, x, y));
        return 0;
    }
}
