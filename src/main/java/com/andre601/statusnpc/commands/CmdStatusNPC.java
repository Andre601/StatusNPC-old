package com.andre601.statusnpc.commands;

import com.andre601.statusnpc.StatusNPC;
import com.andre601.statusnpc.util.FormatUtil;
import com.andre601.statusnpc.util.JSONMessage;
import com.andre601.statusnpc.util.NPCManager;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
    
    private JSONMessage getTooltip(String msg){
        return JSONMessage.create(msg).color(ChatColor.GRAY);
    }
    
    @Default
    public void def(final CommandSender sender){
        sendHelp(sender);
    }
    
    @SubCommand("help")
    @Permission("statusnpc.command.help")
    public void sendHelp(final CommandSender sender){
        JSONMessage msg = JSONMessage.create("==== StatusNPC Help ====")
                .color(ChatColor.AQUA)
                .newline()
                .newline()
                
                .then("/snpc help")
                .color(ChatColor.AQUA)
                .tooltip(getTooltip("Displays this help page."))
                .suggestCommand("/snpc help")
                .newline()
                
                .then("/snpc list")
                .color(ChatColor.AQUA)
                .tooltip(getTooltip("Lists all linked NPCs."))
                .suggestCommand("/snpc list")
                .newline()
                
                .then("/snpc set <player> <id>")
                .color(ChatColor.AQUA)
                .tooltip(
                        JSONMessage.create("Links the provided Player and NPC.")
                                   .color(ChatColor.GRAY)
                                   .newline()
                                   .newline()
                                   .then("The provided Player has to be online.")
                                   .color(ChatColor.RED)
                                   .newline()
                                   .then("This is a Spigot limitation.")
                                   .color(ChatColor.RED)
                )
                .suggestCommand("/snpc set ")
                .newline()
                
                .then("/snpc remove <player>")
                .color(ChatColor.AQUA)
                .tooltip(
                        JSONMessage.create("Removed the provided Player from the Storage.")
                                   .color(ChatColor.GRAY)
                                   .newline()
                                   .newline()
                                   .then("The provided Player has to be online.")
                                   .color(ChatColor.RED)
                                   .newline()
                                   .then("This is a Spigot limitation.")
                                   .color(ChatColor.RED))
                .suggestCommand("/snpc remove ")
                .newline()
                .newline()
                
                .then("[Spigot]")
                .color(ChatColor.GOLD)
                .tooltip(getTooltip("Open Spigot Page."))
                .openURL("https://www.spigotmc.org/resources/75597")
                .then(" - ")
                .color(ChatColor.GRAY)
                .then("[GitHub]")
                .color(ChatColor.WHITE)
                .tooltip(getTooltip("Open GitHub Page."))
                .openURL("https://github.com/Andre601/StatusNPC")
                .newline()
                .newline()
                
                .then("Pro Tip: ")
                .color(ChatColor.AQUA)
                .style(ChatColor.BOLD)
                .then("Hover over a command for more info and click it for the command.")
                .color(ChatColor.WHITE);
        
        formatUtil.sendMsg(
                sender, 
                msg,
                "&b/snpc help",
                "&7Displays this help page.",
                "&b/snpc list",
                "&7Lists all linked NPCs.",
                "&b/snpc set <player> <id>",
                "&7Links the Player with an NPC.",
                "&b/snpc remove <player>",
                "&7Removes a linked Player and NPC from the storage."
        );
    }
    
    @SubCommand("set")
    @Completion({"#players", "#npcs"})
    @Permission("statusnpc.command.set")
    @WrongUsage("#invalidArgs")
    public void setNPC(final CommandSender sender, final Player target, Integer id){
        
        JSONMessage msg;
        if(target == null || id == null){
            msg = JSONMessage.create("Too few arguments provided! Usage: ")
                    .color(ChatColor.RED)
                    .then("/snpc set <player> <id>")
                    .color(ChatColor.GRAY)
                    .tooltip(getTooltip("Click to get the command."))
                    .suggestCommand("/snpc set ");
            
            formatUtil.sendMsg(
                    sender,
                    msg,
                    "&cToo few arguments provided! Usage: &7/snpc set <player> <id>"
            );
            return;
        }
        
        if(plugin.getNpcManager().getLoaded().containsKey(id)){
            msg = JSONMessage.create("The provided NPC is already linked to a player!")
                    .color(ChatColor.RED)
                    .newline()
                    .then("Either remove the NPC or choose another one.")
                    .color(ChatColor.RED);
            
            formatUtil.sendMsg(
                    sender,
                    msg,
                    "&cThe provided NPC is already linked to a player!",
                    "&cEither remove the NPC or choose another one."
            );
            return;
        }
        
        NPC npc = plugin.getNpcManager().getNPC(id);
        if(npc == null){
            msg = JSONMessage.create("Couldn't find an NPC with id ")
                    .color(ChatColor.RED)
                    .then(String.valueOf(id))
                    .color(ChatColor.GRAY)
                    .then("! Make sure you typed it correctly.")
                    .color(ChatColor.RED);
            
            formatUtil.sendMsg(
                    sender,
                    msg,
                    "&cCouldn't find an NPC with id &7" + id + "&c! Make sure you typed it correctly."
            );
            return;
        }
        
        if(!(npc.getEntity() instanceof Player)){
            msg = JSONMessage.create("Invalid NPC type ")
                    .color(ChatColor.RED)
                    .then(npc.getEntity().getType().toString())
                    .color(ChatColor.GRAY)
                    .then("!")
                    .newline()
                    .then("Only NPCs of type PLAYER can be used for this.")
                    .color(ChatColor.RED);
            
            formatUtil.sendMsg(
                    sender,
                    msg,
                    "&cInvalid NPC type &7" + npc.getEntity().getType().toString() + "&c!",
                    "&7Only NPCs of type PLAYER can be used for this."
            );
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
        
        msg = JSONMessage.create("Set NPC ")
                .color(ChatColor.GREEN)
                .then(npc.getName())
                .color(ChatColor.GRAY)
                .tooltip(getTooltip("ID: " + npc.getId()))
                .then(" as StatusNPC for Player ")
                .color(ChatColor.GREEN)
                .then(target.getName())
                .color(ChatColor.GRAY)
                .tooltip(getTooltip("UUID: " + target.getUniqueId()))
                .then(".")
                .color(ChatColor.GREEN);
        
        formatUtil.sendMsg(
                sender,
                msg,
                "&aSet NPC &7" + npc.getName() + " &a(&7ID: " + npc.getId() + "&a) as StatusNPC for Player &7" +
                target.getName() + " &a(&7UUID: " + target.getUniqueId() + "&a)."
        );
    }
    
    @SubCommand("remove")
    @Completion("#players")
    @Permission("statusnpc.command.remove")
    @WrongUsage("#invalidArgs")
    public void removeNPC(final CommandSender sender, final Player target){
        
        JSONMessage msg;
        if(target == null){
            msg = JSONMessage.create("Too few arguments provided! Usage: ")
                    .color(ChatColor.RED)
                    .then("/snpc remove <player>")
                    .color(ChatColor.GRAY)
                    .tooltip(getTooltip("Click to get the command."))
                    .suggestCommand("/snpc remove ");
    
            formatUtil.sendMsg(
                    sender,
                    msg,
                    "&cToo few arguments provided! Usage: &7/snpc remove <player>"
            );
            return;
        }
        
        if(plugin.getNpcManager().hasNPC(target.getUniqueId())){
            plugin.getNpcManager().removeNPCGlow(plugin.getNpcManager().getNPCId(target), true);
            
            msg = JSONMessage.create("Removed Player ")
                    .color(ChatColor.GREEN)
                    .then(target.getName())
                    .color(ChatColor.GRAY)
                    .tooltip(getTooltip("UUID: " + target.getUniqueId()))
                    .then(" from the Storage.")
                    .color(ChatColor.GREEN);
            
            formatUtil.sendMsg(
                    sender,
                    msg,
                    "&aRemoved Player &7" + target.getName() + " &a(&7UUID: " + target.getUniqueId() + "&a) from the Storage."
            );
        }else{
            msg = JSONMessage.create("No NPC was set for Player ")
                    .color(ChatColor.RED)
                    .then(target.getName())
                    .color(ChatColor.GRAY)
                    .tooltip(getTooltip("UUID: " + target.getUniqueId()))
                    .then(".")
                    .color(ChatColor.RED);
            
            formatUtil.sendMsg(
                    sender,
                    msg,
                    "&cNo NPC was set for Player &7" + target.getName() + " &c(&7UUID: " + target.getUniqueId() + "&c)."
            );
        }
    }
    
    @SubCommand("list")
    @Permission("statusnpc.command.list")
    @WrongUsage("#invalidArgs")
    public void getLinkedNPCs(final CommandSender sender){
        Map<NPC, UUID> npcs = plugin.getNpcManager().getNPCs();
        
        JSONMessage msg = JSONMessage.create("==== Linked NPCs (")
                .color(ChatColor.AQUA)
                .then(String.valueOf(npcs.size()))
                .color(ChatColor.WHITE)
                .then(") ====")
                .color(ChatColor.AQUA);
        List<String> lines = new ArrayList<>();
        lines.add("&b==== Linked NPCs (&f" + npcs.size() + "&b) ====");
        
        npcs.forEach((npc, uuid) -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = player.getName() == null ? "Unknown" : player.getName();
            
            JSONMessage tooltip = JSONMessage.create("==== Linked Player ====")
                    .color(ChatColor.AQUA)
                    .newline()
                    .newline()
                    .then("Name:")
                    .color(ChatColor.GRAY)
                    .newline()
                    .then("  " + name)
                    .color(ChatColor.AQUA)
                    .newline()
                    .then("UUID:")
                    .color(ChatColor.GRAY)
                    .newline()
                    .then("  " + uuid)
                    .color(ChatColor.AQUA);
            
            msg.newline()
               .then(npc.getName())
               .color(ChatColor.WHITE)
               .tooltip(tooltip)
               .then(" [")
               .color(ChatColor.AQUA)
               .tooltip(tooltip)
               .then(String.valueOf(npc.getId()))
               .color(ChatColor.WHITE)
               .tooltip(tooltip)
               .then("]")
               .color(ChatColor.AQUA)
               .tooltip(tooltip);
            
            lines.add("&f" + npc.getName() + " &b[&f" + npc.getId() + "&b] &7- &f" + name + " &b[&f" + uuid + "&b]");
        });
        
        formatUtil.sendMsg(
                sender,
                msg,
                lines.toArray(new String[0])
        );
    }
}
