package com.andre601.statusnpc;

import com.andre601.statusnpc.events.NPCEventManager;
import com.andre601.statusnpc.events.ServerEventManager;
import com.andre601.statusnpc.util.FileManager;
import com.andre601.statusnpc.util.FormatUtil;
import com.andre601.statusnpc.util.NPCManager;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import com.andre601.statusnpc.commands.CmdStatusNPC;
import com.andre601.statusnpc.events.EssentialsEventManager;
import com.andre601.statusnpc.events.PlayerEventManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatusNPC extends JavaPlugin{
    
    private boolean essentialsEnabled = false;
    private boolean debug;

    private NPCManager npcManager;
    private FileManager fileManager;
    private FormatUtil formatUtil;
    
    private Map<Integer, String> loaded = new HashMap<>();
    
    public void onEnable(){
        getLogger().info("[Startup] Enabling StatusNPC v" + getDescription().getVersion());
        
        saveDefaultConfig();
        
        debug = getConfig().getBoolean("Debug", false);
    
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
            manager.registerEvents(new EssentialsEventManager(this), this);
        }
        
        npcManager = new NPCManager(this);
        fileManager = new FileManager(this);
        formatUtil = new FormatUtil(this);
        
        getLogger().info("[Startup - File] Loading Storage-file...");
        fileManager.loadFile();
        getLogger().info("[Startup - File] Storage file loaded!");
        
        getLogger().info("[Startup - Events] Loading events...");
        new PlayerEventManager(this);
        new NPCEventManager(this);
        
        // We use this setup to delay the reset of NPCs for when the server should/will be ready.
        try{
            Class.forName("org.bukkit.event.server.ServerLoadEvent");
            new ServerEventManager(this);
        }catch(ClassNotFoundException ex){
            sendDebug("Resetting all NPCs...");
            Bukkit.getScheduler().runTaskLater(this, () -> getNpcManager().resetNPCStatus(), 1);
        }
        getLogger().info("[Startup - Events] Events loaded!");
        
        getLogger().info("[Startup - Command] Loading /statusnpc command...");
        PluginCommand command = getCommand("statusnpc");
        if(command == null){
            getLogger().warning("[Startup - Command] Can't register command! Disabling plugin...");
            manager.disablePlugin(this);
            return;
        }
        command.setExecutor(new CmdStatusNPC(this));
        
        if(CommodoreProvider.isSupported()){
            Commodore commodore = CommodoreProvider.getCommodore(this);
            
            registerCompletions(commodore, command);
        }
        getLogger().info("[Startup - Command] Loaded command /statusnpc");
        
        getLogger().info("[Startup] Starting of StatusNPC complete!");
    }
    
    private void registerCompletions(Commodore commodore, PluginCommand command){
        try{
            //noinspection ConstantConditions
            LiteralCommandNode<?> cmd = CommodoreFileFormat.parse(this.getResource("command.commodore"));
            commodore.register(command, cmd);
        }catch(IOException ex){
            getLogger().warning("[Startup - Command] Couldn't load plugin. Commodore caused an exception!");
            ex.printStackTrace();
        }
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
    
    public Map<Integer, String> getLoaded(){
        return loaded;
    }
}
