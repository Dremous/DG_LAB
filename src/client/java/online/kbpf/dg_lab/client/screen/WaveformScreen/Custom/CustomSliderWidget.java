package online.kbpf.dg_lab.client.screen.WaveformScreen.Custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class CustomSliderWidget extends AbstractSliderButton {

    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("widget/slider");
    private static final Identifier HIGHLIGHTED_TEXTURE = Identifier.withDefaultNamespace("widget/slider_highlighted");
    private static final Identifier HANDLE_TEXTURE = Identifier.withDefaultNamespace("widget/slider_handle");
    private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = Identifier.withDefaultNamespace("widget/slider_handle_highlighted");

    private boolean sliderFocused;

    public CustomSliderWidget(int x, int y, int width, int height, Component message, double value) {
        super(x, y, width, height, message, value);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getHandleTexture(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ARGB.white(this.alpha));
        int i = this.active ? -1 : -6250336;
        graphics.text(minecraft.font, this.getMessage(), this.getX() + this.getWidth(), this.getY(), i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private Identifier getTexture() {
        return this.isFocused() && !this.sliderFocused ? HIGHLIGHTED_TEXTURE : TEXTURE;
    }

    private Identifier getHandleTexture() {
        return !this.isHovered && !this.sliderFocused ? HANDLE_TEXTURE : HANDLE_HIGHLIGHTED_TEXTURE;
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void applyValue() {
    }
}
