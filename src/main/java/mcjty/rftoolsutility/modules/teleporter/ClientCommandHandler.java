package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterBlock;

public class ClientCommandHandler {

    public static final String CMD_RETURN_DESTINATION_INFO = "returnDestinationInfo";
    public static final Key<Integer> PARAM_ID = new Key<>("id", Type.INTEGER);
    public static final Key<String> PARAM_NAME = new Key<>("name", Type.STRING);

    public static void registerCommands() {
        // @todo 1.14
//        McJtyLib.registerClientCommand(RFTools.MODID, CMD_RETURN_SCANNER_CONTENTS, (player, arguments) -> {
//            GuiStorageScanner.fromServer_inventory = arguments.get(PARAM_STACKS);
//            return true;
//        });
//        McJtyLib.registerClientCommand(RFTools.MODID, CMD_RETURN_SCANNER_SEARCH, (player, arguments) -> {
//            GuiStorageScanner.fromServer_foundInventories = new HashSet<>(arguments.get(PARAM_INVENTORIES));
//            return true;
//        });
        McJtyLib.registerClientCommand(RFToolsUtility.MODID, CMD_RETURN_DESTINATION_INFO, (player, arguments) -> {
            MatterTransmitterBlock.setDestinationInfo(arguments.get(PARAM_ID), arguments.get(PARAM_NAME));
            return true;
        });
    }
}
