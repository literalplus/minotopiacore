package io.github.xxyy.mtc.module.shop;

import io.github.xxyy.common.test.util.MockHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.hook.VaultHook;
import net.md_5.bungee.api.plugin.PluginLogger;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.mockito.Mockito;

import java.io.File;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CmdShopMockHelper {
    static ShopModule mockModule(CommandShopTest i) {
        ShopModule shop = mock(ShopModule.class);
        MTC plugin = mockPlugin(i);
        i.shopItemConfiguration = new ShopItemConfiguration(mock(File.class), plugin, null);
        when(shop.getItemConfig())
                .thenReturn(i.shopItemConfiguration);
        when(shop.getPlugin()).thenReturn(plugin);
        return shop;
    }

    private static MTC mockPlugin(CommandShopTest i) {
        MTC plugin = mock(MTC.class);
        when(plugin.getVaultHook()).thenReturn(mockVaultHook(i));
        when(plugin.getLogger()).thenReturn(mock(PluginLogger.class));
        return plugin;
    }

    static VaultHook mockVaultHook(CommandShopTest i) {
        VaultHook vaultHook = mock(VaultHook.class);

        when(vaultHook.getBalance(any()))
                .thenAnswer(invocationOnMock -> i.playerCurrency.doubleValue());

        when(vaultHook.depositPlayer(any(), anyDouble()))
                .thenAnswer(invocationOnMock -> {
                    Integer amount = (Integer) invocationOnMock.getArguments()[1];
                    if (i.playerDepositHandler != null) {
                        return i.playerDepositHandler.apply((Player) invocationOnMock.getArguments()[0], amount);
                    }
                    i.playerCurrency.add(amount);
                    return new EconomyResponse(amount, i.playerCurrency.doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
                });

        when(vaultHook.withdrawPlayer(any(), anyDouble()))
                .thenAnswer(invocationOnMock -> {
                    Integer amount = (Integer) invocationOnMock.getArguments()[1];
                    if (i.playerWithdrawHandler != null) {
                        return i.playerWithdrawHandler.apply((Player) invocationOnMock.getArguments()[0], amount);
                    }
                    i.playerCurrency.add(amount);
                    return new EconomyResponse(amount, i.playerCurrency.doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
                });
        return vaultHook;
    }

    static Player mockPlayer(CommandShopTest i) {
        Player plr = MockHelper.mockPlayer(UUID.randomUUID(), "Klaus_Dieter");
        Mockito.doAnswer(invocationOnMock -> {
            if (i.playerMessagedHandler != null) {
                i.playerMessagedHandler.accept((Player) invocationOnMock.getMock(), (String) invocationOnMock.getArguments()[0]);
            }
            return "";
        }).when(plr).sendMessage(Mockito.anyString());
        when(plr.getInventory())
                .thenReturn(mockPlayerInventory(i));
        when(plr.getItemInHand())
                .thenAnswer(invocationOnMock -> plr.getInventory().getItemInHand());
        return plr;
    }

    static PlayerInventory mockPlayerInventory(CommandShopTest i) {
        i.playerInventory = new MyPlayerInventory(i.plr);
        return i.playerInventory;
    }
}
