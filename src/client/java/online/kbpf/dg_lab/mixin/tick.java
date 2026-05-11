package online.kbpf.dg_lab.mixin;

import online.kbpf.dg_lab.client.Config.StrengthConfig;
import online.kbpf.dg_lab.client.entity.DGStrength;
import online.kbpf.dg_lab.client.webSocketServer.webSocketServer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import online.kbpf.dg_lab.client.Dg_labClient;

import static online.kbpf.dg_lab.client.Dg_labClient.*;


@Mixin(Minecraft.class)
public abstract class tick {

    @Shadow
    public abstract void tick();

    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public ClientLevel level;
    @Shadow @Nullable private IntegratedServer singleplayerServer;
    @Unique
    private int tickCounter = 0;
    @Unique
    private int lastRunTickA = 0;
    @Unique
    private int lastRunTickB = 0;

    @Unique
    private boolean hasDetectedADelay = false;
    @Unique
    private boolean hasDetectedBDelay = false;

    @Unique
    private boolean ClearA = false;
    @Unique
    private boolean ClearB = false;

    @Unique
    private float lastHealth = -1.0f;
    @Unique
    private float Health2P = -1.0f;

    @Unique
    private boolean hasDetectedADelayZeroAndStrength = false;
    @Unique
    private boolean hasDetectedBDelayZeroAndStrength = false;



    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {


        DGStrength dgStrength = webSocketServer.getStrength();

        TwoPlayer();

        int ADelayTime = dgStrength.getADelayTime(), BDelayTime = dgStrength.getBDelayTime();


        ADelayTime = (ADelayTime > 0) ? ADelayTime - 1 : 0;
        BDelayTime = (BDelayTime > 0) ? BDelayTime - 1 : 0;
        webSocketServer.setDelayTime(ADelayTime, BDelayTime);

        int AStrength = dgStrength.getAStrength(), BStrength = dgStrength.getBStrength();
        int AMin = 0, BMin = 0;
        if(player != null){
            AMin = (int) (strengthConfig.getAMin() * ((player.getMaxHealth() - player.getHealth()) / player.getMaxHealth()));
            BMin = (int) (strengthConfig.getBMin() * ((player.getMaxHealth() - player.getHealth()) / player.getMaxHealth()));

            float currentHealth = player.getHealth();
            if (lastHealth != -1.0f && lastHealth != currentHealth) {
                float healthChange = lastHealth - currentHealth;
                if (healthChange > 0.0f) {
                    onPlayerDamage(healthChange);
                } else if (currentHealth <= 0) {
                    onPlayerDeath();
                }
            }
            lastHealth = currentHealth;
        }
        if (tickCounter % strengthConfig.getADownTime() == 0 && ADelayTime <= 0 && AStrength > AMin){
            if(webSocketServer.getStrength().getAStrength() - strengthConfig.getADownValue() < AMin)
                webSocketServer.sendStrengthToClient(AMin, 2, 1);
            else
                webSocketServer.sendStrengthToClient(strengthConfig.getADownValue(), 0, 1);
        }
        if (tickCounter % strengthConfig.getBDownTime() == 0 && BDelayTime <= 0 && BStrength > BMin) {
            if(webSocketServer.getStrength().getBStrength() - strengthConfig.getBDownValue() < BMin)
                webSocketServer.sendStrengthToClient(BMin, 2, 2);
            else
                webSocketServer.sendStrengthToClient(strengthConfig.getBDownValue(), 0, 2);
        }

        if (ADelayTime > 0) {
            hasDetectedADelayZeroAndStrength = false;
            ClearA = false;
            if (!hasDetectedADelay) {
                webSocketServer.sendDgWaveform(2, true, 1);
                hasDetectedADelay = true;
            } else if (tickCounter - lastRunTickA >= waveformMap.get("ADamage").getDuration() * 2) {
                webSocketServer.sendDgWaveform(2, false, 1);
                lastRunTickA = tickCounter;
            }
        } else {
            hasDetectedADelay = false;
            if (AStrength > 0) {
                if (!hasDetectedADelayZeroAndStrength) {
                    webSocketServer.sendDgWaveform(3, true, 1);
                    hasDetectedADelayZeroAndStrength = true;
                } else if (tickCounter - lastRunTickA >= waveformMap.get("AHealing").getDuration() * 2) {
                    webSocketServer.sendDgWaveform(3, false, 1);
                    lastRunTickA = tickCounter;
                }

            }
            else if(!ClearA){
                webSocketServer.CleanFrequency(1);
                ClearA = true;
            }
        }

        if (BDelayTime > 0) {
            ClearB = false;
            hasDetectedBDelayZeroAndStrength = false;
            if (!hasDetectedBDelay) {
                webSocketServer.sendDgWaveform(2, true, 2);
                hasDetectedBDelay = true;
            } else if (tickCounter - lastRunTickB >= waveformMap.get("BDamage").getDuration() * 2) {
                webSocketServer.sendDgWaveform(2, false, 2);
                lastRunTickB = tickCounter;
            }
        } else {
            hasDetectedBDelay = false;
            if (BStrength > 0) {
                if (!hasDetectedBDelayZeroAndStrength) {
                    webSocketServer.sendDgWaveform(3, true, 2);
                    hasDetectedBDelayZeroAndStrength = true;
                } else if (tickCounter - lastRunTickB >= waveformMap.get("BHealing").getDuration() * 2) {
                    webSocketServer.sendDgWaveform(3, false, 2);
                    lastRunTickB = tickCounter;
                }

            }
            else if(!ClearB){
                webSocketServer.CleanFrequency(2);
                ClearB = true;
            }
        }

        tickCounter++;



        if (tickCounter == 2147483625) tickCounter = 0;
    }

