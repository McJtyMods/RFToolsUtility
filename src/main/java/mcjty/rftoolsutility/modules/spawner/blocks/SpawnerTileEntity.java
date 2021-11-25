package mcjty.rftoolsutility.modules.spawner.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.api.module.DefaultModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.blockcommands.ResultCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class SpawnerTileEntity extends GenericTileEntity implements ITickableTileEntity {

    // Client side for CMD_GET_SPAWNERINFO
    public float matterReceived0 = -1;
    public float matterReceived1 = -1;
    public float matterReceived2 = -1;

    public static final int SLOT_SYRINGE = 0;
    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .box(specific(SpawnerModule.SYRINGE.get()).in().out(), SLOT_SYRINGE, 22, 8, 1, 18, 1, 18)
            .playerSlots(10, 70));


    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, SpawnerConfiguration.SPAWNER_MAXENERGY, SpawnerConfiguration.SPAWNER_RECEIVEPERTICK);

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Spawner")
            .containerSupplier(windowId -> new GenericContainer(SpawnerModule.CONTAINER_SPAWNER, windowId, CONTAINER_FACTORY, this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage));

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(SpawnerTileEntity.this);

    private final LazyOptional<IMachineInformation> infoHandler = LazyOptional.of(this::createMachineInfo);

    @Cap(type = CapType.MODULE)
    private final LazyOptional<IModuleSupport> moduleSupportHandler = LazyOptional.of(() -> new DefaultModuleSupport(SLOT_SYRINGE) {
        @Override
        public boolean isModule(ItemStack itemStack) {
            return itemStack.getItem() == SpawnerModule.SYRINGE.get();
        }
    });

    private float matter[] = new float[]{0, 0, 0};
    private boolean checkSyringe = true;
    private String prevMobId = null;
    private String mobId = "";

    private AxisAlignedBB entityCheckBox = null;

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(SpawnerTileEntity::new)
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsutility:todo"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold()));
    }


    public SpawnerTileEntity() {
        super(SpawnerModule.TYPE_SPAWNER.get());
    }

    private void testSyringe() {
        if (!checkSyringe) {
            return;
        }
        checkSyringe = false;
        mobId = null;
        ItemStack itemStack = items.getStackInSlot(0);
        if (itemStack.isEmpty()) {
            clearMatter();
            return;
        }

        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            clearMatter();
            return;
        }

        mobId = tagCompound.getString("mobId");
        if (mobId.isEmpty()) {
            clearMatter();
            return;
        }
        int level = tagCompound.getInt("level");
        if (level < SpawnerConfiguration.maxMobInjections.get()) {
            clearMatter();
            return;
        }
        if (prevMobId != null && !prevMobId.equals(mobId)) {
            clearMatter();
        }
    }

    public NoDirectionItemHander getItems() {
        return items;
    }

    private void clearMatter() {
        if (matter[0] != 0 || matter[1] != 0 || matter[2] != 0) {
            matter[0] = matter[1] = matter[2] = 0;
            setChanged();
        }
    }

    public boolean addMatter(ItemStack stack, int m, float beamerInfusionFactor) {
        testSyringe();
        if (mobId == null || mobId.isEmpty()) {
            return false;       // No matter was added.
        }
        int materialType = 0;
        Float factor = null;
        SpawnerRecipes.MobData mobData = getMobData();
        if (mobData == null) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            factor = mobData.getItem(i).match(stack);
            if (factor != null) {
                break;
            }
            materialType++;
        }
        if (factor == null) {
            // This type of material is not supported by the spawner.
            return false;
        }

        float mm = matter[materialType];
        mm += m * factor * 3.0f / (3.0f - beamerInfusionFactor);
        if (mm > SpawnerConfiguration.maxMatterStorage) {
            mm = SpawnerConfiguration.maxMatterStorage;
        }
        matter[materialType] = mm;
        setChanged();
        return true;
    }

    @Nullable
    private SpawnerRecipes.MobData getMobData() {
        SpawnerRecipes.MobData mobData = SpawnerRecipes.getMobData(level, mobId);
        if (mobData == null) {
            Logging.logError("The mob spawn amounts list for mob " + mobId + " is missing!");
        }
        return mobData;
    }

    public float[] getMatter() {
        return matter;
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        testSyringe();
        if (mobId == null || mobId.isEmpty()) {
            return;
        }

        SpawnerRecipes.MobData mobData = getMobData();
        if (mobData == null) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            if (matter[i] < mobData.getItem(i).getAmount()) {
                return;     // Not enough material yet.
            }
        }

        // We have enough materials. Check power.
        Integer rf = mobData.getSpawnRf();

        rf = (int) (rf * (2.0f - infusable.getInfusedFactor()) / 2.0f);
        if (energyStorage.getEnergyStored() < rf) {
            return;
        }
        energyStorage.consumeEnergy(rf);

        for (int i = 0; i < 3; i++) {
            matter[i] -= mobData.getItem(i).getAmount();
        }

        setChanged();

        BlockState state = level.getBlockState(getBlockPos());
        Direction k = OrientationTools.getOrientation(state);
        int sx = getBlockPos().getX();
        int sy = getBlockPos().getY();
        int sz = getBlockPos().getZ();
        Vector3i dir = k.getNormal();
        sx += dir.getX();
        sy += dir.getY();
        sz += dir.getZ();


