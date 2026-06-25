# Redstone Signal Readout (Fabric, MC 1.21.11)

Look at a block to see its redstone signal (0-15) in the action bar:
- Redstone wire -> its power level (works everywhere, incl. multiplayer)
- Comparator    -> its OUTPUT signal (after compare/subtract)  [singleplayer]
- Container     -> the comparator signal it would emit          [singleplayer]

Comparator output and container fill aren't sent to the client, so those two
read the integrated server and therefore only work in singleplayer. Wire power
is synced and works anywhere.

## Build (needs internet for dependencies)
GitHub Actions: push to a repo; the workflow builds and uploads the jar under
the run's Artifacts. Or locally: `gradle wrapper` then `./gradlew build`.

Before first build, confirm loader_version / loom_version in gradle.properties
against https://fabricmc.net/develop/ . MC 1.21.11 + yarn 1.21.11+build.4 verified.

## Install
Drop the jar in .minecraft/mods/ with Fabric Loader 0.16+. Client-side only.
