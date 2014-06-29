package io.github.xxyy.minotopiacore.helper;

import io.github.xxyy.minotopiacore.MTC;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


public final class LaterMessageHelper {
    private LaterMessageHelper() {

    }

    public static final File storFile = new File("plugins/"+MTC.instance().getName()+"/laterMessages.stor.yml");
    public static YamlConfiguration storage = YamlConfiguration.loadConfiguration(LaterMessageHelper.storFile);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static void addMessage(String senderName, String type, int type2, String msg, boolean delOnFirstRead, boolean sendMTCPrefix){
        int i = 0;
        while(LaterMessageHelper.storage.contains(senderName+"."+i)){
            i++;
        }
        LaterMessageHelper.storage.set(senderName+"."+i+".type", type);
        LaterMessageHelper.storage.set(senderName+"."+i+".type2", type2);
        LaterMessageHelper.storage.set(senderName+"."+i+".message", msg);
        LaterMessageHelper.storage.set(senderName+"."+i+".deleteonfirstread", delOnFirstRead);
        LaterMessageHelper.storage.set(senderName+"."+i+".prefix", sendMTCPrefix);
        LaterMessageHelper.save();
    }
    
    /**
     * pretty self-explaining :P
     * @param senderName
     * @return
     */
    public static boolean hasMessages(String senderName){
        return LaterMessageHelper.storage.contains(senderName);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Removes all messages that have both types.
     * @param type
     * @param type2
     */
    public static void removeType(String senderName, String type, int type2){
        if(!LaterMessageHelper.storage.contains(senderName)) return;
        boolean anyLeft = false;
        int i = 0;
        while(LaterMessageHelper.storage.contains(senderName+"."+i)){
            if(!type.equalsIgnoreCase(LaterMessageHelper.storage.getString(senderName+"."+i+".type","NOOO"))) {
                anyLeft = true;
                continue;
            }
            if(type2 != LaterMessageHelper.storage.getInt(senderName+"."+i+".type2",-1234)) {
                anyLeft = true;
                continue;
            }
            LaterMessageHelper.storage.set(senderName+"."+i, null);
        }
        if(!anyLeft) {
            LaterMessageHelper.storage.set(senderName, null);
        }
        LaterMessageHelper.save();
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Sends all pending messages and, if
     * specified, deletes them.
     * @param plrName Player
     * @return If any messages got printed
     */
    public static boolean sendMessages(CommandSender sender){
        String senderName = sender.getName();
        if(!LaterMessageHelper.storage.contains(senderName)) return false;
        int i = 0; 
        boolean anyLeft = false;
        if(!LaterMessageHelper.storage.contains(senderName+"."+i)) return false;
        while(LaterMessageHelper.storage.contains(senderName+"."+i)){
            MTCHelper.sendLoc(LaterMessageHelper.storage.getString(senderName+"."+i+".message","XU-undefined"), sender, LaterMessageHelper.storage.getBoolean(senderName+"."+i+".prefix",true));
            if(LaterMessageHelper.storage.getBoolean(senderName+"."+i+".deleteonfirstread",false)){
                LaterMessageHelper.storage.set(senderName+"."+i, null);
            } else {
                anyLeft = true;
            }
            i++;
        }
        if(!anyLeft) {
            LaterMessageHelper.storage.set(senderName, null);
        }
        LaterMessageHelper.save();
        return true;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private static void save(){
        try {
            LaterMessageHelper.storage.save(LaterMessageHelper.storFile);
        } catch (IOException e) {
            e.printStackTrace();
            //oh well
        }
    }
    
}
/*
xxyy98:
  0:
    type: <String> (i.e. C 4 clan)
    type2: <int> (i.e. 1 4 removedmsg)
    message: <String> (will be localized!)
    deleteonfirstread: <boolean>
    prefix: <boolean>
*/
