/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.clan;

import li.l1t.mtc.helper.MTCHelper;
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
