package online.kbpf.dg_lab.client.screen.WaveformScreen.Custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import online.kbpf.dg_lab.client.Dg_labClient;
import online.kbpf.dg_lab.client.Tool.DGWaveformTool;
import online.kbpf.dg_lab.client.entity.Waveform.ControlBar;
import online.kbpf.dg_lab.client.screen.WaveformScreen.WaveformConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

import static online.kbpf.dg_lab.client.Dg_labClient.waveformMap;
import static online.kbpf.dg_lab.client.Dg_labClient.webSocketServer;
import static online.kbpf.dg_lab.client.screen.ConfigScreen.*;


@Environment(EnvType.CLIENT)
public class CustomScreen extends Screen {

    public static ArrayList<ControlBar> list;
    private CustomListWidget customListWidget;
    private final String key;

    public CustomScreen(WaveformConfigScreen waveformConfigScreen, String key) {
        super(Component.literal("自定义波形界面"));
        this.key = key;
        if(waveformMap.containsKey(key)) {
            list = DGWaveformTool.convertToWaveformData(waveformMap.get(key).getWaveform());
        } else {
            list = new ArrayList<>();
            list.add(new ControlBar());
        }
    }

    @Override
    public void onClose() {
        Screen waveformConfigScreen = new WaveformConfigScreen();
        this.minecraft.setScreen(waveformConfigScreen);
    }

    @Override
    protected void init() {
        Minecraft client = Minecraft.getInstance();
        customListWidget = new CustomListWidget(client, width, height - 40, 40, ButtonHeight + ButtonDistance);
        for(int i = 0; i < list.size(); i++) {
            CustomListWidget.Entry entry = new CustomListWidget.Entry(customListWidget, client.font, list, i);
            customListWidget.addCustomEntry(entry);
        }
        addRenderableWidget(customListWidget);

        Button addButton = Button.builder(Component.literal("+"), button -> {
            list.add(new ControlBar());
            CustomListWidget.Entry entry = new CustomListWidget.Entry(customListWidget, client.font, list, list.size() - 1);
            customListWidget.addCustomEntry(entry);
        }).bounds(width / 2 - 10, 20, 20, ButtonHeight).tooltip(Tooltip.create(Component.literal("添加波形"))).build();

        Button testButton = Button.builder(Component.literal("\uD83D\uDCE8"), button -> {
            webSocketServer.sendDGWaveForm(DGWaveformTool.convertToWaveformString(list), 1);
        }).bounds(width / 2 + 15, 20, 20, ButtonHeight).tooltip(Tooltip.create(Component.literal("发送到终端1通道"))).build();

        Button saveButton = Button.builder(Component.literal("\uD83D\uDCBE"), button -> {
            if(waveformMap.containsKey(key)) {
                waveformMap.get(key).setWaveform(DGWaveformTool.convertToWaveformString(list));
            }
        }).bounds(width / 2 + 40, 20, 20, ButtonHeight).tooltip(Tooltip.create(Component.literal("保存波形"))).build();

        addRenderableWidget(addButton);
        addRenderableWidget(testButton);
        addRenderableWidget(saveButton);
    }
}
