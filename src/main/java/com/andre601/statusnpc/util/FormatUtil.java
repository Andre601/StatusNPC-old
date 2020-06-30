package com.andre601.statusnpc.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class FormatUtil{
    
    public FormatUtil(){
    }
    
    public String formatString(String text, Object... args){
        return ChatColor.translateAlternateColorCodes('&', String.format(text, args));
    }
    
    public void sendMsg(CommandSender sender, JSONMessage message, String... lines){
        if(sender instanceof Player){
            message.send((Player)sender);
        }else{
            Arrays.stream(lines)
                  .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                  .forEach(sender::sendMessage);
        }
    }
}
