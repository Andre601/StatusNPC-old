package com.andre601.statusnpc.events;

import com.andre601.statusnpc.StatusNPC;
import com.andre601.statusnpc.util.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventManager implements Listener{

    private final StatusNPC plugin;

    public PlayerEventManager(StatusNPC plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        
        if(!plugin.getNpcManager().hasNPC(player.getUniqueId()))
            return;
        
        int id = plugin.getNpcManager().getNPCId(player);
        if(id == -1)
            return;
        
        plugin.getNpcManager().setNPCGlow(player.getUniqueId(), id, NPCManager.OnlineStatus.ONLINE, false);
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
    
        if(!plugin.getNpcManager().hasNPC(player.getUniqueId()))
            return;
    
        int id = plugin.getNpcManager().getNPCId(player);
        if(id == -1)
            return;
    
        plugin.getNpcManager().setNPCGlow(player.getUniqueId(), id, NPCManager.OnlineStatus.OFFLINE, false);
    }
}
