package io.github.xxyy.minotopiacore.warns;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.minotopiacore.MTC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WarnInfo {
	public int id = -1;
	public String plrName = "Unbekannt";
	public String warnedByName = "CONSOLE";
	public String reason = "Deine Mudda ist fett! (AKA nicht geladen)";
	public byte genericReasonId = 0;
	public long timestamp = -1;//in seconds
	public byte status = 0;
	
	public WarnInfo(int id){ this.id = id; }//for tagging of errors
	public WarnInfo(int id,String plrName, String warnedByName, String reason, byte genericReasonId, long timestamp, byte status){
		this.id = id; this.status = status;
		this.plrName = plrName; this.warnedByName = warnedByName; this.reason = reason;
		this.genericReasonId = genericReasonId; this.timestamp = timestamp;
	}
	/**
	 * Saves modifications on this object to db.
	 * @return success
	 */
	public boolean flush(){
		SafeSql sql = MTC.instance().ssql;
		if(sql == null) //no msg
        return false;
		return sql.safelyExecuteUpdate("UPDATE "+sql.dbName+".mtc_warns SET user_name=?,warned_by_name=?" +
				",timestamp="+this.timestamp+",generic_reason="+this.genericReasonId+",reason=?,status="+this.status+" WHERE id="+this.id,
				this.plrName,this.warnedByName,this.reason) >= 0;
	}
	/**
	 * Marks this warn as invalid.
	 * @return success
	 */
	public boolean markInvalid(){
		this.status = 1;
		return this.flush();
	}
	/**
	 * Flag this Warn 'UNKNOWN REASON'
	 * @return success
	 */
	public boolean markUnknownReason(){
		this.status = 2;
		return this.flush();
	}
	/**
	 * Removes this object from db. 
	 * ONLY USE IN EXCEPTIONAL CASES...WARNS ARE MEANT TO BE KEPT IN A CAGE FOR PROOF
	 * @return success
	 */
	public boolean nullify(){
		SafeSql sql = MTC.instance().ssql;
		if(sql == null) //no msg
        return false;
		return sql.executeUpdate("DELETE FROM "+sql.dbName+".mtc_warns WHERE id="+this.id);
	}
	@Override public String toString(){
		return "WarnInfo{id="+this.id+",plr="+this.plrName+",warnedBy="+this.warnedByName+",reason="+this.reason+"," +
				"genReason="+this.genericReasonId+",time="+(new SimpleDateFormat("dd.MM.yyyy/HH:mm:ss").format(this.timestamp*1000))+",status="+this.status+"}";
	}
	/**
	 * Creates a WarnInfo (AKA adds a warn for a plr)
	 * @param plrName Name of Player that will be warned
	 * @param warnedByName Name of the Player that executed the warn
	 * @param reason Reason
	 * @param genericReasonId nyi
	 * @return WarnInfo..if error, id will be negative
	 */
	public static WarnInfo create(String plrName, String warnedByName, String reason, byte genericReasonId){
		SafeSql sql = MTC.instance().ssql;
		if(sql == null) //no msg
        return new WarnInfo(-3);
		long currentTimestamp = (Calendar.getInstance().getTimeInMillis()/1000L);/* convert to unix time */
		boolean suc = sql.safelyExecuteUpdate("INSERT INTO "+sql.dbName+".mtc_warns SET user_name=?,warned_by_name=?" +
				",timestamp="+currentTimestamp+",generic_reason="+genericReasonId+",reason=?",
				plrName,warnedByName,reason) >= 0;
		if(!suc) return new WarnInfo(-3);
		ResultSet rs = sql.executeQuery("SELECT LAST_INSERT_ID()");
		if(rs == null){ System.out.println("§4[MTC] rs == null -> db down? (1)"); return new WarnInfo(-3); }
		int id = -4;
		try {
			rs.next();
			id = rs.getInt(1);
		} catch (SQLException e) {
//			sql.formatAndPrintException(e, "lol");
			return new WarnInfo(-3);
		}
//		APIHelper.sendUpdateToAPI(id, (byte)1, "1");
		return new WarnInfo(id,plrName,warnedByName,reason, genericReasonId, currentTimestamp,(byte)0);
	}
	/**
	 * fetches a WarnInfo from DB.
	 * @param id Unique Id you're lookin' for
	 * @return WarnInfo
	 */
	public static WarnInfo getById(int id){
		SafeSql sql = MTC.instance().ssql;
		if(sql == null) return new WarnInfo(-3);
		ResultSet rs = sql.executeQuery("SELECT * FROM "+sql.dbName+".mtc_warns WHERE id='"+id+"'");
		if(rs == null){ System.out.println("§4[MTC] rs == null -> db down? (3)"); return new WarnInfo(-3); }
		try {
			if(!rs.isBeforeFirst()) return new WarnInfo(-3);
			rs.next();
			String plrName = rs.getString("user_name");
			String warnedByName = rs.getString("warned_by_name");
			long timestamp = rs.getLong("timestamp");
			byte genericReasonId = rs.getByte("generic_reason");
			String reason = rs.getString("reason");
			byte status = rs.getByte("status");
			return new WarnInfo(id,plrName,warnedByName, reason, genericReasonId, timestamp,status);
		} catch (SQLException e) {
			e.printStackTrace();
			sql.formatAndPrintException(e, "[MTC] Warns-3");
			return new WarnInfo(-3);
		} 
	}
	/**
	 * fetches a WarnInfo from DB.
	 * @param plrName player
	 * @param ASC true: ascending by id; false: descending by id
	 * @return WarnInfo
	 */
	public static List<WarnInfo> getByName(String plrName,boolean ASC){
		SafeSql sql = MTC.instance().ssql;
		if(sql == null) //no msg
        return WarnInfo.getErrorList(-3);
		ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM "+sql.dbName+".mtc_warns WHERE user_name=? ORDER BY id "+(ASC ? "ASC" : "DESC"),plrName);
		if(rs == null){ System.out.println("§4[MTC] rs == null -> db down? (2)"); return WarnInfo.getErrorList(-3); }
		List<WarnInfo> lst = new ArrayList<>();
		try {
			if(!rs.isBeforeFirst()) return WarnInfo.getErrorList(-4);
			while(rs.next()){
				int id = rs.getInt("id");
				String warnedByName = rs.getString("warned_by_name");
				long timestamp = rs.getLong("timestamp");
				byte genericReasonId = rs.getByte("generic_reason");
				String reason = rs.getString("reason");
				byte status = rs.getByte("status");
				lst.add(new WarnInfo(id,plrName,warnedByName, reason, genericReasonId, timestamp, status));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sql.formatAndPrintException(e, "[MTC] Warns-1");
			return WarnInfo.getErrorList(-3);
		} 
		return lst;
	}
	/**
	 * for parsing errors
	 * @param errorCode id
	 * @return List<WarnInfo>
	 */
	public static List<WarnInfo> getErrorList(int errorCode){
		List<WarnInfo> lst = new ArrayList<>();
		lst.add(new WarnInfo(errorCode));
		return lst;
	}
	/**
	 * fetches the last warn given by plrName.
	 * Will NOT get flagged warns.
	 * @param warnedByName player
	 * @return WarnInfo
	 */
	public static WarnInfo getLastGivenByName(String warnedByName){
		SafeSql sql = MTC.instance().ssql;
		if(sql == null) return new WarnInfo(-3);
		ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM "+sql.dbName+".mtc_warns WHERE warned_by_name=? AND status=0 ORDER BY id DESC LIMIT 1",warnedByName);
		if(rs == null){ System.out.println("§4[MTC] rs == null -> db down? (2)"); return new WarnInfo(-3); }
		try {
			if(!rs.isBeforeFirst()) return new WarnInfo(-3);
			rs.next();
			int id = rs.getInt("id");
			String plrName = rs.getString("user_name");
			long timestamp = rs.getLong("timestamp");
			byte genericReasonId = rs.getByte("generic_reason");
			String reason = rs.getString("reason");
			byte status = rs.getByte("status");
			return new WarnInfo(id,plrName,warnedByName, reason, genericReasonId, timestamp, status);
		} catch (SQLException e) {
			e.printStackTrace();
			sql.formatAndPrintException(e, "[MTC] Warns-1");
			return new WarnInfo(-3);
		} 
	}
}
