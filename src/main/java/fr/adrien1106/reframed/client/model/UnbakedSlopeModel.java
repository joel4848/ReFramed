package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;

public class UnbakedSlopeModel extends UnbakedRetexturedModel {

    private final Mesh baseMesh;
    private final Mesh itemMesh;

    public UnbakedSlopeModel(Identifier parent) {
        super(parent);
        this.baseMesh = SlopeFullMesh.getSlopeMesh();
        this.itemMesh = SlopeFullMesh.getItemMesh();
        item_state = Blocks.AIR.getDefaultState();
    }

    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings bakeSettings) {
        return new RetexturingBakedModel(
                baker.bake(parent, bakeSettings),
                ReFramedClient.HELPER.getCamoAppearanceManager(textureGetter),
                theme_index,
                bakeSettings,
                item_state
        ) {
            @Override
            protected Mesh convertModel(BlockState state) {
                boolean isItem = state == null || state.isAir();

                if (isItem) {
                    MeshBuilder builder = ReFramedClient.HELPER.getFabricRenderer().meshBuilder();
                    QuadEmitter emitter = builder.getEmitter();

                    itemMesh.forEach(quad -> {
                        emitter.copyFrom(quad);
                        emitter.emit();
                    });

                    return builder.build();
                }

                Edge edge = state.contains(EDGE) ? state.get(EDGE) : Edge.DOWN_SOUTH;

                MeshBuilder builder = ReFramedClient.HELPER.getFabricRenderer().meshBuilder();
                QuadEmitter emitter = builder.getEmitter();

                RenderContext.QuadTransform transform = getTransformForEdge(edge);

                baseMesh.forEach(quad -> {
                    emitter.copyFrom(quad);
                    if (transform != null) {
                        transform.transform(emitter);
                    }
                    emitter.emit();
                });

                return builder.build();
            }
        };
    }

    private static RenderContext.QuadTransform getTransformForEdge(Edge edge) {
        Matrix4f matrix = getRotationMatrix(edge);
        if (matrix == null) return null;

        Map<Direction, Direction> faceMap = createFacePermutation(matrix);

        return quad -> {
            float[] originalU = new float[4];
            float[] originalV = new float[4];
            for (int i = 0; i < 4; i++) {
                originalU[i] = quad.u(i);
                originalV[i] = quad.v(i);
            }

            Vector3f pos = new Vector3f();
            for (int i = 0; i < 4; i++) {
                quad.copyPos(i, pos);
                pos.sub(0.5f, 0.5f, 0.5f);
                pos.mulPosition(matrix);
                pos.add(0.5f, 0.5f, 0.5f);
                quad.pos(i, pos.x(), pos.y(), pos.z());
            }

            for (int i = 0; i < 4; i++) {
                quad.uv(i, originalU[i], originalV[i]);
            }

            Direction oldCull = quad.cullFace();
            if (oldCull != null) {
                quad.cullFace(faceMap.get(oldCull));
            }

            Direction oldNominal = quad.nominalFace();
            if (oldNominal != null) {
                Direction newNominal = faceMap.get(oldNominal);
                quad.nominalFace(newNominal);
                quad.tag(newNominal.ordinal() + 1);
            }

            return true;
        };
    }

    private static Matrix4f getRotationMatrix(Edge edge) {
        Matrix4f matrix = new Matrix4f().identity();

        switch (edge) {
            case DOWN_SOUTH -> { return null; }
            case NORTH_DOWN -> matrix.rotateY((float) Math.PI);
            case UP_NORTH -> matrix.rotateX((float) Math.PI);
            case SOUTH_UP -> {
                matrix.rotateY((float) Math.PI);
                matrix.rotateX((float) Math.PI);
            }

            case DOWN_EAST -> matrix.rotateY((float) (Math.PI / 2));
            case WEST_DOWN -> matrix.rotateY((float) (-Math.PI / 2));
            case EAST_UP -> {
                matrix.rotateY((float) (Math.PI / 2));
                matrix.rotateX((float) Math.PI);
                matrix.rotateY((float) Math.PI);
            }

            case UP_WEST -> {
                matrix.rotateY((float) (-Math.PI / 2));
                matrix.rotateX((float) Math.PI);
                matrix.rotateY((float) Math.PI);
            }

            case EAST_SOUTH -> matrix.rotateZ((float) (-Math.PI / 2));
            case WEST_NORTH -> matrix.rotateZ((float) (Math.PI / 2));
            case NORTH_EAST -> matrix.rotateZ((float) Math.PI);
            case SOUTH_WEST -> {
                matrix.rotateX((float) Math.PI);
                matrix.rotateZ((float) (-Math.PI / 2));
            }
        }

        return matrix;
    }

    private static Map<Direction, Direction> createFacePermutation(Matrix4f matrix) {
        Map<Direction, Direction> map = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            map.put(dir, Direction.transform(matrix, dir));
        }
        return map;
    }
}