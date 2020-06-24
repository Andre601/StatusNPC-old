package com.andre601.statusnpc.util;

import com.andre601.statusnpc.StatusNPC;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager{
    private final File npcs;
    private YamlConfiguration npcConfig = null;
    
    public FileManager(StatusNPC plugin){
        npcs = new File(plugin.getDataFolder(), "npcs.yml");
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadFile(){
        try{
            npcs.createNewFile();
            npcConfig = YamlConfiguration.loadConfiguration(npcs);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public void save(){
        try{
            npcConfig.save(npcs);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public YamlConfiguration getNpcConfig(){
        return npcConfig;
    }
}
