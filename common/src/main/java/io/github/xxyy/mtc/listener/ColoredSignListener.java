package io.github.xxyy.mtc.listener;

import io.github.xxyy.mtc.chat.MTCChatHelper;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;


public final class ColoredSignListener implements Listener
{
    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=false)
    public void onSignChange(SignChangeEvent e){
        if(e.getPlayer().hasPermission("mtc.signcolor.all")){
            for(int i = 0; i <= 3; i++){
                e.setLine(i, ChatColor.translateAlternateColorCodes('&', e.getLine(i)));
            }
        }else if(e.getPlayer().hasPermission("mtc.signcolor.limited")){
            for(int i = 0; i <= 3; i++){
                e.setLine(i, MTCChatHelper.convertStandardColors(e.getLine(i)));
            }
        }
    }
}
