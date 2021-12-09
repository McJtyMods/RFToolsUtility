package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

import static mcjty.rftoolsutility.modules.screen.ScreenModule.TYPE_SCREEN_HIT;

public class ScreenHitTileEntity extends GenericTileEntity {

    private int dx;
    private int dy;
    private int dz;

    public ScreenHitTileEntity() {
        super(TYPE_SCREEN_HIT.get());
    }

    public void setRelativeLocation(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        setChanged();
        BlockState state = getLevel().getBlockState(getBlockPos());
        getLevel().sendBlockUpdated(getBlockPos(), state, state, 3);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
        dx = tagCompound.getInt("dx");
        dy = tagCompound.getInt("dy");
        dz = tagCompound.getInt("dz");
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getDz() {
        return dz;
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putInt("dx", dx);
        tagCompound.putInt("dy", dy);
        tagCompound.putInt("dz", dz);
    }

//    @Override
//    public CompoundNBT getUpdateTag() {
//        CompoundNBT updateTag = super.getUpdateTag();
//        writeToNBT(updateTag);
//        return updateTag;
//    }
//
//    @Nullable
//    @Override
//    public SPacketUpdateTileEntity getUpdatePacket() {
//        CompoundNBT nbtTag = new CompoundNBT();
//        this.writeToNBT(nbtTag);
//        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
//        readFromNBT(packet.getNbtCompound());
//    }
}
