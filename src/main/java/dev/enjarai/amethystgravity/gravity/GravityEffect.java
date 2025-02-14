package dev.enjarai.amethystgravity.gravity;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record GravityEffect(Direction direction, double volume, BlockPos source) {

    public static Box getGravityEffectCollider(Entity entity){
        var d = entity.getDimensions(entity.getPose());
        double hw = d.width / 2.0;
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(hw, 2*hw, hw, GravityChangerAPI.getGravityDirection(entity));
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(-hw, 0.0, -hw, GravityChangerAPI.getGravityDirection(entity));
        return new Box(pos1, pos2).offset(entity.getPos());
    }

    public static Box getLowerGravityEffectCollider(Entity entity){
        var d = entity.getDimensions(entity.getPose());
        double hw = d.width / 2.0;
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(hw, -0.1, hw, GravityChangerAPI.getGravityDirection(entity));
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(-hw, 0.0, -hw, GravityChangerAPI.getGravityDirection(entity));
        return new Box(pos1, pos2).offset(entity.getPos());
    }

    public static void applyGravityEffectToPlayers(GravityEffect gravityEffect, Box box, World world, boolean opposite, List<Direction> directions, boolean lower){
        List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, box.expand(0.5), e -> true);
        for (PlayerEntity player : playerEntities) {
            Vec3d boxCentre = box.getCenter();
            Vec3d playerCentre = getGravityOrigin(player);
            Optional<Direction> optionalEffectiveDirection = directions.stream()
                    .max(Comparator.comparingDouble(d -> boxCentre.add(new Vec3d(d.getUnitVector())).distanceTo(playerCentre)));
            if(optionalEffectiveDirection.isEmpty()) return;
            Direction effectiveDirection = optionalEffectiveDirection.get();
            if(opposite) effectiveDirection = effectiveDirection.getOpposite();
            gravityEffect = new GravityEffect(effectiveDirection, gravityEffect.volume(), gravityEffect.source());
            //Get player collider for gravity effects
            Box gravityEffectCollider = (gravityEffect.direction().getOpposite() == GravityChangerAPI.getGravityDirection(player)) ? player.getBoundingBox() : GravityEffect.getGravityEffectCollider(player);
            Box lowerGravityEffectCollider = GravityEffect.getLowerGravityEffectCollider(player);
            //Check if the player's rotation box is colliding with this gravity plates area of effect
            if (box.intersects(gravityEffectCollider))
                ((GravityData) player).getFieldList().add(gravityEffect);
            if (lower && box.intersects(lowerGravityEffectCollider))
                ((GravityData) player).getLowerFieldList().add(gravityEffect);
        }
    }

    public static void applyGravityEffectToEntities(GravityEffect gravityEffect, Box box, World world, boolean opposite, List<Direction> directions, boolean lower){
        List<Entity> entities = world.getEntitiesByClass(Entity.class, box.expand(0.5), e -> true);
        for (Entity entity : entities) {
            Vec3d boxCentre = box.getCenter();
            Vec3d entityCentre = getGravityOrigin(entity);
            Optional<Direction> optionalEffectiveDirection = directions.stream()
                    .max(Comparator.comparingDouble(d -> boxCentre.add(new Vec3d(d.getUnitVector())).distanceTo(entityCentre)));
            if(optionalEffectiveDirection.isEmpty()) return;
            Direction effectiveDirection = optionalEffectiveDirection.get();
            if(opposite) effectiveDirection = effectiveDirection.getOpposite();
            gravityEffect = new GravityEffect(effectiveDirection, gravityEffect.volume(), gravityEffect.source());
            //Get entity collider for gravity effects
            Box gravityEffectCollider = (gravityEffect.direction().getOpposite() == GravityChangerAPI.getGravityDirection(entity)) ? entity.getBoundingBox() : GravityEffect.getGravityEffectCollider(entity);
            Box lowerGravityEffectCollider = GravityEffect.getLowerGravityEffectCollider(entity);
            //Check if the entity's rotation box is colliding with this gravity plates area of effect
            if (box.intersects(gravityEffectCollider))
                ((GravityData) entity).getFieldList().add(gravityEffect);
            if (lower && box.intersects(lowerGravityEffectCollider))
                ((GravityData) entity).getLowerFieldList().add(gravityEffect);
        }
    }

    public static Vec3d getGravityOrigin(Entity entity){
        var dim = entity.getDimensions(entity.getPose());
        return entity.getPos().add(RotationUtil.vecPlayerToWorld(0.0, dim.width / 2.0, 0.0, GravityChangerAPI.getGravityDirection(entity)));
    }
}
