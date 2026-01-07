package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.client.ReFramedClient;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Function;

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
                MeshBuilder builder = ReFramedClient.HELPER.getFabricRenderer().meshBuilder();
                QuadEmitter emitter = builder.getEmitter();

                baseMesh.forEach(quad -> {
                    emitter.copyFrom(quad);
                    emitter.emit();
                });

                return builder.build();
            }
        };
    }
}