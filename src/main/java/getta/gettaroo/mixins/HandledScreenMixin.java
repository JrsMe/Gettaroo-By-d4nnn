package getta.gettaroo.mixins;

import getta.gettaroo.config.Configs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Shadow @Nullable protected Slot focusedSlot;

    @Shadow @Final protected PlayerInventory playerInventory;

    @Shadow protected int y;

    @Inject(method = "keyPressed", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
            ordinal = 0
    ),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;keyDrop:Lnet/minecraft/client/option/KeyBinding;")
            ), cancellable = true)
    public void stopDropping(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (focusedSlot == null || focusedSlot.getStack().isEmpty()) return;

        for (String item : Configs.Lists.PREVENT_DROPPING_LIST.getStrings()) {
            Identifier itemId = Registries.ITEM.getId(focusedSlot.getStack().getItem());
            if (itemId != null && itemId.equals(new Identifier(item))) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
