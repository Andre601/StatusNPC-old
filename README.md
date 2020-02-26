# StatusNPC
StatusNPC is a simple plugin which allows you to display the online status of a Player using NPCs from Citizens.  
The status is shown through a colored glow.

## How to use
To set a NPC as StatusNPC, use `/snpc set <player> <id>`  
Replace `<player>` with the player's name and `<id>` with the id of the NPC.

Run `/snpc help` to see the available commands.  
All commands require the permission `statusnpc.use`

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