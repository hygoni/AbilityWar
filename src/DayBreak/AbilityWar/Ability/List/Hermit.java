package DayBreak.AbilityWar.Ability.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import DayBreak.AbilityWar.Ability.AbilityBase;
import DayBreak.AbilityWar.Ability.AbilityManifest;
import DayBreak.AbilityWar.Ability.AbilityManifest.Rank;
import DayBreak.AbilityWar.Config.AbilitySettings.SettingObject;
import DayBreak.AbilityWar.Game.Games.AbstractGame.Participant;
import DayBreak.AbilityWar.Utils.Library.Packet.TitlePacket;
import DayBreak.AbilityWar.Utils.Math.LocationUtil;
import DayBreak.AbilityWar.Utils.Thread.AbilityWarThread;

@AbilityManifest(Name = "�츣��", Rank = Rank.C)
public class Hermit extends AbilityBase {

	public static SettingObject<Integer> DistanceConfig = new SettingObject<Integer>(Hermit.class, "Distance", 15, 
			"# ��ĭ �̳��� �÷��̾ ������ �� �˸��� ����� �����մϴ�.") {
		
		@Override
		public boolean Condition(Integer value) {
			return value >= 1;
		}
		
	};

	public Hermit(Participant participant) {
		super(participant,
				ChatColor.translateAlternateColorCodes('&', "&f�ڽ��� �ֺ� " + DistanceConfig.getValue() + "ĭ ���� �÷��̾ ���� ��� �˷��ݴϴ�."));
	}

	@Override
	public boolean ActiveSkill(MaterialType mt, ClickType ct) {
		return false;
	}

	Integer Distance = DistanceConfig.getValue();
	
	@Override
	public void PassiveSkill(Event event) {
		if(event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player p = e.getPlayer();
			if(p.getWorld().equals(getPlayer().getWorld())) {
				if(!LocationUtil.isInCircle(getPlayer().getLocation(), e.getFrom(), Double.valueOf(Distance)) && 
						LocationUtil.isInCircle(getPlayer().getLocation(), e.getTo(), Double.valueOf(Distance))) {
					if(AbilityWarThread.isGameTaskRunning() && AbilityWarThread.getGame().isParticipating(p)) {
						TitlePacket title = new TitlePacket(ChatColor.translateAlternateColorCodes('&', "&8�츣��"),
								ChatColor.translateAlternateColorCodes('&', "&e" + p.getName() + " &f������"), 5, 30, 5);
						title.Send(getPlayer());
					}
				}
			}
		}
	}

	@Override
	public void onRestrictClear() {}

	@Override
	public void TargetSkill(MaterialType mt, Entity entity) {}
	
}