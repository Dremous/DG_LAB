package online.kbpf.dg_lab.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import static online.kbpf.dg_lab.client.Dg_labClient.*;

public class hud implements HudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {

        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.level != null && (modConfig.getRenderingPositionX() < client.getWindow().getGuiScaledWidth() || modConfig.getRenderingPositionY() < client.getWindow().getGuiScaledHeight())) {

            int x = modConfig.getRenderingPositionX();
            int y = modConfig.getRenderingPositionY();

            if(webSocketServer.getConnected()) {
                Component strengthText;
                Component strengthText1;
                String A = "A", B = "B";
                if(twoPlayerMode){
                    A = Minecraft.getInstance().getUser().getName() + ":";
                    B = secondPlayer + ":";
                }
                else {
                    A = "A:";
                    B = "B:";
                }
                if(modConfig.isRenderingMax()) {
                    strengthText = Component.literal(A + webSocketServer.getStrength().getAStrength() + ",Max:" + webSocketServer.getStrength().getAMaxStrength());

                    strengthText1 = Component.literal(B + webSocketServer.getStrength().getBStrength() + ",Max:" + webSocketServer.getStrength().getBMaxStrength());

                }
                else {
                    strengthText = Component.literal(A + webSocketServer.getStrength().getAStrength());

                    strengthText1 = Component.literal(B + webSocketServer.getStrength().getBStrength());
                }
                graphics.text(client.font, strengthText, x, y, 0xFFFFFFFF);
                graphics.text(client.font, strengthText1, x, y + 9, 0xFFFFFFFF);
            }
            else {
                Component strengthText = Component.literal("未连接");
                graphics.text(client.font, strengthText, x, y, 0xFFFF0000);
            }
        }
    }
}
