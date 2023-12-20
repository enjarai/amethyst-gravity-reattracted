package cyborgcabbage.amethystgravity.block.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class PlanetFieldGeneratorScreen extends AbstractFieldGeneratorScreen<PlanetFieldGeneratorScreenHandler> {

    public PlanetFieldGeneratorScreen(PlanetFieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
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
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setRadius(handler.radius + magnitude))
                .dimensions(bX, bY - 48, bWidth, bHeight).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setRadius(handler.radius - magnitude))
                .dimensions(bX, bY - 8, bWidth, bHeight).build());
    }

    @Override
    protected void renderValuesAndLabels(DrawContext context) {
        super.renderValuesAndLabels(context);
        //Draw values
        drawValue(context, handler.radius/10.0, 0);
        //Draw labels
        drawLabel(context, "Radius", 0);
    }
}
