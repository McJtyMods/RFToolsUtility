package mcjty.rftoolsutility.modules.spawner.items;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class SyringeItem extends CommonSyringeItem {

    public SyringeItem() {
        super(new Properties().group(RFToolsUtility.setup.getTab()).maxStackSize(1).containerItem(SpawnerSetup.EMPYY_SYRINGE.get()));
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
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
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
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultSuccess(stack);
    }



}
