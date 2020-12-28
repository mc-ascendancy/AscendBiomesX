# AscendBiomes
AscendBiomes - a plugin allowing custom configuration of biomes developed by [Froogo](https://froogo.co.uk) for [the Ascendancy Project](https://ascendancyproject.com).

### Index
- [Specification](#specification)
    - [Regionalized crop growth](#regionalized-crop-growth-issue-3)
    - [Regionalized mob growth](#regionalized-mob-growth-issue-4)
    - [Regionalized status effects](#regionalized-status-effects-issue-5)
- [Implementation](#implementation)
    - [Regionalized crop growth](#regionalized-crop-growth)
    - [Regionalized mob growth](#regionalized-mob-growth)
    - [Regionalized status effects](#regionalized-status-effects)
- [Setup](#setup)
- [Configuration file](#configuration-file)
    - [Default crop/mob growth](#default-cropmob-growth)
    - [Mob growth tickrate](#mob-growth-tickrate)
    - [Custom biomes](#custom-biomes)
      - [Crop/mob growth rate](#cropmob-growth-rate)
      - [Status effects](#status-effects)
      - [Inheritance](#inheritance)
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

Chorus plants... Oh, chorus plants...
For whatever reason, they are not able to have their growth rates modified in `spigot.yml` like **every** other spreading block.
This means their growth rate can only be decreased, not only that, but only to rates of `1/x {0 < x} where x is an integer`.
So for example, it could be slowed to: `50%, 33%, 25%, 20%, etc.`.
Although, according to Connor, this should be fine for this specific use case, as they will only ever need to be slowed.

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

## Setup

To set up this plugin, download the plugin from [the latest release](../../releases/latest) and move it to your plugins folder.
You can then configure the automatically generating `biomes.json` file with assistance from the [configuration file](#configuration-file) help.

You must also up the growth rate modifiers to 800% for everything available in the server's `spigot.yml` file.

For example, the `spigot.yml` should now contain this:
```yaml
    growth:
      cactus-modifier: 800
      cane-modifier: 800
      melon-modifier: 800
      mushroom-modifier: 800
      pumpkin-modifier: 800
      sapling-modifier: 800
      beetroot-modifier: 800
      carrot-modifier: 800
      potato-modifier: 800
      wheat-modifier: 800
      netherwart-modifier: 800
      vine-modifier: 800
      cocoa-modifier: 800
      bamboo-modifier: 800
      sweetberry-modifier: 800
      kelp-modifier: 800
```

## Configuration file
If you are ever confused about how to format the configuration file,
you can check out the [sample configuration file](src/main/resources/biomes.json).

### Default crop/mob growth
The `defaultMobGrowthRate` and `defaultCropGrowthRate` fields define growth rates for mobs/crops which do not
have a [custom biome](#custom-biomes) or are not described by their custom biome.
These fields are both JSON maps with the key being a string, and the value being a float (allowing for decimal point values).

For example, if we wanted cows in every biome which doesn't have a custom growth rate,
to grow at 200%, we would do this:
```json
{
  "defaultMobGrowthRate": {
    "COW": 200
  }
}
```

Or if we wanted all wheat to grow at one third of its regular rate, we could do this:
```json
{
  "defaultCropGrowthRate": {
    "WHEAT": 33.333
  }
}
```

### Mob growth tickrate
This is how many ticks the mob growth updater will wait before doing its check [as described in regionalized mob growth](#regionalized-mob-growth).
This generally should not be lowered below 100 ticks (every 5 seconds), which is the default.
If you are noticing a significant performance impact due to this check, you could increase the wait.
However, I would strongly advise **NOT** going above 600 ticks (every 30 seconds),
as this may make it noticeably inaccurate.

Example:
```json
{
  "mobGrowthTickRate": 100
}
```

### Custom biomes
The `customBiomes` is a map with the key being the name of the biome, and the value being custom traits of that biome.

#### Crop/mob growth rate
To affect the growth rate of crops/mobs in a specific biome, specify the `cropGrowthRates` or `mobGrowthRates` respectively.
These fields are both maps with the key being the name of the crop/mob, and the value being the growth rate as a percentage.

For example, if I wanted wheat to grow twice as fast in a plains biome, I would do this:
```json
{
  "customBiomes": {
    "PLAINS": {
      "cropGrowthRates": {
        "WHEAT": 200
      }
    }
  }
}
```

Notice that you can also leave values blank.

#### Status effects
The `statusEffects` field allows you to apply status effects to players while they are inside a specific biome.
This field is an array of status effect, where status effect **MUST** contain the `effect` value,
and **CAN** optionally contain the amplifier value.
If the amplifier value is not specified, it is assumed to be zero (e.g. level one).

For example, if I wanted forests to give me jump boost two, and speed one, I would do this:
```json
{
  "customBiomes": {
    "FOREST": {
      "statusEffects": [
        {
          "effect": "JUMP",
          "amplifier": 1
        },
        {
          "effect": "SPEED"
        }
      ]
    }
  }
}
```

#### Inheritance
As [issue #2](../../issues/2) described, you may inherit values from another custom biome using the `inherit` field.
This can be useful if you utilise DESERT and DESERT_HILLS biomes, but want them to be identical, or similar.

Values from the `cropGrowthRates` or `mobGrowthRates` maps will be merged, with the child getting the final say.
The `statusEffects` array will just be the child's value if it exists, otherwise, the parent's value.

For example, if I wanted DESERT_HILLS to be the same as DESERT,
except DESERT_HILLS has none of the status effects,
and DESERT_HILLS had a slightly faster cactus growth rate, I'd do this:

```json
{
  "customBiomes": {
    "DESERT": {
      "mobGrowthRate": {
        "LLAMA": 200
      },
      "cropGrowthRates": {
        "CACTUS": 150
      },
      "statusEffects": [
        {
          "effect": "REGENERATION"
        }
      ]
    },
    "DESERT_HILLS": {
      "cropGrowthRates": {
        "CACTUS": 175
      },
      "statusEffects": [],
      "inherit": "DESERT"
    }
  }
}
```

## Help
The simplest way to receive help is to open an issue on this repository, as I should receive an email.
However, if it is urgent, use the contact information below.

### Contact
Discord: Froogo#5239  
Email: harry@froogo.co.uk
