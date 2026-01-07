package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import fr.adrien1106.reframed.util.VoxelHelper;

import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static net.minecraft.util.shape.VoxelShapes.combineAndSimplify;

public class ReFramedSlopeFullBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] SLOPE_VOXELS;

    public ReFramedSlopeFullBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.DOWN_SOUTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Edge edge = BlockHelper.getPlacementEdge(ctx);
        return super.getPlacementState(ctx).with(EDGE, edge);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlopeShape(state.get(EDGE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(EDGE, state.get(EDGE).rotate(rotation));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(EDGE, state.get(EDGE).mirror(mirror));
    }

    public static VoxelShape getSlopeShape(Edge edge) {
        return SLOPE_VOXELS[edge.getID()];
    }

    static {

        VoxelShape BASE_SLOPE = Stream.of(
                createCuboidShape(0, 0, 0, 16, 2, 16),
                createCuboidShape(0, 2, 2, 16, 4, 16),
                createCuboidShape(0, 4, 4, 16, 6, 16),
                createCuboidShape(0, 6, 6, 16, 8, 16),
                createCuboidShape(0, 8, 8, 16, 10, 16),
                createCuboidShape(0, 10, 10, 16, 12, 16),
                createCuboidShape(0, 12, 12, 16, 14, 16),
                createCuboidShape(0, 14, 14, 16, 16, 16)
        ).reduce((v1, v2) -> combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

        SLOPE_VOXELS = VoxelListBuilder.create(BASE_SLOPE, 12)
                .add(shape -> VoxelHelper.rotateY(VoxelHelper.rotateY(shape)))
                .add(0, VoxelHelper::rotateX, VoxelHelper::rotateX)
                .add(2, VoxelHelper::rotateY, VoxelHelper::rotateY)
                .add(0, VoxelHelper::rotateCY)
                .add(4, VoxelHelper::rotateY, VoxelHelper::rotateY)
                .add(5, VoxelHelper::rotateX, VoxelHelper::rotateX)
                .add(6, VoxelHelper::rotateY, VoxelHelper::rotateY)
                .add(0, VoxelHelper::rotateZ)
                .add(8, VoxelHelper::rotateY)
                .add(9, VoxelHelper::rotateY)
                .add(10, VoxelHelper::rotateY)
                .build();
    }
}