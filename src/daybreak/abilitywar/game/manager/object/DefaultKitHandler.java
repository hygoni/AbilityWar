package daybreak.abilitywar.game.manager.object;

import daybreak.abilitywar.config.Configuration;
import daybreak.abilitywar.game.games.mode.AbstractGame.Participant;
import daybreak.abilitywar.utils.library.SoundLib;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface DefaultKitHandler {

	default void giveDefaultKit(Player p) {
		p.setLevel(0);
		if (Configuration.Settings.getStartLevel() > 0) {
			p.giveExpLevels(Configuration.Settings.getStartLevel());
			SoundLib.ENTITY_PLAYER_LEVELUP.playSound(p);
		}
		Inventory inventory = p.getInventory();
		if (Configuration.Settings.getInventoryClear()) {
			inventory.clear();
		}
		for (ItemStack itemStack : Configuration.Settings.getDefaultKit()) {
			inventory.addItem(itemStack);
		}
	}

	default void giveDefaultKit(Collection<Participant> participants) {
		for (Participant p : participants) {
			giveDefaultKit(p.getPlayer());
		}
	}

}
