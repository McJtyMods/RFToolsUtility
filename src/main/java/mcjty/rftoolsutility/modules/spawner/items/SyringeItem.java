package mcjty.rftoolsutility.modules.spawner.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.items.BaseItem;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.data.SyringeData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SyringeItem extends BaseItem implements ITooltipSettings {

    public static final int MAX_SYRINGE_MODEL_LEVEL = 5;

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(
                    parameter("level", this::getLevelString),
                    parameter("mob", this::hasMob, SyringeItem::getMobName),
                    key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(),
                    parameter("level", this::getLevelString),
                    parameter("mob", this::hasMob, SyringeItem::getMobName))
    );


    public SyringeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    private String getLevelString(ItemStack stack) {
        return Integer.toString(getLevel(stack));
    }

    private boolean hasMob(ItemStack stack) {
        return getMobId(stack) != null;
    }

    public static int getLevel(ItemStack stack) {
        SyringeData data = stack.get(SpawnerModule.ITEM_SYRINGE_DATA);
        return data != null ? (data.level()  * 100 / SpawnerConfiguration.maxMobInjections.get()) : 0;
    }



    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, display, list, flag);
        List<Component> lines = new ArrayList<>();
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, lines, flag);
        lines.forEach(list);
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

    public static ItemStack createMobSyringe(Identifier mobId) {
        ItemStack syringe = new ItemStack(SpawnerModule.SYRINGE.get());
        SyringeData data = new SyringeData(mobId, 0);
        syringe.set(SpawnerModule.ITEM_SYRINGE_DATA, data);
        return syringe;
    }

    public static Identifier getMobId(ItemStack stack) {
        SyringeData data = stack.get(SpawnerModule.ITEM_SYRINGE_DATA);
        return data != null ? data.mob() : null;
    }

    // To be called client-side

    public static String getMobName(ItemStack stack) {
        Identifier id = getMobId(stack);
        if (id == null) {
            return null;
        }
        EntityType<?> type = Tools.getEntity(id);
        if (type != null) {
            return type.getDescription().getString() /* was getFormattedText() */;
        } else {
            return id.toString();
        }
    }

    @Override
    public List<ItemStack> getItemsForTab() {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(this));
        for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (entry.getValue().getCategory() != MobCategory.MISC) {
                items.add(createMobSyringe(id));
            }
        }
        return items;
    }

    @Nonnull
    @Override
    public InteractionResult use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            SyringeData data = stack.get(SpawnerModule.ITEM_SYRINGE_DATA);
            if (data != null) {
                String mobName = getMobName(stack);
                if (mobName != null) {
                    Logging.message(player, ChatFormatting.BLUE + "Mob: " + mobName);
                }
                int level = data.level() == -1 ? SpawnerConfiguration.maxMobInjections.get() : data.level();
                level = level * 100 / SpawnerConfiguration.maxMobInjections.get();
                Logging.message(player, ChatFormatting.BLUE + "Essence level: " + level + "%");
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity entityLiving = getEntityLivingFromClickedEntity(entity);
        if (entityLiving != null) {
            Identifier prevMobId = null;
            SyringeData data = stack.get(SpawnerModule.ITEM_SYRINGE_DATA);
            if (data != null) {
                prevMobId = data.mob();
            } else {
                data = new SyringeData(null, 0);
            }
            Identifier id = findSelectedMobId(entityLiving);
            if (id != null) {
                if (!id.equals(prevMobId)) {
                    data = data.withMob(id).withLevel(1);
                } else {
                    int level = data.level();
                    if (level == -1) {
                        level = 0;
                    }
                    level = Math.min(level + 1, SpawnerConfiguration.maxMobInjections.get());
                    data = data.withLevel(level);
                }
                stack.set(SpawnerModule.ITEM_SYRINGE_DATA, data);
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    private Identifier findSelectedMobId(Entity entity) {
        return Tools.getId(entity.getType());
    }

    @Override
    public ManualEntry getManualEntry() {
        return ManualHelper.create("rftoolsutility:machines/spawner");
    }

}
