package online.kbpf.dg_lab.client.screen.WaveformScreen.Custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import online.kbpf.dg_lab.client.Tool.DGWaveformTool;
import online.kbpf.dg_lab.client.entity.Waveform.ControlBar;

import java.util.ArrayList;
import java.util.List;

import static online.kbpf.dg_lab.client.Dg_labClient.webSocketServer;
import static online.kbpf.dg_lab.client.screen.ConfigScreen.*;


public class CustomListWidget extends ContainerObjectSelectionList<CustomListWidget.Entry> {

    private final int width;

    public CustomListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
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

    public void addCustomEntry(Entry entry) {
        this.addEntry(entry);
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        Minecraft client = Minecraft.getInstance();
        private final CustomSliderWidget strength, frequency;
        private final Button S_enable, sendButton, deleteButton;
        private final Font font;
        private final CustomListWidget parent;

        private static final Component manual = Component.literal("手动").withStyle(style -> style.withBold(true).withUnderlined(true));
        private static final Component automatic = Component.literal("平均").withStyle(style -> style.withColor(TextColor.fromRgb(0xAAAAAA)).withBold(true));

        private ControlBar waveformData = new ControlBar();


        public Entry(CustomListWidget parent, Font font, ArrayList<ControlBar> list, int index) {
            this.parent = parent;
            this.font = font;

            strength = new CustomSliderWidget(0, 0, 100, ButtonHeight, Component.literal(String.valueOf(list.get(index).getStrength())), (double) list.get(index).getStrength() / 100) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Component.literal(String.valueOf((int)(this.value * 100))));
                }

                @Override
                protected void applyValue() {
                    list.get(index).setStrength((int)(this.value * 100));
                }
            };

            frequency = new CustomSliderWidget(0, 0, 100, ButtonHeight, Component.literal(String.valueOf(list.get(index).getFrequency())), (double) list.get(index).getFrequency() / 100) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Component.literal(String.valueOf((int)(this.value * 100))));
                }

                @Override
                protected void applyValue() {
                    list.get(index).setFrequency((int)(this.value * 100));
                }
            };

            S_enable = Button.builder((list.get(index).isS_on_off())? manual : automatic, button -> {
                list.get(index).setS_on_off(!list.get(index).isS_on_off());
                button.setMessage((list.get(index).isS_on_off())? manual : automatic);
            }).build();

            sendButton = Button.builder(Component.literal("\uD83D\uDCE8"), button -> {
                webSocketServer.sendDGWaveForm(DGWaveformTool.convertToWaveformString(list), 1);
            }).tooltip(Tooltip.create(Component.literal("发送到终端1通道"))).build();

            deleteButton = Button.builder(Component.literal("\uD83D\uDDD1"), button -> {
                if(list.size() > 1) {
                    list.remove(index);
                    parent.removeEntry(this);
                }
            }).tooltip(Tooltip.create(Component.literal("删除此波形"))).build();

        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(strength, frequency, S_enable, sendButton, deleteButton);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(strength, frequency, S_enable, sendButton, deleteButton);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            int entryWidth = ((CustomListWidget)this.parent).getRowWidth();
            int y = this.getY();
            int x = ((CustomListWidget)this.parent).getRowLeft();

            strength.setRectangle((int) (entryWidth * 0.2), ButtonHeight, x + (int) (entryWidth * 0.1), y);
            strength.extractRenderState(graphics, mouseX, mouseY, partialTick);

            frequency.setRectangle((int) (entryWidth * 0.2), ButtonHeight, x + (int) (entryWidth * 0.35), y);
            frequency.extractRenderState(graphics, mouseX, mouseY, partialTick);

            S_enable.setRectangle((int) (entryWidth * 0.1), ButtonHeight, x + (int) (entryWidth * 0.6), y);
            S_enable.extractRenderState(graphics, mouseX, mouseY, partialTick);

            sendButton.setRectangle(15, ButtonHeight, x + (int) (entryWidth * 0.75), y);
            sendButton.extractRenderState(graphics, mouseX, mouseY, partialTick);

            deleteButton.setRectangle(15, ButtonHeight, x + (int) (entryWidth * 0.85), y);
            deleteButton.extractRenderState(graphics, mouseX, mouseY, partialTick);

            graphics.text(font, Component.literal("强度"), x + (int) (entryWidth * 0.02), y + 5, 0xffffffff);
            graphics.text(font, Component.literal("频率"), x + (int) (entryWidth * 0.28), y + 5, 0xffffffff);
        }
    }
}
