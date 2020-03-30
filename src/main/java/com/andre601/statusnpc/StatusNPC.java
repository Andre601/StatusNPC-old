package com.andre601.statusnpc;

import com.andre601.statusnpc.events.NPCEventManager;
import com.andre601.statusnpc.events.ServerEventManager;
import com.andre601.statusnpc.util.FileManager;
import com.andre601.statusnpc.util.FormatUtil;
import com.andre601.statusnpc.util.NPCManager;
import com.andre601.statusnpc.commands.CmdStatusNPC;
import com.andre601.statusnpc.events.EssentialsEventManager;
import com.andre601.statusnpc.events.PlayerEventManager;
import me.mattstudios.mf.base.CommandManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class StatusNPC extends JavaPlugin{
    
    private boolean essentialsEnabled = false;
    private boolean debug;

    private NPCManager npcManager;
    private FileManager fileManager;
    private FormatUtil formatUtil;
    
    private List<String> npcs = new ArrayList<>();
    
    @Override
    public void onLoad(){
        getLogger().info("[Startup] Loading StatusNPC v" + getDescription().getVersion());
        
        fileManager = new FileManager(this);
    
        npcManager = new NPCManager(this);
        formatUtil = new FormatUtil(this);
        
        getLogger().info("[Startup - Files] Loading config.yml...");
        saveDefaultConfig();
        debug = getConfig().getBoolean("Debug", false);
        getLogger().info("[Startup - Files] config.yml loaded!");
        
        getLogger().info("[Startup - Files] Loading npcs.yml...");
        fileManager.loadFile();
        getLogger().info("[Startup - Files] npcs.yml loaded!");
    }
    
    @Override
    public void onEnable(){
        getLogger().info("[Startup] Enabling StatusNPC v" + getDescription().getVersion());
    
        PluginManager manager = Bukkit.getPluginManager();
        
        getLogger().info("[Startup - Dependencies] Looking for Citizens...");
        if(!manager.isPluginEnabled("Citizens")){
            getLogger().warning("[Startup - Dependencies] Could not find Citizens! Make sure it is installed and enabled.");
            getLogger().warning("[Startup - Dependencies] Disabling plugin...");
            manager.disablePlugin(this);
            return;
        }
        getLogger().info("[Startup - Dependencies] Found Citizens! Continue loading...");
    
        getLogger().info("[Startup - Dependencies] Looking for Essentials...");
        if(manager.isPluginEnabled("Essentials")){
            essentialsEnabled = true;
            getLogger().info("[Startup - Dependencies] Found Essentials! Hooking into it...");
            new EssentialsEventManager(this);
        }
        
        getLogger().info("[Startup - Events] Loading events...");
        new PlayerEventManager(this);
        new NPCEventManager(this);
        
        // We use this setup to delay the reset of NPCs for when the server should/will be ready.
        try{
            Class.forName("org.bukkit.event.server.ServerLoadEvent");
            new ServerEventManager(this);
        }catch(ClassNotFoundException ignored){
            sendDebug("Resetting all NPCs...");
            Bukkit.getScheduler().runTaskLater(this, () -> getNpcManager().loadNPCs(), 1);
        }
        getLogger().info("[Startup - Events] Events loaded!");
        
        getLogger().info("[Startup - Command] Loading /statusnpc command...");
        setupCmdFramework();
        getLogger().info("[Startup - Command] Loaded command /statusnpc");
        
        getLogger().info("[Startup] Starting of StatusNPC complete!");
    }
    
    public boolean isEssentialsEnabled(){
        return essentialsEnabled;
    }
    
    public boolean isDebug(){
        return debug;
    }
    
    public void sendDebug(String msg){
        if(isDebug())
            getLogger().info("[DEBUG] " + msg);
    }
    
    public NPCManager getNpcManager(){
        return npcManager;
    }
    public FileManager getFileManager(){
        return fileManager;
    }
    public FormatUtil getFormatUtil(){
        return formatUtil;
    }
    
    public FileConfiguration getNpcConfig(){
        return fileManager.getNpcConfig();
    }
    
    public List<String> getNpcs(){
        return npcs;
    }
    
    private void setupCmdFramework(){
        CommandManager manager = new CommandManager(this);
        
        manager.getCompletionHandler().register("#npcs", input -> {
            for(NPC npc : CitizensAPI.getNPCRegistry().sorted()){
                if(npc.getEntity() instanceof Player)
                    npcs.add(String.valueOf(npc.getId()));
            }
            
            return npcs;
        });
        
        manager.getMessageHandler().register("cmd.no.permission", sender -> 
            sender.sendMessage(getFormatUtil().getLine("Messages.Errors.NoPerms"))
        );
        manager.getMessageHandler().register("cmd.no.exists", sender -> 
            sender.sendMessage(getFormatUtil().getLine("Messages.Errors.FewArgs.Other"))
        );
        manager.getMessageHandler().register("cmd.wrong.usage", sender -> 
            sender.sendMessage(getFormatUtil().getLine("Messages.Errors.InvalidArgs"))
        );
        manager.getMessageHandler().register("cmd.no.console", sender -> 
            sender.sendMessage(getFormatUtil().getLine("Messages.Errors.NoPlayer"))
        );
        
        manager.getMessageHandler().register("#invalidArgs", sender -> 
            sender.sendMessage(getFormatUtil().getLine("Messages.Errors.InvalidArgs"))
        );
    
        manager.register(new CmdStatusNPC(this));
    }
}
