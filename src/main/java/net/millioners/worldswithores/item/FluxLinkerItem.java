package net.millioners.worldswithores.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.millioners.worldswithores.blockentity.FluxWirelessBlockEntity;
import net.millioners.worldswithores.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluxLinkerItem extends Item {
    public FluxLinkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        ItemStack stack = context.getItemInHand();

        if (!(be instanceof FluxWirelessBlockEntity wireless)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        CompoundTag tag = stack.getOrCreateTag();
        if (wireless.isTransmitter()) {
            tag.putLong("TxPos", pos.asLong());
            tag.putString("TxDim", level.dimension().location().toString());
            player.displayClientMessage(Component.translatable("message.worlds_with_ores.linker.saved_tx",
                    pos.getX(), pos.getY(), pos.getZ()), true);
            return InteractionResult.CONSUME;
        }

        // Receiver: apply link from saved TX
        if (!tag.contains("TxPos") || !tag.contains("TxDim")) {
            player.displayClientMessage(Component.translatable("message.worlds_with_ores.linker.need_tx"), true);
            return InteractionResult.FAIL;
        }
        if (!tag.getString("TxDim").equals(level.dimension().location().toString())) {
            player.displayClientMessage(Component.translatable("message.worlds_with_ores.linker.wrong_dim"), true);
            return InteractionResult.FAIL;
        }
        BlockPos txPos = BlockPos.of(tag.getLong("TxPos"));
        if (txPos.distManhattan(pos) > FluxWirelessBlockEntity.MAX_RANGE) {
            player.displayClientMessage(Component.translatable("message.worlds_with_ores.wireless.out_of_range",
                    txPos.getX(), txPos.getY(), txPos.getZ()), true);
            return InteractionResult.FAIL;
        }
        BlockEntity txBe = level.getBlockEntity(txPos);
        if (!(txBe instanceof FluxWirelessBlockEntity tx) || !tx.isTransmitter()
                || !level.getBlockState(txPos).is(ModBlocks.FLUX_WIRELESS_TRANSMITTER.get())) {
            player.displayClientMessage(Component.translatable("message.worlds_with_ores.linker.tx_missing"), true);
            return InteractionResult.FAIL;
        }

        tx.setLinkedPos(pos);
        wireless.setLinkedPos(txPos);
        player.displayClientMessage(Component.translatable("message.worlds_with_ores.linker.linked"), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.worlds_with_ores.flux_linker").withStyle(ChatFormatting.GRAY));
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("TxPos")) {
            BlockPos pos = BlockPos.of(tag.getLong("TxPos"));
            tooltip.add(Component.translatable("tooltip.worlds_with_ores.flux_linker.saved",
                    pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.AQUA));
        }
    }
}
