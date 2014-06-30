package io.github.xxyy.minotopiacore.clan;

import io.github.xxyy.minotopiacore.helper.MTCHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public final class RunnableTpClanBase implements Runnable {
    public Player plr;
    public int clanId;
    public Location firstPos;
    public double firstHealth;

    public RunnableTpClanBase(Player plr, Location pos, double d, int clanId) {
        this.plr = plr;
        this.clanId = clanId;
        this.firstPos = pos;
        this.firstHealth = d;
    }

    @Override
    public void run() {
        if (this.plr == null || !this.plr.isOnline()) {
            return;
        }
        Location pos = this.plr.getLocation();
        if (this.firstPos.getBlockX() != pos.getBlockX() || this.firstPos.getBlockZ() != pos.getBlockZ()) {
            MTCHelper.sendLoc("XC-moved", this.plr, true);
            return;
        }
        if (this.firstHealth != this.plr.getHealth()) {
            MTCHelper.sendLoc("XC-damagegot", this.plr, true);
            return;
        }
        if (this.plr.isInsideVehicle()) {
            MTCHelper.sendLocArgs("XC-tpfail", this.plr, true, "Du bist in einem Boot");
            return;
        }
        if (this.plr.isSleeping()) {
            MTCHelper.sendLocArgs("XC-tpfail", this.plr, true, "Du kannst jetzt nicht schlafen!");
            return;
        }
        if (this.plr.isDead()) {
            MTCHelper.sendLocArgs("XC-tpfail", this.plr, true, "Du bist tot. RIP.");
            return;
        }
        if (this.plr.getRemainingAir() != this.plr.getMaximumAir()) {
            MTCHelper.sendLocArgs("XC-tpfail", this.plr, true, "Entscheide dich: Schwimmen oder Clanbase!");
            return;
        }
        ClanInfo ci = ClanHelper.getClanInfoByPlayerName(this.plr.getName());
        if (ci.id < 0) {
            MTCHelper.sendLocArgs("XC-cifetcherr", this.plr, true, ci.id);
            return;
        }
        this.plr.teleport(ci.base);
        MTCHelper.sendLoc("XC-tped", this.plr, true);
    }

}
