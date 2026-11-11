# RFToolsUtility

General RFTools utilities for Minecraft 26.3 on Fabric. Requires Java 25,
Fabric API, Forge Config API Port, McJtyLib, and RFToolsBase. Team Reborn Energy
is included in the release jar for native energy transfers.

Place this checkout at `RFToolsUtility_fabric/` beside `Multi_fabric/`,
`McJtyLib_fabric/`, and `RFToolsBase_fabric/`. The workspace discovers it as
`:RFToolsUtility`; dependency helpers and Client/Server development runs use
included local source projects.

From `Multi_fabric/`:

```sh
./gradlew :RFToolsUtility:build :workspace:classes
./gradlew :RFToolsUtility:runDatagen
./gradlew -PutilityPortTests :RFToolsUtility:runClientGameTest
./gradlew :workspace:runClient
```

Build standalone with a Gradle user home isolated to this Minecraft version:

```sh
./gradlew --gradle-user-home ../Multi_fabric/.gradle-user-home build
```

Standalone settings also prefer sibling McJtyLib, The One Probe, and RFToolsBase
checkouts. Override their locations with `-Pmod.<Name>=<path>`. Without local
sources, dependency helpers resolve Fabric artifacts from Maven/Jenkins.
The shared Gradle script is loaded from `-Pgradletools_path`, the workspace root,
a sibling `Multi_fabric`, a local script, or the matching remote Fabric branch.
Generated resources belong in `src/generated/resources/`.

The opt-in client test checks loader initialization, native tank and porter
transactions (including rollback), screen module installation, recipe loading,
a synchronized tank menu, and sneak-right-clicking the advanced charged porter
with a saved destination and rendering its GUI. Its fixture is excluded from release jars.

Addons can declare a `rftoolsutility` entrypoint implementing
`mcjty.rftoolsutility.api.RFToolsUtilityPlugin` to access the teleportation manager
and screen module registry after the built-in modules are registered. Dimension
addons can expose `IDimensionManager` through `rftoolsutility:dimension_manager`.
The One Probe uses its native `theoneprobe` plugin entrypoint.

Before release, check screen and tablet rendering and clicks, tank bucket
interaction, charged porters and cross-dimension teleportation, crafter recipes
and remainders, redstone channels, spawners, and environmental effects in Minecraft.
