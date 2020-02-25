package com.andre601.statusnpc.util;

import com.andre601.statusnpc.StatusNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.ScoreboardTrait;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class NPCManager {

    private StatusNPC plugin;

    public NPCManager(StatusNPC plugin){
        this.plugin = plugin;
    }
    
    public void setNPCStatus(String uuid, int id, OnlineStatus status){
        plugin.sendDebug("Setting NPC color of NPC " + id + ".");
        
        NPC npc = CitizensAPI.getNPCRegistry().getById(id);
        
        if(npc == null){
            plugin.sendDebug("Could not set color! NPC was null.");
            return;
        }
        
        NPCColor color = NPCColor.getNPCColorByName(plugin.getConfig().getString("NPC.Colors." + status.getStatus()));
        
        if(status == OnlineStatus.AFK && !plugin.isEssentialsEnabled()){
            plugin.sendDebug("Got status AFK, but Essentials isn't enabled! Defaulting to gray color.");
            color = NPCColor.GRAY;
        }
        
        if(color == null){
            plugin.sendDebug("Got invalid status " + status.getStatus() + "! Defaulting to gray color.");
            color = NPCColor.GRAY;
        }
        
        npc.getTrait(ScoreboardTrait.class).setColor(color.getColor());
        npc.data().setPersistent(NPC.GLOWING_METADATA, true);
        
        plugin.getLoaded().put(id, uuid);
        
        plugin.sendDebug("Color for NPC " + id + " set to color " + color.getName());
    }
    
    public void resetNPCStatus(){
        for(String key : plugin.getNpcConfig().getKeys(false)){
            ConfigurationSection section = plugin.getNpcConfig().getConfigurationSection(key);
            if(section == null || !section.contains("ID"))
                continue;
            
            if(CitizensAPI.getNPCRegistry().getById(section.getInt("ID")) == null)
                continue;
            
            setNPCStatus(key, section.getInt("ID"), OnlineStatus.OFFLINE);
        }
    }
    
    public void saveNPC(CommandSender sender, Player player, int id){
        plugin.sendDebug("Saving Player " + player.getName() + " (" + player.getUniqueId() + ") with NPC " + id);
        
        if(CitizensAPI.getNPCRegistry().getById(id) == null){
            sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidNPC").replace("%id%", "" + id));
            return;
        }
        
        if(!(CitizensAPI.getNPCRegistry().getById(id).getEntity() instanceof Player)){
            sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.NPCNotPlayer"));
            
            return;
        }
        
        plugin.getNpcConfig().set(player.getUniqueId().toString() + ".ID", id);
        plugin.getFileManager().save();
        
        setNPCStatus(player.getUniqueId().toString(), id, OnlineStatus.ONLINE);
        plugin.sendDebug("Player saved!");
        
        sender.sendMessage(plugin.getFormatUtil().getLine("Messages.SetNPC")
                .replace("%id%", "" + id)
                .replace("%player%", player.getName())
        );
    }
    
    public void deleteNPC(CommandSender sender, Player player){
        plugin.sendDebug("Deleting NPC for Player " + player.getName() + " (" + player.getUniqueId() + ") from storage.");
        
        if(hasNPC(player)){
            int id = plugin.getNpcConfig().getInt(player.getUniqueId().toString() + ".ID");
            
            NPC npc = CitizensAPI.getNPCRegistry().getById(id);
            if(npc != null){
                npc.data().setPersistent(NPC.GLOWING_METADATA, false);
                npc.getTrait(ScoreboardTrait.class).setColor(NPCColor.WHITE.getColor());
            }
            
            plugin.getNpcConfig().set(player.getUniqueId().toString(), null);
            plugin.getFileManager().save();
            
            plugin.getLoaded().remove(id);
            
            plugin.sendDebug("Removed NPC of player " + player.getName() + " (" + player.getUniqueId() + ")");
            sender.sendMessage(plugin.getFormatUtil().getLine("Messages.RemovedNPC").replace("%player%", player.getName()));
        }else{
            sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.NotSet"));
        }
    }
    
    public void deleteNPC(int id){
        for(String key : plugin.getNpcConfig().getKeys(false)){
            ConfigurationSection section = plugin.getNpcConfig().getConfigurationSection(key);
            if(section == null || !section.contains("ID"))
                continue;
            
            if(section.getInt("ID") != id)
                continue;
            
            plugin.getNpcConfig().set(key, null);
            plugin.getFileManager().save();
            
            plugin.getLoaded().remove(id);
            break;
        }
    }
    
    public boolean hasNPC(Player player){
        return plugin.getLoaded().containsValue(player.getUniqueId().toString());
    }
    
    public boolean hasNPC(int id){
        return plugin.getLoaded().containsKey(id);
    }
    
    public boolean hasSavedNPC(int id){
        for(String key : plugin.getNpcConfig().getKeys(false)){
            ConfigurationSection section = plugin.getNpcConfig().getConfigurationSection(key);
            if(section == null || !section.contains("ID"))
                continue;
            
            if(section.getInt("ID") != id)
                continue;
            
            return true;
        }
        
        return false;
    }
    
    public String getUUID(int id){
        if(hasNPC(id))
            return plugin.getLoaded().get(id);
        
        for(String key : plugin.getNpcConfig().getKeys(false)){
            ConfigurationSection section = plugin.getNpcConfig().getConfigurationSection(key);
    
            if(section == null || !section.contains("ID"))
                continue;
    
            if(section.getInt("ID") != id)
                continue;
            
            return key;
        }
        
        return null;
    }
    
    /*
     * Enum for convenience as Spigot is really shit with their Color class.
     */
    public enum NPCColor{
        AQUA        (ChatColor.AQUA,         "Aqua"),
        BLACK       (ChatColor.BLACK,        "Black"),
        BLUE        (ChatColor.BLUE,         "Blue"),
        DARK_AQUA   (ChatColor.DARK_AQUA,    "Dark Aqua"),
        DARK_BLUE   (ChatColor.DARK_BLUE,    "Dark Blue"),
        DARK_GRAY   (ChatColor.DARK_GRAY,    "Dark Gray"),
        DARK_GREEN  (ChatColor.DARK_GREEN,   "Dark Green"),
        DARK_PURPLE (ChatColor.DARK_PURPLE,  "Dark Purple"),
        DARK_RED    (ChatColor.DARK_RED,     "Dark Red"),
        GOLD        (ChatColor.GOLD,         "Gold"),
        GRAY        (ChatColor.GRAY,         "Gray"),
        GREEN       (ChatColor.GREEN,        "Green"),
        LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, "Light Purple"),
        RED         (ChatColor.RED,          "Red"),
        WHITE       (ChatColor.WHITE,        "White"),
        YELLOW      (ChatColor.YELLOW,       "Yellow");
        
        private ChatColor color;
        private String name;
        
        NPCColor(ChatColor color, String name){
            this.color = color;
            this.name = name;
        }
        
        public static NPCColor getNPCColorByName(String name){
            for(NPCColor color : values()){
                if(color.name().equalsIgnoreCase(name))
                    return color;
            }
            
            return null;
        }
        
        public ChatColor getColor(){
            return color;
        }
        
        public String getName(){
            return name;
        }
    }

}
