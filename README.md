# RFToolsUtility

General RFTools utilities for Minecraft 26.1.2 on NeoForge. Requires Java 25, McJtyLib, and RFToolsBase.

Place this checkout at `RFToolsUtility_neo/` beside `Multi_neo/`, `McJtyLib_neo/`, and `RFToolsBase_neo/`.
The multiworkspace discovers it as `:RFToolsUtility`; dependency helpers and development runs use included local sources.

From `Multi_neo/`:

```sh
./gradlew :RFToolsUtility:build
./gradlew :RFToolsUtility:runClientData
./gradlew :workspace:runClient
```

From this checkout, build standalone with an isolated Gradle user home for this Minecraft version and loader:

```sh
./gradlew --gradle-user-home ../Multi_neo/.gradle-user-home build
```

Standalone builds resolve published McJtyLib and RFToolsBase artifacts from McJty's Maven/Jenkins repository.
The build uses the target's shared Gradle script from the workspace, a sibling `Multi_neo/`, a parent checkout, or the matching remote branch.
Generated resources belong in `src/generated/resources/`.

Before release, check screen and tablet rendering and clicks, tank fluids, charged porters and cross-dimension teleportation,
crafter recipes and remainders, redstone channels, spawners, and environmental effects in Minecraft.
