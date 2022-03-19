# MultiWorldPets
### Never leave your good bois behind again.

MWP is a simple, lightweight plugin that allows you to keep track of your pets and take them with you to other worlds. All Tamable entities are compatible.

### Commands
- `/mwp [player]` List all your pets, or pets of optional player.
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

The teleport system uses an algorithm that finds a random location nearby to teleport each pet to (so that they don't all get stuck together with you in your hitbox).

First, it will choose a random X and Z coordinate nearby, then it will scan for the nearest solid ground above or below the player, with at least two blocks of open air above, and no adjacent dangerous blocks. It will repeat this process until a safe location is found, or *maxTries* is exceeded.