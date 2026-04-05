package site.otools.Wasted.block;

import site.otools.Wasted.block.custom.GlassRecyclerBlock;
import site.otools.Wasted.block.custom.MetalRecyclerBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.otools.Wasted.WastedMod;
import site.otools.Wasted.block.custom.RecyclerBlock;
import site.otools.Wasted.block.custom.TrashbagBlock;
import site.otools.Wasted.item.ModItems;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS=
            DeferredRegister.createBlocks(WastedMod.MOD_ID);

    public static final DeferredBlock<Block> TRASHBAG = registerBlock("trashbag", ()-> new TrashbagBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredBlock<Block> RECYCLER = registerBlock("recycler", ()-> new RecyclerBlock(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<Block> GLASS_RECYCLER = registerBlock("glass_recycler", () -> new GlassRecyclerBlock(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<Block> METAL_RECYCLER = registerBlock("metal_recycler", () -> new MetalRecyclerBlock(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<Block> TRASHBAG_V1 = BLOCKS.register("trashbag_v1",
            () -> new TrashbagBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.5f)
                    .sound(SoundType.WOOL)
                    .noOcclusion()));

    public static final DeferredBlock<Block> TRASHBAG_V2 = BLOCKS.register("trashbag_v2",
            () -> new TrashbagBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.5f)
                    .sound(SoundType.WOOL)
                    .noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
