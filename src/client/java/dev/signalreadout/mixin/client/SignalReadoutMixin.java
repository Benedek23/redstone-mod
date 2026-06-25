package dev.signalreadout.mixin.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class SignalReadoutMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void signal_readout$onTick(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.world == null || client.player == null || client.inGameHud == null) return;

        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult bhr)) return;

        BlockPos pos = bhr.getBlockPos();
        BlockState state = client.world.getBlockState(pos);
        Block block = state.getBlock();

        Integer signal = null;
        String label = null;

        if (block instanceof RedstoneWireBlock) {
            // Wire power IS synced -- works everywhere, including the SMP.
            signal = state.get(Properties.POWER);
            label = "Wire";
        } else {
            // Comparator output & container fill aren't synced to the client.
            // In singleplayer, read the authoritative (integrated server) world.
            World authWorld = client.world;
            MinecraftServer server = client.getServer();
            if (server != null) {
                ServerWorld sw = server.getWorld(client.world.getRegistryKey());
                if (sw != null) authWorld = sw;
            }
            BlockEntity be = authWorld.getBlockEntity(pos);
            if (be instanceof ComparatorBlockEntity comp) {
                signal = comp.getOutputSignal();
                label = "Comparator";
            } else if (be instanceof Inventory) {
                signal = ScreenHandler.calculateComparatorOutput(be);
                label = "Container";
            }
        }

        if (signal != null) {
            Text msg = Text.literal(label + " signal: " + signal)
                    .formatted(signal > 0 ? Formatting.RED : Formatting.GRAY);
            client.inGameHud.setOverlayMessage(msg, false);
        }
    }
}
