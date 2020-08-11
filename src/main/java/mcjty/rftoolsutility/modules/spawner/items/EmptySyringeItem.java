package mcjty.rftoolsutility.modules.spawner.items;

import mcjty.rftoolsutility.RFToolsUtility;

public class EmptySyringeItem extends CommonSyringeItem {

    public EmptySyringeItem() {
        super(new Properties().group(RFToolsUtility.setup.getTab()).maxDamage(1));
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
//            CompoundNBT tagCompound = stack.getTagCompound();
//            int level = 0;
//            if (tagCompound != null) {
//                level = tagCompound.getInteger("level");
//            }
//            if (level <= 0) {
//                level = 0;
//            } else if (level >= GeneralConfiguration.maxMobInjections.get()) {
//                level = 5;
//            } else {
//                level = ((level-1) * 4 / (GeneralConfiguration.maxMobInjections.get()-1)) + 1;
//            }
//            String domain = getRegistryName().getResourceDomain();
//            String path = getRegistryName().getResourcePath();
//            return new ModelResourceLocation(new ResourceLocation(domain, path + level), "inventory");
//        });
//    }


    // @todo 1.15
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
//        super.addInformation(itemStack, player, list, whatIsThis);
//        CompoundNBT tagCompound = itemStack.getTagCompound();
//        if (tagCompound != null) {
//            String mobName = getMobName(itemStack);
//            if (mobName != null) {
//                list.add(TextFormatting.BLUE + "Mob: " + mobName);
//            }
//            int level = tagCompound.getInteger("level");
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

}
