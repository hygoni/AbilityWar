package Marlang.AbilityWar.Ability.List;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import Marlang.AbilityWar.Ability.AbilityBase;
import Marlang.AbilityWar.Ability.Timer.CooldownTimer;
import Marlang.AbilityWar.Config.AbilitySettings.SettingObject;
import Marlang.AbilityWar.Utils.Messager;
import Marlang.AbilityWar.Utils.Math.LocationUtil;
import Marlang.AbilityWar.Utils.Thread.TimerBase;
import Marlang.AbilityWar.Utils.VersionCompat.PlayerCompat;
import Marlang.AbilityWar.Utils.VersionCompat.PotionEffectType;

public class Gladiator extends AbilityBase {
	
	public static SettingObject<Integer> CooldownConfig = new SettingObject<Integer>("글래디에이터", "Cooldown", 120,
			"# 쿨타임") {
		
		@Override
		public boolean Condition(Integer value) {
			return value >= 0;
		}
		
	};

	public Gladiator(Player player) {
		super(player, "글래디에이터", Rank.S,
				ChatColor.translateAlternateColorCodes('&', "&f상대방을 철괴로 타격하면 투기장이 생성되며 그 안에서"),
				ChatColor.translateAlternateColorCodes('&', "&f1:1 대결을 하게 됩니다."));
	}
	
	CooldownTimer Cool = new CooldownTimer(this, CooldownConfig.getValue());
	
	HashMap<Block, BlockState> Saves = new HashMap<Block, BlockState>();
	
	TimerBase FieldClear = new TimerBase(20) {
		
		@Override
		public void onStart() {}
		
		@Override
		public void TimerProcess(Integer Seconds) {
			Messager.sendMessage(target, ChatColor.translateAlternateColorCodes('&', "&4[&c투기장&4] &f" + Seconds + "초 후에 투기장이 삭제됩니다."));
			Messager.sendMessage(getPlayer(), ChatColor.translateAlternateColorCodes('&', "&4[&c투기장&4] &f" + Seconds + "초 후에 투기장이 삭제됩니다."));
		}
		
		@Override
		public void onEnd() {
			for(Block b : Saves.keySet()) {
				BlockState state = Saves.get(b);
				b.setType(state.getType());
			}
			
			Saves.clear();
		}
		
	};
	
	Player target = null;
	
	TimerBase Field = new TimerBase(26) {
		
		Integer Count;
		Integer TotalCount;
		Location center;
		
		@Override
		public void onStart() {
			Count = 1;
			TotalCount = 1;
			center = getPlayer().getLocation();
			Saves.putIfAbsent(center.clone().subtract(0, 1, 0).getBlock(), center.clone().subtract(0, 1, 0).getBlock().getState());
			center.subtract(0, 1, 0).getBlock().setType(Material.SMOOTH_BRICK);
		}
		
		@Override
		public void TimerProcess(Integer Seconds) {
			if(TotalCount <= 10) {
				for(Location l : LocationUtil.getCircle(center, Count, Count * this.getCount() * 30, false)) {
					Saves.putIfAbsent(l.getBlock(), l.getBlock().getState());
					l.getBlock().setType(Material.SMOOTH_BRICK);
				}
				
				Count++;
			} else if(TotalCount > 10 && TotalCount <= 15) {
				for(Location l : LocationUtil.getCircle(center, Count - 1, Count * 30, false)) {
					Saves.putIfAbsent(l.clone().add(0, TotalCount - 10, 0).getBlock(), l.clone().add(0, TotalCount - 10, 0).getBlock().getState());
					l.add(0, TotalCount - 10, 0).getBlock().setType(Material.IRON_FENCE);
				}
				for(Location l : LocationUtil.getCircle(center, Count, Count * 30, false)) {
					Saves.putIfAbsent(l.clone().add(0, TotalCount - 10, 0).getBlock(), l.clone().add(0, TotalCount - 10, 0).getBlock().getState());
					l.add(0, TotalCount - 10, 0).getBlock().setType(Material.IRON_FENCE);
				}
			} else if(TotalCount > 15 && TotalCount <= 26) {
				for(Location l : LocationUtil.getCircle(center, Count, Count * 30, false)) {
					Saves.putIfAbsent(l.clone().add(0, 6, 0).getBlock(), l.clone().add(0, 6, 0).getBlock().getState());
					l.add(0, 6, 0).getBlock().setType(Material.SMOOTH_BRICK);
				}
				
				Count--;
			}
			TotalCount++;
		}
		
		@Override
		public void onEnd() {
			Location check = center.clone().add(0, 6, 0);
			
			if(!check.getBlock().getType().equals(Material.SMOOTH_BRICK)) {
				Saves.putIfAbsent(check.getBlock(), check.getBlock().getState());
				check.getBlock().setType(Material.SMOOTH_BRICK);
			}
			
			Location teleport = center.clone().add(0, 1, 0);
			
			getPlayer().teleport(teleport);
			PlayerCompat.addPotionEffect(getPlayer(), PotionEffectType.ABSORPTION, 400, 4, true);
			PlayerCompat.addPotionEffect(getPlayer(), PotionEffectType.DAMAGE_RESISTANCE, 400, 0, true);
			target.teleport(teleport);
			
			Gladiator.this.target = target;
			FieldClear.StartTimer();
		}
		
	}.setPeriod(2);
	
	@Override
	public boolean ActiveSkill(ActiveMaterialType mt, ActiveClickType ct) {
		if(mt.equals(ActiveMaterialType.Iron_Ingot)) {
			if(ct.equals(ActiveClickType.LeftClick)) {
				Cool.isCooldown();
			}
		}
		
		return false;
	}

	@Override
	public void PassiveSkill(Event event) {
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if(e.getDamager().equals(getPlayer())) {
				if(e.getEntity() instanceof Player) {
					if(!e.isCancelled()) {
						if(PlayerCompat.getItemInHand(getPlayer()).getType().equals(Material.IRON_INGOT)) {
							if(!Cool.isCooldown()) {
								this.target = (Player) e.getEntity();
								Field.StartTimer();
								
								Cool.StartTimer();
							}
						}
					}
				}
			}
		} else if(event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			if(Saves.keySet().contains(e.getBlock())) {
				if(!e.isCancelled()) {
					e.setCancelled(true);
					Player p = e.getPlayer();
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c투기장&f은 부술 수 없습니다!"));
				}
			}
		}
	}

	@Override
	public void onRestrictClear() {}
	
}
