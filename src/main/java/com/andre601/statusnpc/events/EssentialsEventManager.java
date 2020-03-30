package com.andre601.statusnpc.events;

import com.andre601.statusnpc.StatusNPC;
import com.andre601.statusnpc.util.OnlineStatus;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsEventManager implements Listener{
    
    private StatusNPC plugin;
    
    public EssentialsEventManager(StatusNPC plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onAFK(AfkStatusChangeEvent event){
        Player player = event.getAffected().getBase();
    
        if(!plugin.getNpcManager().hasNPC(player.getUniqueId()))
            return;
    
        int id = plugin.getNpcConfig().getInt(player.getUniqueId() + ".ID", -1);
        if(id == -1)
            return;
        
        if(event.getValue())
            plugin.getNpcManager().setNPCGlow(player.getUniqueId(), id, OnlineStatus.AFK, false);
        else
            plugin.getNpcManager().setNPCGlow(player.getUniqueId(), id, OnlineStatus.ONLINE, false);
    }
}