    @Unique
    private void TwoPlayer(){
        if(!twoPlayerMode) return;
        Minecraft client = Minecraft.getInstance();
        if(!client.hasSingleplayerServer()) {
            quit2PMode();
            return;
        }
        IntegratedServer server = client.getSingleplayerServer();
        if(server != null && server.isPublished()){


            if (server.getPlayerList().getPlayerByName(secondPlayer) == null) {
                if(Health2P > 0.0F){
                    webSocketServer.sendStrengthToClient((Math.min(webSocketServer.getStrength().getBStrength() + secondPlayerQuitStrength, webSocketServer.getStrength().getBMaxStrength())), 2, 2);
                }
                quit2PMode();
                return;
            }

            float health2P = server.getPlayerList().getPlayerByName(secondPlayer).getHealth();

            if (Health2P == -1.0F){
                Health2P = health2P;
                return;
            }
            if(Health2P == -2.0f){
                if(health2P > 0.0f)
                    Health2P = health2P;
                return;
            }
            float damage = Health2P - health2P;


            if (damage > 0.0F) {
                if (strengthConfig.getBDelayTime() > 0) {
                    webSocketServer.setBDelayTime(strengthConfig.getBDeathDelay());
                    webSocketServer.sendStrengthToClient((int) Math.max(1.0F,  damage * strengthConfig.getBDamageStrength()), 1, 2);
                }
            }
            if (health2P <= 0) {
                DGStrength dgStrength = webSocketServer.getStrength();
                webSocketServer.setBDelayTime(strengthConfig.getBDeathDelay());
                webSocketServer.sendStrengthToClient((Math.min(dgStrength.getBStrength() + strengthConfig.getBDeathStrength(), dgStrength.getBMaxStrength())), 2, 2);
                Health2P = -2.0F;
                return;
            }

            Health2P = health2P;

        }

    }

    @Unique
    private void quit2PMode(){
        twoPlayerMode = false;
        Health2P = -1.0F;
    }

    @Unique
    private void onPlayerDamage(float damage) {
        webSocketServer server = Dg_labClient.webSocketServer;
        StrengthConfig config = Dg_labClient.strengthConfig;
        if (server != null && server.getConnected()) {
            if (!Dg_labClient.twoPlayerMode) {
                server.setDelayTime(config.getADelayTime(), config.getBDelayTime());
                if (config.getADamageStrength() > 0)
                    server.sendStrengthToClient(Math.max(1, ((int) (damage * config.getADamageStrength()))), 1, 1);
                if (config.getBDamageStrength() > 0)
                    server.sendStrengthToClient(Math.max(1, ((int) (damage * config.getBDamageStrength()))), 1, 2);
            } else {
                server.setADelayTime(config.getADelayTime());
                if (config.getADamageStrength() > 0)
                    server.sendStrengthToClient(Math.max(1, ((int) (damage * config.getADamageStrength()))), 1, 1);
            }
        }
    }

    @Unique
    private void onPlayerDeath() {
        webSocketServer server = Dg_labClient.webSocketServer;
        StrengthConfig config = Dg_labClient.strengthConfig;
        if (server != null && server.getConnected()) {
            DGStrength dgStrength = server.getStrength();
            if (!Dg_labClient.twoPlayerMode) {
                server.setDelayTime(config.getADeathDelay(), config.getBDeathDelay());
                server.sendStrengthToClient((Math.min(dgStrength.getAStrength() + config.getADeathStrength(), dgStrength.getAMaxStrength())), 2, 1);
                server.sendStrengthToClient((Math.min(dgStrength.getBStrength() + config.getBDeathStrength(), dgStrength.getBMaxStrength())), 2, 2);
            } else {
                server.setADelayTime(config.getADeathDelay());
                server.sendStrengthToClient((Math.min(dgStrength.getAStrength() + config.getADeathStrength(), dgStrength.getAMaxStrength())), 2, 1);
            }
        }
    }



}
