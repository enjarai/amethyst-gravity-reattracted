package cyborgcabbage.amethystgravity.mixin;

import cyborgcabbage.amethystgravity.gravity.AnchorGravity;
import cyborgcabbage.amethystgravity.gravity.FieldGravityVerifier;
import cyborgcabbage.amethystgravity.gravity.GravityData;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import cyborgcabbage.amethystgravity.item.GravityAnchor;
import gravity_changer.GravityComponent;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.api.RotationParameters;
import gravity_changer.util.RotationUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
//                GravityChangerAPI.addGravity(entity, FieldGravityVerifier.newFieldGravity(newDirection, new RotationParameters()));
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

//    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    private Vec3d moveInject(Vec3d movement){
        Entity e = (Entity) (Object) this;
        if (movement == e.getVelocity() && e instanceof LivingEntity entity) {
            if((entity instanceof ClientPlayerEntity) || (!entity.getWorld().isClient)) {
                boolean isFlying = false;
                if (entity instanceof PlayerEntity pe) isFlying = pe.getAbilities().flying;
                boolean isFallFlying = entity.isFallFlying();
                //Init vars
                final GravityEffect currentGravity = getFieldGravity();
                GravityEffect newGravity = null;
                List<GravityEffect> directions = getFieldList();
                //If the player is flying or in spectator
                if (!entity.isSpectator() && !isFlying) {
                    //Find the elements of directions which have the lowest volume
                    final double lowestVolume = directions.stream().map(GravityEffect::volume).min(Double::compare).orElse(0.0);
                    List<GravityEffect> highestPriority = directions.stream().filter(g -> g.volume() == lowestVolume).toList();
                    if (highestPriority.size() > 0) {
                        newGravity = highestPriority.get(0);
                    }
                    //Get colliding directions
                    List<Direction> localCollidingDirections = new ArrayList<>();
                    Box box = entity.getBoundingBox();
                    List<VoxelShape> entityCollisions = entity.getWorld().getEntityCollisions(entity, box.stretch(movement));
                    Vec3d adjustedMovement = (movement.lengthSquared() == 0.0) ? movement : Entity.adjustMovementForCollisions(entity, movement, box, entity.getWorld(), entityCollisions);
                    if (movement.x > adjustedMovement.x) localCollidingDirections.add(Direction.EAST);
                    if (movement.x < adjustedMovement.x) localCollidingDirections.add(Direction.WEST);
                    if (movement.y > adjustedMovement.y) localCollidingDirections.add(Direction.UP);
                    if (movement.y < adjustedMovement.y) localCollidingDirections.add(Direction.DOWN);
                    if (movement.z > adjustedMovement.z) localCollidingDirections.add(Direction.SOUTH);
                    if (movement.z < adjustedMovement.z) localCollidingDirections.add(Direction.NORTH);
                    if (currentGravity != null && highestPriority.size() > 0) {
                        //Find an element with equal direction to currentGravity
                        newGravity = highestPriority.stream().filter(ge -> ge.direction() == currentGravity.direction()).findFirst().orElse(newGravity);
                    }
                    if (currentGravity != null && localCollidingDirections.contains(Direction.DOWN)) {
                        //Inside corner snap (if the player is on the ground)
                        newGravity = getInsideCornerSnapDirection(currentGravity, highestPriority, localCollidingDirections).orElse(newGravity);
                    }
                    if (currentGravity != null && !localCollidingDirections.contains(Direction.DOWN)) {
                        //Outside corner snap (if the player just left the ground)
                        newGravity = getOutsideCornerSnapDirection(currentGravity, newGravity, movement).orElse(newGravity);
                    }
                }
                Direction newDirection = newGravity == null ? null : newGravity.direction();
                if (newDirection != null) {
//                    PacketByteBuf info = newGravity == null ? PacketByteBufs.create() : FieldGravityVerifier.packInfo(newGravity.source());
                    RotationParameters rotationParameters = new RotationParameters(entity.isOnGround(), !isFallFlying, RotationParameters.getDefault().rotationTimeMS());

                    FieldGravityVerifier.applyGravity(entity, newDirection, rotationParameters);
//                    if (entity instanceof ClientPlayerEntity cpe) {
//                        GravityChangerAPI.addGravityClient(cpe, FieldGravityVerifier.newFieldGravity(newDirection, rotationParameters), FieldGravityVerifier.FIELD_GRAVITY_SOURCE, info);
//                    } else {
//                        GravityChangerAPI.addGravity(entity, FieldGravityVerifier.newFieldGravity(newDirection, rotationParameters));
//                    }
                }
                setFieldGravity(newGravity);
            }
            //Clear direction pool
            getFieldList().clear();
            getLowerFieldList().clear();
            return entity.getVelocity();
        }
        return movement;
    }
    
    private Optional<GravityEffect> getInsideCornerSnapDirection(GravityEffect currentGravity, List<GravityEffect> effects, List<Direction> localCollidingDirections) {
        for(Direction localDirection : localCollidingDirections){
            if(localDirection != Direction.UP && localDirection != Direction.DOWN) {
                //collidingDirections will be relative to the player's gravity, we need to convert to be relative to the world
                Direction globalDirection = RotationUtil.dirPlayerToWorld(localDirection, currentGravity.direction());
                Optional<GravityEffect> effect = effects.stream().filter(ge -> ge.direction() == globalDirection).findFirst();
                if (effect.isPresent()) {
                    return effect;
                }
            }
        }
        return Optional.empty();
    }

    private Optional<GravityEffect> getOutsideCornerSnapDirection(GravityEffect currentGravity, @Nullable GravityEffect newGravity, Vec3d movement) {
        //If the new gravity effect is more than 4 times larger than the current one
        if(newGravity == null || currentGravity.volume() < newGravity.volume() / 4.0) {
            ArrayList<GravityEffect> effectsBelowPlayer = getLowerFieldList();
            Optional<GravityEffect> min = effectsBelowPlayer.stream().min(Comparator.comparingDouble(GravityEffect::volume));
            if(min.isPresent()){
                double minVolume = min.get().volume();
                effectsBelowPlayer.removeIf(ge -> ge.volume() > minVolume);
                //Get horizontal directions and sort by close-ness to velocity
                Vec3d velocity = RotationUtil.vecPlayerToWorld(movement, currentGravity.direction());
                List<Direction> hDir = getHorizontalDirections();
                hDir.sort((d1, d2) -> {
                    double dot1 = velocity.dotProduct(new Vec3d(d1.getUnitVector()));
                    double dot2 = velocity.dotProduct(new Vec3d(d2.getUnitVector()));
                    return Double.compare(dot1, dot2);
                });
                //Go through directions in order of close-ness to velocity
                for(Direction d : hDir){
                    Optional<GravityEffect> effect = effectsBelowPlayer.stream().filter(g -> g.direction() == d).findFirst();
                    if(effect.isPresent()){
                        return effect;
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static boolean arePerpendicular(Direction dir0, Direction dir1){
        return dir0 != dir1 && dir0 != dir1.getOpposite();
    }

    private List<Direction> getHorizontalDirections(){
        ArrayList<Direction> directions = new ArrayList<>();
        //Get all horizontal directions
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        //Convert to world direction
        Direction gravityDirection = getFieldGravity().direction();
        directions.replaceAll(direction -> RotationUtil.dirPlayerToWorld(direction, gravityDirection));
        return directions;
    }
}
