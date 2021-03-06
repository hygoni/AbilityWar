package daybreak.abilitywar.game.manager.gui;

import daybreak.abilitywar.ability.AbilityManifest.Rank;
import daybreak.abilitywar.ability.AbilityManifest.Species;
import daybreak.abilitywar.config.Configuration;
import daybreak.abilitywar.config.Configuration.Settings;
import daybreak.abilitywar.game.manager.AbilityList;
import daybreak.abilitywar.utils.Messager;
import daybreak.abilitywar.utils.library.SoundLib;
import daybreak.abilitywar.utils.library.item.ItemLib;
import daybreak.abilitywar.utils.library.item.ItemLib.ItemColor;
import daybreak.abilitywar.utils.library.item.MaterialLib;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 능력 금지 GUI
 */
public class BlackListGUI implements Listener {

	private static final Logger logger = Logger.getLogger(BlackListGUI.class.getName());

	private final Player p;

	public BlackListGUI(Player p, Plugin Plugin) {
		this.p = p;
		Bukkit.getPluginManager().registerEvents(this, Plugin);
	}

	private int PlayerPage = 1;

	private Inventory BlackListGUI;

	public ArrayList<String> getBlackList() {
		ArrayList<String> list = new ArrayList<String>();

		for (String name : AbilityList.nameValues()) {
			if (!list.contains(name)) {
				list.add(name);
			}
		}

		for (String name : Settings.getBlackList()) {
			if (!list.contains(name)) {
				list.add(name);
			}
		}

		list.sort(new Comparator<String>() {

			public int compare(String obj1, String obj2) {
				return obj1.compareToIgnoreCase(obj2);
			}

		});

		return list;
	}

	public void openBlackListGUI(Integer page) {
		Integer MaxPage = ((AbilityList.nameValues().size() - 1) / 36) + 1;
		if (MaxPage < page)
			page = 1;
		if (page < 1)
			page = 1;
		BlackListGUI = Bukkit.createInventory(null, 54,
				ChatColor.translateAlternateColorCodes('&', "&c&l✖ &8&l능력 블랙리스트 &c&l✖"));
		PlayerPage = page;
		int Count = 0;

		List<String> blackList = Settings.getBlackList();

		for (String name : getBlackList()) {
			ItemStack is;

			if (blackList.contains(name)) {
				is = ItemLib.WOOL.getItemStack(ItemColor.RED);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b" + name));
				im.setLore(Messager.asList(ChatColor.translateAlternateColorCodes('&', "&7이 능력은 능력을 추첨할 때 예외됩니다."),
						ChatColor.translateAlternateColorCodes('&', "&b» &f예외 처리를 해제하려면 클릭하세요.")));
				is.setItemMeta(im);
			} else {
				is = ItemLib.WOOL.getItemStack(ItemColor.LIME);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b" + name));
				im.setLore(Messager.asList(ChatColor.translateAlternateColorCodes('&', "&7이 능력은 능력을 추첨할 때 예외되지 않습니다."),
						ChatColor.translateAlternateColorCodes('&', "&b» &f예외 처리를 하려면 클릭하세요.")));
				is.setItemMeta(im);
			}

			if (Count / 36 == page - 1) {
				BlackListGUI.setItem(Count % 36, is);
			}
			Count++;
		}

		int rankCount = 38;
		Rank[] forEach = Rank.values();
		ArrayUtils.reverse(forEach);
		for (Rank r : forEach) {
			ItemStack RankItem;
			switch (r) {
				case D:
					RankItem = new ItemStack(Material.STONE);
					break;
				case C:
					RankItem = new ItemStack(Material.IRON_BLOCK);
					break;
				case B:
					RankItem = new ItemStack(Material.GOLD_BLOCK);
					break;
				case A:
					RankItem = new ItemStack(Material.DIAMOND_BLOCK);
					break;
				case S:
					RankItem = new ItemStack(Material.EMERALD_BLOCK);
					break;
				default:
					RankItem = new ItemStack(Material.BARRIER);
					break;
			}
			ItemMeta RankMeta = RankItem.getItemMeta();
			String RankName = r.getRankName();
			RankMeta.setDisplayName(RankName);
			RankMeta.setLore(Messager.asList(
					ChatColor.translateAlternateColorCodes('&', "&f모든 " + RankName + " &f능력을 예외 처리 하려면 좌클릭,"),
					ChatColor.translateAlternateColorCodes('&', "&f모든 " + RankName + " &f능력을 예외 처리 해제하려면 우클릭을 해주세요.")));
			RankItem.setItemMeta(RankMeta);
			BlackListGUI.setItem(rankCount, RankItem);
			rankCount++;
		}

		if (page > 1) {
			ItemStack previousPage = new ItemStack(Material.ARROW, 1);
			ItemMeta previousMeta = previousPage.getItemMeta();
			previousMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b이전 페이지"));
			previousPage.setItemMeta(previousMeta);
			BlackListGUI.setItem(48, previousPage);
		}

		if (page != MaxPage) {
			ItemStack nextPage = new ItemStack(Material.ARROW, 1);
			ItemMeta nextMeta = nextPage.getItemMeta();
			nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b다음 페이지"));
			nextPage.setItemMeta(nextMeta);
			BlackListGUI.setItem(50, nextPage);
		}

		ItemStack Page = new ItemStack(Material.PAPER, 1);
		ItemMeta PageMeta = Page.getItemMeta();
		PageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6페이지 &e" + page + " &6/ &e" + MaxPage));
		Page.setItemMeta(PageMeta);
		BlackListGUI.setItem(49, Page);

		p.openInventory(BlackListGUI);
	}

