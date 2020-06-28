package com.andre601.statusnpc.commands;

import com.andre601.statusnpc.StatusNPC;
import com.andre601.statusnpc.util.FormatUtil;
import com.andre601.statusnpc.util.JSONMessage;
import com.andre601.statusnpc.util.NPCManager;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@Command("statusnpc")
@Alias("snpc")
public class CmdStatusNPC extends CommandBase{
    
    private final StatusNPC plugin;
    private final FormatUtil formatUtil;
    
    public CmdStatusNPC(StatusNPC plugin){
        this.plugin = plugin;
        this.formatUtil = plugin.getFormatUtil();
    }
    
    @Default
    @SubCommand("help")
    @Permission("statusnpc.command.help")
    public void sendHelp(final CommandSender sender){
        if(sender instanceof Player){
            JSONMessage message = JSONMessage.create(formatUtil.formatString(
                    "&b/snpc help"
            )).tooltip(formatUtil.formatString(
                    "&7Displays this help page."
            )).suggestCommand(
                    "/snpc help"
            ).newline().then(formatUtil.formatString(
                    "&b/snpc list"
            )).tooltip(formatUtil.formatString(
                    "&7Lists all linked NPCs."
            )).suggestCommand(
                    "/snpc list"
            ).newline().then(formatUtil.formatString(
                    "&b/snpc set <player> <id>"
            )).tooltip(formatUtil.formatString(
                    "&7Links a Player with an NPC.\n" +
                    "\n" +
                    "&cThe Player has to be online (Spigot limitation)!"
            )).suggestCommand(
                    "/snpc set "
            ).newline().then(formatUtil.formatString(
                    "&b/snpc remove <player>"
            )).tooltip(formatUtil.formatString(
                    "&7Removes a linked Player and NPC from the storage.\n" +
                    "\n" +
                    "&cThe player has to be online (Spigot limitation)!"
            )).suggestCommand(
                    "/snpc remove "
            ).newline().newline().then(formatUtil.formatString(
                    "&b&lPro Tip: &fHover over a command for info and click for the command."
            ));
            
            message.send((Player)sender);
        }else{
            sender.sendMessage(formatUtil.formatString(
                    "&b/snpc help\n" +
                    "  &7Displays this Help page.\n" +
                    "&b/snpc list\n" +
                    "  &7Lists all linked NPCs.\n" +
                    "&b/snpc set <player> <id>\n" +
                    "  &7Links a Player with an NPC.\n" +
                    "&b/snpc remove <player>\n" +
                    "  &7Removes a linked Player and NPC."
            ));
        }
    }
    
    @SubCommand("set")
    @Completion({"#players", "#npcs"})
    @Permission("statusnpc.command.set")
    @WrongUsage("#invalidArgs")
    public void setNPC(final CommandSender sender, final Player target, Integer id){
        if(target == null || id == null){
            sender.sendMessage(formatUtil.formatString(
                    "&cToo few arguments provided! Usage: /snpc set <player> <id>"
            ));
            return;
        }
        
        if(plugin.getNpcManager().getLoaded().containsKey(id)){
            sender.sendMessage(formatUtil.formatString(
                    "&cThe provided NPC is already linked to a player!"
            ));
            sender.sendMessage(formatUtil.formatString(
                    "&cEither remove the link, or choose another NPC."
            ));
            return;
        }
        
        NPC npc = plugin.getNpcManager().getNPC(id);
        if(npc == null){
            sender.sendMessage(formatUtil.formatString(
                    "&cCould not find NPC with id " + id + "! Make sure you typed it correctly."
            ));
            return;
        }
        
        if(!(npc.getEntity() instanceof Player)){
            sender.sendMessage(formatUtil.formatString(
                    "The provided NPC is of type " + npc.getEntity().getType().toString() + " is not allowed!"
            ));
            sender.sendMessage(formatUtil.formatString(
                    "&cOnly NPCs of type PLAYER can be used for this."
            ));
            return;
        }
        
        if(plugin.getNpcManager().hasNPC(target.getUniqueId())){
            int oldId = plugin.getNpcManager().getNPCId(target);
            if(oldId >= 0){
                plugin.getNpcManager().updateNPC(target, oldId, id);
            }else{
                plugin.getNpcManager().setNPCGlow(target.getUniqueId(), id, NPCManager.OnlineStatus.ONLINE, true);
            }
        }else{
            plugin.getNpcManager().setNPCGlow(target.getUniqueId(), id, NPCManager.OnlineStatus.ONLINE, true);
        }
        
        if(sender instanceof Player){
            JSONMessage message = JSONMessage.create(formatUtil.formatString(
                    "&aSet NPC "
            )).then(formatUtil.formatString(
                    "&f%s", 
                    npc.getName()
            )).tooltip(formatUtil.formatString(
                    "&7ID: %d", 
                    npc.getId()
            )).then(formatUtil.formatString(
                    " &aas StatusNPC for Player "
            )).then(formatUtil.formatString(
                    "&f%s", 
                    target.getName()
            )).tooltip(formatUtil.formatString(
                    "&7UUID: %s", 
                    target.getUniqueId().toString()
            )).then(formatUtil.formatString(
                    "&a."
            ));
            
            message.send((Player)sender);
        }else{
            sender.sendMessage(formatUtil.formatString(
                    "&aSet NPC " + npc.getName() + " (id: " + npc.getId() + ") as StatusNPC for Player " + target.getName() + "."
            ));
        }
    }
    
