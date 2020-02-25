package com.andre601.statusnpc.commands;

import com.andre601.statusnpc.StatusNPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdStatusNPC implements CommandExecutor{
    
    private StatusNPC plugin;
    
    public CmdStatusNPC(StatusNPC plugin){
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player)sender;
            
            if(!player.hasPermission("statusnpc.use")){
                player.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.NoPerms"));
                return true;
            }
            
            if(args.length == 0){
                sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.FewArgs.Other"));
                return true;
            }
    
            if(args[0].equalsIgnoreCase("help")){
                sender.sendMessage(plugin.getFormatUtil().getLines("Messages.Help"));
                return true;
            }else
            if(args[0].equalsIgnoreCase("set")){
                if(args.length < 3){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.FewArgs.Set"));
                    return true;
                }
        
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidPlayer"));
                    return true;
                }
        
                try{
                    int id = Integer.parseInt(args[2]);
                    plugin.getNpcManager().saveNPC(player, target, id);
                    return true;
                }catch(NumberFormatException ex){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidNPC").replace("%id%", args[2]));
                    return true;
                }
        
            }else
            if(args[0].equalsIgnoreCase("remove")){
                if(args.length < 2){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.FewArgs.Remove"));
                    return true;
                }
        
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidPlayer"));
                    return true;
                }
        
                plugin.getNpcManager().deleteNPC(player, target);
            }else{
                sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidArgs"));
                return true;
            }
        }else{
            if(args.length == 0){
                sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.FewArgs.Other"));
                return true;
            }
    
            if(args[0].equalsIgnoreCase("help")){
                sender.sendMessage(plugin.getFormatUtil().getLines("Messages.Help"));
                return true;
            }else
            if(args[0].equalsIgnoreCase("set")){
                if(args.length < 3){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.FewArgs.Set"));
                    return true;
                }
        
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidPlayer"));
                    return true;
                }
        
                try{
                    int id = Integer.parseInt(args[2]);
                    plugin.getNpcManager().saveNPC(sender, target, id);
                    return true;
                }catch(NumberFormatException ex){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidNPC").replace("%id%", args[2]));
                    return true;
                }
        
            }else
            if(args[0].equalsIgnoreCase("remove")){
                if(args.length < 2){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.FewArgs.Remove"));
                    return true;
                }
        
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidPlayer"));
                    return true;
                }
        
                plugin.getNpcManager().deleteNPC(sender, target);
            }else{
                sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.InvalidArgs"));
                return true;
            }
        }
        return true;
    }
}
