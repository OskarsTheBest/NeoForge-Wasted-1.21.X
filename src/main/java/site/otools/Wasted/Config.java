package site.otools.Wasted;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue TRASH_MOUNTAIN_SPAWN_CHANCE = BUILDER
            .comment("Per-chunk chance for a Trash Mountain to spawn in a swamp biome.",
                    "Range: 0.0 (never) to 1.0 (every chunk). Default: 0.013 (~1 in 75 chunks).")
            .defineInRange("trashMountainSpawnChance", 0.013, 0.0, 1.0);

    public static final ModConfigSpec.IntValue TRASH_MOUNTAIN_MIN_SWAMP_NEIGHBORS = BUILDER
            .comment("How many of the 8 surrounding sample points (16 blocks out) must also be swamp biome",
                    "before a Trash Mountain is allowed to spawn. Higher = needs a bigger swamp.",
                    "Range: 0 (no size check) to 8 (must be surrounded by swamp). Default: 6.")
            .defineInRange("trashMountainMinSwampNeighbors", 6, 0, 8);

    // --- Trash piles inside vanilla structures ---
    // Each value is the chance, per chunk that overlaps the structure, to drop a cluster of trashbags.
    // A structure spans many chunks, so higher values = trash in more parts of the structure.

    public static final ModConfigSpec.DoubleValue TRASH_PILE_MINESHAFT = BUILDER
            .comment("Per-overlapping-chunk chance for a trashbag cluster in mineshafts. Default: 0.25.")
            .defineInRange("trashPileMineshaftChance", 0.25, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASH_PILE_STRONGHOLD = BUILDER
            .comment("Per-overlapping-chunk chance for a trashbag cluster in strongholds. Default: 0.10.")
            .defineInRange("trashPileStrongholdChance", 0.10, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASH_PILE_TRAIL_RUINS = BUILDER
            .comment("Per-overlapping-chunk chance for a trashbag cluster in trail ruins. Default: 0.25.")
            .defineInRange("trashPileTrailRuinsChance", 0.25, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASH_PILE_JUNGLE_TEMPLE = BUILDER
            .comment("Per-overlapping-chunk chance for an EXTRA trashbag cluster in jungle temples.",
                    "Jungle temples also always get at least one cluster in their origin chunk. Default: 0.25.")
            .defineInRange("trashPileJungleTempleChance", 0.25, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASH_PILE_VILLAGE = BUILDER
            .comment("Per-overlapping-chunk chance for a trashbag cluster in villages (incl. zombie villages). Default: 0.25.")
            .defineInRange("trashPileVillageChance", 0.25, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASH_PILE_BASTION = BUILDER
            .comment("Per-overlapping-chunk chance for a trashbag cluster in bastion remnants. Default: 0.10.")
            .defineInRange("trashPileBastionChance", 0.10, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASH_PILE_OCEAN_RUINS = BUILDER
            .comment("Per-overlapping-chunk chance for a trashbag cluster in ocean ruins. Default: 0.20.")
            .defineInRange("trashPileOceanRuinsChance", 0.20, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASH_HEAP_SPAWN_CHANCE = BUILDER
            .comment("Per-chunk chance for a little dirt-pile-with-trashbags to spawn on the overworld surface",
                    "(dry land only, never in ocean/water). Default: 0.02 (~1 in 50 chunks).")
            .defineInRange("trashHeapSpawnChance", 0.02, 0.0, 1.0);

    // --- Trashbag break drops ---
    // When a trashbag is broken it always drops one item: either TRASH (the "best" drop) or one
    // of glasshatter/metal/plastic. These set the chance of getting TRASH; the rest is split evenly.

    public static final ModConfigSpec.DoubleValue TRASHBAG_TRASH_CHANCE = BUILDER
            .comment("Chance to drop the TRASH item when breaking a trashbag by hand.",
                    "Otherwise drops glasshatter/metal/plastic evenly. Default: 0.25.")
            .defineInRange("trashbagTrashChance", 0.25, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue TRASHBAG_TRASH_CHANCE_GRABBER = BUILDER
            .comment("Chance to drop the TRASH item when breaking a trashbag with the Trash Grabber.",
                    "Otherwise drops glasshatter/metal/plastic evenly. Default: 0.50.")
            .defineInRange("trashbagTrashChanceWithGrabber", 0.50, 0.0, 1.0);

    public static final ModConfigSpec.BooleanValue POLLUTION_ENABLED = BUILDER
            .comment("If true, throwing Trash or Trashbag items into water pollutes it.")
            .define("pollutionEnabled", true);

    public static final ModConfigSpec.IntValue POLLUTION_SPREAD_DELAY = BUILDER
            .comment("Ticks between each pollution spread step. Higher = slower spread (20 ticks = 1 second).",
                    "Default: 40 (one new block every ~2 seconds per frontier block).")
            .defineInRange("pollutionSpreadDelayTicks", 40, 1, 24000);

    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> SHOP_TRADES = BUILDER
            .comment("List of ShopKeeper trades.",
                    "Format: \"costItem,costAmount,resultItem,resultAmount,maxUses,xp\"",
                    "Example: \"wastedmod:coin,5,minecraft:diamond,1,10,5\"")
            .defineListAllowEmpty("shopTrades", java.util.List.of(
                    "wastedmod:coin,5,minecraft:diamond,1,10,5",
                    "wastedmod:coin,2,minecraft:emerald,1,15,3"
            ), s -> s instanceof String str && str.split(",").length == 6);

    public record TradeEntry(String costItem, int costAmount, String resultItem, int resultAmount, int maxUses, int xp) {}

    public static java.util.List<TradeEntry> getParsedTrades() {
        java.util.List<TradeEntry> result = new java.util.ArrayList<>();
        for (String entry : SHOP_TRADES.get()) {
            String[] p = entry.split(",");
            result.add(new TradeEntry(
                    p[0].trim(), Integer.parseInt(p[1].trim()),
                    p[2].trim(), Integer.parseInt(p[3].trim()),
                    Integer.parseInt(p[4].trim()), Integer.parseInt(p[5].trim())
            ));
        }
        return result;
    }
    static final ModConfigSpec SPEC = BUILDER.build();
}
