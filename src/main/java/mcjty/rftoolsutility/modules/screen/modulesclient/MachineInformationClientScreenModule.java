package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ITextRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleDataString;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MachineInformationClientScreenModule implements IClientScreenModule<IModuleDataString> {

    private String line = "";
    private int labcolor = 0xffffff;
    private int txtcolor = 0xffffff;
    protected ResourceKey<Level> dim = Level.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;

    private final ITextRenderHelper labelCache = new ScreenTextHelper();

    public static final Codec<MachineInformationClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.labcolor),
            Codec.INT.fieldOf("txtcolor").forGetter(module -> module.txtcolor),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(module -> module.dim),
            BlockPos.CODEC.fieldOf("coordinate").forGetter(module -> module.coordinate)
    ).apply(instance, MachineInformationClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineInformationClientScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.labcolor,
            ByteBufCodecs.INT, module -> module.txtcolor,
            ResourceKey.streamCodec(Registries.DIMENSION), module -> module.dim,
            BlockPos.STREAM_CODEC, module -> module.coordinate,
            MachineInformationClientScreenModule::new);

    public MachineInformationClientScreenModule(String line, int labcolor, int txtcolor, ResourceKey<Level> dim, BlockPos coordinate) {
        this.line = line;
        this.labcolor = labcolor;
        this.txtcolor = txtcolor;
        this.dim = dim;
        this.coordinate = coordinate;
    }

    public MachineInformationClientScreenModule() {
    }

    @Override
    public TransformMode getTransformMode(ItemStack moduleItem) {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight(ItemStack moduleItem) {
        return 10;
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleDataString screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();
        int xoffset;
        if (!line.isEmpty()) {
            labelCache.setup(line, 160, renderInfo);
            labelCache.renderText(graphics, buffer,0, currenty, labcolor, renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        if ((!BlockPosTools.INVALID.equals(coordinate)) && screenData != null) {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, txtcolor, renderInfo, screenData.get());
        } else {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, 0xff0000, renderInfo, "<invalid>");
        }
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {

    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
