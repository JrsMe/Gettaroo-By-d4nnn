package getta.gettaroo.mixins;

import getta.gettaroo.config.Configs;
import getta.gettaroo.config.FeatureToggle;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void stopRenderingItemEntity(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if (itemEntity == null || itemEntity.getStack().isEmpty()) return;

        if (FeatureToggle.DISABLE_RENDERING_ENTITIES.getBooleanValue()) {
            for (String item : Configs.Lists.DISABLED_ENTITIES.getStrings()) {
                Identifier itemId = Registries.ITEM.getId(itemEntity.getStack().getItem());
                if (itemId != null && itemId.equals(new Identifier(item))) {
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
