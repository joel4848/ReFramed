package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.block.ReFramedSlopeFullBlock;
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

    public UnbakedSlopeModel(Identifier parent) {
        super(parent);
        this.baseMesh = SlopeFullMesh.getSlopeMesh();
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
                // Get the edge from the blockstate
                Edge edge = state != null && state.contains(EDGE)
                        ? state.get(EDGE)
                        : Edge.DOWN_SOUTH;

                MeshBuilder builder = ReFramedClient.HELPER.getFabricRenderer().meshBuilder();
                QuadEmitter emitter = builder.getEmitter();

                // Get transformation for this edge orientation
                RenderContext.QuadTransform transform = getTransformForEdge(edge);

                // Apply transformation to base mesh
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

    /**
     * Get the transformation to apply based on the Edge orientation.
     * Base mesh is oriented for Edge.DOWN_SOUTH (slope rising from north to south)
     */
    private static RenderContext.QuadTransform getTransformForEdge(Edge edge) {
        Matrix4f matrix = getRotationMatrix(edge);
        if (matrix == null) return null;

        Map<Direction, Direction> faceMap = createFacePermutation(matrix);

        return quad -> {
            // Transform vertex positions
            Vector3f pos = new Vector3f();
            for (int i = 0; i < 4; i++) {
                quad.copyPos(i, pos);
                pos.sub(0.5f, 0.5f, 0.5f); // Center around origin
                pos.mulPosition(matrix);    // Apply rotation
                pos.add(0.5f, 0.5f, 0.5f); // Move back
                quad.pos(i, pos.x(), pos.y(), pos.z());
            }

            // Transform face directions
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

    /**
     * Get the rotation matrix for transforming from base orientation (DOWN_SOUTH)
     * to the target edge orientation
     */
    private static Matrix4f getRotationMatrix(Edge edge) {
        Matrix4f matrix = new Matrix4f().identity();

        switch (edge) {
            case DOWN_SOUTH -> { return null; } // Base orientation, no transform
            case NORTH_DOWN -> matrix.rotateY((float) Math.PI); // 180° around Y
            case UP_NORTH -> matrix.rotateX((float) Math.PI); // 180° around X
            case SOUTH_UP -> { // 180° around Y, then 180° around X
                matrix.rotateY((float) Math.PI);
                matrix.rotateX((float) Math.PI);
            }
            case DOWN_EAST -> matrix.rotateY((float) (Math.PI / 2)); // 90° CW around Y
            case WEST_DOWN -> matrix.rotateY((float) (-Math.PI / 2)); // 90° CCW around Y
            case EAST_UP -> { // 90° CW around Y, then 180° around X
                matrix.rotateY((float) (Math.PI / 2));
                matrix.rotateX((float) Math.PI);
            }
            case UP_WEST -> { // 90° CCW around Y, then 180° around X
                matrix.rotateY((float) (-Math.PI / 2));
                matrix.rotateX((float) Math.PI);
            }
            case EAST_SOUTH -> matrix.rotateZ((float) (-Math.PI / 2)); // 90° CW around Z
            case WEST_NORTH -> matrix.rotateZ((float) (Math.PI / 2)); // 90° CCW around Z
            case NORTH_EAST -> { // 180° around Z
                matrix.rotateZ((float) Math.PI);
            }
            case SOUTH_WEST -> { // 180° around X, then 90° CW around Z
                matrix.rotateX((float) Math.PI);
                matrix.rotateZ((float) (-Math.PI / 2));
            }
        }

        return matrix;
    }

    /**
     * Create a map of how directions transform under the given matrix
     */
    private static Map<Direction, Direction> createFacePermutation(Matrix4f matrix) {
        Map<Direction, Direction> map = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            map.put(dir, Direction.transform(matrix, dir));
        }
        return map;
    }
}