package dev.enjarai.amethystgravity.gravity;

import dev.enjarai.amethystgravity.AmethystGravity;
import gravity_changer.GravityComponent;
import gravity_changer.api.RotationParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class AnchorGravity {
    public static Identifier FIELD_GRAVITY_SOURCE = new Identifier(AmethystGravity.MOD_ID, "anchor");
    public static int FIELD_GRAVITY_PRIORITY = 200;
    public static int FIELD_GRAVITY_MAX_DURATION = 100;

    public static void applyGravity(GravityComponent component, @NotNull Direction direction) {
        component.applyGravityDirectionEffect(direction, RotationParameters.getDefault(), FIELD_GRAVITY_PRIORITY);
    }

//    public static Gravity newGravity(Direction direction, RotationParameters rp){
//        return new Gravity(direction, FIELD_GRAVITY_PRIORITY, FIELD_GRAVITY_MAX_DURATION, FIELD_GRAVITY_SOURCE.toString(), rp);
//    }
}
