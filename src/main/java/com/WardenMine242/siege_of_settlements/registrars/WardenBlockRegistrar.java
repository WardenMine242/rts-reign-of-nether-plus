package com.WardenMine242.siege_of_settlements.registrars;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.registrars.BlockRegistrar;
import com.solegendary.siege_of_settlements.registrars.ItemRegistrar;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.function.Supplier;

public class WardenBlockRegistrar {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SiegeOfSettlements.MOD_ID);

    public static final RegistryObject<Block> STONE_PILLAR = registerBlock("stone_pillar", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STONE)), CreativeModeTabs.BUILDING_BLOCKS);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, ResourceKey<CreativeModeTab> tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);

        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            ResourceKey<CreativeModeTab> tab) {
        return ItemRegistrar.ITEMS.register(name, () -> {
            BlockItem blockItem = new BlockItem(block.get(), new Item.Properties());
            BlockRegistrar.blockItems.computeIfAbsent(tab, (k) -> new ArrayList<>()).add(blockItem);
            return blockItem;
        });
    }

    public static void init(FMLJavaModLoadingContext context) {
        BLOCKS.register(context.getModEventBus());
    }
}