//        if (entityCheckBox == null) {
//            entityCheckBox = AxisAlignedBB.getBoundingBox(xCoord-9, yCoord-9, zCoord-9, xCoord+sx+10, yCoord+sy+10, zCoord+sz+10);
//        }
//
//        int cnt = countEntitiesWithinAABB(entityCheckBox);
//        if (cnt >= SpawnerConfiguration.maxEntitiesAroundSpawner) {
//            return;
//        }


        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mobId));
        if (type == null) {
            Logging.logError("Fail to spawn mob: " + mobId);
            return;
        }

        Entity entityLiving = type.create(level);
        if (entityLiving == null) {
            Logging.logError("Fail to spawn mob: " + mobId);
            return;
        }

        // @todo 1.15
//        if (entityLiving instanceof EntityDragon) {
//            // Ender dragon needs to be spawned with an additional NBT key set
//            CompoundNBT dragonTag = new CompoundNBT();
//            entityLiving.writeEntityToNBT(dragonTag);
//            dragonTag.setShort("DragonPhase", (short) 0);
//            entityLiving.readEntityFromNBT(dragonTag);
//        }

        if (k == Direction.DOWN) {
            sy -= entityLiving.getEyeHeight() - 1;  // @todo right? (used to be height)
        }

        entityLiving.moveTo(sx + 0.5D, sy, sz + 0.5D, 0.0F, 0.0F);
        level.addFreshEntity(entityLiving);
    }

