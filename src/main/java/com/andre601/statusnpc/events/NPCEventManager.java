package com.andre601.statusnpc.events;

import com.andre601.statusnpc.StatusNPC;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCEventManager implements Listener{
    
    private StatusNPC plugin;
    
    public NPCEventManager(StatusNPC plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onRemove(NPCRemoveEvent event){
        plugin.getNpcManager().deleteNPC(event.getNPC().getId());
    }
    
    @EventHandler
    public void onDeath(NPCDeathEvent event){
        for(Integer id : plugin.getLoaded().keySet()){
            if(id == event.getNPC().getId()){
                plugin.getLoaded().remove(id);
                break;
            }
        }
    }
    
    @EventHandler
    public void onRespawn(NPCSpawnEvent event){
        int id = event.getNPC().getId();
        
        if(plugin.getNpcManager().hasSavedNPC(id))
            plugin.getLoaded().put(id, plugin.getNpcManager().getUUID(id));
    }
}
