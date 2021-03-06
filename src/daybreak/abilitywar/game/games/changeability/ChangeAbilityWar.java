package daybreak.abilitywar.game.games.changeability;

import daybreak.abilitywar.AbilityWar;
import daybreak.abilitywar.config.Configuration.Settings;
import daybreak.abilitywar.config.Configuration.Settings.ChangeAbilityWarSettings;
import daybreak.abilitywar.game.events.GameCreditEvent;
import daybreak.abilitywar.game.games.mode.GameManifest;
import daybreak.abilitywar.game.games.mode.decorator.Winnable;
import daybreak.abilitywar.game.games.standard.Game;
import daybreak.abilitywar.game.manager.AbilityList;
import daybreak.abilitywar.game.manager.object.AbilitySelect;
import daybreak.abilitywar.game.manager.object.DeathManager;
import daybreak.abilitywar.game.manager.object.DefaultKitHandler;
import daybreak.abilitywar.game.manager.object.InfiniteDurability;
import daybreak.abilitywar.utils.Messager;
import daybreak.abilitywar.utils.PlayerCollector;
import daybreak.abilitywar.utils.library.SoundLib;
import daybreak.abilitywar.utils.math.NumberUtil;
import daybreak.abilitywar.utils.thread.AbilityWarThread;
import daybreak.abilitywar.utils.versioncompat.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;

/**
 * 체인지 능력 전쟁
 *
 * @author Daybreak 새벽
 */
@GameManifest(Name = "체인지 능력 전쟁", Description = {"§f일정 시간마다 바뀌는 능력을 가지고 플레이하는 심장 쫄깃한 모드입니다.", "§f모든 플레이어에게는 일정량의 생명이 주어지며, 죽을 때마다 생명이 소모됩니다.", "§f생명이 모두 소모되면 설정에 따라 게임에서 탈락합니다.", "§f모두를 탈락시키고 최후의 1인으로 남는 플레이어가 승리합니다.", "", "§a● §f스크립트가 적용되지 않습니다.",
		"§a● §f일부 콘피그가 임의로 변경될 수 있습니다.", "", "§6● §f체인지 능력전쟁 전용 콘피그가 있습니다. Config.yml을 확인해보세요."})
public class ChangeAbilityWar extends Game implements Winnable, DefaultKitHandler {

	public ChangeAbilityWar() {
		super(PlayerCollector.EVERY_PLAYER_EXCLUDING_SPECTATORS());
		setRestricted(invincible);
		this.maxLife = ChangeAbilityWarSettings.getLife();
	}

	@SuppressWarnings("deprecation")
	private final Objective lifeObjective = ServerVersion.getVersion() >= 13 ?
			getScoreboardManager().getScoreboard().registerNewObjective("생명", "dummy", ChatColor.translateAlternateColorCodes('&', "&c생명"))
			: getScoreboardManager().getScoreboard().registerNewObjective("생명", "dummy");

	private final AbilityChanger changer = new AbilityChanger(this);

	private final boolean invincible = Settings.InvincibilitySettings.isEnabled();

	private final InfiniteDurability infiniteDurability = new InfiniteDurability();

	private final TimerBase NoHunger = new TimerBase() {

		@Override
		public void onStart() {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a배고픔 무제한이 적용됩니다."));
		}

		@Override
		public void onProcess(int count) {
			for (Participant p : getParticipants()) {
				p.getPlayer().setFoodLevel(19);
			}
		}

		@Override
		public void onEnd() {
		}
	};

	@Override
	protected void progressGame(int Seconds) {
		switch (Seconds) {
			case 1:
				broadcastPlayerList();
				if (getParticipants().size() < 2) {
					AbilityWarThread.StopGame();
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c최소 참가자 수를 충족하지 못하여 게임을 중지합니다. &8(&72명&8)"));
				}
				break;
			case 3:
				broadcastPluginDescription();
				break;
			case 5:
				broadcastAbilityReady();
				break;
			case 7:
				scoreboardSetup();
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7스코어보드 &f설정중..."));
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d잠시 후 &f게임이 시작됩니다."));
				break;
			case 9:
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f게임이 &55&f초 후에 시작됩니다."));
				SoundLib.BLOCK_NOTE_BLOCK_HARP.broadcastSound();
				break;
			case 10:
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f게임이 &54&f초 후에 시작됩니다."));
				SoundLib.BLOCK_NOTE_BLOCK_HARP.broadcastSound();
				break;
			case 11:
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f게임이 &53&f초 후에 시작됩니다."));
				SoundLib.BLOCK_NOTE_BLOCK_HARP.broadcastSound();
				break;
			case 12:
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f게임이 &52&f초 후에 시작됩니다."));
				SoundLib.BLOCK_NOTE_BLOCK_HARP.broadcastSound();
				break;
			case 13:
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f게임이 &51&f초 후에 시작됩니다."));
				SoundLib.BLOCK_NOTE_BLOCK_HARP.broadcastSound();
				break;
			case 14:
				GameStart();
				break;
		}
	}

	private final int maxLife;

	private void scoreboardSetup() {
		lifeObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		if (ServerVersion.getVersion() < 13)
			lifeObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c생명"));
		for (Participant p : getParticipants()) {
			Score score = lifeObjective.getScore(p.getPlayer().getName());
			score.setScore(maxLife);
		}
	}

	private final ArrayList<Participant> noLife = new ArrayList<>();

