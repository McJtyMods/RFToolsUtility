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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;

public class CommandSetBuffs implements Command<CommandSource> {

    private static final CommandSetBuffs CMD = new CommandSetBuffs();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("setbuffs")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("style", StringArgumentType.string())
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                        .executes(CMD))));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        String styleS = context.getArgument("style", String.class);
        BuffStyle buffStyle = BuffStyle.getStyle(styleS);
        if (buffStyle == null) {
            context.getSource().sendFailure(new StringTextComponent("Unknown style '" + styleS + "'! Use one of 'off', 'topleft', 'topright', 'botleft', 'botright'"));
            return 0;
        }
        int x = context.getArgument("x", Integer.class);
        int y = context.getArgument("y", Integer.class);
        PlayerEntity playerEntity = context.getSource().getPlayerOrException();
        McJtyLib.getPreferencesProperties(playerEntity).ifPresent(p -> p.setBuffXY(buffStyle, x, y));
        return 0;
    }
}
