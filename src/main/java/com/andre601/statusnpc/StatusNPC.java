package com.andre601.statusnpc;

import com.andre601.statusnpc.events.EssentialsEventManager;
import com.andre601.statusnpc.events.NPCEventManager;
import com.andre601.statusnpc.util.FileManager;
import com.andre601.statusnpc.util.FormatUtil;
import com.andre601.statusnpc.util.NPCManager;
import com.andre601.statusnpc.commands.CmdStatusNPC;
import com.andre601.statusnpc.events.PlayerEventManager;
import me.mattstudios.mf.base.CommandManager;
import me.rayzr522.jsonmessage.JSONMessage;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class StatusNPC extends JavaPlugin{
    
    private EssentialsVersion essentials = null;
    
    private boolean debug;
    
    private NPCManager npcManager;
    private FileManager fileManager;
    private FormatUtil formatUtil;
    
    private PluginManager manager;
    
    private final List<String> npcs = new ArrayList<>();
    
    @Override
    public void onLoad(){
        getLogger().info("[Startup] Loading StatusNPC v" + getDescription().getVersion());
        
        fileManager = new FileManager(this);
        
        npcManager = new NPCManager(this);
        formatUtil = new FormatUtil();
        
        send("[Files] Loading config.yml...");
        saveDefaultConfig();
        debug = getConfig().getBoolean("Debug", false);
        send("[&aFiles&7] config.yml successfully loaded!");
        
        send("[Files] Loading npcs.yml...");
        fileManager.loadFile();
        send("[&aFiles&7] npcs.yml successfully loaded!");
    }
    
    @Override
    public void onEnable(){
        long start = System.currentTimeMillis();
        send("Starting StatusNPC v%s", getDescription().getVersion());
        
        manager = Bukkit.getPluginManager();
        
        send("[Dependencies] Hooking into Citizens...");
        if(!manager.isPluginEnabled("Citizens")){
            send("[&cDependencies&7] Couldn't find Citizens! The plugin requires it to work.");
            send("[&cDependencies&7] Disabling StatusNPC...");
            manager.disablePlugin(this);
            return;
        }
        send("[&aDependencies&7] Successfully found Citizens! Continue loading...");
        
        resolveEssentialsDependency();
        
        send("[Events] Loading events...");
        new PlayerEventManager(this);
        sendDebug("[&aEvents&7] Loaded Player Events");
        
        new NPCEventManager(this);
        sendDebug("[&aEvents&7] Loaded NPC Events");
        
        send("[&aEvents&7] Successfully loaded all events!");
        
        send("[Command] Registering command /statusnpc (/snpc)...");
        setupCmdFramework();
        send("[&aCommand&7] Successfully registered command!");
        
        send("&aStartup of StatusNPC complete (Took %dms)!", System.currentTimeMillis() - start);
    }
    
    public boolean isEssentialsEnabled(){
        return essentials != null;
    }
    
    public boolean isDebug(){
        return debug;
    }
    
    public void sendDebug(String msg, Object... args){
        if(isDebug())
            send("[DEBUG] " + msg, args);
    }
    
    public void send(String msg, Object... args){
        getServer().getConsoleSender().sendMessage(formatUtil.formatString(
                "&7[&f" + getName() + "&7] " + msg, args
        ));
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
    
    public EssentialsVersion getEssentials(){
        return essentials;
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
                sender.sendMessage(formatUtil.formatString(
                        "&cYou don't have permissions to use this command!"
                ))
        );
        manager.getMessageHandler().register("cmd.no.exists", sender -> {
            if(sender instanceof Player){
                JSONMessage message = JSONMessage.create(formatUtil.formatString(
                        "&cInvalid arguments! Run "
                )).then(formatUtil.formatString(
                        "&7/snpc help"
                )).tooltip(formatUtil.formatString(
                        "&7Click to execute the command."
                )).runCommand(
                        "/snpc help"
                ).then(formatUtil.formatString(
                        " &cfor all commands."
                ));
                
                message.send((Player) sender);
            }else{
                sender.sendMessage(formatUtil.formatString(
                        "&cInvalid arguments! Run &7/snpc help &cfor all commands."
                ));
            }
        });
        manager.getMessageHandler().register("cmd.wrong.usage", sender -> {
            if(sender instanceof Player){
                JSONMessage message = JSONMessage.create(formatUtil.formatString(
                        "&cInvalid arguments! Run "
                )).then(formatUtil.formatString(
                        "&7/snpc help"
                )).tooltip(formatUtil.formatString(
                        "&7Click to execute the command."
                )).runCommand(
                        "/snpc help"
                ).then(formatUtil.formatString(
                        " &cfor all commands."
                ));
                
                message.send((Player) sender);
            }else{
                sender.sendMessage(formatUtil.formatString(
                        "&cInvalid arguments! Run &7/snpc help &cfor all commands."
                ));
            }
        });
        
        manager.getMessageHandler().register("#invalidArgs", sender -> {
            if(sender instanceof Player){
                JSONMessage message = JSONMessage.create(formatUtil.formatString(
                        "&cInvalid arguments! Run "
                )).then(formatUtil.formatString(
                        "&7/snpc help"
                )).tooltip(formatUtil.formatString(
                        "&7Click to execute the command."
                )).runCommand(
                        "/snpc help"
                ).then(formatUtil.formatString(
                        " &cfor all commands."
                ));
                
                message.send((Player) sender);
            }else{
                sender.sendMessage(formatUtil.formatString(
                        "&cInvalid arguments! Run &7/snpc help &cfor all commands."
                ));
            }
        });
        
        manager.register(new CmdStatusNPC(this));
    }
    
    private void resolveEssentialsDependency(){
        send("[Dependencies] Looking for Essentials (EssentialsX)...");
        
        Plugin ess = manager.getPlugin("Essentials");
        if(!manager.isPluginEnabled(ess)){
            send("[Dependencies] Essentials not found. Continue without it.");
            return;
        }
    
        send("[Dependencies] Found Essentials! Resolving version...");
        essentials = new EssentialsVersion(ess);
        
        if(essentials.getMajor() == -1){
            send("[Dependencies] Unable to resolve Essentials version. Continue without it.");
        }else{
            if(essentials.supportsCause())
                send("[Dependencies] Essentials version is at least 2.17.0! Enabled enhanced AFK handling.");
            
            send("[&aDependencies&7] Successfully hooked into %s", essentials.toString());
            new EssentialsEventManager(this);
        }
        
    }
    
    public static class EssentialsVersion{
        private int major;
        private int minor;
        private String rev;
        
        public EssentialsVersion(Plugin plugin){
            try{
                String[] versions = plugin.getDescription().getVersion().split("\\.");
                
                major = Integer.parseInt(versions[0]);
                minor = Integer.parseInt(versions[1]);
                rev = versions[2];
            }catch(NumberFormatException | ArrayIndexOutOfBoundsException ignored){
                major = -1;
                minor = -1;
                rev = "null";
            }
        }
    
        public int getMajor(){
            return major;
        }
    
        public boolean supportsCause(){
            return major >= 2 && minor >= 17;
        }
        
        @Override
        public String toString(){
            return String.format(
                    "Essentials v%d.%d.%s",
                    major,
                    minor,
                    rev
            );
        }
    }
}
