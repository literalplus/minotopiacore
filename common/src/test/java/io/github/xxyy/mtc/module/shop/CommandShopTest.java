package io.github.xxyy.mtc.module.shop;

import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.mutable.MutableDouble;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Tests whether the shop command is doing its thing right
 *
 * @author Janmm14
 */
public class CommandShopTest {

    private CommandShop cmd;
    Player plr;
    MutableDouble playerCurrency = new MutableDouble(0);
    ShopItemConfiguration shopItemConfiguration;
    PlayerInventory playerInventory;

    BiConsumer<Player, String> playerMessagedHandler;
    /**
     * custom playerDepositHandler, if set, there is no update of {@link #playerCurrency}
     */
    BiFunction<Player, Integer, EconomyResponse> playerDepositHandler;
    /**
     * custom playerWithdrawHandler, if set, there is no update of {@link #playerCurrency}
     */
    BiFunction<Player, Integer, EconomyResponse> playerWithdrawHandler;

    @Before
    public void setUp() throws Exception {
        ShopModule module = CmdShopMockHelper.mockModule(this);
        populateWithExamples(module.getItemConfig());

        cmd = new CommandShop(module);
        plr = CmdShopMockHelper.mockPlayer(this);
    }

    @Test
    public void priceTest() { //TODO actually do test
        cmd.onCommand(plr, null, "shop", new String[]{"price", "TODO_TODO_TODO"}); //TODO fill item name
    }

    private void populateWithExamples(ShopItemConfiguration itemConfig) {
        //TODO add some ShopItems
    }
}
