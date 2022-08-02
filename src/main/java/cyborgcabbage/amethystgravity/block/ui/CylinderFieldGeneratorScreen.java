package cyborgcabbage.amethystgravity.block.ui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class CylinderFieldGeneratorScreen extends AbstractFieldGeneratorScreen<CylinderFieldGeneratorScreenHandler>{
    public CylinderFieldGeneratorScreen(CylinderFieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int bWidth = 20;
        int bHeight = 20;
        int bX = (width - bWidth) / 2;
        int bY = (height - bHeight) / 2 + 5;
        //Radius
        addDrawableChild(new ButtonWidget(bX-25, bY - 48, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setRadius(handler.radius + magnitude)));
        addDrawableChild(new ButtonWidget(bX-25, bY - 8, bWidth, bHeight,Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setRadius(handler.radius - magnitude)));
        //Width
        addDrawableChild(new ButtonWidget(bX+25, bY - 48, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setWidth(handler.width + magnitude)));
        addDrawableChild(new ButtonWidget(bX+25, bY - 8, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setWidth(handler.width - magnitude)));
    }

    @Override
    protected void renderValuesAndLabels(MatrixStack matrices) {
        super.renderValuesAndLabels(matrices);
        //Draw values
        drawValue(matrices, handler.radius/10.0, -25);
        drawValue(matrices, handler.width/10.0, 25);
        //Draw labels
        drawLabel(matrices, "Radius", -25);
        drawLabel(matrices, "Width", 25);
    }
}
