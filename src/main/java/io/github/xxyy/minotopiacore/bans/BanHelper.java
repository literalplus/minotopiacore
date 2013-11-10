package io.github.xxyy.minotopiacore.bans;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;


public class BanHelper {
	public static volatile HashMap<String,BanInfo> banCache = new HashMap<>();
	public static void broadcastBanChatMsg(BanInfo bi){
//		String timeMsg = "§lpermanent";
//		if(bi.banExpiryTimestamp > 0){
//			timeMsg = "bis §6"+(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(bi.banExpiryTimestamp*1000L))+"";
//		}
		CommandHelper.broadcast(MTC.banChatPrefix+"§6"+bi.plrName+"§c wurde von §6"+bi.bannedByName+"§c gebannt:", "mtc.ban.adminmsg");
//		Bukkit.broadcastMessage(MinoTopiaCore.banChatPrefix+"§6"+bi.plrName+"§c wurde "+timeMsg+" §cgebannt. Grund:\n"
//				+ MinoTopiaCore.banChatPrefix+"§c=>§6"+bi.reason+"§c<=");
		
	}
	
	public static boolean deleteBan(String plrName){
		BanInfo bi = BanHelper.getBanInfoByPlayerName(plrName);
		BanHelper.banCache.remove(plrName);
		return bi.nullify();
	}
	
	public static BanInfo getBanInfoByPlayerName(String plrName){
		if(BanHelper.banCache.containsKey(plrName)){
			BanInfo bi = BanHelper.banCache.get(plrName);
			if(bi == null) return BanInfo.getByName(plrName);
            else if(bi.id < 0) return BanInfo.getByName(plrName);
			return bi;
		}
        BanInfo bi = BanInfo.getByName(plrName);
        BanHelper.banCache.put(plrName, bi);
        return bi;
	}
	
	public static String getBanReasonForKick(BanInfo bi,boolean justBanned){
		String verb = "bist ";
		if(justBanned) {
            verb = "wurdest soeben ";
        }
		return "§cDu "+verb
				+((bi.banExpiryTimestamp <= 0) ? "§lpermanent" : ("bis §e"+new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(bi.banExpiryTimestamp*1000/*convert back to millis*/)))+
				" §cgebannt! §4Grund:\n" +
				"§b"+bi.reason+"\n\n" +
				"§6Unban im Forum: §bminotopia.me\n" +
				"§9Deine Warns: §bminotopia.me?p=5&n="+bi.plrName;
	}
	
	/**
	 * Parses a string and returns a unix date (MILLISECONDS!).
	 * Format: 0y Years; 0M Months; 0w Weeks; 0d Days; 0h Hours; 0m Minutes; 0s Seconds 
	 * @param str String to parse
	 * @return long
	 */
	public static long getMillisFromRelativeString(String str){
		if(str.length() == 0) return Calendar.getInstance().getTimeInMillis();
		long SecondsToAdd = 0;
		String currNum = "";
		for(byte i = 0; i < str.length(); i++){
			char cr = str.charAt(i);
			if(StringUtils.isNumeric(cr+"")){
				currNum += cr;
				continue;
			}
			if(cr == 's'){
				int num = 0;
				try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
				SecondsToAdd += num;
				currNum = "";
				continue;
			}else if(cr == 'm'){
				int num = 0;
				try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
				SecondsToAdd += num*60L;
				currNum = "";
				continue;
			}else if(cr == 'h'){
				int num = 0;
				try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
				SecondsToAdd += num*3600L;
				currNum = "";
				continue;
			}else if(cr == 'd'){
				int num = 0;
				try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
				SecondsToAdd += num*86400L;
				currNum = "";
				continue;
			}else if(cr == 'w'){
				int num = 0;
				try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
				SecondsToAdd += num*604800L;
				currNum = "";
				continue;
			}else if(cr == 'M'){
				int num = 0;
				try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
				SecondsToAdd += num*2592000L;
				currNum = "";
				continue;
			}else if(cr == 'y'){
				int num = 0;
				try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
				SecondsToAdd += num*31536000L;
				currNum = "";
				continue;
			} else
                return -2;
		}//end for
		if(!currNum.isEmpty()){
			int num = 0;
			try { num = Integer.parseInt(currNum); } catch (NumberFormatException e) { return -1; }
			SecondsToAdd += num;
			currNum = "";
		}
		return Calendar.getInstance().getTimeInMillis()+(SecondsToAdd*1000);
	}
	
	
	public static boolean isBanned(String plrName){
		BanInfo bi = BanHelper.getBanInfoByPlayerName(plrName);
		return bi.id > 0;//< 0 -> error or not found; > 0 -> id 
	}
	
	public static BanInfo setBanned(String plrName, String bannedByName, String reason, byte genericReasonId, long banExpiryTimestamp){
		BanInfo bi;
		if(!BanHelper.isBanned(plrName)){
			bi = BanInfo.create(plrName, bannedByName, reason, genericReasonId, banExpiryTimestamp);
		}else{
			bi = BanHelper.getBanInfoByPlayerName(plrName);
			bi.plrName=plrName; bi.bannedByName = bannedByName;
			bi.reason = reason; bi.genericReasonId = genericReasonId;
			bi.banExpiryTimestamp = banExpiryTimestamp;
			bi.flush();
		}
		BanHelper.banCache.put(plrName, bi);
		return bi;
	}
}