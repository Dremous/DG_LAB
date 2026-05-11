package online.kbpf.dg_lab.client.screen.WaveformScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import online.kbpf.dg_lab.client.entity.Waveform.ControlBar;
import online.kbpf.dg_lab.client.screen.WaveformScreen.Custom.CustomScreen;

import java.util.List;

import static online.kbpf.dg_lab.client.screen.ConfigScreen.*;


public class WaveformListWidget extends ContainerObjectSelectionList<WaveformListWidget.Entry> {

    private final int width;

    public WaveformListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.width = width;
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }
    @Override
    public int getRowWidth() {
        return this.width;
    }
    @Override
    protected int scrollBarX() {
        return this.getRight() - 6;
    }

    public void addWaveformEntry(Entry entry) {
        this.addEntry(entry);
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        Minecraft client = Minecraft.getInstance();
        private final EditBox waveformDataText;
        private final Button customButton, deleteButton;
        private final Font font;
        private final WaveformListWidget parent;
        private final String key;
        private ControlBar waveformData = new ControlBar();

        public Entry(WaveformListWidget parent, Font font, Component text, String key) {
            this.parent = parent;
            this.font = font;
            this.key = key;

            waveformDataText = new EditBox(font, 100, ButtonHeight, Component.literal(""));
            waveformDataText.setMaxLength(100);
            waveformDataText.setValue(text.getString());
            waveformDataText.setTooltip(Tooltip.create(Component.literal(key)));

            customButton = Button.builder(Component.literal("\uD83D\uDD27"), button -> {
                CustomScreen customScreen = new CustomScreen(new WaveformConfigScreen(), key);
                client.setScreen(customScreen);
            }).tooltip(Tooltip.create(Component.literal("自定义波形"))).build();

            deleteButton = Button.builder(Component.literal("\uD83D\uDDD1"), button -> {
                parent.removeEntry(this);
            }).tooltip(Tooltip.create(Component.literal("删除此波形"))).build();

        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(waveformDataText, customButton, deleteButton);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(waveformDataText, customButton, deleteButton);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            int entryWidth = ((WaveformListWidget)this.parent).getRowWidth();
            int y = this.getY();
            int x = ((WaveformListWidget)this.parent).getRowLeft();

            waveformDataText.setRectangle((int) (entryWidth * 0.3), ButtonHeight, x + (int) (entryWidth * 0.4), y);
            waveformDataText.extractRenderState(graphics, mouseX, mouseY, partialTick);

            customButton.setRectangle((int) (entryWidth * 0.1), ButtonHeight, x + (int) (entryWidth * 0.75), y);
            customButton.extractRenderState(graphics, mouseX, mouseY, partialTick);

            deleteButton.setRectangle((int) (entryWidth * 0.1), ButtonHeight, x + (int) (entryWidth * 0.88), y);
            deleteButton.extractRenderState(graphics, mouseX, mouseY, partialTick);

            graphics.text(font, Component.literal(key), x + (int) (entryWidth * 0.02), y + 5, 0xffffffff);
        }
    }
}
