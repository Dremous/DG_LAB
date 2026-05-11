//package online.kbpf.dg_lab.client.screen.StrengthScreen;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.events.GuiEventListener;
//import net.minecraft.client.gui.components.NarratableEntry;
//import net.minecraft.client.gui.components.Tooltip;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.components.ContainerObjectSelectionList;
//import net.minecraft.client.gui.components.AbstractSliderButton;
//import net.minecraft.client.gui.components.EditBox;
//import net.minecraft.network.chat.Component;
//
//
//
//import java.util.List;
//
//public class StrengthListWidget extends ContainerObjectSelectionList<StrengthListWidget.Entry> {
//
//    public StrengthListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
//        super(minecraft, width, height, y, itemHeight);
//    }
//
//    public void addWaveformEntry(Entry entry) {
//        this.addEntry(entry);
//    }
//
//    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
//        private final EditBox waveformDataText;
//        private final Button sendButton;
//        private final Font font;
//        private final AbstractSliderButton value;
//        private final Component text;
//
//        public Entry(Font font, Component text, Runnable runnable) {
//            waveformDataText = new EditBox(font, 100, 15, Component.literal("输入波形代码"));
//            waveformDataText.setHint(Component.literal("输入波形代码").withStyle(style -> style.withColor(0xaaaaaa)));
//            value = new AbstractSliderButton(0, 0, 100, 15, Component.literal(""), 0.5) {
//                @Override
//                protected void updateMessage() {
//                }
//
//                @Override
//                protected void applyValue() {
//
//                }
//            };
//            sendButton = Button.builder(Component.literal("❏"), button -> {
//                Minecraft.getInstance().keyboardHandler.setClipboard(waveformDataText.getValue());
//            }).tooltip(Tooltip.create(Component.literal("点击复制波形代码"))).build();
//            this.font = font;
//            this.text = text;
//
//        }
//
//        @Override
//        public List<? extends NarratableEntry> narratables() {
//            return List.of(waveformDataText, sendButton);
//        }
//
//        @Override
//        public List<? extends GuiEventListener> children() {
//            return List.of(waveformDataText, sendButton);
//        }
//
//
//        @Override
//        public void render(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
//            waveformDataText.setX((int) (getX() + (getRowWidth() / 2.5)));
//            waveformDataText.setY(getY());
//            waveformDataText.setWidth(getRowWidth() / 2);
//            waveformDataText.setHeight(15);
//            waveformDataText.render(graphics, mouseX, mouseY, partialTick);
//
//            sendButton.setX((int) (getX() + (getRowWidth() / 2.5) + ((double) getRowWidth() * 0.51)));
//            sendButton.setY(getY());
//            sendButton.setWidth(15);
//            sendButton.setHeight(15);
//            sendButton.render(graphics, mouseX, mouseY, partialTick);
//
//
//            graphics.drawString(font, this.text, getX(), getY() + 5, 0xffffff);
//
//        }
//
//
//    }
//
//}
