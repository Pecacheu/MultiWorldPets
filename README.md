# MultiWorldPets
### Never leave your good bois behind again.

MWP is a simple, lightweight plugin that allows you to keep track of your pets and take them with you to other worlds. All Tamable entities are compatible.

**NEW:** `/mwp` now shows your pet's health, so you can check up on them with no more guesswork!

Find the plugin on [Modrinth](https://modrinth.com/plugin/multiworldpets), or see [GitHub](https://github.com/Pecacheu/MultiWorldPets) for the latest updates.

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
- Your pet has been named, or you only have one pet of a given species.
- There is a safe location to teleport to in the vicinity.

The teleport system uses an algorithm that finds a random location nearby to teleport each pet to (so that they don't all get stuck together with you in your hitbox.)

## See Also
[Elevators Plugin](https://github.com/Pecacheu/Elevators-v2) - *Because who said mineshafts were the only type of shaft in Minecraft?*

[ForestFire Server](https://forestfire.net) - *Come have fun on my Minecraft server!*

[TabPlus Extension for Chrome](http://chrome.google.com/webstore/detail/tabplus/hfcdmjginkilbcfeffkkggemafdjflhp) - *Save the headache and manage your tabs*