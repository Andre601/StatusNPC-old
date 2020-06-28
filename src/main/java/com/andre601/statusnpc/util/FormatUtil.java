package com.andre601.statusnpc.util;

import com.andre601.statusnpc.StatusNPC;
import org.bukkit.ChatColor;

public class FormatUtil{
    
    private final StatusNPC plugin;
    
    public FormatUtil(StatusNPC plugin){
        this.plugin = plugin;
    }
    
    public String getLine(String path){
        return formatString(plugin.getConfig().getString(path));
    }
    
    public String formatString(String text, Object... args){
        return ChatColor.translateAlternateColorCodes('&', String.format(text, args));
    }
}
