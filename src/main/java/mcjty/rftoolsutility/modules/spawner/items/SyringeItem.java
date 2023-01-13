package mcjty.rftoolsutility.modules.spawner.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
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
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1));
    }

    private String getLevelString(ItemStack stack) {
        return Integer.toString(getLevel(stack));
    }

    private boolean hasMob(ItemStack stack) {
        return getMobId(stack) != null;
    }

    public static int getLevel(ItemStack stack) {
        return NBTTools.getInt(stack, "level", SpawnerConfiguration.maxMobInjections.get()) * 100 / SpawnerConfiguration.maxMobInjections.get();
    }

    public static void initOverrides(SyringeItem item) {
        ItemProperties.register(item, new ResourceLocation(RFToolsUtility.MODID, "level"), (stack, world, livingEntity, seed) -> {
            int level = NBTTools.getInt(stack, "level", SpawnerConfiguration.maxMobInjections.get());
            level = level * MAX_SYRINGE_MODEL_LEVEL / SpawnerConfiguration.maxMobInjections.get();
            return level;
        });
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);
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
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putString("mobId", mobId.toString());
        syringe.setTag(tagCompound);
        return syringe;
    }

    public static String getMobId(ItemStack stack) {
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null) {
            return tagCompound.getString("mobId");
        }
        return null;
    }

    // To be called client-side
    public static String getMobName(ItemStack stack) {
        String id = getMobId(stack);
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
        if (type != null) {
            return type.getDescription().getString() /* was getFormattedText() */;
        } else {
            return id;
        }
    }

    // @todo 1.19.3
    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowedIn(group)) {
            items.add(new ItemStack(this));
            for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> entry : ForgeRegistries.ENTITY_TYPES.getEntries()) {
                ResourceLocation id = entry.getKey().location();
                if (entry.getValue().getCategory() != MobCategory.MISC) {
                    items.add(createMobSyringe(id));
                }
            }
        }
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            CompoundTag tagCompound = stack.getTag();
            if (tagCompound != null) {
                String mobName = getMobName(stack);
                if (mobName != null) {
                    Logging.message(player, ChatFormatting.BLUE + "Mob: " + mobName);
                }
                int level = tagCompound.contains("level") ? tagCompound.getInt("level") : SpawnerConfiguration.maxMobInjections.get();
                level = level * 100 / SpawnerConfiguration.maxMobInjections.get();
                Logging.message(player, ChatFormatting.BLUE + "Essence level: " + level + "%");
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.success(stack);
    }


    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity entityLiving = getEntityLivingFromClickedEntity(entity);
        if(entityLiving != null) {
            String prevMobId = null;
            CompoundTag tagCompound = stack.getTag();
            if (tagCompound != null) {
                prevMobId = tagCompound.getString("mobId");
            } else {
                tagCompound = new CompoundTag();
                stack.setTag(tagCompound);
            }
            String id = findSelectedMobId(entityLiving);
            if (id != null && !id.isEmpty()) {
                if (!id.equals(prevMobId)) {
                    tagCompound.putString("mobName", Tools.getId(entityLiving.getType()).toString());
                    tagCompound.putString("mobId", id);
                    tagCompound.putInt("level", 1);
                } else {
                    tagCompound.putInt("level", Math.min((tagCompound.contains("level") ? tagCompound.getInt("level") : SpawnerConfiguration.maxMobInjections.get()) + 1, SpawnerConfiguration.maxMobInjections.get()));
                }
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    private String findSelectedMobId(Entity entity) {
        ResourceLocation key = Tools.getId(entity.getType());
        return key != null ? key.toString() : null;
    }
}