    @SubCommand("remove")
    @Completion("#players")
    @Permission("statusnpc.command.remove")
    @WrongUsage("#invalidArgs")
    public void removeNPC(final CommandSender sender, final Player target){
        if(target == null){
            sender.sendMessage(formatUtil.formatString(
                    "&cToo few arguments provided! Usage: /snpc remove <player>"
            ));
            return;
        }
        
        if(plugin.getNpcManager().hasNPC(target.getUniqueId())){
            plugin.getNpcManager().removeNPCGlow(plugin.getNpcManager().getNPCId(target), true);
            
            if(sender instanceof Player){
                JSONMessage message = JSONMessage.create(formatUtil.formatString(
                        "&aRemoved Player "
                )).then(formatUtil.formatString(
                        "&f%s", 
                        target.getName()
                )).tooltip(formatUtil.formatString(
                        "&7UUID: %s", 
                        target.getUniqueId().toString()
                )).then(formatUtil.formatString(
                        " &afrom Storage."
                ));
                
                message.send((Player)sender);
            }else{
                sender.sendMessage(formatUtil.formatString(
                        "&aRemoved Player &f%s (UUID: %s) &afrom Storage.",
                        target.getName(),
                        target.getUniqueId().toString()
                ));
            }
        }else{
            if(sender instanceof Player){
                JSONMessage message = JSONMessage.create(formatUtil.formatString(
                        "&cNo NPC set for player "
                )).then(formatUtil.formatString(
                        "&f%s", 
                        target.getName()
                )).tooltip(formatUtil.formatString(
                        "&7UUID: %s",
                        target.getUniqueId().toString()
                )).then("&c.");
                
                message.send((Player)sender);
            }else{
                sender.sendMessage(formatUtil.formatString(
                        "&cNo NPC set for player &f%s (UUID: %s)&c.",
                        target.getName(),
                        target.getUniqueId().toString()
                ));
            }
        }
    }
    
    @SubCommand("list")
    @Permission("statusnpc.command.list")
    @WrongUsage("#invalidArgs")
    public void getLinkedNPCs(final CommandSender sender){
        Map<NPC, UUID> npcs = plugin.getNpcManager().getNPCs();
    
        if(sender instanceof Player){
            JSONMessage message = JSONMessage.create(formatUtil.formatString(
                    "&b==== Linked NPCs (&f%d&b) ====",
                    npcs.size()
            )).newline();
    
            npcs.forEach((npc, uuid) -> {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                String name = offlinePlayer.getName() == null ? "&oUnknown" : offlinePlayer.getName();
        
                message.newline().then(formatUtil.formatString(
                        "&f%s &b[&f%d&b]",
                        npc.getName(),
                        npc.getId()
                )).tooltip(formatUtil.formatString(
                        "&7Playername:\n" +
                        "  &b%s\n" +
                        "&7UUID:\n" +
                        "  &b%s",
                        name,
                        uuid.toString()
                ));
            });
    
            message.send((Player)sender);
        }else{
            sender.sendMessage(formatUtil.formatString(
                    "&b==== Linked NPCs (&f%d&b) ====",
                    npcs.size()
            ));
            sender.sendMessage("");
            
            npcs.forEach((npc, uuid) -> {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                String name = offlinePlayer.getName() == null ? "&oUnknown" : offlinePlayer.getName();
                
                sender.sendMessage(formatUtil.formatString(
                        "&f%s &b[&f%d&b] &7- &f%s &b[&f%s&b]",
                        npc.getName(),
                        npc.getId(),
                        name,
                        uuid.toString()
                ));
            });
        }
    }
}
