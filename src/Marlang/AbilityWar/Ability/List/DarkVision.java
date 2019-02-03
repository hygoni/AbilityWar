package Marlang.AbilityWar.Ability.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Marlang.AbilityWar.Ability.AbilityBase;
import Marlang.AbilityWar.Config.AbilitySettings.SettingObject;
import Marlang.AbilityWar.Utils.LocationUtil;
import Marlang.AbilityWar.Utils.TimerBase;

public class DarkVision extends AbilityBase {
	
	public static SettingObject<Integer> DistanceConfig = new SettingObject<Integer>("심안", "Distance", 30,
			"# 거리 설정") {
		
		@Override
		public boolean Condition(Integer value) {
			return value >= 1;
		}
		
	};
	
	public DarkVision() {
		super("심안", Rank.C,
				ChatColor.translateAlternateColorCodes('&', "&f앞이 보이지 않는 대신, 플레이어의 " + DistanceConfig.getValue() + "칸 안에 있는 플레이어들은"),
				ChatColor.translateAlternateColorCodes('&', "&f발광 효과가 적용됩니다."));
		
		Dark.setPeriod(2);
		
		registerTimer(Dark);
		
		Vision.setPeriod(2);

		registerTimer(Vision);
	}

	TimerBase Dark = new TimerBase() {
		
		@Override
		public void TimerStart(Data<?>... args) {}
		
		@Override
		public void TimerProcess(Integer Seconds) {
			getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0), true);
			getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2), true);
		}
		
		@Override
		public void TimerEnd() {}
		
	};
	
	TimerBase Vision = new TimerBase() {
		
		Integer Distance = DistanceConfig.getValue();
		
		@Override
		public void TimerStart(Data<?>... args) {}
		
		@Override
		public void TimerProcess(Integer Seconds) {
			for(Player p : LocationUtil.getNearbyPlayers(getPlayer(), Distance, Distance)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0), true);
			}
		}
		
		@Override
		public void TimerEnd() {}
		
	};
	
	@Override
	public boolean ActiveSkill(ActiveMaterialType mt, ActiveClickType ct) {
		return false;
	}

	@Override
	public void PassiveSkill(Event event) {}

	@Override
	public void AbilityEvent(EventType type) {
		if(type.equals(EventType.RestrictClear)) {
			Dark.StartTimer();
			Vision.StartTimer();
		}
	}

}
