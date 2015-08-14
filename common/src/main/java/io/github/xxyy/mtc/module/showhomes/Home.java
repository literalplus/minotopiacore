package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.xxyy.mtc.module.MTCModule;
import io.github.xxyy.mtc.module.ModuleManager;
import lombok.*;
import org.bukkit.Location;
import sun.security.pkcs11.Secmod;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "hologram")
@ToString(exclude = "hologram")
public class Home {

    private final EssentialsDataUser essentialsDataUser;
    private final Location location;
    private final String name;
    private Hologram hologram;

    public void showHologram(@NonNull ShowHomesModule plugin, UUID executor) {
        try {
            showHologram(plugin);
            plugin.holosByExecutingUser.get(executor).add(this);
        } catch (Exception ex) {
            ShowHomesModule.handleException(new Exception("Home#showHologram(plugin, executor);this:" + this, ex));
        }
    }

    public void showHologram(@NonNull ShowHomesModule module) {
        try {
            Location loc = location.clone().add(0, 1.2, 0);
            hologram = HologramsAPI.createHologram(module.getPlugin(), loc);

            List<UUID> plrsToShow = ShowHomesModule.getPlayersWithShowHomesPermission();

            //set visibility
            VisibilityManager visibilityManager = hologram.getVisibilityManager();
            plrsToShow.stream()
                .map(module.getPlugin().getServer()::getPlayer)
                .forEach(visibilityManager::showTo);
            visibilityManager.setVisibleByDefault(false);

            // set touch handler
            TextLine homeNameLine = hologram.appendTextLine("§6Home §c" + name);
            homeNameLine.setTouchHandler(new HomeInfoTouchHandler(this, plrsToShow));

            TextLine homeOwnerLine = hologram.appendTextLine("§6by §c" + essentialsDataUser.getLastName());
            homeOwnerLine.setTouchHandler(homeNameLine.getTouchHandler());

            TextLine uuidLine = hologram.appendTextLine("§6UUID: " + essentialsDataUser.getUuid());
            uuidLine.setTouchHandler(homeNameLine.getTouchHandler());
        } catch (Exception ex) {
            ShowHomesModule.handleException(new Exception("Home#showHologram(plugin);this:" + this, ex));
        }
    }
}
