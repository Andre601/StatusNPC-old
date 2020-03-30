package com.andre601.statusnpc.commands;

import com.andre601.statusnpc.StatusNPC;
import com.andre601.statusnpc.util.FormatUtil;
import com.andre601.statusnpc.util.OnlineStatus;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.text.TextComponent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@Command("statusnpc")
@Alias("snpc")
public class CmdStatusNPC extends CommandBase{
    
    private StatusNPC plugin;
    private FormatUtil formatUtil;
    
    public CmdStatusNPC(StatusNPC plugin){
        this.plugin = plugin;
        this.formatUtil = plugin.getFormatUtil();
    }
    
    @Default
    @SubCommand("help")
    public void sendHelp(final CommandSender sender){
        sender.sendMessage(plugin.getFormatUtil().getLines("Messages.Help"));
    }
    
    @SubCommand("set")
    @Completion({"#players", "#npcs"})
    @Permission("statusnpc.command.set")
    @WrongUsage("#invalidArgs")
    public void setNPC(final CommandSender sender, final Player target, Integer id){
        if(target == null || id == null){
            sender.sendMessage(formatUtil.getLine("Messages.Errors.FewArgs.Set"));
            return;
        }
        
        if(plugin.getNpcManager().getLoaded().containsKey(id)){
            sender.sendMessage(formatUtil.getLine("Messages.Errors.AlreadySet"));
            return;
        }
        
        NPC npc = plugin.getNpcManager().getNPC(id);
        if(npc == null){
            sender.sendMessage(formatUtil.getLine("Messages.Errors.InvalidNPC")
                    .replace("%id%", String.valueOf(id))
            );
            return;
        }
        
        if(!(npc.getEntity() instanceof Player)){
            sender.sendMessage(formatUtil.getLine("Messages.Errors.NPCNotPlayer")
                    .replace("%type%", npc.getEntity().getType().toString())
            );
            return;
        }
        
        if(plugin.getNpcManager().hasNPC(target.getUniqueId())){
            int oldId = plugin.getNpcManager().getNPCId(target);
            if(oldId >= 0){
                plugin.getNpcManager().updateNPC(target, oldId, id);
                sender.sendMessage(formatUtil.getLine("Messages.SetNPC")
                        .replace("%id%", String.valueOf(id))
                        .replace("%name%", npc.getName())
                        .replace("%player%", target.getName())
                        .replace("%uuid%", target.getUniqueId().toString())
                );
                return;
            }
        }
        
        plugin.getNpcManager().setNPCGlow(target.getUniqueId(), id, OnlineStatus.ONLINE, true);
        sender.sendMessage(formatUtil.getLine("Messages.SetNPC")
                .replace("%id%", String.valueOf(id))
                .replace("%name%", npc.getName())
                .replace("%player%", target.getName())
                .replace("%uuid%", target.getUniqueId().toString())
        );
    }
    
    @SubCommand("remove")
    @Completion("#players")
    @Permission("statusnpc.command.remove")
    @WrongUsage("#invalidArgs")
    public void removeNPC(final CommandSender sender, final Player target){
        if(target == null){
            sender.sendMessage(plugin.getFormatUtil().getLine("Messages.Errors.FewArgs.Remove"));
            return;
        }
        
        if(plugin.getNpcManager().hasNPC(target.getUniqueId())){
            plugin.getNpcManager().removeNPCGlow(plugin.getNpcManager().getNPCId(target), true);
            sender.sendMessage(formatUtil.getLine("Messages.RemovedNPC")
                    .replace("%player%", target.getName())
            );
        }else{
            sender.sendMessage(formatUtil.getLine("Messages.Errors.NotSet"));
        }
    }
    
    @SubCommand("list")
    @Permission("statusnpc.command.list")
    @WrongUsage("#invalidArgs")
    public void getLinkedNPCs(final Player player){
        player.spigot().sendMessage(ComponentSerializer.parse(getList()));
    }
    
    private String getList(){
        Map<NPC, UUID> npcs = plugin.getNpcManager().getAllNPC();
        TextComponent.Builder builder = TextComponent.builder(formatUtil.getLine("Messages.List.Title"));
        
        TextComponent.Builder list = TextComponent.builder();
        npcs.forEach((npc, uuid) -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = player.getName();
            if(name == null)
                name = plugin.getFormatUtil().getLine("Messages.List.Unknown");
            
            list.append(TextComponent.newline())
                    .append(TextComponent.builder()
                            .content(plugin.getFormatUtil().getLine("Messages.List.Syntax")
                                    .replace("%id%", String.valueOf(npc.getId()))
                                    .replace("%name%", npc.getName())
                                    .replace("%player%", name)
                                    .replace("%uuid%", uuid.toString())
                            ).hoverEvent(HoverEvent.showText(TextComponent.builder()
                                    .content(plugin.getFormatUtil().getLines("Messages.List.Hover")
                                            .replace("%id%", String.valueOf(npc.getId()))
                                            .replace("%name%", npc.getName())
                                            .replace("%player%", name)
                                            .replace("%uuid%", uuid.toString())
                                    )
                                    .build()
                            ))
                    );
        });
        
        return GsonComponentSerializer.INSTANCE.serialize(builder.append(list.build()).build());
    }
}
