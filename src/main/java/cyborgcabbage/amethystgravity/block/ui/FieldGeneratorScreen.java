package cyborgcabbage.amethystgravity.block.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class FieldGeneratorScreen extends AbstractFieldGeneratorScreen<FieldGeneratorScreenHandler>{

    public FieldGeneratorScreen(FieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
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
        //Height
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setHeight(handler.height + magnitude))
                .dimensions(bX-50, bY - 48, bWidth, bHeight).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setHeight(handler.height - magnitude))
                .dimensions(bX-50, bY - 8, bWidth, bHeight).build());
        //Width
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setWidth(handler.width + magnitude))
                .dimensions(bX, bY - 48, bWidth, bHeight).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setWidth(handler.width - magnitude))
                .dimensions(bX, bY - 8, bWidth, bHeight).build());
        //Depth
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setDepth(handler.depth + magnitude))
                .dimensions(bX+50, bY - 48, bWidth, bHeight).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setDepth(handler.depth - magnitude))
                .dimensions(bX+50, bY - 8, bWidth, bHeight).build());
    }

    @Override
    protected void renderValuesAndLabels(DrawContext context) {
        super.renderValuesAndLabels(context);
        //Draw values
        drawValue(context, handler.height/10.0, -50);
        drawValue(context, handler.width/10.0, 0);
        drawValue(context, handler.depth/10.0, 50);
        //Draw labels
        drawLabel(context, "Height", -50);
        drawLabel(context, "Width", 0);
        drawLabel(context, "Depth", 50);
    }
}
