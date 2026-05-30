package site.otools.Wasted.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import site.otools.Wasted.Config;
import site.otools.Wasted.item.ModItems;

public class ShopKeeperEntity extends AbstractVillager {

    public ShopKeeperEntity(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        offers.clear();

        for (Config.TradeEntry trade : Config.getParsedTrades()) {
            try {
                ItemStack costStack = new ItemStack(
                        net.minecraft.core.registries.BuiltInRegistries.ITEM
                                .get(net.minecraft.resources.ResourceLocation.parse(trade.costItem()))
                );
                ItemStack resultStack = new ItemStack(
                        net.minecraft.core.registries.BuiltInRegistries.ITEM
                                .get(net.minecraft.resources.ResourceLocation.parse(trade.resultItem())),
                        trade.resultAmount()
                );
                offers.add(new MerchantOffer(
                        new ItemCost(costStack.getItem(), trade.costAmount()),
                        resultStack,
                        trade.maxUses(),
                        trade.xp(),
                        0.0f
                ));
            } catch (Exception e) {
                // Nevalidēts trade config ieraksts — skip
            }
        }

        // Fallback ja config tukšs
        if (offers.isEmpty()) {
            offers.add(new MerchantOffer(
                    new ItemCost(ModItems.COIN.get(), 5),
                    new ItemStack(Items.DIAMOND),
                    10, 5, 0.0f
            ));
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            this.setTradingPlayer(player);
            this.openTradingScreen(player, Component.translatable("entity.wastedmod.shopkeeper"), 1);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public @Nullable AbstractVillager getBreedOffspring(
            net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.entity.AgeableMob mob) {
        return null; // NPC nevairojas
    }
    @Override
    public void rewardTradeXp(MerchantOffer offer) {
        // NPC nedod XP
    }
}