package mcjty.rftoolsutility.modules.spawner.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SyringeItem extends Item {

    public static final int MAX_SYRINGE_MODEL_LEVEL = 5;

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(
                    parameter("level", this::getLevelString),
                    parameter("mob", this::hasMob, SyringeItem::getMobName),
                    key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(),
                    parameter("level", this::getLevelString),
                    parameter("mob", this::hasMob, SyringeItem::getMobName))
            ;


    public SyringeItem() {
        super(new Properties().tab(RFToolsUtility.setup.getTab()).stacksTo(1));
    }

    private String getLevelString(ItemStack stack) {
        return Integer.toString(getLevel(stack));
    }

    private boolean hasMob(ItemStack stack) {
        return getMobId(stack) != null;
    }

    public static int getLevel(ItemStack stack) {
        return NBTTools.getInt(stack, "level", 0) * 100 / SpawnerConfiguration.maxMobInjections.get();
    }

    public static void initOverrides(SyringeItem item) {
        ItemModelsProperties.register(item, new ResourceLocation(RFToolsUtility.MODID, "level"), (stack, world, livingEntity) -> {
            int level = NBTTools.getInt(stack, "level", 0);
            level = level * MAX_SYRINGE_MODEL_LEVEL / SpawnerConfiguration.maxMobInjections.get();
            return level;
        });
    }

    @Override
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flag);
    }


    @Nullable
    public static LivingEntity getEntityLivingFromClickedEntity(Entity entity) {
        if(entity instanceof LivingEntity) {
            return (LivingEntity) entity;
// @todo 1.15
            //        } else if(entity instanceof MultiPartEntityPart) {
//            IEntityMultiPart parent = ((MultiPartEntityPart)entity).parent;
//            if(parent instanceof LivingEntity) {
//                return (LivingEntity) parent;
//            }
        }
        return null;
    }

    public static ItemStack createMobSyringe(ResourceLocation mobId) {
        ItemStack syringe = new ItemStack(SpawnerModule.SYRINGE.get());
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putString("mobId", mobId.toString());
        tagCompound.putInt("level", SpawnerConfiguration.maxMobInjections.get());
        syringe.setTag(tagCompound);
        return syringe;
    }

    public static String getMobId(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            return tagCompound.getString("mobId");
        }
        return null;
    }

    // To be called client-side
    public static String getMobName(ItemStack stack) {
        String id = getMobId(stack);
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
        if (type != null) {
            return type.getDescription().getString() /* was getFormattedText() */;
        } else {
            return id;
        }
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
            for (Map.Entry<RegistryKey<EntityType<?>>, EntityType<?>> entry : ForgeRegistries.ENTITIES.getEntries()) {
                ResourceLocation id = entry.getKey().getRegistryName();
                if (entry.getValue().getCategory() != EntityClassification.MISC) {
                    items.add(createMobSyringe(id));
                }
            }
        }
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void initModel() {
//        for (int i = 0 ; i <= 5 ; i++) {
//            String domain = getRegistryName().getResourceDomain();
//            String path = getRegistryName().getResourcePath();
//            ModelBakery.registerItemVariants(this, new ModelResourceLocation(new ResourceLocation(domain, path + i), "inventory"));
//        }
//
//        ModelLoader.setCustomMeshDefinition(this, stack -> {
//            CompoundNBT tagCompound = stack.getTag();
//            if (tagCompound != null) {
//                String mobName = getMobName(stack);
//                if (mobName != null) {
//                    Logging.message(player, TextFormatting.BLUE + "Mob: " + mobName);
//                }
//                int level = tagCompound.getInt("level");
//                level = level * 100 / GeneralConfiguration.maxMobInjections.get();
//                Logging.message(player, TextFormatting.BLUE + "Essence level: " + level + "%");
//            }
//            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
//        }
//        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
//    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            CompoundNBT tagCompound = stack.getTag();
            if (tagCompound != null) {
                String mobName = getMobName(stack);
                if (mobName != null) {
                    Logging.message(player, TextFormatting.BLUE + "Mob: " + mobName);
                }
                int level = tagCompound.getInt("level");
                level = level * 100 / SpawnerConfiguration.maxMobInjections.get();
                Logging.message(player, TextFormatting.BLUE + "Essence level: " + level + "%");
            }
            return ActionResult.success(stack);
        }
        return ActionResult.success(stack);
    }


    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        LivingEntity entityLiving = getEntityLivingFromClickedEntity(entity);
        if(entityLiving != null) {
            String prevMobId = null;
            CompoundNBT tagCompound = stack.getTag();
            if (tagCompound != null) {
                prevMobId = tagCompound.getString("mobId");
            } else {
                tagCompound = new CompoundNBT();
                stack.setTag(tagCompound);
            }
            String id = findSelectedMobId(entityLiving);
            if (id != null && !id.isEmpty()) {
                if (!id.equals(prevMobId)) {
                    tagCompound.putString("mobName", entityLiving.getType().getRegistryName().toString());
                    tagCompound.putString("mobId", id);
                    tagCompound.putInt("level", 1);
                } else {
                    tagCompound.putInt("level", Math.min(tagCompound.getInt("level") + 1, SpawnerConfiguration.maxMobInjections.get()));
                }
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    private String findSelectedMobId(Entity entity) {
        ResourceLocation key = entity.getType().getRegistryName();
        return key != null ? key.toString() : null;
    }
}
