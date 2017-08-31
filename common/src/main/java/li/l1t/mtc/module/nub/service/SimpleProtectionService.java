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

package li.l1t.mtc.module.nub.service;

import com.google.common.base.Preconditions;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.nub.LocalProtectionManager;
import li.l1t.mtc.module.nub.NubConfig;
import li.l1t.mtc.module.nub.api.NoSuchProtectionException;
import li.l1t.mtc.module.nub.api.NubProtection;
import li.l1t.mtc.module.nub.api.ProtectionRepository;
import li.l1t.mtc.module.nub.api.ProtectionService;
import li.l1t.mtc.module.nub.repository.SqlProtectionRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of a protection service that uses a {@link ProtectionRepository} for communicating with a data
 * source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class SimpleProtectionService implements ProtectionService {
    private final ProtectionRepository repository;
    private final LocalProtectionManager manager;
    private final NubConfig config;

    @InjectMe
    public SimpleProtectionService(SqlProtectionRepository repository, LocalProtectionManager manager, NubConfig config) {
        this.repository = repository;
        this.manager = manager;
        this.config = config;
    }

    @Override
    public void startProtection(Player player) {
        Preconditions.checkNotNull(player, "player");
        NubProtection protection = repository.createProtection(player.getUniqueId(), config.getProtectionDurationMinutes());
        manager.addProtection(protection);
        config.getIntro().sendTo(player, protection.getMinutesLeft());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player,
                "Du bist jetzt für §p%d§a Minuten geschützt.",
                protection.getMinutesLeft());
    }

    @Override
    public boolean cancelProtection(Player player) {
        Preconditions.checkNotNull(player, "player");
        if (manager.hasProtection(player.getUniqueId())) {
            repository.deleteProtection(manager.removeProtection(player.getUniqueId()));
            config.getOutro().sendTo(player, 0);
            MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Dein Schutz wurde aufgehoben.");
            MessageType.WARNING.sendTo(player, "Du kannst ab jetzt Schaden erhalten!");
            return true;
        } else {
            MessageType.USER_ERROR.sendTo(player, "Du bist momentan nicht geschützt.");
            return false;
        }
    }

    @Override
    public void pauseProtection(Player player) {
        Preconditions.checkNotNull(player, "player");
        NubProtection protection = manager.removeProtection(player.getUniqueId());
        if (protection != null) {
            repository.saveProtection(protection);
            MessageType.RESULT_LINE_SUCCESS.sendTo(player,
                    "Dein Schutz wurde pausiert. Du hast noch §p%d§a Minuten.",
                    protection.getMinutesLeft());
        }
    }

    @Override
    public void resumeProtection(Player player) {
        Preconditions.checkNotNull(player, "player");
        NubProtection protection = repository.findProtectionFor(player.getUniqueId())
                .orElseThrow(() -> new NoSuchProtectionException(player.getUniqueId()));
        resumeInternal(player, protection);
    }

    private void resumeInternal(Player player, NubProtection protection) {
        manager.addProtection(protection);
        MessageType.RESULT_LINE_SUCCESS.sendTo(player,
                "Du bist noch für §p%d§s Minuten von N.u.b. geschützt.",
                protection.getMinutesLeft());
    }

    @Override
    public void startOrResumeProtection(Player player) {
        Optional<NubProtection> protection = repository.findProtectionFor(player.getUniqueId());
        if (protection.isPresent()) {
            resumeInternal(player, protection.get());
        } else {
            startProtection(player);
        }
    }

    @Override
    public boolean hasProtection(Player player) {
        return manager.hasProtection(player.getUniqueId());
    }

    @Override
    public void expireProtection(Player player, NubProtection protection) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(protection, "protection");
        Preconditions.checkArgument(player.getUniqueId().equals(protection.getPlayerId()), "non-matching ids: ", protection.getPlayerId(), player.getUniqueId());
        Preconditions.checkArgument(protection.isExpired(), "protection must be expired: ", protection.getPlayerId());
        manager.removeProtection(player.getUniqueId());
        repository.deleteProtection(protection);
        config.getOutro().sendTo(player, 0);
        MessageType.WARNING.sendTo(player, "Du bist jetzt nicht mehr durch N.u.b. geschützt.");
    }

    @Override
    public boolean isEligibleForProtection(Player player) {
        return !player.hasPlayedBefore() || hasPausedProtection(player);
    }

    public boolean hasPausedProtection(Player player) {
        return repository.findProtectionFor(player.getUniqueId()).isPresent();
    }

    @Override
    public void showProtectionStatusTo(CommandSender sender, UUID playerId) {
        Preconditions.checkNotNull(sender, "sender");
        Preconditions.checkNotNull(playerId, "playerId");
        if (sender instanceof Player && CommandHelper.getSenderId(sender).equals(playerId)) {
            showOwnProtectionStatusTo((Player) sender);
        } else {
            Optional<NubProtection> protection = manager.getProtection(playerId);
            if (protection.isPresent()) {
                MessageType.RESULT_LINE.sendTo(sender,
                        "Dieser Spieler ist noch für %d Minuten geschützt.",
                        protection.get().getMinutesLeft());
            } else {
                MessageType.RESULT_LINE.sendTo(sender,
                        "§e§lDieser Spieler ist nicht geschützt. §6/nub"
                );
            }
        }
    }

    @Override
    public void showOwnProtectionStatusTo(Player player) {
        Optional<NubProtection> protection = manager.getProtection(player.getUniqueId());
        if (protection.isPresent()) {
            MessageType.RESULT_LINE.sendTo(player,
                    " §e§lDu bist noch für %d Minuten geschützt. §6/nub",
                    protection.get().getMinutesLeft());
        } else {
            MessageType.RESULT_LINE.sendTo(player,
                    "§e§lDu bist nicht durch N.u.b. geschützt. §6/nub"
            );
        }
    }
}
