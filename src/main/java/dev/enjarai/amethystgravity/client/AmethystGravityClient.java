package dev.enjarai.amethystgravity.client;

import dev.enjarai.amethystgravity.AmethystGravity;
import dev.enjarai.amethystgravity.block.ui.CylinderFieldGeneratorScreen;
import dev.enjarai.amethystgravity.block.ui.FieldGeneratorScreen;
import dev.enjarai.amethystgravity.block.ui.PlanetFieldGeneratorScreen;
import dev.enjarai.amethystgravity.client.render.block.entity.CylinderFieldGeneratorBlockEntityRenderer;
import dev.enjarai.amethystgravity.client.render.block.entity.FieldGeneratorBlockEntityRenderer;
import dev.enjarai.amethystgravity.client.render.block.entity.PlanetFieldGeneratorBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class AmethystGravityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AmethystGravity.PLATING_BLOCK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AmethystGravity.DENSE_PLATING_BLOCK);

        /* Registers our particle client-side.
         * First argument is our particle's instance, created previously on ExampleMod.
         * Second argument is the particle's factory. The factory controls how the particle behaves.
         * In this example, we'll use FlameParticle's Factory.*/
        //ParticleFactoryRegistry.getInstance().register(AmethystGravity.GRAVITY_INDICATOR, FlameParticle.Factory::new);
        HandledScreens.register(AmethystGravity.FIELD_GENERATOR_SCREEN_HANDLER, FieldGeneratorScreen::new);
        HandledScreens.register(AmethystGravity.PLANET_FIELD_GENERATOR_SCREEN_HANDLER, PlanetFieldGeneratorScreen::new);
        HandledScreens.register(AmethystGravity.CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER, CylinderFieldGeneratorScreen::new);
        BlockEntityRendererRegistry.register(AmethystGravity.FIELD_GENERATOR_BLOCK_ENTITY, FieldGeneratorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_ENTITY, PlanetFieldGeneratorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY, CylinderFieldGeneratorBlockEntityRenderer::new);
    }
}
