package io.github.xxyy.minotopiacore.games.teambattle;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LeaveManager {
	private final File locationLocation = new File("plugins/MinoTopiaCore","teambattle_prevlocs.donotedit.yml");
	public YamlConfiguration fl = YamlConfiguration.loadConfiguration(this.locationLocation);
	private final List<String> loginUsedPlrs=new ArrayList<>();
	
	public LeaveManager(){}
	
	public void addPlayerToLoginUsed(String playerName){
		if(this.hasPlayerUsedLogin(playerName)) {
			System.out.println(":/");
			return;
		}
		this.loginUsedPlrs.add(playerName);
	}
	
	public void clearLocation(String playerName){
		this.fl.set("locations."+playerName, null);
		try {
			this.fl.save(this.locationLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean doesLocExist(String playerName){
		return this.fl.isSet("locations."+playerName);
	}
	
	public Location getLocFromName(String playerName){
		String path="locations."+playerName+".";
		int x = this.fl.getInt(path+"loc.x",0);
		int y = this.fl.getInt(path+"loc.y",75);
		int z = this.fl.getInt(path+"loc.z",0);
		float yaw = (this.fl.getInt(path+"loc.yaw",0))/100;
		float pitch = (this.fl.getInt(path+"loc.pitch",0))/100;
		String worldName = this.fl.getString(path+"loc.worldName");
		return new Location(Bukkit.getWorld(worldName),x,y,z,yaw,pitch);
	}
	
	public String getWorldName(String playerName){
		if(!this.doesLocExist(playerName)) return "world";
		return this.fl.getString("locations."+playerName+".loc.worldName","world");
	}
	
	public boolean hasPlayerUsedLogin(String playerName){
		return this.loginUsedPlrs.contains(playerName);
	}
	
	public void removePlayerFromLoginUsed(String playerName){
		this.loginUsedPlrs.remove(playerName);
	}
	
	public void writePlayerNode(Location loc,Player plr){
		String path="locations."+plr.getName()+".";
		this.fl.set(path+"timestamp", (new SimpleDateFormat("yyMMddHHmmssZ")).format(Calendar.getInstance().getTime()));
		this.fl.set(path+"loc.x", loc.getBlockX());
		this.fl.set(path+"loc.y", loc.getBlockY());
		this.fl.set(path+"loc.z", loc.getBlockZ());
		this.fl.set(path+"loc.yaw", loc.getYaw()*100);
		this.fl.set(path+"loc.pitch", loc.getPitch()*100);
		this.fl.set(path+"loc.worldName", loc.getWorld().getName());
		try {
			this.fl.save(this.locationLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
