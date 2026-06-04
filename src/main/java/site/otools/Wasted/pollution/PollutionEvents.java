package site.otools.Wasted.pollution;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import site.otools.Wasted.WastedMod;

@EventBusSubscriber(modid = WastedMod.MOD_ID)
public class PollutionEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity item)) return;
        if (item.level().isClientSide) return;
        if (!item.isInWaterOrBubble()) return;
        if (!PollutionLogic.isPollutingItemStack(item.getItem())) return;

        if (PollutionLogic.seedPollution((ServerLevel) item.level(), item.blockPosition())) {
            item.discard();
        }
    }
}
