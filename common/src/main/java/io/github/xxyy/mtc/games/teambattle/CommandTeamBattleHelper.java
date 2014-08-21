package io.github.xxyy.mtc.games.teambattle;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class CommandTeamBattleHelper {
    public CommandSender sender;
    public String[] args;
    public String label;
    public Player plr;
    public Location startLoc;
    public double startHealth;
    public int taskId;

    public CommandTeamBattleHelper(CommandSender sender, String[] args, String label) {
        this.sender = sender;
        this.args = args;
        this.label = label;
    }

    public boolean doJoinLobby() {
        this.plr.setHealth(this.plr.getMaxHealth());
        TeamBattle.instance().setPrevLocation(this.plr);
        if (!TeamBattle.instance().tpPlayerToLobbySpawn(this.plr)) {
            TeamBattle.instance().tpPlayerToPrevLocation(this.plr);
            this.plr.sendMessage(this.formatError("Du konntest nicht in die Lobby teleportiert werden.", "LOBBY_TP"));
            return true;
        }
        return false;
    }

    public void doLeaveGame() {
        if (!TeamBattle.instance().tpPlayerToPrevLocation(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du konntest leider nicht zu deiner vorherigen Position teleportiert werden.");
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Das tut uns sehr leid. Du wirst jetzt zum Spawn teleportiert.");
            this.plr.teleport(this.plr.getWorld().getSpawnLocation());
            return;
        }
        CommandHelper.clearInv(this.plr);
        TeamBattle.instance().removePlayerFromGame(this.plr);

        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du hast das Spiel verlassen.");
    }

    public void doTpPrev() {
        Location loc = TeamBattle.leaveMan.getLocFromName(this.plr.getName());
        if (!this.plr.teleport(loc)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du konntest leider nicht tpt werden :(");
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Koordinaten: " + loc.toString());
            return;
        }
        TeamBattle.leaveMan.clearLocation(this.plr.getName());
        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du wurdest teleportiert.");
    }

    public String formatError(String desc, String errCode) {
        return "Â§8Es ist ein Fehler aufgetreten: " + desc + " §8Bitte versuche es erneut. §8Wenn dir dies schon öfter passiert ist, melde dies einem Admin! §7Fehlercode: " + errCode;
    }

    public boolean leaveGame() {
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.teambattle.member.cmd.leave", this.label + " leave")) {
            return true;
        }
        if (CommandHelper.kickConsoleFromMethod(this.sender, this.label)) {
            return true;
        }

        this.plr = (Player) this.sender;
        if (!TeamBattle.instance().isPlayerInGame(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du bist nicht im Spiel, daher kannst du dieses auch nicht verlassen!");
            return true;
        }
        Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableLeaveBattle(this), 10);
        return true;
    }

    public boolean leaveLobby() {
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.teambattle.member.cmd.lobby", this.label + " lobby")) {
            return true;
        }
        if (CommandHelper.kickConsoleFromMethod(this.sender, this.label)) {
            return true;
        }
        this.plr = (Player) this.sender;
        if (!TeamBattle.instance().isPlayerInQueue(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Dieser Bug wurde entfernt. §8Das tut uns sehr leid :(");
            return true;
        }
        /*if (!*/TeamBattle.instance().removePlayerFromLobby(this.plr);/*) {
            //plr.sendMessage(TeamBattle.CHAT_PREFIX+" Du konntest nicht aus der Warteliste entfernt werden.");
            //return true;
            //REMOVED BECAUSE OF /war lobby -> Players not always in queue
        }*/
        if (!TeamBattle.instance().tpPlayerToPrevLocation(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du konntest leider nicht zu deiner vorherigen Position teleportiert werden.");
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Das tut uns sehr leid. Du wirst jetzt zum Spawn teleportiert.");
            this.plr.teleport(this.plr.getWorld().getSpawnLocation());
            return true;
        }

        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du hast die Lobby verlassen.");

        return true;
    }

    public boolean listPlayersInGame() {
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.teambattle.member.cmd.list", this.label + " list")) {
            return true;
        }

        this.sender.sendMessage("§7}==={§l§8ˢᵖᶤᵉˡᵉʳ ᶤᵐ ˢᵖᶤᵉˡ§r§7}==={");
        String bluePlayers = TeamBattle.instance().getTeamChatColor(TeamBattleTeams.Blue);
        for (Player item : TeamBattle.instance().getPlayerListFromTeam(TeamBattleTeams.Blue)) {
            bluePlayers += item.getName() + ",";
        }
        String redPlayers = TeamBattle.instance().getTeamChatColor(TeamBattleTeams.Red);
        for (Player item : TeamBattle.instance().getPlayerListFromTeam(TeamBattleTeams.Red)) {
            redPlayers += item.getName() + ",";
        }
        this.sender.sendMessage("§7Spieler im blauen Team (§8" + TeamBattle.instance().getTeamPoints(TeamBattleTeams.Blue) + " §7Punkt[e]):");
        this.sender.sendMessage(bluePlayers + "\nÂ§7Spieler im roten Team (§8" + TeamBattle.instance().getTeamPoints(TeamBattleTeams.Red) + " §7Punkt[e]):\n" + redPlayers);

        return true;
    }

    public boolean lobbyPreChecks() {
        if (TeamBattle.instance().isPlayerInGame(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du bist im Spiel!");
            return true;
        }
        if (TeamBattle.instance().isPlayerInQueue(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du bist bereits in der Lobby.");
            return true;
        }
        if (!CommandHelper.isInventoryEmpty(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Dein Inventar muss leer sein.");
            return true;
        }
        if (this.plr.isFlying()) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst die Lobby nicht betreten, während du fliegst. §8Lösung: Aufhören zu fliegen!");
            return true;
        }
        if (this.plr.isSleeping()) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst die Lobby nicht betreten, während du schläfst.");
            return true;
        }
        if (this.plr.getFoodLevel() != 20) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du musst vollgefressen sein!");
            return true;
        }
        if (this.plr.getFireTicks() > 0) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst die Lobby nicht betreten, während du brennst.");
            return true;
        }
        if (this.plr.getGameMode() != GameMode.SURVIVAL) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst die Lobby nur betreten, wenn dein Gamemode §3ＳＵＲＶＩＶＡＬ§7 ist.");
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Dein Gamemode wurde für dich auf §3ＳＵＲＶＩＶＡＬ§7 geändert.");
            this.plr.setGameMode(GameMode.SURVIVAL);
        }
        if (this.plr.getRemainingAir() != this.plr.getMaximumAir()) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Entscheide dich: Lobby oder Baden, aber nicht beides gleichzeitig! §8Lösung: Geh aus dem Wasser.");
            return true;
        }
        return false;
    }

    public boolean preChecks() {
        if (TeamBattle.instance().isPlayerInGame(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du bist schon im Spiel!");
            return true;
        }
        if (TeamBattle.instance().isPlayerInQueue(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Verlasse zuerst die Lobby! §3/war lobby leave");
            return true;
        }
        if (this.plr.getActivePotionEffects().size() != 0) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst keinem Spiel beitreten, während du Trankeffekten ausgesetzt bist. §8Lösung: Milch trinken oder Effekte abwarten.");
            return true;
        }
        if (!CommandHelper.isInventoryEmpty(this.plr)) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Dein Inventar muss leer sein.");
            return true;
        }
        if (this.plr.isFlying()) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst das Spiel nicht betreten, während du fliegst.§8Lösung: Aufhören zu fliegen!");
            return true;
        }
        if (this.plr.isSleeping()) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst das Spiel nicht betreten, während du schläfst. §8Lösung: Bett verlassen.");
            return true;
        }
        if (this.plr.getFoodLevel() != 20) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du musst vollgefressen sein!");
            return true;
        }
        if (this.plr.getFireTicks() > 0) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst das Spiel nicht betreten, während du brennst. §8Lösung: Spring ins kalte Wasser oder warte, bis du nicht mehr brennst.");
            return true;
        }
        if (this.plr.getGameMode() != GameMode.SURVIVAL) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du kannst das Spiel nur betreten, wenn dein Gamemode §3ＳＵＲＶＩＶＡＬ§7 ist.");
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Dein Gamemode wurde für dich auf §3ＳＵＲＶＩＶＡＬ§7 geändert.");
            this.plr.setGameMode(GameMode.SURVIVAL);
        }
        if (this.plr.getRemainingAir() != this.plr.getMaximumAir()) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Entscheide dich: §3/" + this.label + "§7 oder Baden, aber nicht beides gleichzeitig! §8Lösung: Geh aus dem Wasser.");
            return true;
        }
        return false;
    }

    public boolean prepareJoinGame() {
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.teambattle.member.cmd.join", this.label + " join")) {
            return true;
        }
        if (CommandHelper.kickConsoleFromMethod(this.sender, this.label)) {
            return true;
        }
        this.plr = (Player) this.sender;
        this.startLoc = this.plr.getLocation();
        this.startHealth = this.plr.getHealth();

        //checks
        if (this.preChecks()) {
            return true;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(MTC.instance(), new RunnableJoinBattle(this), 2 * 20L);
        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Bewege dich 2 Sekunden nicht!");

        return true;
    }

    public boolean prepareJoinLobby() {
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.teambattle.member.cmd.lobby", this.label + " lobby")) {
            return true;
        }
        if (CommandHelper.kickConsoleFromMethod(this.sender, this.label)) {
            return true;
        }
        this.plr = (Player) this.sender;
        this.startLoc = this.plr.getLocation();
        this.startHealth = this.plr.getHealth();

        //checks
        if (this.preChecks()) {
            return true;
        }
        Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableJoinLobby(this), 2 * 20L);
        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Bewege dich 2 Sekunden nicht!");

        return true;
    }

    public boolean tpToPrevLocFromFl() {
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.teambattle.member.cmd.prev", this.label + " prev")) {
            return true;
        }
        if (CommandHelper.kickConsoleFromMethod(this.sender, this.label)) {
            return true;
        }
        this.plr = (Player) this.sender;
        if (!TeamBattle.leaveMan.doesLocExist(this.plr.getName())) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Für dich ist keine vorherige Position gespeichert.");
            return true;
        }

        this.startLoc = this.plr.getLocation();
        this.startHealth = this.plr.getHealth();

        Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableTpToPrevLoc(this), 2 * 20);

        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Bitte warte 2 Sekunden...");

        return true;
    }

    @Deprecated
    public void tryJoinGame() {
        this.plr.leaveVehicle();
        this.plr.setHealth(this.plr.getMaxHealth());

        if (TeamBattle.instance().getAllPlayers().size() >= TeamBattle.maxPlayers) {
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Die Arena ist derzeit voll.");
            if (this.doJoinLobby()) {
                this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du konntest auch nicht in die Lobby teleportiert werden.");
                this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Versuche es später erneut oder tippe §3/" + this.label + " lobby§7.");
            }
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Sobald ein Platz frei wird, wirst du teleportiert.");
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Warte bitte solange in der Lobby.");
            this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Um die Lobby zu verlassen, tippe §3/" + this.label + " lobby l§7.");
            TeamBattle.instance().addPlayerToLobby(this.plr);
            return;
        }

        if (!TeamBattle.instance().addPlayerToGame(this.plr)) {
            this.plr.sendMessage(this.formatError("Du konntest dem Spiel nicht hinzugefügt werden.", "JOIN_ADD"));
            return;
        }
        TeamBattle.instance().setPrevLocation(this.plr);
        if (!TeamBattle.instance().tpPlayerToTeamSpawn(this.plr)) {
            TeamBattle.instance().removePlayerFromGame(this.plr);
            TeamBattle.instance().tpPlayerToPrevLocation(this.plr);
            this.plr.sendMessage(this.formatError("Du konntest nicht in die Arena teleportiert werden.", "JOIN_TP"));
            return;
        }

        TeamBattle.instance().printJoinInfoToPlayer(this.plr);
        TeamBattle.instance().giveKitToPlayer(this.plr);
    }

    public void tryJoinLobby() {
        if (this.doJoinLobby()) {
            return;
        }
        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Du wurdest in die Lobby teleportiert.");
        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Beachte die Statusschilder für den Spielstand.");
        this.plr.sendMessage(TeamBattle.CHAT_PREFIX + " Zum Verlassen tippe §3/war lobby leave§7.");

    }
}
