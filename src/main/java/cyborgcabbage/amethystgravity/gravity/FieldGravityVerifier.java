package cyborgcabbage.amethystgravity.gravity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.api.RotationParameters;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class FieldGravityVerifier {
    public static Identifier FIELD_GRAVITY_SOURCE = new Identifier(AmethystGravity.MOD_ID, "field");
    public static int FIELD_GRAVITY_PRIORITY = 100;
    public static int FIELD_GRAVITY_MAX_DURATION = 100;

    public static void applyGravity(Entity entity, @NotNull Direction direction, RotationParameters rotationParameters) {
        GravityChangerAPI.getGravityComponent(entity).applyGravityDirectionEffect(direction, rotationParameters, FIELD_GRAVITY_PRIORITY);
    }



//    public static Gravity newFieldGravity(Direction direction, RotationParameters rp){
//        return new Gravity(direction, FIELD_GRAVITY_PRIORITY, FIELD_GRAVITY_MAX_DURATION, FIELD_GRAVITY_SOURCE.toString(), rp);
//    }

//    public static boolean check(ServerPlayerEntity player, PacketByteBuf info, UpdateGravityPacket packet){
//        if(packet.gravity.duration() > FIELD_GRAVITY_MAX_DURATION) return false;
//        if(packet.gravity.priority() > FIELD_GRAVITY_PRIORITY) return false;
//        if(!packet.gravity.source().equals(FIELD_GRAVITY_SOURCE.toString())) return false;
//        if(packet.gravity.direction() == null) return true;
//        BlockPos blockPos = info.readBlockPos();
//        World world = player.getWorld();
//        if(world == null) return false;
//        BlockEntity blockEntity = world.getBlockEntity(blockPos);
//        BlockState blockState = world.getBlockState(blockPos);
//        if(blockState.getBlock() instanceof PlatingBlock){
//            ArrayList<Direction> directions = PlatingBlock.getDirections(blockState);
//            if(directions.contains(packet.gravity.direction())){
//                double distance = GravityEffect.getGravityOrigin(player).distanceTo(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));
//                return distance < 5;
//            }
//        }else if(blockEntity instanceof AbstractFieldGeneratorBlockEntity fieldBlockEntity){
//            Box gravityEffectBox = fieldBlockEntity.getGravityEffectBox();
//            return gravityEffectBox.expand(3.0).intersects(GravityEffect.getGravityEffectCollider(player));
//        }
//        return false;
//    }
//
//    public static PacketByteBuf packInfo(BlockPos block){
//        var buf = PacketByteBufs.create();
//        buf.writeBlockPos(block);
//        return buf;
//    }
}
