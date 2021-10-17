package mcjty.rftoolsutility.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.PorterTools;
import net.minecraft.util.math.BlockPos;

public class CommandHandler {

    public static final String CMD_GET_DESTINATION_INFO = "getDestinationInfo";
    public static final Key<Integer> PARAM_ID = new Key<>("id", Type.INTEGER);

    public static final String CMD_CLEAR_TARGET = "clearTarget";
    public static final String CMD_SET_TARGET = "setTarget";
    public static final Key<Integer> PARAM_TARGET = new Key<>("target", Type.INTEGER);

    public static final String CMD_GET_TARGETS = "getTargets";
    public static final String CMD_FORCE_TELEPORT = "forceTeleport";

    public static final Key<String> PARAM_DIMENSION = new Key<>("dimension", Type.STRING);
    public static final Key<BlockPos> PARAM_POS = new Key<>("pos", Type.BLOCKPOS);


    public static void registerCommands() {
        McJtyLib.registerCommand(RFToolsUtility.MODID, CMD_GET_DESTINATION_INFO, (player, arguments) -> {
            PorterTools.returnDestinationInfo(player, arguments.get(PARAM_ID));
            return true;
        });

        McJtyLib.registerCommand(RFToolsUtility.MODID, CMD_CLEAR_TARGET, (player, arguments) -> {
            PorterTools.clearTarget(player, arguments.get(PARAM_TARGET));
            return true;
        });
        McJtyLib.registerCommand(RFToolsUtility.MODID, CMD_SET_TARGET, (player, arguments) -> {
            PorterTools.setTarget(player, arguments.get(PARAM_TARGET));
            return true;
        });
        McJtyLib.registerCommand(RFToolsUtility.MODID, CMD_GET_TARGETS, (player, arguments) -> {
            PorterTools.returnTargets(player);
            return true;
        });
        McJtyLib.registerCommand(RFToolsUtility.MODID, CMD_FORCE_TELEPORT, (player, arguments) -> {
            PorterTools.forceTeleport(player, LevelTools.getId(arguments.get(PARAM_DIMENSION)), arguments.get(PARAM_POS));
            return true;
        });
    }

}
