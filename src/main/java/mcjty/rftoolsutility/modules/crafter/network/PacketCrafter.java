package mcjty.rftoolsutility.modules.crafter.network;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.crafter.CraftingRecipe;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterBaseTE;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCrafter {
    private BlockPos pos;

    private int recipeIndex;
    private ItemStack items[];
    private boolean keepOne;
    private CraftingRecipe.CraftMode craftInternal;

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(keepOne);
        buf.writeByte(craftInternal.ordinal());

        buf.writeByte(recipeIndex);
        for (ItemStack item : items) {
            buf.writeItemStack(item);
        }
    }

    public PacketCrafter() {
    }

    public PacketCrafter(PacketBuffer buf) {
        pos = buf.readBlockPos();
        keepOne = buf.readBoolean();
        craftInternal = CraftingRecipe.CraftMode.values()[buf.readByte()];

        recipeIndex = buf.readByte();
        items = new ItemStack[10];
        for (int i = 0 ; i < 10 ; i++) {
            items[i] = buf.readItemStack();
        }
    }

    public PacketCrafter(BlockPos pos, int recipeIndex, CraftingInventory inv, ItemStack result, boolean keepOne, CraftingRecipe.CraftMode craftInternal) {
        this.pos = pos;
        this.recipeIndex = recipeIndex;
        this.items = new ItemStack[10];
        for (int i = 0 ; i < 9 ; i++) {
            items[i] = inv.getStackInSlot(i).copy();
        }
        items[9] = result.copy();
        this.keepOne = keepOne;
        this.craftInternal = craftInternal;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = ctx.getSender().getEntityWorld().getTileEntity(pos);
            if(!(te instanceof CrafterBaseTE)) {
                Logging.logError("Wrong type of tile entity (expected CrafterBaseTE)!");
                return;
            }
            CrafterBaseTE crafterBlockTileEntity = (CrafterBaseTE) te;
            crafterBlockTileEntity.noRecipesWork = false;
            if (recipeIndex != -1) {
                updateRecipe(crafterBlockTileEntity);
            }
        });
        ctx.setPacketHandled(true);
    }

    private void updateRecipe(CrafterBaseTE crafterBlockTileEntity) {
        CraftingRecipe recipe = crafterBlockTileEntity.getRecipe(recipeIndex);
        recipe.setRecipe(items, items[9]);
        recipe.setKeepOne(keepOne);
        recipe.setCraftMode(craftInternal);
        crafterBlockTileEntity.selectRecipe(recipeIndex);
        crafterBlockTileEntity.markDirtyClient();
    }
}
