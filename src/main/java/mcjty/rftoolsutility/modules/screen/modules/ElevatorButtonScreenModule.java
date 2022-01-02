package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ElevatorButtonScreenModule implements IScreenModule<ElevatorButtonScreenModule.ModuleElevatorInfo> {
    private ResourceKey<Level> dim = Level.OVERWORLD;
    private BlockPos coordinate = BlockPosTools.INVALID;
    private ScreenModuleHelper helper = new ScreenModuleHelper();

    public static class ModuleElevatorInfo implements IModuleData {

        public static final String ID = RFToolsUtility.MODID + ":elevator";

        private int level;
        private int maxLevel;
        private BlockPos pos;
        private List<Integer> heights;

        @Override
        public String getId() {
            return ID;
        }

        public ModuleElevatorInfo(int level, int maxLevel, BlockPos pos, List<Integer> heights) {
            this.level = level;
            this.maxLevel = maxLevel;
            this.pos = pos;
            this.heights = heights;
        }

        public ModuleElevatorInfo(FriendlyByteBuf buf) {
            level = buf.readInt();
            maxLevel = buf.readInt();
            pos = buf.readBlockPos();
            int s = buf.readByte();
            heights = new ArrayList<>(s);
            for (int i = 0; i < s; i++) {
                heights.add((int) buf.readShort());
            }
        }

        public BlockPos getPos() {
            return pos;
        }

        public List<Integer> getHeights() {
            return heights;
        }

        public int getLevel() {
            return level;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        @Override
        public void writeToBuf(FriendlyByteBuf buf) {
            buf.writeInt(level);
            buf.writeInt(maxLevel);
            buf.writeBlockPos(pos);
            buf.writeByte(heights.size());
            for (Integer height : heights) {
                buf.writeShort(height);
            }
        }
    }

    @Override
    public ModuleElevatorInfo getData(IScreenDataHelper helper, Level worldObj, long millis) {
        Level world = LevelTools.getLevel(worldObj, dim);
        if (world == null) {
            return null;
        }

        if (!LevelTools.isLoaded(world, coordinate)) {
            return null;
        }

        BlockEntity te = world.getBlockEntity(coordinate);

        // @todo 1.14
//        if (!(te instanceof ElevatorTileEntity)) {
//            return null;
//        }
//
//        ElevatorTileEntity elevatorTileEntity = (ElevatorTileEntity) te;
//        List<Integer> heights = new ArrayList<>();
//        elevatorTileEntity.findElevatorBlocks(heights);
//        return new ModuleElevatorInfo(elevatorTileEntity.getCurrentLevel(heights),
//                elevatorTileEntity.getLevelCount(heights),
//                elevatorTileEntity.findBottomElevator(),
//                heights);
        return null;
    }

    @Override
    public void setupFromNBT(CompoundTag tagCompound, ResourceKey<Level> dim, BlockPos pos) {
        if (tagCompound != null) {
            coordinate = BlockPosTools.INVALID;
            if (tagCompound.contains("elevatorx")) {
                if (tagCompound.contains("elevatordim")) {
                    this.dim = LevelTools.getId(tagCompound.getString("elevatordim"));
                } else {
                    // Compatibility reasons
                    this.dim = LevelTools.getId(tagCompound.getString("dim"));
                }
                if (Objects.equals(dim, this.dim)) {
                    BlockPos c = new BlockPos(tagCompound.getInt("elevatorx"), tagCompound.getInt("elevatory"), tagCompound.getInt("elevatorz"));
                    int dx = Math.abs(c.getX() - pos.getX());
                    int dz = Math.abs(c.getZ() - pos.getZ());
                    if (dx <= 64 && dz <= 64) {
                        coordinate = c;
                    }
                }
                boolean vertical = tagCompound.getBoolean("vertical");
                boolean large = tagCompound.getBoolean("large");
            }
        }
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {
        if (BlockPosTools.INVALID.equals(coordinate)) {
            if (player != null) {
                player.displayClientMessage(new TextComponent(ChatFormatting.RED + "Module is not linked to elevator!"), false);
            }
            return;
        }
        Level w = LevelTools.getLevel(world, dim);
        if (w == null) {
            return;
        }

        if (!LevelTools.isLoaded(world, coordinate)) {
            return;
        }

        // @todo 1.14
//        TileEntity te = w.getTileEntity(coordinate);
//        if (!(te instanceof ElevatorTileEntity)) {
//            return;
//        }
//        ElevatorTileEntity elevatorTileEntity = (ElevatorTileEntity) te;
//
//        List<Integer> heights = new ArrayList<>();
//        elevatorTileEntity.findElevatorBlocks(heights);
//        int levelCount = elevatorTileEntity.getLevelCount(heights);
//        int level = -1;
//
//        if (vertical) {
//            int max = large ? 6 : 8;
//            int numcols = (levelCount + max - 1) / max;
//            int colw = getColumnWidth(numcols);
//
//            int yoffset = 0;
//            if (y >= yoffset) {
//                level = (y - yoffset) / (((large ? LARGESIZE : SMALLSIZE) - 2));
//                if (level < 0) {
//                    return;
//                }
//                if (numcols > 1) {
//                    int col = (x - 5) / (colw + 7);
//                    level = max - level - 1 + col * max;
//                    if (col == numcols - 1) {
//                        level -= max - (levelCount % max);
//                    }
//                } else {
//                    level = levelCount - level - 1;
//                }
//            }
//        } else {
//            int xoffset = 5;
//            if (x >= xoffset) {
//                level = (x - xoffset) / (((large ? LARGESIZE : SMALLSIZE) - 2));
//            }
//        }
//        if (level >= 0 && level < levelCount) {
//            elevatorTileEntity.toLevel(level);
//        }
    }

    public static int getColumnWidth(int numcols) {
        int colw;
        switch (numcols) {
            case 1: colw = 120; break;
            case 2: colw = 58; break;
            case 3: colw = 36; break;
            case 4: colw = 25; break;
            case 5: colw = 19; break;
            default: colw = 15; break;
        }
        return colw;
    }


    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.ELEVATOR_BUTTON_RFPERTICK.get();
    }
}
