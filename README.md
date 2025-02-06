# MultiWorldPets
### Never leave your good bois behind again.

MWP is a simple, lightweight plugin that allows you to keep track of your pets and take them with you to other worlds (works with [Multiverse](https://modrinth.com/plugin/multiverse-core) and other multi-world systems.) All Tamable entities are compatible.

**NEW:** `/mwp` now shows your pet's health, so you can check up on them without the guesswork!

### Commands
- `/mwp [player]` List all your pets, or pets of another player.
- `/tppet <name>` Teleport your pet nearby.

### Config Settings
- `debug` Enable debug info. *(Default: false)*
- `spawnRadius` Max radius around the player to teleport pets. *(Default: 10)*
- `maxTries` Max tries to find a safe location. *(Default: 25)*
- `unsafeBlocks` Blocks considered unsafe. *(Default: LAVA, FIRE, CACTUS)*

### Notes About Teleportation
Your tamed pets will be teleported along with you under the following conditions:
- You have the permission `multiworldpets.use` *(Default: Everyone)*
- Your pet is named, or you only have one pet of a given species.
- Your pet is not currently sitting or leashed with a lead.
- There is a safe location to teleport to in the vicinity.

The teleport system uses an algorithm that finds a random location nearby to teleport each pet to (so that they don't all get stuck together with you in your hitbox.)

## See Also
[Elevators Plugin](https://github.com/Pecacheu/Elevators-v2) - *Because who said mineshafts were the only type of shaft in Minecraft?*

[ForestFire Server](https://forestfire.net) - *Come have fun on my Minecraft server!*

[TabPlus Extension for Chrome](http://chrome.google.com/webstore/detail/tabplus/hfcdmjginkilbcfeffkkggemafdjflhp) - *Save the headache and manage your tabs*