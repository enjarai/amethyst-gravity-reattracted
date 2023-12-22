package dev.enjarai.amethystgravity.block;

import dev.enjarai.amethystgravity.AmethystGravity;
import dev.enjarai.amethystgravity.block.entity.PlanetFieldGeneratorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class PlanetFieldGeneratorBlock extends AbstractFieldGeneratorBlock<PlanetFieldGeneratorBlockEntity> {

    public PlanetFieldGeneratorBlock(boolean _creative, Settings settings) {
        super(_creative, settings);
    }

    @Override
    public BlockEntityType<PlanetFieldGeneratorBlockEntity> getBlockEntity() {
        return AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_ENTITY;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlanetFieldGeneratorBlockEntity(pos, state);
    }
}
