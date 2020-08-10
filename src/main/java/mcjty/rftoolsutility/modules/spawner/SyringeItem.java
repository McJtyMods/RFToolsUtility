package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.varia.EntityTools;
import mcjty.lib.varia.Logging;
import mcjty.rftools.config.GeneralConfiguration;
import mcjty.rftools.setup.GuiProxy;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

//import net.minecraft.entity.monster.SkeletonType;

public class SyringeItem extends Item {

    public SyringeItem() {
        super(new Properties().group(RFToolsUtility.setup.getTab()).maxStackSize(1).containerItem(SpawnerSetup.EMPYY_SYRINGE));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        return tagCompound != null && tagCompound.getInt("level") > 0;
    }

    @Override
    public void fillItemGroup(ItemGroup groep, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));
            for (Map.Entry<ResourceLocation, EntityType<?>> entry : ForgeRegistries.ENTITIES.getEntries()) {
                if (entry.getValue().getClassification() != EntityClassification.MISC) {
                    items.add(createMobSyringe(entry.getKey()));
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

    private static @Nullable LivingEntity getEntityLivingFromClickedEntity(Entity entity) {
        if(entity instanceof LivingEntity) {
            return (LivingEntity) entity;
        } else if(entity instanceof MultiPartEntityPart) {
            IEntityMultiPart parent = ((MultiPartEntityPart)entity).parent;
            if(parent instanceof LivingEntity) {
                return (LivingEntity) parent;
            }
        }
        return null;
    }

    private String findSelectedMobId(Entity entity) {
        ResourceLocation key = EntityList.getKey(entity.getClass());
        return key != null ? key.toString() : null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            String mobName = getMobName(itemStack);
            if (mobName != null) {
                list.add(TextFormatting.BLUE + "Mob: " + mobName);
            }
            int level = tagCompound.getInt("level");
            level = level * 100 / GeneralConfiguration.maxMobInjections.get();
            list.add(TextFormatting.BLUE + "Essence level: " + level + "%");
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Use this to extract essence from mobs");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }

    public static ItemStack createMobSyringe(Class<? extends Entity> mobClass) {
        String id = EntityTools.findEntityIdByClass(mobClass);
        String name = EntityTools.findEntityLocNameByClass(mobClass);
        return createMobSyringe(id, name);
    }

    private static ItemStack createMobSyringe(String id, String name) {
        ItemStack syringe = new ItemStack(ModItems.syringeItem);
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putString("mobId", id);
        if (name == null || name.isEmpty()) {
            name = id;
        }
        tagCompound.putString("mobName", name);
        tagCompound.putInt("level", GeneralConfiguration.maxMobInjections.get());
        syringe.setTag(tagCompound);
        return syringe;
    }

    public static String getMobId(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            String mob = tagCompound.getString("mobId");
            if (mob == null) {
                // For compatibility only!
                return tagCompound.getString("mobName");
            } else {
                mob = EntityTools.fixEntityId(mob);
            }
            return mob;
        }
        return null;
    }

    public static String getMobName(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            String mob = tagCompound.getString("mobName");
            if (mob == null || "unknown".equals(mob)) {
                if (tagCompound.hasKey("mobId")) {
                    String mobId = tagCompound.getString("mobId");
                    mobId = EntityTools.fixEntityId(mobId);
                    return mobId;
                } else {
                    return "?";
                }
            }
            return mob;
        }
        return null;
    }

}
