package io.github.xxyy.minotopiacore.misc.cmd;

import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.MTCCommandExecutor;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CommandTeam extends MTCCommandExecutor
{
    private static final HashMap<String, ArrayList<String>> groups = new HashMap<>();
    private static final HashMap<String, String> groupPrefixes = new HashMap<>();
    
    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        MTCHelper.sendLoc("XU-teamheader", sender, false);
        if(CommandTeam.groups.isEmpty())
        {
            boolean overridePrefixes = MTC.instance().getConfig().getBoolean("team.overridePrefixes");
            try{
                if(!PermissionsEx.isAvailable()){
                    sender.sendMessage("§cPermissionsEx ist nicht verfügbar.");
                    return true;
                }
                PermissionManager pMan = PermissionsEx.getPermissionManager();

                for(String groupName : ConfigHelper.getTeamGroupsInOrder())
                {
                    ArrayList<String> userNames = new ArrayList<>();
                    PermissionUser[] users = pMan.getUsers(groupName, false);
                    for(PermissionUser pUser : users){
                        userNames.add(pUser.getName());
                    }
                    CommandTeam.groups.put(groupName, userNames);
                    if(overridePrefixes){
                        CommandTeam.groupPrefixes.put(groupName, ChatColor.translateAlternateColorCodes('&', ConfigHelper.getTeamMap().get(groupName)));
                    }else{
                        CommandTeam.groupPrefixes.put(groupName, ChatColor.translateAlternateColorCodes('&', pMan.getGroup(groupName).getPrefix()));
                    }
                }
            }catch(Exception e)
            {
                sender.sendMessage("§cFehler beim Holen der Teammitglieder.");
                e.printStackTrace();
                System.out.println("PermissionsEx Hook failed. (CommandTeam)");
                return true;
            }
        }
        Set<String> groupKeys = CommandTeam.groups.keySet();
        for(int i = 0; i < groupKeys.size(); i++)
        {
           String groupName = ConfigHelper.getTeamGroupsInOrder().get(i);
           List<String> members = CommandTeam.groups.get(groupName);
           String groupPrefix = CommandTeam.groupPrefixes.get(groupName);
           String seperator = MTCHelper.loc("XU-teamseperator", senderName, false);
           Iterator<String> it = members.iterator();
           if(!it.hasNext()) { continue; }   
           String line = groupPrefix + " " + CommandTeam.getPlayerString(it.next(), senderName);
           while(it.hasNext())
           {
               line += seperator + CommandTeam.getPlayerString(it.next(), senderName);
           }
           sender.sendMessage(line);
        }
        return true;
    }
    
    private static String getPlayerString(String plrName, String senderName)
    {
        Player plr = Bukkit.getPlayerExact(plrName);
        if(plr == null) return MTCHelper.locArgs("XU-teamoffline", senderName, false, plrName);
        return MTCHelper.locArgs("XU-teamonline", senderName, false, plrName);
    }
    
}
