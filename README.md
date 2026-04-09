<!-- modrinth_exclude.start -->

# FTB Ultimine Legacy

A 1.7.10 backport of [FTB Ultimine](https://github.com/FTBTeam/FTB-Ultimine) by the FTB Team, designed for [GT New Horizons](https://www.gtnewhorizons.com/).

## Links

- [Original Mod - CurseForge](https://www.curseforge.com/minecraft/mc-mods/ftb-ultimine-forge)
- [Credits](https://github.com/FTBTeam/FTB-Ultimine)

---

<!-- modrinth_exclude.end -->

Allows you to break multiple blocks at once by holding a key. Hold the Ultimine key (default: `` ` `` grave/tilde), mine a block, and all connected matching blocks will break too. Scroll the mouse wheel while holding the key to cycle between mining shapes.

## Mining Shapes

- **Shapeless** - Flood-fill that breaks all connected blocks of the same type.
- **Small Tunnel** - 1-wide, 2-high tunnel in the direction you face.
- **Large Tunnel** - 3-wide, 3-high tunnel in the direction you face.
- **Small Square** - 3x3 area on the face you hit.
- **Mining Tunnel** - 3-wide, 2-high tunnel in the direction you face.
- **Escape Tunnel** - Staircase going upward in the direction you face.

## Right-Click Features

- **Hoe Tilling** - Right-click with a hoe to till multiple grass/dirt blocks into farmland.
- **Crop Harvesting** - Right-click mature vanilla crops to harvest and replant them in bulk.

## Configuration

All settings are in `config/ftbultimine.cfg`:

- `maxBlocks` - Maximum blocks per operation (default: 64)
- `exhaustionPerBlock` - Hunger cost per extra block (default: 20)
- `preventToolBreak` - Stop ultimining when tool durability is low (default: true)
- `cancelOnBlockBreakFail` - Stop if a block can't be broken, e.g. protected area (default: true)
- `requireTool` - Require a damageable tool to ultimine (default: false)
- `ultimineCooldown` - Cooldown in ticks between uses (default: 0)
- `mergeTagsShapeless` - Treat blocks sharing Ore Dictionary entries as the same in shapeless mode (default: true)
- `renderOutlineMax` - Max blocks to show in the preview outline (default: 256)

## Changes from the Original Mod

This backport targets Minecraft 1.7.10 with Forge 10.13.4.1614. The following changes have been made compared to the original FTB Ultimine:

### Removed Features

- **FTB Library Dependency** - Not required; the mod is fully standalone with no library dependencies.
- **Architectury Dependency** - Not required; replaced with direct Forge API calls.
- **Data-Driven Tags** - Item and block tags (excluded tools, block whitelist, etc.) are not available. Block matching uses Ore Dictionary instead.
- **Radial Shape Menu** - The shape selection wheel GUI is not implemented; shapes are cycled via scroll wheel with the name shown in chat.
- **FTB Ranks Integration** - Permission node overrides for max blocks and cooldown are not available.
- **Modder API** - The plugin system for custom right-click handlers, crop types, restriction handlers, shapes, block-break handlers, and block-selection handlers is not implemented.
- **Custom Attributes** - Attribute modifiers for max blocks, cooldown, exhaustion, and experience are not available.
- **Axe Stripping** - Multi-block log stripping did not exist in 1.7.10.
- **Shovel Path Creation** - Dirt paths did not exist in 1.7.10.
- **SNBT Config System** - Replaced with standard Forge Configuration file.
- **Ultiminer Item** - The ultiminer tool item is not included.

### Changed Features

- **Block Matching** - Uses Ore Dictionary lookups instead of data-driven block tags. Blocks sharing any Ore Dictionary entry are treated as equivalent in shapeless mode.
- **Harvest Check** - Each block is checked with `ForgeHooks.canHarvestBlock` before breaking, so blocks requiring a higher-tier tool are skipped rather than broken without drops.
- **Shape Cycling** - Scroll wheel while holding the Ultimine key cycles shapes and cancels the vanilla hotbar scroll. Shape name is displayed via chat message.
- **Crop Harvesting** - Supports vanilla crops only (blocks extending `BlockCrops` with max metadata 7). AgriCraft and other custom crop integrations are not included.
- **Networking** - Uses Forge `SimpleNetworkWrapper` instead of Architectury's `SimpleNetworkManager`.
- **Outline Renderer** - Uses GL immediate mode line drawing instead of modern `VoxelShape`-based rendering.

### Technical Details

- Built with RetroFuturaGradle for GTNH compatibility.
- Uses `ForgeDirection` and raw `(x, y, z)` coordinates instead of `BlockPos` (not available in 1.7.10).
- Keybinding localization uses `assets/ftbultimine/lang/en_US.lang` instead of JSON.
- Scroll wheel interception uses Forge's cancellable `MouseEvent` at `HIGHEST` priority to prevent hotbar switching.
- Block breaking fires `BlockEvent.BreakEvent` for each extra block, respecting protection mods and other event listeners.

## Licence

All Rights Reserved to Feed The Beast Ltd. Source code is `visible source`, please see our [LICENSE.md](/LICENSE.md) for more information. Any Pull Requests made to this mod must have the CLA (Contributor Licence Agreement) signed and agreed to before the request will be considered.
