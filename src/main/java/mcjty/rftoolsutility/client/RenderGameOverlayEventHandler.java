package mcjty.rftoolsutility.client;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.BuffStyle;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.playerprops.PlayerBuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RenderGameOverlayEventHandler {

    private static final int BUFF_ICON_SIZE = 16;

    public static List<PlayerBuff> buffs = null;

    public static void onRender(GuiGraphicsExtractor graphics) {
        // @todo 1.21
//        if (event.isCanceled()) {  // @todo 1.18, is this the right spot?
//            return;
//        }

        renderBuffs(graphics);
    }

    private static void renderBuffs(GuiGraphicsExtractor graphics) {
        if (buffs == null || buffs.isEmpty()) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PreferencesProperties preferences = McJtyLib.getPreferencesProperties(player);
        BuffStyle style = preferences.getBuffStyle();
        if (style == BuffStyle.OFF) {
            return;
        }
        int x = preferences.getBuffX();
        int y = preferences.getBuffY();

        boolean leftToRight = true;
        switch (style) {
            case TOPLEFT:
                break;
            case TOPRIGHT:
                leftToRight = false;
                x = Minecraft.getInstance().getWindow().getGuiScaledWidth() + x;
                break;
            case BOTLEFT:
                y = Minecraft.getInstance().getWindow().getGuiScaledHeight() + y;
                break;
            case BOTRIGHT:
                leftToRight = false;
                x = Minecraft.getInstance().getWindow().getGuiScaledWidth() + x;
                y = Minecraft.getInstance().getWindow().getGuiScaledHeight() + y;
                break;
        }

//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            GL11.glDisable(GL11.GL_LIGHTING);

        for (PlayerBuff buff : buffs) {
            Item item = getBuffItem(buff);
            if (item != null) {
                graphics.item(new ItemStack(item), x, y);
                if (leftToRight) {
                    x += BUFF_ICON_SIZE;
                } else {
                    x -= BUFF_ICON_SIZE;
                }
            }
        }
    }

    private static Item getBuffItem(PlayerBuff buff) {
        return switch (buff) {
            case BUFF_FEATHERFALLING -> EnvironmentalModule.FEATHERFALLING_MODULE.get();
            case BUFF_FEATHERFALLINGPLUS -> EnvironmentalModule.FEATHERFALLINGPLUS_MODULE.get();
            case BUFF_HASTE -> EnvironmentalModule.HASTE_MODULE.get();
            case BUFF_HASTEPLUS -> EnvironmentalModule.HASTEPLUS_MODULE.get();
            case BUFF_REGENERATION -> EnvironmentalModule.REGENERATION_MODULE.get();
            case BUFF_REGENERATIONPLUS -> EnvironmentalModule.REGENERATIONPLUS_MODULE.get();
            case BUFF_SATURATION -> EnvironmentalModule.SATURATION_MODULE.get();
            case BUFF_SATURATIONPLUS -> EnvironmentalModule.SATURATIONPLUS_MODULE.get();
            case BUFF_SPEED -> EnvironmentalModule.SPEED_MODULE.get();
            case BUFF_SPEEDPLUS -> EnvironmentalModule.SPEEDPLUS_MODULE.get();
            case BUFF_FLIGHT -> EnvironmentalModule.FLIGHT_MODULE.get();
            case BUFF_PEACEFUL -> EnvironmentalModule.PEACEFUL_MODULE.get();
            case BUFF_GLOWING -> EnvironmentalModule.GLOWING_MODULE.get();
            case BUFF_WATERBREATHING -> EnvironmentalModule.WATERBREATHING_MODULE.get();
            case BUFF_NIGHTVISION -> EnvironmentalModule.NIGHTVISION_MODULE.get();
            case BUFF_BLINDNESS -> EnvironmentalModule.BLINDNESS_MODULE.get();
            case BUFF_WEAKNESS -> EnvironmentalModule.WEAKNESS_MODULE.get();
            case BUFF_POISON -> EnvironmentalModule.POISON_MODULE.get();
            case BUFF_SLOWNESS -> EnvironmentalModule.SLOWNESS_MODULE.get();
            case BUFF_LUCK -> EnvironmentalModule.LUCK_MODULE.get();
            case BUFF_NOTELEPORT -> EnvironmentalModule.NOTELEPORT_MODULE.get();
        };
    }
}
