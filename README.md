[![Downloads](http://cf.way2muchnoise.eu/1457036.svg)](https://www.curseforge.com/minecraft/mc-mods/ae2-toggleable-view-cells)
[![MCVersion](http://cf.way2muchnoise.eu/versions/1457036.svg)](https://www.curseforge.com/minecraft/mc-mods/ae2-toggleable-view-cells)
[![GitHub issues](https://img.shields.io/github/issues/NuanKi/AE2ToggleableViewCells.svg)](https://github.com/NuanKi/AE2ToggleableViewCells/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/NuanKi/AE2ToggleableViewCells.svg)](https://github.com/NuanKi/AE2ToggleableViewCells/pulls)

# AE2 Toggleable View Cells

Adds an **on/off toggle** to Applied Energistics 2 View Cells so you can temporarily disable their filtering without removing them or clearing their configuration.

## CurseForge
https://www.curseforge.com/minecraft/mc-mods/ae2-toggleable-view-cells

## Features
- Toggle AE2 View Cell filtering **Enabled / Disabled**
- **Two toggle methods**
  - **Right-click in hand** to toggle
  - **Right-click in the View Cell slot** (in terminals) to toggle in-place (no need to take it out)
- Disabled View Cells are **ignored** by the AE2 view-cell filtering logic
- Clear **tooltip** showing the current state
- Colored **status message** when toggling (Enabled = green, Disabled = red)
- Disabled View Cells display a **distinct disabled icon/texture**
- Works with the standard AE2 View Cell and compatible modded variants (subclasses of the AE2 View Cell item)

## Usage
### Toggle in hand
1. Hold a View Cell in your hand.
2. **Right-click** to toggle Enabled/Disabled.

### Toggle inside the View Cell slot
1. Place a View Cell into a terminal’s View Cell slot (ME Terminal, Wireless Terminal, etc.).
2. Make sure your cursor is empty.
3. **Right-click the View Cell slot** to toggle the cell without removing it.

## Notes
- “Disabled” means the cell still keeps its settings, it’s just **not applied** to filtering until re-enabled.
- Slot toggling respects AE2’s usual edit permissions (same rules as taking/placing items in that slot).

## Building
This is a Minecraft Forge mod for 1.12.2.

Typical workflow:
1. Clone the repository
2. Import into your IDE (IntelliJ IDEA or Eclipse)
3. Run the ForgeGradle setup tasks for your environment
4. Build using Gradle

## Contributing
Issues and pull requests are welcome. If you submit a PR, keep changes focused and include a short description of what changed and why.

## License
MIT (see `LICENSE`).
