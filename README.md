# StatusNPC
StatusNPC is a simple plugin which allows you to display the online status of a Player using NPCs from Citizens.  
The status is shown through a colored glow.

![Example](https://thumbs.gfycat.com/TenseUnsightlyAmericankestrel-size_restricted.gif)

## How to use
To set a NPC as StatusNPC, use `/snpc set <player> <id>`  
Replace `<player>` with the Player's name and `<id>` with the id of the NPC.  
The plugin offers tab-completion for both player names and NPC ids.

## Commands and Permissions

### `/snpc set <player> <id>`
> **Permission**: `statusnpc.command.set`

Links the NPC with the provided Player to display a glow color depending on their [Online Status](#supported-states)

Note that setting an NPC for a Player, that has an NPC already linked, will remove the old link.

### `/snpc remove <player>`
> **Permission**: `statusnpc.command.remove`

Removes the linked NPC and player from the storage.

### `/snpc list`
> **Permission**: `statusnpc.command.list`

Lists all linked NPCs.  
The list contains Hover text to provide additional information.

### `/snpc help`
> **Permission**: `statusnpc.command.help`

Lists all commands available.

## Supported States
StatusNPC currently supports the following status types.

- **Online**  
The Color to display when the player is currently on the server.
- **Offline**  
The Color to display when the player is not on the server.
- **AFK**  
The Color to display when the player is currently AFK.  
This supports and requires Essentials (EssentialsX is recommendet).

## Issues and limitations
- The plugin utilizes the Scoreboard (Through Citizens' ScoreboardTrait option) to display a different glow color for the NPC.  
This has the unwanted side-effect that Players with the same name will have their name-tag color changed in the tab.
- Only Player NPCs are usable through this plugin.