	@EventHandler
	private void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().equals(this.BlackListGUI)) {
			HandlerList.unregisterAll(this);
			try {
				Configuration.update();
			} catch (IOException | InvalidConfigurationException e1) {
				logger.log(Level.SEVERE, "콘피그를 업데이트하는 도중 오류가 발생하였습니다.");
			}
		}
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().equals(BlackListGUI)) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()
					&& e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if (e.getCurrentItem().getItemMeta().getDisplayName()
						.equals(ChatColor.translateAlternateColorCodes('&', "&b이전 페이지"))) {
					openBlackListGUI(PlayerPage - 1);
				} else if (e.getCurrentItem().getItemMeta().getDisplayName()
						.equals(ChatColor.translateAlternateColorCodes('&', "&b다음 페이지"))) {
					openBlackListGUI(PlayerPage + 1);
				} else {
					String itemName = e.getCurrentItem().getItemMeta().getDisplayName();

					if (ItemLib.WOOL.compareType(e.getCurrentItem().getType())) {
						String stripItemName = ChatColor.stripColor(itemName);
						if (MaterialLib.RED_WOOL.compareMaterial(e.getCurrentItem())) {
							Settings.removeBlackList(stripItemName);
							SoundLib.ENTITY_EXPERIENCE_ORB_PICKUP.playSound(p);
							openBlackListGUI(PlayerPage);
						} else if (MaterialLib.LIME_WOOL.compareMaterial(e.getCurrentItem())) {
							Settings.addBlackList(stripItemName);
							SoundLib.BLOCK_ANVIL_LAND.playSound(p);
							openBlackListGUI(PlayerPage);
						}
					} else {
						for (Rank r : Rank.values()) {
							if (itemName.equals(r.getRankName())) {
								blacklist(e.getClick(), AbilityList.nameValues(r));
							}
						}

						for (Species s : Species.values()) {
							if (itemName.equals(s.getSpeciesName())) {
								blacklist(e.getClick(), AbilityList.nameValues(s));
							}
						}
					}
				}
			}
		}
	}

	private void blacklist(ClickType clickType, ArrayList<String> abilityNames) {
		if (clickType.equals(ClickType.LEFT)) {
			for (String name : abilityNames) {
				Settings.addBlackList(name);
			}
			SoundLib.BLOCK_ANVIL_LAND.playSound(p);
		} else if (clickType.equals(ClickType.RIGHT)) {
			for (String name : abilityNames) {
				Settings.removeBlackList(name);
			}
			SoundLib.ENTITY_EXPERIENCE_ORB_PICKUP.playSound(p);
		}
		openBlackListGUI(PlayerPage);
	}

}