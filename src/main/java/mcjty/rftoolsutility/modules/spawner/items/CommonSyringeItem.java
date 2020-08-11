package mcjty.rftoolsutility.modules.spawner.items;

import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class CommonSyringeItem extends Item {

    public CommonSyringeItem(Properties properties) {
        super(properties);
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

    public static @Nullable
    LivingEntity getEntityLivingFromClickedEntity(Entity entity) {
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

    private String findSelectedMobId(Entity entity) {
        ResourceLocation key = entity.getType().getRegistryName();
        return key != null ? key.toString() : null;
    }

    // @todo 1.15
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
//        super.addInformation(itemStack, player, list, whatIsThis);
//        CompoundNBT tagCompound = itemStack.getTag();
//        if (tagCompound != null) {
//            String mobName = getMobName(itemStack);
//            if (mobName != null) {
//                list.add(TextFormatting.BLUE + "Mob: " + mobName);
//            }
//            int level = tagCompound.getInt("level");
//            level = level * 100 / GeneralConfiguration.maxMobInjections.get();
//            list.add(TextFormatting.BLUE + "Essence level: " + level + "%");
//        }
//
//        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
//            list.add(TextFormatting.WHITE + "Use this to extract essence from mobs");
//        } else {
//            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
//        }
//    }

    public static ItemStack createMobSyringe(ResourceLocation mobId) {
        ItemStack syringe = new ItemStack(SpawnerSetup.SYRINGE.get());
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

    // @todo 1.15
    public static String getMobName(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            String mob = tagCompound.getString("mobId");
            return mob;
        }
        return null;
    }}
