package com.andre601.statusnpc.events;

import com.andre601.statusnpc.StatusNPC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerEventManager implements Listener{
    
    private StatusNPC plugin;
    
    public ServerEventManager(StatusNPC plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onLoad(ServerLoadEvent event){
        plugin.sendDebug("Resetting all NPCs...");
        plugin.getNpcManager().resetNPCStatus();
    }
}
