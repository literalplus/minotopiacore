package io.github.xxyy.minotopiacore.misc;


@Deprecated //did never work, will never work nor will it be needed
public class APIHelper {
//	/**
//	 * syncs with db aka notifies ppl about new things on other servers
//	 * i.e. warns or bans.
//	 * @return true if anything was fetched
//	 */
//	public static boolean applyUpdates(){
//		boolean rtrn = false;
//		SqlUtil sql = MinoTopiaCore.instance().sqlu;
//		if(sql == null){ System.out.println("[MTS]Tried to apply updates before reload was complete."); return false; }
//		ResultSet rs = sql.executeQuery("SELECT * FROM "+sql.dbName+".mtc_sync WHERE timestamp >= "+((Calendar.getInstance().getTimeInMillis()-300000)/1000));
//		try {
//			while(rs.next()){
//				rtrn = true;
//				/*int id = rs.getInt("id"); */short actionId = rs.getShort("action_id");
//				int idThere = rs.getInt("id_there"); //long timestamp = rs.getLong("timestamp");
//				String additionalData = rs.getString("add_data");
//				switch(actionId){
//				case 1:
//					if(additionalData.equalsIgnoreCase("1")){//added
//						WarnInfo wi = WarnInfo.getById(idThere);
//						if(wi.id < 0)
//                         {
//                            continue; //We'll survive if a msg is not posted
//                        }
//						CommandHelper.broadcast(MinoTopiaCore.warnChatPrefix+"§e"+wi.plrName+"§6 wurde von §e"+wi.warnedByName+"§c gewarnt. §7{REMOTE}", "mtc.warns.adminmsg");
//						Bukkit.broadcastMessage(MinoTopiaCore.warnChatPrefix+"§e"+wi.plrName+"§c wurde gewarnt. Grund:");
//						Bukkit.broadcastMessage(MinoTopiaCore.warnChatPrefix+"§c=>§6"+ChatColor.translateAlternateColorCodes('&', wi.reason)+"§c<=");
//					}
//					break;
//				case 2:
//					if(additionalData.equalsIgnoreCase("1")){//added
//						BanInfo bi = BanInfo.getById(idThere);
//						if(bi.id < 0)
//                         {
//                            continue; //We'll survive if a msg is not posted
//                        }
//						BanHelper.broadcastBanChatMsg(bi);
//						Player target = Bukkit.getPlayerExact(bi.plrName);
//						if(target != null){
//							target.kickPlayer(BanHelper.getBanReasonForKick(bi, true));
//						}
//					}
//					break;
//				}
//			}
//		} catch (SQLException e) {
//			return false; //no one wants to know that
//		}
//		return rtrn;
//	}
//	/**
//	 * Cleans the sync table, as specified in table comment.
//	 */
//	public static void cleanSyncTable(){
//		SqlUtil sql = MinoTopiaCore.instance().sqlu;
//		if(sql == null) return; //no one cares!
//		sql.executeUpdate("DELETE FROM "+sql.dbName+".mtc_sync WHERE timestamp < "+((Calendar.getInstance().getTimeInMillis()-600000)/1000));//10m
//	}
//	/**
//	 * Notifies API listening servers that an action has been taken.
//	 * 0=other (will be ignored)
//	 * 1=warn {1=add}
//	 * 2=ban {1=ban}
//	 * @param idThere
//	 * @param type
//	 */
//	public static void sendUpdateToAPI(int idThere, byte type,String data){
//		SafeSql sql = MinoTopiaCore.instance().ssql;
//		if(sql == null){ System.out.println("[MTC] sql == null; wait 'til rld is finished."); return; }
//		sql.safelyExecuteUpdate("INSERT INTO "+sql.dbName+".mtc_sync SET action_id="+type+",id_there="+idThere+",add_data='"+data+"',timestamp="+(Calendar.getInstance().getTimeInMillis()/1000));
//	}
}
