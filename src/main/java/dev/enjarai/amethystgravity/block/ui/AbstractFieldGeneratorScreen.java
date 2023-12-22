package dev.enjarai.amethystgravity.block.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.amethystgravity.AmethystGravity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.text.DecimalFormat;

public abstract class AbstractFieldGeneratorScreen<T extends AbstractFieldGeneratorScreenHandler<T>> extends HandledScreen<T> {
    private static final Identifier TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/gui/blank.png");
    ButtonWidget polarityButton;
    ButtonWidget visibilityButton;
    ButtonWidget applyChanges;
    protected int magnitude = 10;

    public AbstractFieldGeneratorScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventoryTitleY = -100;
        backgroundWidth = 192;
        backgroundHeight = 182;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        renderValuesAndLabels(context);
        //Tooltip
        drawMouseoverTooltip(context, mouseX, mouseY);

        if(polarityButton != null) polarityButton.setMessage(getPolarityText());
        if(visibilityButton != null) visibilityButton.setMessage(getVisibilityText());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        magnitude = 10;
        if(hasShiftDown()) magnitude /= 10;
        if(hasControlDown()) magnitude *= 10;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int bWidth = 130;
        int bHeight = 20;
        int bX = (width - bWidth) / 2;
        int bY = (height - bHeight) / 2 + 5;
        //Polarity Button
        polarityButton = addDrawableChild(ButtonWidget.builder(getPolarityText(), button -> handler.polarity = 1 - handler.polarity)
                .dimensions(bX, bY + 20, bWidth, bHeight).build());
        //Visibility
        visibilityButton = addDrawableChild(ButtonWidget.builder(getVisibilityText(), button -> {
            handler.visibility++;
            if(handler.visibility >= 3 || handler.visibility < 0){
                handler.visibility = 0;
            }
        }).dimensions(bX, bY + 45, bWidth, bHeight).build());
        //Apply Changes
        applyChanges = addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.applyChanges"), button -> {
            sendMenuUpdatePacket(handler.height, handler.width, handler.depth, handler.radius, handler.polarity, handler.visibility);
            close();
        }).dimensions(bX, bY + 70, bWidth, bHeight).build());
    }

    private Text getPolarityText(){
        if(handler.polarity == 0){
            return Text.translatable("amethystgravity.fieldGenerator.attract");
        }else{
            return Text.translatable("amethystgravity.fieldGenerator.repel");
        }
    }

    private Text getVisibilityText(){
        if(handler.visibility == 0){
            return Text.translatable("amethystgravity.fieldGenerator.with_glasses");
        }else if(handler.visibility == 1){
            return Text.translatable("amethystgravity.fieldGenerator.always");
        }else{
            return Text.translatable("amethystgravity.fieldGenerator.never");
        }
    }

    protected void renderValuesAndLabels(DrawContext context){
    }

    protected void drawValue(DrawContext context, double value, int xOffset){
        int tX = (width) / 2;
        int tY = (height - textRenderer.fontHeight) / 2 + 6;
        DecimalFormat df = new DecimalFormat("0.0");

        String heightValue = df.format(value);
        context.drawText(textRenderer, heightValue, (int) (tX-textRenderer.getWidth(heightValue)/2.f+xOffset), tY-28, Color.DARK_GRAY.getRGB(), true);
    }

    protected void drawLabel(DrawContext context, String label, int xOffset){
        int tX = (width) / 2;
        int tY = (height - textRenderer.fontHeight) / 2 + 6;
        context.drawText(textRenderer, label, (int) (tX-textRenderer.getWidth(label)/2.f+0.5f+xOffset), tY-68, Color.DARK_GRAY.getRGB(), true);
    }

    protected void sendMenuUpdatePacket(int height, int width, int depth, int radius, int polarity, int visibility){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(height);
        buf.writeInt(width);
        buf.writeInt(depth);
        buf.writeInt(radius);
        buf.writeInt(polarity);
        buf.writeInt(visibility);
        ClientPlayNetworking.send(AmethystGravity.FIELD_GENERATOR_MENU_CHANNEL, buf);
    }
}
