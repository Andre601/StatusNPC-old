package com.andre601.statusnpc.util;

import com.andre601.statusnpc.StatusNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.ScoreboardTrait;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCManager{
    
    private StatusNPC plugin;
    
    private Map<Integer, UUID> loaded = new HashMap<>();
    
    public NPCManager(StatusNPC plugin){
        this.plugin = plugin;
    }
    
    public void setNPCGlow(UUID uuid, int id, OnlineStatus status, boolean withSave){
        NPC npc = getNPC(id);
        if(npc == null){
            plugin.sendDebug("Could not set NPC color. The NPC was null!");
            return;
        }
        
        NPCColor color = NPCColor.getColorByName(plugin.getConfig().getString("NPC.Colors." + status.getStatus()));
        if(status == OnlineStatus.AFK && !plugin.isEssentialsEnabled()){
            plugin.sendDebug("Received Online status AFK, but Essentials is not installed/enabled. Defaulting to GRAY");
            color = NPCColor.GRAY;
        }
        
        setNPCGlow(npc, color);
        
        if(withSave){
            plugin.getNpcConfig().set(uuid.toString() + ".ID", id);
            plugin.getFileManager().save();
        }
        
        loaded.put(npc.getId(), uuid);
        plugin.sendDebug("Set Color for NPC " + id + " to " + color.getName());
    }
    
    public void loadNPCs(){
        for(String key : plugin.getNpcConfig().getKeys(false)){
            ConfigurationSection section = plugin.getNpcConfig().getConfigurationSection(key);
            
            if(section == null || !section.contains("ID"))
                continue;
            
            NPC npc = getNPC(UUID.fromString(key));
            if(npc == null)
                continue;
            
            loaded.put(npc.getId(), UUID.fromString(key));
            setNPCGlow(UUID.fromString(key), npc.getId(), OnlineStatus.OFFLINE, false);
        }
    }
    
    public void updateNPC(Player player, int oldId, int newId){
        NPC npcOld = getNPC(oldId);
        NPC npcNew = getNPC(newId);
        
        if(npcOld == null || npcNew == null)
            return;
        
        removeNPCGlow(npcOld.getId(), false);
        setNPCGlow(player.getUniqueId(), newId, OnlineStatus.ONLINE, true);
    }
    
    public void removeNPCGlow(int id, boolean withDelete){
        if(!loaded.containsKey(id))
            return;
        
        NPC npc = getNPC(id);
        if(npc == null)
            return;
        
        setNPCGlow(npc, null);
        
        String uuid = getUUID(id);
        if((uuid != null) && withDelete){
            plugin.getNpcConfig().set(uuid, null);
            plugin.getFileManager().save();
        }
        loaded.remove(id);
    }
    
    public boolean hasNPC(UUID uuid){
        return getNPC(uuid) != null;
    }
    
    public NPC getNPC(UUID uuid){
        return getNPC(plugin.getNpcConfig().getInt(uuid.toString() + ".ID"));
    }
    
    public NPC getNPC(int id){
        return CitizensAPI.getNPCRegistry().getById(id);
    }
    
    public int getNPCId(Player player){
        return plugin.getNpcConfig().getInt(player.getUniqueId().toString() + ".ID", -1);
    }
    
    public Map<NPC, UUID> getAllNPC(){
        Map<NPC, UUID> npcs = new HashMap<>();
        for(String key : plugin.getNpcConfig().getKeys(false)){
            ConfigurationSection section = plugin.getNpcConfig().getConfigurationSection(key);
            
            if(section == null || !section.contains("ID"))
                continue;
            
            NPC npc = getNPC(section.getInt("ID"));
            if(npc == null)
                continue;
            
            npcs.put(npc, UUID.fromString(key));
        }
        
        return npcs;
    }
    
    public Map<Integer, UUID> getLoaded(){
        return loaded;
    }
    
    private void setNPCGlow(NPC npc, NPCColor color){
        npc.getTrait(ScoreboardTrait.class).setColor(color == null ? NPCColor.WHITE.getColor() : color.getColor());
        npc.data().setPersistent(NPC.GLOWING_METADATA, color != null);
    }
    
    private String getUUID(int id){
        for(String key : plugin.getNpcConfig().getKeys(false)){
            ConfigurationSection section = plugin.getNpcConfig().getConfigurationSection(key);
            
            if(section == null || !section.contains("ID"))
                continue;
            
            if(section.getInt("ID") == id)
                return key;
        }
        
        return null;
    }
    
    /*
     * Enum for convenience as Spigot is really shit with their Color class.
     */
    @SuppressWarnings("unused")
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
        
        public static NPCManager.NPCColor getColorByName(String name){
            for(NPCManager.NPCColor color : values()){
                if(color.name().equalsIgnoreCase(name))
                    return color;
            }
            
            return GRAY;
        }
        
        public ChatColor getColor(){
            return color;
        }
        
        public String getName(){
            return name;
        }
    }
}