//    private int countEntitiesWithinAABB(AxisAlignedBB aabb) {
//        int i = MathHelper.floor_double((aabb.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
//        int j = MathHelper.floor_double((aabb.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
//        int k = MathHelper.floor_double((aabb.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
//        int l = MathHelper.floor_double((aabb.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);
//
//        int cnt = 0;
//        for (int i1 = i; i1 <= j; ++i1) {
//            for (int j1 = k; j1 <= l; ++j1) {
//                if (world.getChunkProvider().chunkExists(i1, j1)) {
//                    cnt += countEntitiesWithinChunkAABB(world.getChunkFromChunkCoords(i1, j1), aabb);
//                }
//            }
//        }
//        return cnt;
//    }
//
//    private int countEntitiesWithinChunkAABB(Chunk chunk, AxisAlignedBB aabb) {
//        int cnt = 0;
//        int i = MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
//        int j = MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
//        i = MathHelper.clamp_int(i, 0, chunk.entityLists.length - 1);
//        j = MathHelper.clamp_int(j, 0, chunk.entityLists.length - 1);
//
//        for (int k = i; k <= j; ++k) {
//            List entityList = chunk.entityLists[k];
//            cnt += entityList.size();
//        }
//        return cnt;
//    }
//
//

    // Called from client side when a wrench is used.
    public void useWrench(PlayerEntity player) {
        BlockPos coord = RFToolsBase.instance.clientInfo.getSelectedTE();
        if (coord == null) {
            return; // Nothing to do.
        }
        TileEntity tileEntity = level.getBlockEntity(coord);

        double d = new Vector3d(coord.getX(), coord.getY(), coord.getZ()).distanceTo(new Vector3d(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
        if (d > SpawnerConfiguration.maxBeamDistance) {
            Logging.message(player, "Destination distance is too far!");
        } else if (tileEntity instanceof MatterBeamerTileEntity) {
            MatterBeamerTileEntity matterBeamerTileEntity = (MatterBeamerTileEntity) tileEntity;
            matterBeamerTileEntity.setDestination(getBlockPos());
            Logging.message(player, "Destination set!");
        }

        RFToolsBase.instance.clientInfo.setSelectedTE(null);
        RFToolsBase.instance.clientInfo.setDestinationTE(null);
    }


    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        matter[0] = info.getFloat("matter0");
        matter[1] = info.getFloat("matter1");
        matter[2] = info.getFloat("matter2");
        if (info.contains("mobId")) {
            mobId = info.getString("mobId");
        } else {
            mobId = null;
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        super.save(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putFloat("matter0", matter[0]);
        info.putFloat("matter1", matter[1]);
        info.putFloat("matter2", matter[2]);
        if (mobId != null && !mobId.isEmpty()) {
            info.putString("mobId", mobId);
        }
    }


    @Override
    public boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        if (world.isClientSide) {
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            useWrench(player);
        }
        return true;
    }

    public static final Key<Double> PARAM_MATTER0 = new Key<>("matter0", Type.DOUBLE);
    public static final Key<Double> PARAM_MATTER1 = new Key<>("matter1", Type.DOUBLE);
    public static final Key<Double> PARAM_MATTER2 = new Key<>("matter2", Type.DOUBLE);
    @ServerCommand
    public static final ResultCommand<?> CMD_GET_SPAWNERINFO = ResultCommand.<SpawnerTileEntity>create("getSpawnerInfo",
            (te, player, params) -> TypedMap.builder()
                    .put(PARAM_MATTER0, (double) te.matter[0])
                    .put(PARAM_MATTER1, (double) te.matter[1])
                    .put(PARAM_MATTER2, (double) te.matter[2])
                    .build(),
            (te, player, params) -> {
                te.matterReceived0 = params.get(PARAM_MATTER0).floatValue();
                te.matterReceived1 = params.get(PARAM_MATTER1).floatValue();
                te.matterReceived2 = params.get(PARAM_MATTER2).floatValue();
            });


    @Nonnull
    private IMachineInformation createMachineInfo() {
        return new IMachineInformation() {
            private final String[] TAGS = new String[]{"matter1", "matter2", "matter3", "mob"};
            private final String[] TAG_DESCRIPTIONS = new String[]{"The amount of matter in the first slot", "The amount of matter in the second slot",
                    "The amount of matter in the third slot", "The name of the mob being spawned"};

            @Override
            public int getTagCount() {
                return TAGS.length;
            }

            @Override
            public String getTagName(int index) {
                return TAGS[index];
            }

            @Override
            public String getTagDescription(int index) {
                return TAG_DESCRIPTIONS[index];
            }

            @Override
            public String getData(int index, long millis) {
                switch (index) {
                    case 0:
                        return Float.toString(matter[0]);
                    case 1:
                        return Float.toString(matter[1]);
                    case 2:
                        return Float.toString(matter[2]);
                    case 3:
                        return mobId;
                }
                return null;
            }
        };
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {

            @Override
            protected void onUpdate(int index) {
                super.onUpdate(index);
                checkSyringe = true;
                prevMobId = mobId;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == SpawnerModule.SYRINGE.get();
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY) {
            return infoHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

}
