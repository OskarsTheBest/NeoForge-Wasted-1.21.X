# Wasted

Wasted is a Minecraft Java Edition 1.21.8 mod created with NeoForge. The goal of the mod is to add a waste collection and recycling system to Minecraft.

The mod adds new trash-related items, trash bags, recycler blocks, a recycling GUI, coins, and additional gameplay mechanics. Players can find trash in the world, collect it, recycle it in special recycler blocks, and receive useful resources or coins as a reward.

## Main features

* New trash items such as plastic, metal scraps, glass shards and trash bags.
* Trash objects that can appear in the Minecraft world.
* Recycler blocks for processing collected waste.
* Specialized recyclers for different material types.
* Mega recycler for advanced recycling.
* Recycler GUI with input, output and progress display.
* Coin item used as an in-game currency.
* Planned shop and additional gameplay mechanics.

## Requirements

* Minecraft Java Edition 1.21.8
* NeoForge 21.8
* Java 21
* Gradle 9.2.1

## Installation information

To run or develop the mod:

1. Clone or download this repository.
2. Open the project in IntelliJ IDEA or Eclipse.
3. Make sure Java 21 is installed and selected as the project SDK.
4. Let Gradle import and download all required dependencies.
5. To build the mod, run:

```bash
./gradlew build
```

On Windows, use:

```bash
gradlew.bat build
```

If some libraries are missing or the project does not load correctly, run:

```bash
./gradlew --refresh-dependencies
```

or on Windows:

```bash
gradlew.bat --refresh-dependencies
```

The compiled mod file will be created in the `build/libs` folder.

## Additional resources

* NeoForge documentation: https://docs.neoforged.net/
* NeoForged Discord: https://discord.neoforged.net/
