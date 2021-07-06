package mcjty.rftoolsutility.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.McJtyLib;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.playerprops.PlayerBuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderGameOverlayEventHandler {

    private static final int BUFF_ICON_SIZE = 16;

    public static List<PlayerBuff> buffs = null;

    public static void onRender(RenderGameOverlayEvent event) {
        if (event.isCanceled() || event.getType() != RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            return;
        }

        renderBuffs(event.getMatrixStack());
    }

    private static void renderBuffs(MatrixStack matrixStack) {
        if (buffs == null || buffs.isEmpty()) {
            return;
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        McJtyLib.getPreferencesProperties(player).ifPresent(preferences -> {

            int x = preferences.getBuffX();
            int y = preferences.getBuffY();

            if (x == -1 || y == -1) {
                return;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);

//        Minecraft.getInstance().renderEngine.bindTexture(texture);

            for (PlayerBuff buff : buffs) {
                Item item = getBuffItem(buff);
                if (item != null) {
                    ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                    RenderHelper.renderItemStack(matrixStack, itemRender, new ItemStack(item), x, y, "", false);
//                itemRender.renderItem(new ItemStack(item), player, ItemCameraTransforms.TransformType.FIXED, true);
                    x += BUFF_ICON_SIZE;
                }
            }
        });
    }

    private static Item getBuffItem(PlayerBuff buff) {
        Item item = null;
        switch (buff) {
            case BUFF_FEATHERFALLING:
                item = EnvironmentalModule.FEATHERFALLING_MODULE.get();
                break;
            case BUFF_FEATHERFALLINGPLUS:
                item = EnvironmentalModule.FEATHERFALLINGPLUS_MODULE.get();
                break;
            case BUFF_HASTE:
                item = EnvironmentalModule.HASTE_MODULE.get();
                break;
            case BUFF_HASTEPLUS:
                item = EnvironmentalModule.HASTEPLUS_MODULE.get();
                break;
            case BUFF_REGENERATION:
                item = EnvironmentalModule.REGENERATION_MODULE.get();
                break;
            case BUFF_REGENERATIONPLUS:
                item = EnvironmentalModule.REGENERATIONPLUS_MODULE.get();
                break;
            case BUFF_SATURATION:
                item = EnvironmentalModule.SATURATION_MODULE.get();
                break;
            case BUFF_SATURATIONPLUS:
                item = EnvironmentalModule.SATURATIONPLUS_MODULE.get();
                break;
            case BUFF_SPEED:
                item = EnvironmentalModule.SPEED_MODULE.get();
                break;
            case BUFF_SPEEDPLUS:
                item = EnvironmentalModule.SPEEDPLUS_MODULE.get();
                break;
            case BUFF_FLIGHT:
                item = EnvironmentalModule.FLIGHT_MODULE.get();
                break;
            case BUFF_PEACEFUL:
                item = EnvironmentalModule.PEACEFUL_MODULE.get();
                break;
            case BUFF_GLOWING:
                item = EnvironmentalModule.GLOWING_MODULE.get();
                break;
            case BUFF_WATERBREATHING:
                item = EnvironmentalModule.WATERBREATHING_MODULE.get();
                break;
            case BUFF_NIGHTVISION:
                item = EnvironmentalModule.NIGHTVISION_MODULE.get();
                break;
            case BUFF_BLINDNESS:
                item = EnvironmentalModule.BLINDNESS_MODULE.get();
                break;
            case BUFF_WEAKNESS:
                item = EnvironmentalModule.WEAKNESS_MODULE.get();
                break;
            case BUFF_POISON:
                item = EnvironmentalModule.POISON_MODULE.get();
                break;
            case BUFF_SLOWNESS:
                item = EnvironmentalModule.SLOWNESS_MODULE.get();
                break;
            case BUFF_LUCK:
                item = EnvironmentalModule.LUCK_MODULE.get();
                break;
            case BUFF_NOTELEPORT:
                item = EnvironmentalModule.NOTELEPORT_MODULE.get();
                break;
            default:
                item = null;
        }
        return item;
    }
}
