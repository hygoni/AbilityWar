package daybreak.abilitywar.ability.list;

import daybreak.abilitywar.ability.AbilityBase;
import daybreak.abilitywar.ability.AbilityManifest;
import daybreak.abilitywar.ability.AbilityManifest.Rank;
import daybreak.abilitywar.ability.AbilityManifest.Species;
import daybreak.abilitywar.ability.SubscribeEvent;
import daybreak.abilitywar.config.AbilitySettings.SettingObject;
import daybreak.abilitywar.game.games.mode.AbstractGame.Participant;
import daybreak.abilitywar.utils.Messager;
import daybreak.abilitywar.utils.library.ParticleLib;
import daybreak.abilitywar.utils.library.SoundLib;
import daybreak.abilitywar.utils.math.LocationUtil;
import daybreak.abilitywar.utils.math.geometry.Circle;
import daybreak.abilitywar.utils.versioncompat.VersionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@AbilityManifest(Name = "마술사", Rank = Rank.A, Species = Species.HUMAN)
public class TheMagician extends AbilityBase {

	public static final SettingObject<Integer> CooldownConfig = new SettingObject<Integer>(TheMagician.class, "Cooldown", 8,
			"# 쿨타임") {

		@Override
		public boolean Condition(Integer value) {
			return value >= 0;
		}

	};

	public TheMagician(Participant participant) {
		super(participant,
				ChatColor.translateAlternateColorCodes('&', "&f활을 쐈을 때, 화살이 맞은 위치에서 5칸 범위 내에 있는 생명체들에게"),
				ChatColor.translateAlternateColorCodes('&', "&f최대체력의 1/5 만큼의 데미지를 추가로 입히고 위치를 뒤바꿉니다. " + Messager.formatCooldown(CooldownConfig.getValue())));
	}

	@Override
	public boolean ActiveSkill(Material materialType, ClickType ct) {
		return false;
	}

	private final CooldownTimer Cool = new CooldownTimer(CooldownConfig.getValue());

	@SubscribeEvent
	public void onProjectileHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {
			if (getPlayer().equals(e.getEntity().getShooter())) {
				if (!Cool.isCooldown()) {
					SoundLib.ENTITY_EXPERIENCE_ORB_PICKUP.playSound(getPlayer());
					Location center = e.getEntity().getLocation();
					HashMap<Damageable, Location> locationMap = new HashMap<>();
					ArrayList<Damageable> damageables = LocationUtil.getNearbyDamageableEntities(center, 5, 5);
					for (Damageable damageable : damageables) {
						locationMap.put(damageable, damageable.getLocation());
						if (!damageable.equals(getPlayer())) {
							if (LocationUtil.isInCircle(center, damageable.getLocation(), 5)) {
								damageable.damage(VersionUtil.getMaxHealth(damageable) / 5, getPlayer());
								if (damageable instanceof Player) {
									SoundLib.ENTITY_ILLUSIONER_CAST_SPELL.playSound((Player) damageable);
								}
							}
						}
					}

					Collections.shuffle(damageables);
					ArrayList<Damageable> keySet = new ArrayList<>(locationMap.keySet());
					for (int i = 0; i < damageables.size(); i++) {
						damageables.get(i).teleport(locationMap.get(keySet.get(i)));
					}

					for (Location l : new Circle(center, 5).setAmount(70).setHighestLocation(true).getLocations()) {
						ParticleLib.SPELL_WITCH.spawnParticle(l, 0, 0, 0, 1);
					}
					ParticleLib.CLOUD.spawnParticle(center, 5, 5, 5, 50);

					Cool.startTimer();
				}
			}
		}
	}

	@Override
	public void TargetSkill(Material materialType, LivingEntity entity) {
	}

}