	@Override
	protected DeathManager setupDeathManager() {
		return new DeathManager(this) {
			@Override
			protected void Operation(Participant victim) {
				Player victimPlayer = victim.getPlayer();
				Score score = lifeObjective.getScore(victimPlayer.getName());
				if (score.isScoreSet()) {
					if (score.getScore() >= 1) {
						score.setScore(score.getScore() - 1);
					}
					if (score.getScore() <= 0) {
						noLife.add(victim);
						super.Operation(victim);

						Participant winner = null;
						int count = 0;
						for (Participant participant : getParticipants()) {
							if (!noLife.contains(participant)) {
								count++;
								winner = participant;
							}
						}

						if (count == 1) {
							Win(winner);
						}
					}
				}
			}
		};
	}

	public void broadcastPlayerList() {
		ArrayList<String> lines = Messager.asList(ChatColor.translateAlternateColorCodes('&', "&d==== &f게임 참여자 목록 &d===="));
		int count = 0;
		for (Participant p : getParticipants()) {
			count++;
			lines.add(ChatColor.translateAlternateColorCodes('&', "&5" + count + ". &f" + p.getPlayer().getName()));
		}
		lines.add(ChatColor.translateAlternateColorCodes('&', "&f총 인원수 &5: &d" + count + "명"));
		lines.add(ChatColor.translateAlternateColorCodes('&', "&d=========================="));

		for (String line : lines) {
			Bukkit.broadcastMessage(line);
		}
	}

	public void broadcastPluginDescription() {
		ArrayList<String> msg = new ArrayList<>();
		msg.add(ChatColor.translateAlternateColorCodes('&', "&5&l체인지! &d&l능력 &f&l전쟁"));
		msg.add(ChatColor.translateAlternateColorCodes('&', "&e플러그인 버전 &7: &f" + AbilityWar.getPlugin().getDescription().getVersion()));
		msg.add(ChatColor.translateAlternateColorCodes('&', "&b모드 개발자 &7: &fDaybreak 새벽"));
		msg.add(ChatColor.translateAlternateColorCodes('&', "&9디스코드 &7: &f새벽&7#5908"));

		GameCreditEvent event = new GameCreditEvent();
		Bukkit.getPluginManager().callEvent(event);

		for (String str : event.getCreditList()) {
			msg.add(str);
		}

		for (String m : msg) {
			Bukkit.broadcastMessage(m);
		}
	}

	public void broadcastAbilityReady() {

		for (String m : new String[]{
				ChatColor.translateAlternateColorCodes('&', "&f플러그인에 총 &d" + AbilityList.nameValues().size() + "개&f의 능력이 등록되어 있습니다."),
				ChatColor.translateAlternateColorCodes('&', "&7게임 시작시 &f첫번째 능력&7이 할당되며, 이후 &f" + NumberUtil.parseTimeString(changer.getPeriod()) + "&7마다 능력이 변경됩니다.")}) {
			Bukkit.broadcastMessage(m);
		}
	}

	public void GameStart() {
		for (String m : new String[]{
				ChatColor.translateAlternateColorCodes('&', "&d■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■"),
				ChatColor.translateAlternateColorCodes('&', "&f                &5&l체인지! &d&l능력 &f&l전쟁"),
				ChatColor.translateAlternateColorCodes('&', "&f                    게임 시작                "),
				ChatColor.translateAlternateColorCodes('&', "&d■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■")}) {
			Bukkit.broadcastMessage(m);
		}
		SoundLib.ENTITY_WITHER_SPAWN.broadcastSound();

		giveDefaultKit(getParticipants());

		for (Participant p : getParticipants()) {
			if (Settings.getSpawnEnable()) {
				p.getPlayer().teleport(Settings.getSpawnLocation());
			}
		}

		if (Settings.getNoHunger()) {
			NoHunger.setPeriod(1);
			NoHunger.startTimer();
		} else {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4배고픔 무제한&c이 적용되지 않습니다."));
		}

		if (invincible) {
			getInvincibility().Start(false);
		} else {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4초반 무적&c이 적용되지 않습니다."));
			for (Participant participant : this.getParticipants()) {
				if (participant.hasAbility()) {
					participant.getAbility().setRestricted(false);
				}
			}
		}

		if (Settings.getInfiniteDurability()) {
			attachObserver(infiniteDurability);
		} else {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4내구도 무제한&c이 적용되지 않습니다."));
		}

		for (World w : Bukkit.getWorlds()) {
			if (Settings.getClearWeather()) {
				w.setStorm(false);
			}
		}

		changer.StartTimer();

		startGame();
	}

	/**
	 * 기본 킷 유저 지급
	 */
	@Override
	public void giveDefaultKit(Player p) {
		List<ItemStack> DefaultKit = Settings.getDefaultKit();

		if (Settings.getInventoryClear()) {
			p.getInventory().clear();
		}

		for (ItemStack is : DefaultKit) {
			p.getInventory().addItem(is);
		}

		p.setLevel(0);
		if (Settings.getStartLevel() > 0) {
			p.giveExpLevels(Settings.getStartLevel());
			SoundLib.ENTITY_PLAYER_LEVELUP.playSound(p);
		}
	}

	@Override
	protected AbilitySelect setupAbilitySelect() {
		return null;
	}

	@Override
	protected void onEnd() {
		super.onEnd();
		lifeObjective.unregister();
	}

}
