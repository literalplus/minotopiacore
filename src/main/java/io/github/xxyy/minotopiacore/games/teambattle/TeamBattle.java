package io.github.xxyy.minotopiacore.games.teambattle;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;


public class TeamBattle {
	private static TeamBattle instance;
	@Deprecated
	private final boolean gameRunning=false;
	private final List<Player> playersBlue=new ArrayList<>();
	private final List<Player> playersRed=new ArrayList<>();
	private final List<Player> playersLobby=new ArrayList<>();
	private short pointsBlue=0; //wins after 50 points, 1point/kill
	private short pointsRed=0; 
	public static int winPoints=20;
	public static int betterKitPoints=10;
	public static int maxPlayers=16;
	private final File configLocation = new File("plugins/MinoTopiaCore","teambattle.cfg.yml");
	public YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.configLocation);
	private final HashMap<Player,Location> prevLocations = new HashMap<>();
	private Location spawnBlue;
	private Location spawnRed;
	private Location lobby;
	public static final String chatPrefix = "§aтᴇᴀмвᴀттʟᴇ§7";
	public final String reloadTpMsg = TeamBattle.chatPrefix+" Du wurdest wegen einem Reload zu deiner vorherigen Position teleportiert.";
	public static int maxPointDifferenceForBetterKit=5;//very short name
	public boolean isGameEndInProgress=false; //to prevent kills when game ends -> kit
	
	public boolean isBetterKit=false;//if a team has more than 10p
	public TeamBattleTeams betterKitTeam=TeamBattleTeams.None;
	
	public static LeaveManager leaveMan=new LeaveManager();
	
	/**
	 * public TeamBattle()
	 * Initializes TeamBattle.
	 * @author xxyy98
	 */
	public TeamBattle(){
		TeamBattle.instance=this;
		//Initialization
		this.initConfig();
		
		try {
			this.cfg.save(this.configLocation);
		} catch (IOException e) {
			MTC.instance().getLogger().log(Level.SEVERE, "Could not save config to " + this.configLocation);
			e.printStackTrace();
			this.fetchConfigValues();//to avoid bugs; default values will be used
		}
		
		this.fetchConfigValues();
	}
	
	/**
	 * public void addAllLobbyPlayersToGame()
	 *  Adds all players in queue to the game,
	 *  with a delay of 3 ticks between teleports.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void addAllLobbyPlayersToGame(){
		this.addNextLobbyPlayerToGame();
		if(this.arePlayersInLobby()){
			Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableTpNextPlayerFromLobby(), 3);
		}
	}
	
	/**
	 * public void addNextLobbyPlayerToGame()
	 *  Adds the first player it finds in the lobby to the game.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void addNextLobbyPlayerToGame(){
		if(this.lobby == null) return;
		if(!this.arePlayersInLobby()) return;
		
		Player plr = this.playersLobby.get(0);
		if(plr==null) return;
		
		plr.leaveVehicle();
		plr.setHealth(plr.getMaxHealth());
		
		if(!this.addPlayerToGame(plr)){
			plr.sendMessage(TeamBattle.chatPrefix+" Du konntest dem Spiel nicht hinzugefügt werden.");
			return;
		}
		if(!this.tpPlayerToTeamSpawn(plr)){
			this.removePlayerFromGame(plr);
			this.tpPlayerToPrevLocation(plr);
			plr.sendMessage(TeamBattle.chatPrefix+" Du konntest nicht in die Arena teleportiert werden.");
			return;
		}
		
		this.printJoinInfoToPlayer(plr);
		this.giveKitToPlayer(plr);
		this.playersLobby.remove(plr);
	}
	
	/**
	 * public boolean addPlayerToGame(Player plr)
	 * Adds a new Player to the game.
	 * If null is given, returns false.
	 * If plr is already in list, returns true.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr The player to add
	 * @return true, if added/already in list, false if null or error while adding
	 * @see List.add(T)
	 */
	public boolean addPlayerToGame(Player plr){
		if(plr == null) return false;
		if(this.isPlayerInGame(plr)) return true;
		this.notifyPlayersJoinLeave(plr, true);
		if(this.getNextTeam() == TeamBattleTeams.Blue) return this.playersBlue.add(plr);
        return this.playersRed.add(plr);
	}
	
	/**
	 * 	public boolean addPlayerToLobby(Player plr)
	 * Adds plr to queue.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr
	 * @return
	 */
	public boolean addPlayerToLobby(Player plr){
		if(this.isPlayerInGame(plr)) return false;
		if(this.lobby == null) return false;
		if(this.playersLobby.contains(plr)) return false;
		return this.playersLobby.add(plr);
	}
	
	/**
	 * public void addTeamPoint(TeamBattleTeams team)
	 * Adds a point to team.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team Team
	 */
	public void addTeamPoint(TeamBattleTeams team){
		if(team == TeamBattleTeams.Red){
			this.pointsRed++;
			if(this.pointsRed >= TeamBattle.winPoints){
				this.doEndGame(team);
			}else if(!this.isBetterKit && this.pointsRed >= TeamBattle.betterKitPoints && ((this.pointsRed - this.pointsBlue) >= TeamBattle.maxPointDifferenceForBetterKit)){
				this.giveTeamBetterKit(this.invertTeam(team));
				this.notifyPlayersBetterKit(this.invertTeam(team));
				this.isBetterKit=true;
			}
		}else{
			this.pointsBlue++;
			if(this.pointsBlue >= TeamBattle.winPoints){
				this.doEndGame(team);
			}else if(!this.isBetterKit && this.pointsBlue >= TeamBattle.betterKitPoints && ((this.pointsBlue - this.pointsRed) >= TeamBattle.maxPointDifferenceForBetterKit)){
				this.giveTeamBetterKit(this.invertTeam(team));
				this.notifyPlayersBetterKit(this.invertTeam(team));
				this.isBetterKit=true;
			}
		}
	}
	
	/**
	 * public boolean arePlayersInLobby()
	 * Checks if there are players waiting in the queue.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @return true if some1 is waiting
	 */
	public boolean arePlayersInLobby(){
		return !(this.playersLobby.size() == 0);
	}
	
	/**
	 * public void checkEmptyTeamsAndReset()
	 * checks if any team is empty and prints a message if.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void checkEmptyTeamsAndReset(){
		
		if(this.playersBlue.size() <= 0){
			this.resetPoints();
			Bukkit.broadcastMessage(TeamBattle.chatPrefix+" §7Das Team "+this.getChatTeamName(TeamBattleTeams.Blue)+" §7hat den §3/war§7 aufgegeben!");
			Bukkit.broadcastMessage(TeamBattle.chatPrefix+" §8/ＷＡＲ ＪＯＩＮ");
			this.isBetterKit=false;
			this.betterKitTeam=TeamBattleTeams.None;
			List<Player> plrs = this.getAllPlayers();
			for(int i=0;i<plrs.size();i++){
				CommandHelper.clearInv(plrs.get(i));
				this.giveKitToPlayer(plrs.get(i));
			}
		}else if(this.playersRed.size() <= 0){
			this.resetPoints();
			Bukkit.broadcastMessage(TeamBattle.chatPrefix+" §7Das Team "+this.getChatTeamName(TeamBattleTeams.Red)+" §7hat den §3/war§7 aufgegeben!");
			Bukkit.broadcastMessage(TeamBattle.chatPrefix+" §8/ＷＡＲ ＪＯＩＮ");
			this.isBetterKit=false;
			this.betterKitTeam=TeamBattleTeams.None;
			List<Player> plrs = this.getAllPlayers();
			for(int i=0;i<plrs.size();i++){
				CommandHelper.clearInv(plrs.get(i));
				this.giveKitToPlayer(plrs.get(i));
			}
		}
	}
	
	/**
	 * public void doBetterKit(TeamBattleTeams team)
	 *  Gives team the better kit.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team The team
	 */
	public void doBetterKit(TeamBattleTeams team){
		this.isBetterKit=true;
		this.betterKitTeam=team;
	}
	
	/**
	 * public void doEndGame(TeamBattleTeams winner)
	 *  Ends the current game, teleports all players to their PrevLocation 
	 *  and broadcasts a message to all players on the server.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param winner The winning Team (will be used in message)
	 */
	public void doEndGame(TeamBattleTeams winner){
		this.isGameEndInProgress=true;
		//List<Player> plrs=this.getAllPlayers();
		this.resetPoints();
		this.tpAllPlayersToPrevLoc(false);
		this.notifyPlayersGameEnd();
		Bukkit.broadcastMessage(TeamBattle.chatPrefix+" §8Das Team "+this.getChatTeamName(winner)+"§8 hat bei §3/war§8 gewonnen!");
		this.addAllLobbyPlayersToGame();
		//CommandHelper.clearInvList(plrs);
		this.isGameEndInProgress=false;
	}
	
	/**
	 * public void fetchConfigValues()
	 *  Gets static values from config (to be run when config is reloaded)
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void fetchConfigValues(){
		TeamBattle.winPoints=this.cfg.getInt("options.pointsToWin",20);
		TeamBattle.betterKitPoints=this.cfg.getInt("options.pointsForBetterKit",10);
		TeamBattle.maxPointDifferenceForBetterKit=this.cfg.getInt("options.maxPointDifferenceForBetterKit",5);
		TeamBattle.maxPlayers=this.cfg.getInt("options.maxPlayers",16);
		this.spawnBlue = new Location(Bukkit.getWorld(this.cfg.getString("options.spawn.blue.worldName","world")), this.cfg.getInt("options.spawn.blue.x",0), this.cfg.getInt("options.spawn.blue.y",0), this.cfg.getInt("options.spawn.blue.z",0),this.cfg.getInt("options.spawn.blue.yaw",0),this.cfg.getInt("options.spawn.blue.pitch",0));
		this.spawnRed = new Location(Bukkit.getWorld(this.cfg.getString("options.spawn.red.worldName","world")), this.cfg.getInt("options.spawn.red.x",0), this.cfg.getInt("options.spawn.red.y",0), this.cfg.getInt("options.spawn.red.z",0),this.cfg.getInt("options.spawn.red.yaw",0),this.cfg.getInt("options.spawn.red.pitch",0));
		this.lobby = new Location(Bukkit.getWorld(this.cfg.getString("options.lobby.worldName","world")), this.cfg.getInt("options.lobby.x",0), this.cfg.getInt("options.lobby.y",0), this.cfg.getInt("options.lobby.z",0),this.cfg.getInt("options.lobby.yaw",0),this.cfg.getInt("options.lobby.pitch",0));
		TeamBattleSign.getSignLoc();
	}
	
	/**
	 * public void finish()
	 * Saves config and does some tasks.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void finish(){
		this.saveConfig();
	}
	
	/**
	 * public List<Player> getAllPlayers()
	 * Joins players of both teams into a single list.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @return playerBlaue + playersRed
	 */
	public List<Player> getAllPlayers(){
		List<Player> rtrn = new ArrayList<>(this.playersBlue);
		rtrn.addAll(this.playersRed);
		//System.out.println(Arrays.toString(rtrn.toArray()));
		return rtrn;
	}
	
	/**
	 * public String getChatTeamName(TeamBattleTeams team)
	 * Gets the name of team for the chat, with color codes.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team Team
	 * @return example: §cROT
	 */
	public String getChatTeamName(TeamBattleTeams team){
		return this.getTeamChatColor(team)+this.getTeamName(team);
	}
	
	/**
	 * public String getInternalTeamName(TeamBattleTeams team)
	 * Gets the internal team name of team (i.e. red or blue)
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team The team
	 * @return red/blue
	 */
	public String getInternalTeamName(TeamBattleTeams team){
		if(team == TeamBattleTeams.Blue) return "blue";
        return "red";
	}
	
	/**
	 * public TeamBattleTeams getNextTeam()
	 * Gets the team the next player should be in
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @return TeamBattleTeams
	 */
	public TeamBattleTeams getNextTeam(){
		if(this.playersBlue.size() < this.playersRed.size()) return TeamBattleTeams.Blue;
        if(this.playersBlue.size() == this.playersRed.size()){
        	int rand = (new Random()).nextInt(2);
        	if(rand == 0) return TeamBattleTeams.Red;
            return TeamBattleTeams.Blue;
        }
        return TeamBattleTeams.Red;
	}
	
	/**
	 * public String getPlayerChatColor(Player plr)
	 * Gets the chat color of the team plr is in.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr Player
	 * @return §<color code>
	 * @see getTeamChatColor()
	 */
	public String getPlayerChatColor(Player plr){
		return this.getTeamChatColor(this.getPlayerTeam(plr));
	}
	
	/**
	 * public List<Player> getPlayerListFromTeam(TeamBattleTeams team)
	 * Gets players currently in game.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team Team
	 * @return List<Player>
	 */
	public List<Player> getPlayerListFromTeam(TeamBattleTeams team){
		if(team == TeamBattleTeams.Blue) return this.playersBlue;
        return this.playersRed;
	}
	
	/**
	 * public TeamBattleTeams getPlayerTeam(Player plr)
	 * gets the TeamBattleTeams plr is in.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr THE player
	 * @return teamBattleTeams.Red/Blue/None
	 */
	public TeamBattleTeams getPlayerTeam(Player plr){
		if(this.playersRed.contains(plr)) return TeamBattleTeams.Red;
		if(this.playersBlue.contains(plr)) return TeamBattleTeams.Blue;
		return TeamBattleTeams.None;
	}
	
	public Location getPrevLocation(Player plr){
		return this.prevLocations.get(plr);
	}
	
	/**
	 * public String getTeamChatColor(TeamBattleTeams team)
	 * Gets the chat color for team. (i.e. §c)
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team Team
	 * @return § + color from config
	 */
	public String getTeamChatColor(TeamBattleTeams team){
		return "§" + this.cfg.getString("options.teams."+this.getInternalTeamName(team)+".chatcolor");
	}
	
	/**
	 * public String getTeamName(TeamBattleTeams team)
	 * gets the name specified for team from config
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team The Team
	 * @return String
	 */
	public String getTeamName(TeamBattleTeams team){
		return this.cfg.getString("options.teams."+this.getInternalTeamName(team)+".name");
	}
	
	/**
	 * public short getTeamPoints(TeamBattleTeams team)
	 * Gets the team's points.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team The team to check
	 * @return short
	 */
	public short getTeamPoints(TeamBattleTeams team){
		if(team == TeamBattleTeams.Blue) return this.pointsBlue;
        return this.pointsRed;
	}
	
	/**
	 * public boolean giveBetterKitToPlayer(Player plr)
	 * gives the better kit to plr.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr The player
	 * @return success
	 */
    @SuppressWarnings("deprecation")
   	public boolean giveBetterKitToPlayer(Player plr){
		
		try {
			for(int i=0;i<=8;i++){
				Object obj=this.cfg.get("options.betterkit.slot"+i);
				if(!(obj instanceof ItemStack))
                {
                    continue;
                }
				plr.getInventory().setItem(i, (ItemStack)obj);
			}
			Object boots=this.cfg.get("options.betterkit.boots");
			Object leggings=this.cfg.get("options.betterkit.leggings");
			Object chestplate=this.cfg.get("options.betterkit.chestplate");
			if(boots instanceof ItemStack)
            {
                plr.getInventory().setBoots((ItemStack)boots);
            }
			if(leggings instanceof ItemStack)
            {
                plr.getInventory().setLeggings((ItemStack)leggings);
            }
			if(chestplate instanceof ItemStack)
            {
                plr.getInventory().setChestplate((ItemStack)chestplate);
            }
			boolean isLeather=this.cfg.getBoolean("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.isLeather",true);
			if(isLeather){
				int r=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.color.r",0);
				int g=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.color.g",0);
				int b=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.color.b",0);
				ItemStack is=new ItemStack(Material.LEATHER_HELMET);
				LeatherArmorMeta meta=(LeatherArmorMeta)is.getItemMeta();
				meta.setColor(Color.fromRGB(r, g, b));
				is.setItemMeta(meta);
				plr.getInventory().setHelmet(is);
			}else{
				int hatId=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.id",298);
				int hatDmg=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.data",0);
				plr.getInventory().setHelmet(new ItemStack(hatId,1,(short)hatDmg));
			}
		} catch (Exception e) {
			System.out.println("Error when giving a player his better TeamBattle kit :(");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * public boolean giveKitToPlayer(Player plr)
	 * gives the player kit to plr.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr The player

     *
     * @return success
     * @deprecated TeamBattle is not really used any more.
     */
    @Deprecated
   	public boolean giveKitToPlayer(Player plr){
		if(!this.isPlayerInGame(plr)) return false;
		//System.out.println("Giving kit to player "+plr.getDisplayName()+"!!");
		if(this.getPlayerTeam(plr) == this.betterKitTeam) return this.giveBetterKitToPlayer(plr);
		
		try {
			for(int i=0;i<=8;i++){
				Object obj=this.cfg.get("options.kit.slot"+i);
				if(!(obj instanceof ItemStack))
                {
                    continue;
                }
				plr.getInventory().setItem(i, (ItemStack)obj);
			}
			Object boots=this.cfg.get("options.kit.boots");
			Object leggings=this.cfg.get("options.kit.leggings");
			Object chestplate=this.cfg.get("options.kit.chestplate");
			if(boots instanceof ItemStack)
            {
                plr.getInventory().setBoots((ItemStack)boots);
            }
			if(leggings instanceof ItemStack)
            {
                plr.getInventory().setLeggings((ItemStack)leggings);
            }
			if(chestplate instanceof ItemStack)
            {
                plr.getInventory().setChestplate((ItemStack)chestplate);
            }
			boolean isLeather=this.cfg.getBoolean("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.isLeather",true);
			if(isLeather){
				int r=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.color.r",0);
				int g=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.color.g",0);
				int b=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.color.b",0);
				ItemStack is=new ItemStack(Material.LEATHER_HELMET);
				LeatherArmorMeta meta=(LeatherArmorMeta)is.getItemMeta();
				meta.setColor(Color.fromRGB(r, g, b));
				is.setItemMeta(meta);
				plr.getInventory().setHelmet(is);
			}else{
				int hatId=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.id",298);
				int hatDmg=this.cfg.getInt("options.teams."+this.getInternalTeamName(this.getPlayerTeam(plr))+".hat.data",0);
				plr.getInventory().setHelmet(new ItemStack(hatId,1,(short)hatDmg));
			}
		} catch (Exception e) {
			System.out.println("Error when giving a player his TeamBattle kit :(");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void giveTeamBetterKit(TeamBattleTeams team){
		List<Player> plrs;
		if(this.isBlue(team))
        {
            plrs=this.playersBlue;
        }
        else if(this.isRed(team))
        {
            plrs=this.playersRed;
        }
        else return;
		//String msg=TeamBattle.chatPrefix+" Das andere Team hat mehr als §3"+TeamBattle.betterKitPoints+"§6 Punkte. Daher hat dein Team ein besseres Kit erhalten.";
		for(int i = 0;i<plrs.size();i++){
			Player p = plrs.get(i);
			CommandHelper.clearInv(p);
			this.giveBetterKitToPlayer(p);
			//p.sendMessage(msg);
		}
	}
	
	/**
	 * public TeamBattleTeams invertTeam(TeamBattleTeams team)
	 * Negates the team. if team==blue, returns red and vice versa.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team The Team
	 * @return Inverted Team
	 */
	public TeamBattleTeams invertTeam(TeamBattleTeams team){
		if(team == TeamBattleTeams.Blue) return TeamBattleTeams.Red;
        return TeamBattleTeams.Blue;
	}
	
	/**
	 * public boolean isBlue(TeamBattleTeams team)
	 * If team is TeamBattleTeams.blue, returns true.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team team to check
	 * @return If team is TeamBattleTeams.blue, returns true.
	 */
	public boolean isBlue(TeamBattleTeams team){
		if(team == TeamBattleTeams.Blue) return true;
        return false;
	}
	
	/**
	 * public boolean isGameRunning()
	 * Asks if the game is running ATM.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @return boolean
	 * @deprecated
	 */
	@Deprecated
	public boolean isGameRunning(){
		return this.gameRunning;
	}
	
	/**
	 * public boolean isPlayerInGame(Player plr)
	 * Checks if plr is currently in the game.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr The player to check
	 * @return boolean
	 * @see List.contains(Object)
	 */
	public boolean isPlayerInGame(Player plr){
		if(this.isGameEndInProgress) return false;
		return this.isPlayerInGameIgnoreEnd(plr);
	}
	
	/**
	 * public boolean isPlayerInGameIgnoreEnd(Player plr)
	 * Ignores gameEnd. DO NOT USE
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr Player
	 * @return boolean
	 */
	public boolean isPlayerInGameIgnoreEnd(Player plr){
		if(this.playersRed.contains(plr)) return true;
		if(this.playersBlue.contains(plr)) return true;
		return false;
	}
	
	/**
	 * public boolean isPlayerInQueue(Player plr)
	 * Checks if plr is in queue. 
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr Player to check
	 * @return boolean
	 */
	public boolean isPlayerInQueue(Player plr){
		return this.playersLobby.contains(plr);
	}
	
	/**
	 * public boolean isRed(TeamBattleTeams team)
	 * If team is TeamBattleTeams.Red, returns true.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team team to check
	 * @return If team is TeamBattleTeams.Red, returns true.
	 */
	public boolean isRed(TeamBattleTeams team){
		if(team == TeamBattleTeams.Red) return true;
        return false;
	}
	
	/**
	 * public void notifyPlayersBetterKit(TeamBattleTeams team)
	 * Notifies the players that team has been given the better kit.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team The team who got the better kit
	 */
	public void notifyPlayersBetterKit(TeamBattleTeams team){
		List<Player> plrs = this.getAllPlayers();
		String msg = TeamBattle.chatPrefix+" Das Team "+this.getChatTeamName(team)+"§8 hat ein besseres Kit bekommen.";
		for(int i=0;i<plrs.size();i++){
			Player plr = plrs.get(i);
			plr.sendMessage(msg);
		}
	}
	
	/**
	 * public boolean notifyPlayersWin(TeamBattleNotification noti)
	 * Notifies all players that the game has ended.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param noti
	 * @return
	 */
	public void notifyPlayersGameEnd(){
		boolean isBlueWinner=false;
		if(this.pointsBlue >= TeamBattle.winPoints)
        {
            isBlueWinner=true;
        }
		String redMsg;
		String blueMsg;
		if(isBlueWinner){
			blueMsg = TeamBattle.chatPrefix+"§b Wir haben gewonnen, gut gemacht!";
			redMsg = TeamBattle.chatPrefix+"§b Beim nächsten Mal schaffen wir das!";
		}else{
			blueMsg = TeamBattle.chatPrefix+"§b Beim nächsten Mal schaffen wir das!";
			redMsg = TeamBattle.chatPrefix+"§b Wir haben gewonnen, gut gemacht!";
		}
		for(int i=0;i<this.playersBlue.size();i++){
			this.playersBlue.get(i).sendMessage(blueMsg);
		}
		for(int i=0;i<this.playersRed.size();i++){
			this.playersRed.get(i).sendMessage(redMsg);
		}
	}
	public void notifyPlayersJoinLeave(Player who,boolean joins){
		if(!this.isPlayerInGame(who)) return;
		TeamBattleTeams team = this.getPlayerTeam(who);
		for(int i=0;i<this.playersBlue.size();i++){
			this.playersBlue.get(i).sendMessage(TeamBattle.chatPrefix+" "+this.getTeamChatColor(team)+who.getName()+"§7 hat das Spiel "+((joins) ? "betreten" : "verlassen"));
		}
		for(int i=0;i<this.playersRed.size();i++){
			this.playersRed.get(i).sendMessage(TeamBattle.chatPrefix+" "+this.getTeamChatColor(team)+who.getName()+"§7 hat das Spiel "+((joins) ? "betreten" : "verlassen"));
		}
	}
	
	/**
	 * public void notifyPlayersKill(Player target, Player killer)
	 * Notifies all players that a player has been slain.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param target The player that got killed
	 * @param killer The player that killed
	 */
	public void notifyPlayersKill(Player target, Player killer){
		
		String chatColKiller=this.getPlayerChatColor(killer);
		String chatColTarget=this.getPlayerChatColor(target);
		for(int i=0;i<this.playersBlue.size();i++){
			this.playersBlue.get(i).sendMessage(TeamBattle.chatPrefix+" "+chatColKiller+killer.getName()+"§7 hat "+chatColTarget+target.getName()+"§7 umgebracht!");
		}
		for(int i=0;i<this.playersRed.size();i++){
			this.playersRed.get(i).sendMessage(TeamBattle.chatPrefix+" "+chatColKiller+killer.getName()+"§7 hat "+chatColTarget+target.getName()+"§7 umgebracht!");
		}
	}
	
	/**
	 * public void printJoinInfoToPlayer(Player plr)
	 * Prints some info to plr. Should be executed at join.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr
	 */
	public void printJoinInfoToPlayer(Player plr){
		TeamBattleTeams plrTeam = TeamBattle.instance().getPlayerTeam(plr);
		plr.sendMessage("§8}-------------{§aтᴇᴀмвᴀттʟᴇ§r§8}-------------{");
		plr.sendMessage("§7Du hast das TeamBattle betreten!");
		plr.sendMessage("§7Du bist im Team "+TeamBattle.instance().getChatTeamName(plrTeam)+ "§7.");
		plr.sendMessage("§7Dein Team hat §3"+TeamBattle.instance().getTeamPoints(plrTeam)+" §7Punkte.");
		plr.sendMessage("§7Das andere Team hat §3"+TeamBattle.instance().getTeamPoints(TeamBattle.instance().invertTeam(plrTeam))+" §7Punkte.");
		plr.sendMessage("§7Den Punktestand siehst du mit §3/war list§7.");
		plr.sendMessage("§8}-------------{§l§aтᴇᴀмвᴀттʟᴇ§r§8}-------------{");
	}
	
	/**
	 * public void reloadConfig()
	 * Reloads the config, without saving.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void reloadConfig(){
		try {
			this.cfg.load(this.configLocation);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			CommandHelper.sendMessageToOpsAndConsole(TeamBattle.chatPrefix+"WARNING: Could not reload config!");
		}
		this.fetchConfigValues();
	}
	
	/**
	 * public boolean removePlayerFromGame(Player plr)
	 * Removes plr from the list.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr The player to remove
	 * @return false, if null or error while removing, true if removed.
	 * @see List.remove(Object)
	 */
	public boolean removePlayerFromGame(Player plr){
		if(plr == null) return false;
		if(!this.isPlayerInGame(plr)) return true;
		this.notifyPlayersJoinLeave(plr, false);
		this.addNextLobbyPlayerToGame();
		boolean rtrn = true;
		if(this.getPlayerTeam(plr) == TeamBattleTeams.Blue)
        {
            rtrn = this.playersBlue.remove(plr);
        }
        else
        {
            rtrn = this.playersRed.remove(plr);
        }
		this.checkEmptyTeamsAndReset();
		return rtrn;
	}
	
	/**
	 * public boolean removePlayerFromGame(Player plr,boolean win)
	 * Removes plr from the list.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr The player to remove
	 * @return false, if null or error while removing, true if removed.
	 * @see List.remove(Object)
	 */
	public boolean removePlayerFromGame(Player plr, boolean win){
		if(plr == null) return false;
		if(!this.isPlayerInGameIgnoreEnd(plr)) return true;
		
		this.notifyPlayersJoinLeave(plr, false);
		this.addNextLobbyPlayerToGame();
		boolean rtrn = true;
		if(this.getPlayerTeam(plr) == TeamBattleTeams.Blue)
        {
            rtrn = this.playersBlue.remove(plr);
        }
        else
        {
            rtrn = this.playersRed.remove(plr);
        }
		if(!win)
        {
            this.checkEmptyTeamsAndReset();
        }
		return rtrn;
	}
	
	/**
	 * public boolean removePlayerFromLobby(Player plr)
	 * Removes plr from the lobby list.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr Player
	 * @return success
	 */
	public boolean removePlayerFromLobby(Player plr){
		if(!this.playersLobby.contains(plr)) return false;
		this.playersLobby.remove(plr);
		return true;
	}
	
	/**
	 * public void resetPoints()
	 * Resets both team's points.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void resetPoints(){
		this.pointsBlue=0;
		this.pointsRed=0;
	}
	
	/**
	 * public void saveConfig()
	 * Saves the config.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void saveConfig(){
		try {
			this.cfg.save(this.configLocation);
		} catch (IOException e) {
			System.out.println("[MTC] IOException while saving TeamBattle config:");
			e.printStackTrace();
		}
	}
	
	/**
	 * public void setBetterKit(Player plr)
	 * Sets the BetterKit to plr's Inventory (hotbar+armour slots)
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr
	 */
	public void setBetterKit(Player plr){
		for(int i=0;i<=8;i++){
			this.cfg.set("options.betterkit.slot"+i+"", plr.getInventory().getItem(i));
		}
		this.cfg.set("options.betterkit.boots", plr.getInventory().getBoots());
		this.cfg.set("options.betterkit.leggings", plr.getInventory().getLeggings());
		this.cfg.set("options.betterkit.chestplate", plr.getInventory().getChestplate());
	}
	
	/**
	 * public void setKit(Player plr)
	 * Sets the kit to plr's Inventory (hotbar+armour slots)
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr
	 */
	public void setKit(Player plr){
		for(int i=0;i<=8;i++){
			this.cfg.set("options.kit.slot"+i+"", plr.getInventory().getItem(i));
		}
		this.cfg.set("options.kit.boots", plr.getInventory().getBoots());
		this.cfg.set("options.kit.leggings", plr.getInventory().getLeggings());
		this.cfg.set("options.kit.chestplate", plr.getInventory().getChestplate());
	}
	
	/**
	 * public void setLobbySpawn(Player plr)
	 *  Sets the lobby spawn to plr's Location.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr
	 */
	public void setLobbySpawn(Player plr){
		Location loc = plr.getLocation();
		this.cfg.set("options.lobby.x", loc.getBlockX());
		this.cfg.set("options.lobby.y", loc.getBlockY());
		this.cfg.set("options.lobby.z", loc.getBlockZ());
		this.cfg.set("options.lobby.yaw", loc.getYaw());
		this.cfg.set("options.lobby.pitch", loc.getPitch());
		this.cfg.set("options.lobby.worldName", loc.getWorld().getName());
	}
	
	/**
	 * public boolean setPrevlocation(Player plr)
	 * Sets the previous location of plr.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr
	 * @return
	 */
	public boolean setPrevLocation(Player plr){
		if(this.prevLocations.containsKey(plr)) return false;
		this.prevLocations.put(plr, plr.getLocation());
		//System.out.println("Players in Game: "+this.playersBlue.size()+","+this.playersRed.size());
		return true;
	}
	
	/**
	 * public void setTeamSpawn(TeamBattleTeams team,Player plr)
	 * Sets the spawn of team
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param team
	 * @param plr
	 */
	public void setTeamSpawn(TeamBattleTeams team,Player plr){
		String teamStr = "red";
		Location loc = plr.getLocation();
		if(team == TeamBattleTeams.Blue)
        {
            teamStr="blue";
        }
		this.cfg.set("options.spawn."+teamStr+".x", loc.getBlockX());
		this.cfg.set("options.spawn."+teamStr+".y", loc.getBlockY());
		this.cfg.set("options.spawn."+teamStr+".z", loc.getBlockZ());
		this.cfg.set("options.spawn."+teamStr+".yaw", loc.getYaw());
		this.cfg.set("options.spawn."+teamStr+".pitch", loc.getPitch());
		this.cfg.set("options.spawn."+teamStr+".worldName", loc.getWorld().getName());
	}
	
	/**
	 * public void tpAllPlayersToPrevLoc()
	 *  Teleports all players to their PrevLocations and removes them from the game.
	 *  DOES broadcast a message, use ONLY for reloads!
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void tpAllPlayersToPrevLoc(){
		List<Player> plrs = this.getAllPlayers();
		for(int i=0;i<plrs.size();i++){
			plrs.get(i).sendMessage(this.reloadTpMsg);
			CommandHelper.clearInv(plrs.get(i));
			this.removePlayerFromGame(plrs.get(i),true);
			this.tpPlayerToPrevLocation(plrs.get(i));
		}
		for(Player p : this.prevLocations.keySet()){
			p.sendMessage(this.reloadTpMsg);
			p.teleport(this.prevLocations.get(p));
			CommandHelper.clearInv(p);
			this.removePlayerFromLobby(p);
		}

	}
	
	/**
	 * public void tpAllPlayersToPrevLoc(boolean)
	 * Teleports all players to their PrevLocations and removes them from the game.
	 * Does NOT broadcast a message.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	public void tpAllPlayersToPrevLoc(boolean doLobby){
		List<Player> plrs = this.getAllPlayers();
		for(int i = 0;i<plrs.size();i++){
			Player plr = plrs.get(i);
			//System.out.println("Clearing Inv of "+plr.getDisplayName());
			CommandHelper.clearInv(plr);
			this.tpPlayerToPrevLocation(plr);
			this.removePlayerFromGame(plr,true);
			//CommandHelper.clearInv(plr);
		}
		if(!doLobby) return;
		List<Player> lplrs=this.playersLobby;
		for(int i = 0;i<lplrs.size();i++){
			CommandHelper.clearInv(lplrs.get(i));
			this.tpPlayerToPrevLocation(plrs.get(i));
			this.removePlayerFromLobby(lplrs.get(i));
		}
	}
	
	/**
	 * public boolean tpPlayerToLobbySpawn(Player plr)
	 * Teleports plr to the lobby spawn.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr Player
	 * @return success
	 * @see Player.teleport
	 */
	public boolean tpPlayerToLobbySpawn(Player plr){
		if(this.isPlayerInGame(plr)) return false;
		if(this.lobby == null) return false;
		return plr.teleport(this.lobby);
	}
	
	/**
	 * public boolean tpPlayerToPrevLocation(Player plr)
	 * Teleports the player back to his previous location.
	 * (before joining the game)
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr
	 * @return
	 */
	public boolean tpPlayerToPrevLocation(Player plr){
		for(PotionEffect pot:plr.getActivePotionEffects()){
			plr.removePotionEffect(pot.getType());
		}
		plr.setFireTicks(0);
		plr.setHealth(plr.getMaxHealth());
		plr.setFoodLevel(20);
		plr.chat("/spawn"); // /back :/
		if(!this.prevLocations.containsKey(plr)) return false;
		boolean rtrn =  plr.teleport(this.prevLocations.get(plr));
		this.prevLocations.remove(plr);
		return rtrn;
	}
	
	/**
	 * public boolean tpPlayerToTeamSpawn(Player plr)
	 * Teleports plr to the corresponding team's spawn.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @param plr The player to teleport
	 * @return Success of teleport
	 */
	public boolean tpPlayerToTeamSpawn(Player plr){
		if(!this.isPlayerInGame(plr)) return false;
		if(this.getPlayerTeam(plr) == TeamBattleTeams.Blue) {
			if(this.spawnBlue == null) return false;
			boolean rtrn = plr.teleport(this.spawnBlue);
			for (Player p : this.getAllPlayers())
			  {
			    if (p.canSee(plr))
                {
                    p.showPlayer(plr);
                }
			  }
			//plr.setCustomName(plr.getName()+"§b{"+this.getChatTeamName(TeamBattleTeams.Blue)+"§b}");
			return rtrn;
		}
        if(this.spawnRed == null) return false;
        boolean rtrn = plr.teleport(this.spawnRed);
        for (Player p : Bukkit.getOnlinePlayers())
          {
            if (p.canSee(plr))
            {
                p.showPlayer(plr);
            }
          }
        //plr.setCustomName(plr.getName()+"§b{"+this.getChatTeamName(TeamBattleTeams.Red)+"§b}");
        return rtrn;
	}
	
	/**
	 * private void initConfig()
	 * Initializes the config.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 */
	private void initConfig(){
		this.cfg.options().copyDefaults(true);
		this.cfg.options().header("TeamBattle Config - Use valid YAML!");
		this.cfg.addDefault("options.spawn.blue.x", 0);
		this.cfg.addDefault("options.spawn.blue.y", 0);
		this.cfg.addDefault("options.spawn.blue.z", 0);
		this.cfg.addDefault("options.spawn.blue.yaw", 0);
		this.cfg.addDefault("options.spawn.blue.pitch", 0);
		this.cfg.addDefault("options.spawn.blue.worldName", "world");
		this.cfg.addDefault("options.spawn.red.x", 0);
		this.cfg.addDefault("options.spawn.red.y", 0);
		this.cfg.addDefault("options.spawn.red.z", 0);
		this.cfg.addDefault("options.spawn.red.yaw", 0);
		this.cfg.addDefault("options.spawn.red.pitch", 0);
		this.cfg.addDefault("options.spawn.red.worldName", "world");
		this.cfg.addDefault("options.lobby.x", 0);
		this.cfg.addDefault("options.lobby.y", 0);
		this.cfg.addDefault("options.lobby.z", 0);
		this.cfg.addDefault("options.lobby.yaw", 0);
		this.cfg.addDefault("options.lobby.pitch", 0);
		this.cfg.addDefault("options.lobby.worldName", "world");
		
		this.cfg.addDefault("options.sign.enabled", false);
		this.cfg.addDefault("options.sign.updateEveryXSeconds", -1);
		this.cfg.addDefault("options.sign.x", 0);
		this.cfg.addDefault("options.sign.y", 0);
		this.cfg.addDefault("options.sign.z", 0);
		this.cfg.addDefault("options.sign.worldName", "world");
		
		for(int i=0;i<=8;i++){
			this.cfg.addDefault("options.kit.slot"+i+"", 0);
		}
		
		this.cfg.addDefault("options.kit.boots", 0);
		this.cfg.addDefault("options.kit.leggings", 0);
		this.cfg.addDefault("options.kit.chestplate", 0);
		
		//better kit
		
		for(int i=0;i<=8;i++){
			this.cfg.addDefault("options.betterkit.slot"+i+"", 0);
		}
		
		this.cfg.addDefault("options.betterkit.boots", 0);
		this.cfg.addDefault("options.betterkit.leggings", 0);
		this.cfg.addDefault("options.betterkit.chestplate", 0);
		
		this.cfg.addDefault("options.teams.red.name", "ROT");
		this.cfg.addDefault("options.teams.blue.name", "BLAU");
		this.cfg.addDefault("options.teams.red.chatcolor", "c");
		this.cfg.addDefault("options.teams.blue.chatcolor", "9");
		this.cfg.addDefault("options.teams.red.hat.id", 35);
		this.cfg.addDefault("options.teams.blue.hat.id", 35);
		this.cfg.addDefault("options.teams.red.hat.data", 14);
		this.cfg.addDefault("options.teams.blue.hat.data", 11);
		this.cfg.addDefault("options.teams.red.hat.isLeather", true);
		this.cfg.addDefault("options.teams.blue.hat.isLeather", true);
		this.cfg.addDefault("options.teams.blue.hat.color.r", 76);
		this.cfg.addDefault("options.teams.red.hat.color.r", 153);
		this.cfg.addDefault("options.teams.blue.hat.color.g", 127);
		this.cfg.addDefault("options.teams.red.hat.color.g", 51);
		this.cfg.addDefault("options.teams.blue.hat.color.b", 153);
		this.cfg.addDefault("options.teams.red.hat.color.b", 51);
		
//		cfg.addDefault("options.arena.bounds.x1", 0);
//		cfg.addDefault("options.arena.bounds.x2", 0);
//		cfg.addDefault("options.arena.bounds.z1", 0);
//		cfg.addDefault("options.arena.bounds.z2", 0);
		
		this.cfg.addDefault("options.pointsToWin", 20);
		this.cfg.addDefault("options.pointsForBetterKit", 10);
		this.cfg.addDefault("options.maxPointDifferenceForBetterKit", 5);
		this.cfg.addDefault("options.maxPlayers", 16);
	}
	
	/**
	 * public static TeamBattle instance()
	 * Returns the instance.
	 * @author xxyy98(xxyy98[at]gmail.com)
	 * @return (private static TeamBattle) instance
	 */
	public static TeamBattle instance(){
		return TeamBattle.instance;
	}
}
