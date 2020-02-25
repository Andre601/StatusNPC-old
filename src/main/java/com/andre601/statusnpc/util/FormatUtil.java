package com.andre601.statusnpc.util;

import com.andre601.statusnpc.StatusNPC;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class FormatUtil{
    
    private StatusNPC plugin;
    
    public FormatUtil(StatusNPC plugin){
        this.plugin = plugin;
    }
    
    public String getLines(String path){
        return String.join("\n", formatLines(plugin.getConfig().getStringList(path)));
    }
    
    public String getLine(String path){
        return formatString(plugin.getConfig().getString(path));
    }
    
    private List<String> formatLines(List<String> lines){
        List<String> list = new ArrayList<>();
        for(String line : lines){
            list.add(formatString(line));
        }
        
        return list;
    }
    
    private String formatString(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
