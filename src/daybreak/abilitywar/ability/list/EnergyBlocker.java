package daybreak.abilitywar.ability.list;

import daybreak.abilitywar.ability.AbilityBase;
import daybreak.abilitywar.ability.AbilityManifest;
import daybreak.abilitywar.ability.AbilityManifest.Rank;
import daybreak.abilitywar.ability.AbilityManifest.Species;
import daybreak.abilitywar.ability.SubscribeEvent;
import daybreak.abilitywar.ability.event.AbilityRestrictionClearEvent;
import daybreak.abilitywar.config.AbilitySettings.SettingObject;
import daybreak.abilitywar.game.games.mode.AbstractGame.Participant;
import daybreak.abilitywar.utils.Messager;
import daybreak.abilitywar.utils.library.ParticleLib;
import daybreak.abilitywar.utils.library.ParticleLib.RGB;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@AbilityManifest(Name = "에너지 블로커", Rank = Rank.A, Species = Species.HUMAN)
public class EnergyBlocker extends AbilityBase {

	public static final SettingObject<Integer> CooldownConfig = new SettingObject<Integer>(EnergyBlocker.class, "Cooldown", 3,
			"# 쿨타임") {

		@Override
		public boolean Condition(Integer value) {
			return value >= 0;
		}

	};

	private boolean Default = true;

	public EnergyBlocker(Participant participant) {
		super(participant,
				ChatColor.translateAlternateColorCodes('&', "&f원거리 공격 피해를 1/3로, 근거리 공격 피해를 두 배로 받거나"),
				ChatColor.translateAlternateColorCodes('&', "&f원거리 공격 피해를 두 배로, 근거리 공격 피해를 1/3로 받을 수 있습니다."),
				ChatColor.translateAlternateColorCodes('&', "&f철괴를 우클릭하면 각각의 피해 정도를 뒤바꿉니다. " + Messager.formatCooldown(CooldownConfig.getValue())),
				ChatColor.translateAlternateColorCodes('&', "&f철괴를 좌클릭하면 현재 상태를 확인할 수 있습니다."));
	}

	private final CooldownTimer Cool = new CooldownTimer(CooldownConfig.getValue());

	@Override
	public boolean ActiveSkill(Material materialType, ClickType ct) {
		if (materialType.equals(Material.IRON_INGOT)) {
			if (ct.equals(ClickType.RIGHT_CLICK)) {
				if (!Cool.isCooldown()) {
					Default = !Default;
					Player p = getPlayer();
					if (Default) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b원거리 &f1/3&7, &a근거리 &f두 배로 변경되었습니다."));
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b원거리 &f두 배&7, &a근거리 &f1/3로 변경되었습니다."));
					}

					Cool.startTimer();
				}
			} else if (ct.equals(ClickType.LEFT_CLICK)) {
				if (Default) {
					getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6현재 상태&f: &b원거리 &f1/3&7, &a근거리 &f두 배"));
				} else {
					getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6현재 상태&f: &b원거리 &f두 배&7, &a근거리 &f1/3"));
				}
			}
		}

		return false;
	}

	private final Timer Particle = new Timer() {

		@Override
		public void onStart() {
		}

		@Override
		public void onProcess(int count) {
			if (Default) {
				ParticleLib.REDSTONE.spawnParticle(getPlayer().getLocation().add(0, 2.2, 0), new RGB(116, 237, 167));
			} else {
				ParticleLib.REDSTONE.spawnParticle(getPlayer().getLocation().add(0, 2.2, 0), new RGB(85, 237, 242));
			}
		}

		@Override
		public void onEnd() {
		}

	}.setPeriod(1);

	@SubscribeEvent
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity().equals(getPlayer())) {
			DamageCause dc = e.getCause();
			if (dc != null) {
				if (dc.equals(DamageCause.PROJECTILE)) {
					if (Default) {
						e.setDamage(e.getDamage() / 3);
					} else {
						e.setDamage(e.getDamage() * 2);
					}
				} else if (dc.equals(DamageCause.ENTITY_ATTACK)) {
					if (Default) {
						e.setDamage(e.getDamage() * 2);
					} else {
						e.setDamage(e.getDamage() / 3);
					}
				}
			}
		}
	}

	@SubscribeEvent(onlyRelevant = true)
	public void onRestrictionClear(AbilityRestrictionClearEvent e) {
		Particle.startTimer();
	}

	@Override
	public void TargetSkill(Material materialType, LivingEntity entity) {
	}

}
