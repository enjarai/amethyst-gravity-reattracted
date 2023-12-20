package cyborgcabbage.amethystgravity.client.render.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

public abstract class AbstractFieldGeneratorBlockEntityRenderer<BE extends AbstractFieldGeneratorBlockEntity> implements BlockEntityRenderer<BE> {
    protected static final Identifier ARROW_TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/misc/arrow_forcefield.png");
    protected static final Identifier WALL_TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/misc/wall_forcefield.png");
    protected static final float SMIDGE = 0.01f;

    public AbstractFieldGeneratorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BE entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        boolean show = false;
        switch(entity.getVisibility()){
            case 0 -> {//With glasses
                Entity ce = MinecraftClient.getInstance().getCameraEntity();
                if(ce instanceof LivingEntity le) {
                    ItemStack equippedStack = le.getEquippedStack(EquipmentSlot.HEAD);
                    show = equippedStack.getItem() == AmethystGravity.GRAVITY_GLASSES;
                }
            }
            case 1 -> {//Always
                show = true;
            }
            case 2 -> {//Never
                show = false;
            }
        }
        if(show){
            double time = tickDelta;
            if (entity.getWorld() != null) {
                time += entity.getWorld().getTime();
                time /= 20;
            }
            //Animation
            float animation = (float) (time % 1);
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0.5);
            renderForceField(entity, matrixStack, vertexConsumerProvider, animation);
            matrixStack.pop();
        }
    }

    protected abstract void renderForceField(BE entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float animation);

    protected void addVertex(Matrix4f m, Matrix3f n, VertexConsumer buffer, Vector3f vec, float u, float v){
        addVertex(m, n, buffer, vec.x(), vec.y(), vec.z(), u, v);
    }

    protected void addVertex(Matrix4f m, Matrix3f n, VertexConsumer buffer, float x, float y, float z, float u, float v){
        buffer.vertex(m, x, y, z).color(.7f, .5f, .9f, .4f).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(n, 0, 1,0).next();
    }

    //Each point should be next to the previous going round the quad
    protected void renderFaceEdges(Matrix4f m, Matrix3f n, VertexConsumer buffer, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3){
        Vector3f faceCentre = new Vector3f(p0);
        faceCentre.lerp(p2, .5f);
        ArrayList<VecPair> faceEdges = new ArrayList<>();
        faceEdges.add(new VecPair(p0, p1));
        faceEdges.add(new VecPair(p1, p2));
        faceEdges.add(new VecPair(p2, p3));
        faceEdges.add(new VecPair(p3, p0));
        for (VecPair fe : faceEdges) {
            Vector3f edgeCentre = new Vector3f(fe.a());
            edgeCentre.lerp(fe.b(), 0.5f);
            Vector3f towardsFaceCentre = new Vector3f(faceCentre);
            towardsFaceCentre.sub(edgeCentre);
            towardsFaceCentre.normalize();
            towardsFaceCentre.mul(.25f,.25f,.25f);
            Vector3f towardsEdgeCentre = new Vector3f(edgeCentre);
            towardsEdgeCentre.sub(fe.b());
            towardsEdgeCentre.normalize();
            towardsEdgeCentre.mul(.25f,.25f,.25f);
            Vector3f i0 = new Vector3f(fe.b());
            Vector3f i1 = new Vector3f(fe.a());
            i0.add(towardsFaceCentre);
            i0.add(towardsEdgeCentre);
            i1.add(towardsFaceCentre);
            i1.sub(towardsEdgeCentre);
            addVertex(m, n, buffer, fe.a(), 0, 0);
            addVertex(m, n, buffer, fe.b(), 0, 0);
            addVertex(m, n, buffer, i0, 0, 0);
            addVertex(m, n, buffer, i1, 0, 0);
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(BE blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    public record VecPair(Vector3f a, Vector3f b){}
    public record IntPair(int a, int b){}
    public record IntFour(int a, int b, int c, int d){}
}
