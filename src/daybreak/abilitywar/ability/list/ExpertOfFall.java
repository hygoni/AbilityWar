package daybreak.abilitywar.ability.list;

import daybreak.abilitywar.ability.AbilityBase;
import daybreak.abilitywar.ability.AbilityManifest;
import daybreak.abilitywar.ability.AbilityManifest.Rank;
import daybreak.abilitywar.ability.AbilityManifest.Species;
import daybreak.abilitywar.ability.SubscribeEvent;
import daybreak.abilitywar.game.games.mode.AbstractGame.Participant;
import daybreak.abilitywar.utils.library.SoundLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@AbilityManifest(Name = "낙법의 달인", Rank = Rank.C, Species = Species.HUMAN)
public class ExpertOfFall extends AbilityBase {

	public ExpertOfFall(Participant participant) {
		super(participant,
				ChatColor.translateAlternateColorCodes('&', "&f30년간의 고된 수련으로 낙법과 일심동체가 된 낙법의 달인."),
				ChatColor.translateAlternateColorCodes('&', "&f낙하해 땅에 닿았을 때 자동으로 물낙법을 합니다."));
	}

	@Override
	public boolean ActiveSkill(Material materialType, ClickType ct) {
		return false;
	}

	@SubscribeEvent
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity().equals(getPlayer())) {
			if (e.getCause().equals(DamageCause.FALL)) {
				e.setCancelled(true);
				Block block = getPlayer().getLocation().getBlock();
				Material origin = block.getType();
				new Timer(2) {
					@Override
					protected void onStart() {
						block.setType(Material.WATER);
					}

					@Override
					protected void onProcess(int count) {
					}

					@Override
					protected void onEnd() {
						block.setType(origin);
					}
				}.startTimer();
				SoundLib.ENTITY_PLAYER_SPLASH.playSound(getPlayer());
			}
		}
	}

	@Override
	public void TargetSkill(Material materialType, LivingEntity entity) {
	}

}
