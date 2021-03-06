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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.ArrayList;
import java.util.Collection;

@AbilityManifest(Name = "아레스", Rank = Rank.A, Species = Species.GOD)
public class Ares extends AbilityBase {

	public static final SettingObject<Integer> DamageConfig = new SettingObject<Integer>(Ares.class, "DamagePercent", 75,
			"# 스킬 데미지") {

		@Override
		public boolean Condition(Integer value) {
			return value >= 0;
		}

	};

	public static final SettingObject<Integer> CooldownConfig = new SettingObject<Integer>(Ares.class, "Cooldown", 60,
			"# 쿨타임") {

		@Override
		public boolean Condition(Integer value) {
			return value >= 0;
		}

	};

	public static final SettingObject<Boolean> DashConfig = new SettingObject<Boolean>(Ares.class, "DashIntoTheAir", false,
			"# true로 설정하면 아레스 능력 사용 시 공중으로 돌진 할 수 있습니다.") {

		@Override
		public boolean Condition(Boolean value) {
			return true;
		}

	};

	public Ares(Participant participant) {
		super(participant,
				ChatColor.translateAlternateColorCodes('&', "&f전쟁의 신 아레스."),
				ChatColor.translateAlternateColorCodes('&', "&f철괴를 우클릭하면 앞으로 돌진하며 주위의 엔티티에게 데미지를 주며,"),
				ChatColor.translateAlternateColorCodes('&', "&f데미지를 받은 엔티티들을 밀쳐냅니다. ") + Messager.formatCooldown(CooldownConfig.getValue()));
	}

	private final CooldownTimer Cool = new CooldownTimer(CooldownConfig.getValue());

	private final DurationTimer Duration = new DurationTimer(20, Cool) {

		private final boolean DashIntoTheAir = DashConfig.getValue();
		private final int DamagePercent = DamageConfig.getValue();
		private ArrayList<Damageable> Attacked;

		@Override
		protected void onDurationStart() {
			Attacked = new ArrayList<>();
			Collection<Player> nearby = LocationUtil.getNearbyPlayers(getPlayer().getLocation(), 10, 10);
			SoundLib.ENTITY_PLAYER_ATTACK_SWEEP.playSound(nearby);
			SoundLib.SWOOSH.playSound(getPlayer());
		}

		@Override
		public void onDurationProcess(int seconds) {
			Player p = getPlayer();

			ParticleLib.LAVA.spawnParticle(p.getLocation(), 4, 4, 4, 40);

			if (DashIntoTheAir) {
				p.setVelocity(p.getVelocity().add(p.getLocation().getDirection().multiply(0.7)));
			} else {
				p.setVelocity(p.getVelocity().add(p.getLocation().getDirection().multiply(0.7).setY(0)));
			}

			for (Damageable d : LocationUtil.getNearbyDamageableEntities(p, 4, 4)) {
				double Damage = (d.getHealth() / 100) * DamagePercent;
				if (!Attacked.contains(d)) {
					d.damage(Damage, p);
					Attacked.add(d);
					SoundLib.BLOCK_ANVIL_LAND.playSound(p, 0.5f, 1);
				} else {
					d.damage(Damage / 5, p);
				}

				d.setVelocity(p.getLocation().toVector().subtract(d.getLocation().toVector()).multiply(-1).setY(1));
			}
		}

		@Override
		protected void onDurationEnd() {
		}

	}.setPeriod(1);

	@Override
	public boolean ActiveSkill(Material materialType, ClickType ct) {
		if (materialType.equals(Material.IRON_INGOT)) {
			if (ct.equals(ClickType.RIGHT_CLICK)) {
				if (!Duration.isDuration() && !Cool.isCooldown()) {
					Duration.startTimer();

					return true;
				}
			}
		}

		return false;
	}

	@SubscribeEvent
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity().equals(getPlayer()) && e.getCause().equals(DamageCause.FALL) && Duration.isDuration()) {
			e.setCancelled(true);
		}
	}

	@Override
	public void TargetSkill(Material materialType, LivingEntity entity) {
	}

}
