package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.client.ReFramedClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.Direction;

public class SlopeFullMesh {

    public static Mesh getSlopeMesh() {
        Renderer renderer = ReFramedClient.HELPER.getFabricRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        emitter
                .pos(0, 1f, 1f, 0f)
                .pos(1, 0f, 1f, 0f)
                .pos(2, 0f, 0f, 1f)
                .pos(3, 1f, 0f, 1f)
                .uv(0, 1f, 0f)
                .uv(1, 0f, 0f)
                .uv(2, 0f, 1f)
                .uv(3, 1f, 1f)
                .color(-1, -1, -1, -1)
                .cullFace(null)
                .nominalFace(Direction.UP)
                .tag(Direction.UP.ordinal() + 1)
                .emit();

        emitter
                .square(Direction.DOWN, 0, 0, 1, 1, 0)
                .uvUnitSquare()
                .color(-1, -1, -1, -1)
                .cullFace(Direction.DOWN)
                .tag(Direction.DOWN.ordinal() + 1)
                .emit();

        emitter
                .pos(0, 0f, 0f, 1f)
                .pos(1, 0f, 1f, 0f)
                .pos(2, 0f, 0f, 0f)
                .pos(3, 0f, 0f, 1f)
                .uv(0, 1f, 1f)
                .uv(1, 1f, 0f)
                .uv(2, 0f, 1f)
                .uv(3, 1f, 1f)
                .color(-1, -1, -1, -1)
                .cullFace(Direction.WEST)
                .nominalFace(Direction.WEST)
                .tag(Direction.WEST.ordinal() + 1)
                .emit();

        emitter
                .pos(0, 1f, 0f, 0f)
                .pos(1, 1f, 1f, 0f)
                .pos(2, 1f, 0f, 1f)
                .pos(3, 1f, 0f, 0f)
                .uv(0, 0f, 1f)
                .uv(1, 0f, 0f)
                .uv(2, 1f, 1f)
                .uv(3, 0f, 1f)
                .color(-1, -1, -1, -1)
                .cullFace(Direction.EAST)
                .nominalFace(Direction.EAST)
                .tag(Direction.EAST.ordinal() + 1)
                .emit();

        emitter
                .pos(0, 0f, 1f, 0f)
                .pos(1, 1f, 1f, 0f)
                .pos(2, 1f, 0f, 0f)
                .pos(3, 0f, 0f, 0f)
                .uv(0, 0f, 0f)
                .uv(1, 1f, 0f)
                .uv(2, 1f, 1f)
                .uv(3, 0f, 1f)
                .color(-1, -1, -1, -1)
                .cullFace(Direction.NORTH)
                .nominalFace(Direction.NORTH)
                .tag(Direction.NORTH.ordinal() + 1)
                .emit();

        emitter
                .pos(0, 0f, 0f, 1f)
                .pos(1, 1f, 0f, 1f)
                .pos(2, 1f, 0f, 1f)
                .pos(3, 0f, 0f, 1f)
                .uv(0, 0f, 1f)
                .uv(1, 1f, 1f)
                .uv(2, 1f, 1f)
                .uv(3, 0f, 1f)
                .color(-1, -1, -1, -1)
                .cullFace(Direction.SOUTH)
                .nominalFace(Direction.SOUTH)
                .tag(Direction.SOUTH.ordinal() + 1)
                .emit();

        return builder.build();
    }
}