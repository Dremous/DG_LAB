package online.kbpf.dg_lab.mixin;


import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {

    @Unique
    float Dg_labHealth = 0.0f;

    public ClientPlayerEntityMixin(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

}
