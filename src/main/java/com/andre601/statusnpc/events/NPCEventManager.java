package com.andre601.statusnpc.events;

import com.andre601.statusnpc.StatusNPC;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        plugin.getNpcManager().removeNPCGlow(event.getNPC().getId(), true);
        plugin.getNpcs().remove(String.valueOf(event.getNPC().getId()));
    }
    
    @EventHandler
    public void onCreate(NPCCreateEvent event){
        NPC npc = event.getNPC();
        if(npc.getEntity() instanceof Player)
            plugin.getNpcs().add(String.valueOf(npc.getId()));
    }
}
