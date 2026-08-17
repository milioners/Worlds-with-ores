package net.millioners.worldswithores.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.millioners.worldswithores.block.ModPortalBlock;
import net.millioners.worldswithores.util.SoftFrames;

import java.util.List;
import java.util.function.Supplier;

public class PortalIgniterItem extends Item {
    private final List<Supplier<Block>> frameBlocks;
    private final Supplier<ModPortalBlock> portalBlock;

    public PortalIgniterItem(Properties properties, Supplier<Block> frameBlock, Supplier<ModPortalBlock> portalBlock) {
        this(properties, List.of(frameBlock), portalBlock);
    }

    public PortalIgniterItem(Properties properties, List<Supplier<Block>> frameBlocks, Supplier<ModPortalBlock> portalBlock) {
        super(properties);
        this.frameBlocks = frameBlocks;
        this.portalBlock = portalBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        BlockPos adjacent = clicked.relative(context.getClickedFace());
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null && !player.mayUseItemAt(adjacent, context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }

        List<Block> frames = SoftFrames.resolveAll(this.frameBlocks);
        boolean lit = ModPortalBlock.trySpawnPortal(level, adjacent, frames, this.portalBlock.get())
                || ModPortalBlock.trySpawnPortal(level, clicked, frames, this.portalBlock.get());

        if (lit) {
            level.playSound(null, adjacent, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
                    level.getRandom().nextFloat() * 0.4F + 0.8F);
            if (player != null && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.FAIL;
    }
}
