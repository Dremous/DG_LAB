package online.kbpf.dg_lab.client.screen.WaveformScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import online.kbpf.dg_lab.client.screen.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static online.kbpf.dg_lab.client.screen.ConfigScreen.*;

@Environment(EnvType.CLIENT)
public class WaveformConfigScreen extends Screen {

    private WaveformListWidget waveformListWidget;

    public WaveformConfigScreen() {

        super(Component.literal("波形配置界面"));
    }

    @Override
    public void onClose() {
        Screen configScreen = new ConfigScreen();
        this.minecraft.setScreen(configScreen);
    }

    @Override
    protected void init() {
        Minecraft client = Minecraft.getInstance();
        waveformListWidget = new WaveformListWidget(client, width, height - 40, 40, ButtonHeight + ButtonDistance);
        WaveformListWidget.Entry a = new WaveformListWidget.Entry(waveformListWidget, client.font, Component.literal("A通道受伤波形"), "ADamage");
        WaveformListWidget.Entry b = new WaveformListWidget.Entry(waveformListWidget, client.font, Component.literal("A通道恢复波形"), "AHealing");
        WaveformListWidget.Entry c = new WaveformListWidget.Entry(waveformListWidget, client.font, Component.literal("B通道受伤波形"), "BDamage");
        WaveformListWidget.Entry d = new WaveformListWidget.Entry(waveformListWidget, client.font, Component.literal("B通道恢复波形"), "BHealing");


        waveformListWidget.addWaveformEntry(a);
        waveformListWidget.addWaveformEntry(b);
        waveformListWidget.addWaveformEntry(c);
        waveformListWidget.addWaveformEntry(d);
        addRenderableWidget(waveformListWidget);
    }

}
