package dev.enjarai.amethystgravity.mixin;

import dev.enjarai.amethystgravity.gravity.AnchorGravity;
import dev.enjarai.amethystgravity.gravity.FieldGravityVerifier;
import dev.enjarai.amethystgravity.gravity.GravityData;
import dev.enjarai.amethystgravity.gravity.GravityEffect;
import dev.enjarai.amethystgravity.item.GravityAnchor;
import gravity_changer.GravityComponent;
import gravity_changer.api.RotationParameters;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Entity.class, priority = 999)
public abstract class EntityMixinClient implements GravityData {
    public ArrayList<GravityEffect> amethystgravity$gravityEffectList = new ArrayList<>();
    public ArrayList<GravityEffect> amethystgravity$lowerGravityEffectList = new ArrayList<>();
    public GravityEffect amethystgravity$gravityEffect = null;

    @Override
    public ArrayList<GravityEffect> getFieldList() {
        return amethystgravity$gravityEffectList;
    }

    @Override
    public ArrayList<GravityEffect> getLowerFieldList() {
        return amethystgravity$lowerGravityEffectList;
    }

    @Override
    public void setFieldGravity(GravityEffect _gravityEffect) {
        amethystgravity$gravityEffect = _gravityEffect;
    }

    @Override
    public GravityEffect getFieldGravity() {
        return amethystgravity$gravityEffect;
    }

    @Override
    public void updateGravity(GravityComponent component){
        Entity entity = (Entity) (Object) this;
        if(entity instanceof LivingEntity living && !entity.isSpectator()){
            anchorGravity(living, component);
        }
        if((entity instanceof ClientPlayerEntity) || (!entity.getWorld().isClient)){
            //Init vars
            final GravityEffect currentGravity = getFieldGravity();
            boolean isFallFlying = entity instanceof LivingEntity living && living.isFallFlying();
            GravityEffect newGravity = null;
            List<GravityEffect> directions = getFieldList();
            boolean isFlying = entity instanceof PlayerEntity pe && pe.getAbilities().flying;
            //If the player is flying or in spectator
            if (!entity.isSpectator() && !isFlying) {
                //Find the elements of directions which have the lowest volume
                final double lowestVolume = directions.stream().map(GravityEffect::volume).min(Double::compare).orElse(0.0);
                List<GravityEffect> highestPriority = directions.stream().filter(g -> g.volume() == lowestVolume).toList();
                if (highestPriority.size() > 0) {
                    newGravity = highestPriority.get(0);
                }
                if (currentGravity != null && highestPriority.size() > 0) {
                    //Find an element with equal direction to currentGravity
                    newGravity = highestPriority.stream().filter(ge -> ge.direction() == currentGravity.direction()).findFirst().orElse(newGravity);
                }
            }
            Direction newDirection = newGravity == null ? null : newGravity.direction();
            if (newDirection != null) {
                RotationParameters rotationParameters = new RotationParameters(entity.isOnGround(), !isFallFlying, RotationParameters.getDefault().rotationTimeMS());
                FieldGravityVerifier.applyGravity(entity, newDirection, rotationParameters);
            }
            setFieldGravity(newGravity);
            //Clear direction pool
            getFieldList().clear();
            getLowerFieldList().clear();
        }
    }

    private void anchorGravity(LivingEntity entity, GravityComponent component){
        if(entity.getOffHandStack().getItem() instanceof GravityAnchor anchor){
            AnchorGravity.applyGravity(component, anchor.direction);
        }else if(entity.getMainHandStack().getItem() instanceof GravityAnchor anchor) {
            AnchorGravity.applyGravity(component, anchor.direction);
        }
    }
}
