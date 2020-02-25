package com.andre601.statusnpc.events;

import com.andre601.statusnpc.StatusNPC;
import com.andre601.statusnpc.util.OnlineStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventManager implements Listener{

    private StatusNPC plugin;

    public PlayerEventManager(StatusNPC plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        
        if(!plugin.getNpcManager().hasNPC(player))
            return;
        
        int id = plugin.getNpcConfig().getInt(player.getUniqueId() + ".ID", -1);
        if(id == -1)
            return;
        
        plugin.getNpcManager().setNPCStatus(player.getUniqueId().toString(), id, OnlineStatus.ONLINE);
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
    
        if(!plugin.getNpcManager().hasNPC(player))
            return;
    
        int id = plugin.getNpcConfig().getInt(player.getUniqueId() + ".ID", -1);
        if(id == -1)
            return;
    
        plugin.getNpcManager().setNPCStatus(player.getUniqueId().toString(), id, OnlineStatus.OFFLINE);
    }
}
