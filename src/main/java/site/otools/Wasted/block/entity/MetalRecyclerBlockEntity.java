package site.otools.Wasted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.item.ModItems;

import java.util.List;

public class MetalRecyclerBlockEntity extends RecyclerBlockEntity {

    private static final ResourceKey<LootTable> METAL_RECYCLER_LOOT =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(WastedMod.MOD_ID, "blocks/recycler/metal"));

    public MetalRecyclerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.METAL_RECYCLER_BE.get(), pos, blockState);
    }

    @Override
    protected boolean hasRecipe() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);

        boolean isMetalItem = input.is(net.neoforged.neoforge.common.Tags.Items.INGOTS) ||
                input.is(net.neoforged.neoforge.common.Tags.Items.NUGGETS) ||
                input.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "iron_ores"))) ||
                input.getItem() == Items.CHAIN ||
                input.getItem() == Items.IRON_BARS ||
                input.is(ModItems.METAL);

        if (!isMetalItem) return false;

        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) return true;
            if (stack.getCount() < stack.getMaxStackSize()) return true;
        }
        return false;
    }

    @Override
    protected void craftItem() {
        if (level == null || level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(METAL_RECYCLER_LOOT);
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .create(LootContextParamSets.CHEST);
        List<ItemStack> loot = table.getRandomItems(params);
        if (loot.isEmpty()) return;
        boolean insertedAny = false;
        for (ItemStack output : loot) {
            if (output.isEmpty()) continue;
            for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (stack.isEmpty()) {
                    itemHandler.setStackInSlot(i, output.copy());
                    insertedAny = true;
                    break;
                }
                if (stack.getItem() == output.getItem() &&
                        stack.getCount() + output.getCount() <= stack.getMaxStackSize()) {
                    stack.grow(output.getCount());
                    itemHandler.setStackInSlot(i, stack);
                    insertedAny = true;
                    break;
                }
            }
        }
        if (insertedAny) itemHandler.extractItem(INPUT_SLOT, 1, false);
    }
}