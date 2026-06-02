package site.otools.Wasted.compat.jei;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public final class LootJsonReader {
    private LootJsonReader() {}

    public static List<RecyclerDisplay.Output> read(String type) {
        String path = "/data/wastedmod/loot_table/blocks/recycler/" + type + ".json";
        List<RecyclerDisplay.Output> out = new ArrayList<>();
        try (InputStream in = LootJsonReader.class.getResourceAsStream(path)) {
            if (in == null) return out;
            JsonObject root = JsonParser.parseReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray pools = GsonHelper.getAsJsonArray(root, "pools", new JsonArray());
            for (JsonElement poolEl : pools) {
                JsonObject pool = poolEl.getAsJsonObject();
                float poolChance = poolChance(pool);
                JsonArray entries = GsonHelper.getAsJsonArray(pool, "entries", new JsonArray());
                int totalWeight = 0;
                for (JsonElement e : entries) {
                    totalWeight += GsonHelper.getAsInt(e.getAsJsonObject(), "weight", 1);
                }
                if (totalWeight <= 0) continue;
                for (JsonElement e : entries) {
                    JsonObject entry = e.getAsJsonObject();
                    if (!"minecraft:item".equals(GsonHelper.getAsString(entry, "type", ""))) continue;
                    String name = GsonHelper.getAsString(entry, "name", "");
                    if (name.isEmpty()) continue;
                    Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(name));
                    int weight = GsonHelper.getAsInt(entry, "weight", 1);
                    float chance = poolChance * weight / totalWeight * 100f;
                    int count = maxCount(entry);
                    out.add(new RecyclerDisplay.Output(new ItemStack(item, Math.max(1, count)), chance));
                }
            }
        } catch (Exception ignored) {

        }
        return out;
    }

    private static float poolChance(JsonObject pool) {
        if (!pool.has("conditions")) return 1f;
        for (JsonElement c : pool.getAsJsonArray("conditions")) {
            JsonObject cond = c.getAsJsonObject();
            if ("minecraft:random_chance".equals(GsonHelper.getAsString(cond, "condition", ""))) {
                return GsonHelper.getAsFloat(cond, "chance", 1f);
            }
        }
        return 1f;
    }

    private static int maxCount(JsonObject entry) {
        if (!entry.has("functions")) return 1;
        for (JsonElement f : entry.getAsJsonArray("functions")) {
            JsonObject fn = f.getAsJsonObject();
            if (!"minecraft:set_count".equals(GsonHelper.getAsString(fn, "function", ""))) continue;
            JsonElement count = fn.get("count");
            if (count == null) return 1;
            if (count.isJsonObject()) return GsonHelper.getAsInt(count.getAsJsonObject(), "max", 1);
            if (count.isJsonPrimitive()) return count.getAsInt();
        }
        return 1;
    }
}
