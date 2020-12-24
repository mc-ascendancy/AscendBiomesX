# Ascend Biomes
Ascend Biomes - a plugin allowing custom configuration of biomes developed by [Froogo](https://froogo.co.uk) for [Ascendancy Project](https://ascendancyproject.com).

### Index
- [Specification](#specification)
    - [Regionalized crop growth](#regionalized-crop-growth-issue-3issues3)
    - [Regionalized mob growth](#regionalized-mob-growth-issue-4issues4)
    - [Regionalized status effects](#regionalized-status-effects-issue-5issues5)
- [Implementation](#implementation)
    - [Regionalized crop growth](#regionalized-crop-growth)
    - [Regionalized mob growth](#regionalized-mob-growth)
    - [Regionalized status effects](#regionalized-status-effects)
- [Configuration file](#configuration-file)
    - [Default crop/mob growth](#default-cropmob-growth)
    - [Mob growth tickrate](#mob-growth-tickrate)
    - [Custom biomes](#custom-biomes)
- [Help](#help)
    - [Contact](#contact)

## Specification
### Regionalized crop growth [(issue #3)](../../issues/3)
- All edible crops should have customizable growth rates. In other words, farming should be easier in some biomes rather than others.
- This custom growth rate should impact the following crops: wheat, beetroot, carrot, potato, melon, pumpkin, cocoa beans, sweet berries, and nether wart.
    - The previous developer claimed that sugar cane and chorus fruit work differently than other crops. It would be nice, though not necessary, if sugar cane and chorus fruit were impacted by the custom growth rates.
- It would be fantastic if there was not just a standard growth rate for all the crops listed above per-biome, but instead maybe carrots grow extremely fast in "Biome A" but potatoes practically don't grow at all in "Biome A."

### Regionalized mob growth [(issue #4)](../../issues/4)
- All passive baby farm animals should have customizable growth rates. Just like before, farming should be easier in some biomes rather than others-- not applying this same rule to passive baby farm animals would make crop farming irrelevant.
- This custom growth rate (for passive baby farm animals) should impact the following mobs: cows, chickens, pigs, sheep, mooshroom, rabbits, and polar bears.
- Specifically, the time it takes for passive baby farm animals from being born to growing into adulthood should be nerfed / buffed depending on the biome.
- Just like the third suggestion under Regionalized Crop Growth, it would be fantastic if there was not just a standard growth rate for all passive baby farm animals, but instead maybe cows grow up extremely fast in "Biome A" but sheep practically don't grow up at all in "Biome A."

### Regionalized status effects [(issue #5)](../../issues/5)
- All biomes should have the potential to be impacted by a persistent status effect (or multiple status effects). All of the status effects listed here we should have control over: https://minecraft.gamepedia.com/Status_effect.
- We should also be able to indicate the strength / "modifier" of status effects. For example, maybe two different biomes both have "Slowness" as a persistent status effect... but "Biome A" just has the default amplifier of "Slowness" whereas "Biome B" has a stronger level (higher amplification) of "Slowness." In this case, players in “Biome B” would move slower than players in “Biome A” even though both biomes are impacted by a persistent regional status effect.
- The player should not be able to rid themselves in any way of these persistent status effects. Drinking milk should still function, even in biomes with regionalized status effects, but it cannot impact the persistent status effects prescribed to that biome.
    - This is important. If a player with a PvP playstyle is using potions for combat, we want to make sure milk still takes away player-caused status effects… we just want to make sure that players cannot avoid the regionalized persistent status effects of particular biomes.
    
## Implementation
### Regionalized crop growth
To increase the fidelity of growth, all [growth modifiers in the server's spigot.yml](https://www.spigotmc.org/wiki/spigot-configuration/#per-world-settings)
must be increased to 800% (this is what I concluded to be the maximum value after testing).
This allows us to now not have 7 as the maximum age, but 56, significantly increasing the control we have.
This age is not stored in the standard [Ageable NBT data](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/Ageable.html),
but in our plugin's metadata, as the Ageable is displayed to the client, and has strict maximum values.

For standard crops (e.g. wheat, carrots, and beetroots), we can now listen on the 7x more frequent [BlockGrowEvent](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/block/BlockGrowEvent.html).
Then, every time this event is procced, increment our metadata age.
The Ageable age will also be updated to reflect our metadata age, with the maximum age determined by the
[default crop growth](#default-cropmob-growth) and [custom biome](#custom-biomes).
The maximum age, for example, would be 56 at 100%, 28 at 200%, or 112 at 50%.
This solution allows us to entirely depend on Minecraft's existing growth randomness,
keeping it the same vanilla, while allowing us to increase the fidelity to support different growth rates.

For non-standard crops (e.g. sugar cane, melons, vines) which do not have an age,
an alternative (similar) solution is used.
The same metadata trick is used, however, it instead stores the amount of times it has attempted to spread,
only allowing it to spread after the amount of times specified in the config (e.g. 8 for 100%, 4 for 200%, or 16 for 50%).
This, like with standard crops, introduces no randomness on the plugin's end, keeping timings similar to vanilla.

TODO: explain chorus plants solution after [issue #8](../../issues/8) is resolved.

### Regionalized mob growth
As there is no event triggered on an entity's growth, a scheduled ticker has been used.
This ticker's frequency is defined by the [mob growth tickrate](#mob-growth-tickrate) field in the configuration file.
Every time this ticker is procced, all ageable entities will have their ages updated.
Their age can be compensated via de-aging or aging them depending on what the
[default mob growth](#default-cropmob-growth) and [custom biome](#custom-biomes) defines.

Notice that this means an animal constantly switching between biomes, does not have their age interpolated,
but simply takes into effect the biome they are in on the update.
This is a trade-off I decided was work making due to the performance implications of keeping track of
every ageable entity in their move event, despite being less accurate.

### Regionalized status effects
On every [PlayerMoveEvent](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/player/PlayerMoveEvent.html)
and [PlayerTeleportEvent](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/player/PlayerTeleportEvent.html)
(see [issue #9](../../issues/9)), it is checked whether a player has changed biomes.
If they have changed biomes, all status effects from the previous biome will be stripped,
then the new effects will be applied with an effectively infinite duration (32-bit signed integer maximum).

On every [EntityPotionEffectEvent](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityPotionEffectEvent.html)
(which is called for every individual effect in the case of a mass removal via milk),
all events removing a persistent biome effect will be cancelled.
This means that a player drinking milk will have all effects except persistent biome effects removed.

## Configuration file
### Default crop/mob growth


### Mob growth tickrate


### Custom biomes


## Help
### Contact
