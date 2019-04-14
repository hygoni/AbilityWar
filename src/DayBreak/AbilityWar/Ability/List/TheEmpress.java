package DayBreak.AbilityWar.Ability.List;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import DayBreak.AbilityWar.Ability.AbilityBase;
import DayBreak.AbilityWar.Ability.AbilityManifest;
import DayBreak.AbilityWar.Ability.AbilityManifest.Rank;
import DayBreak.AbilityWar.Ability.Timer.CooldownTimer;
import DayBreak.AbilityWar.Config.AbilitySettings.SettingObject;
import DayBreak.AbilityWar.Game.Games.AbstractGame.Participant;
import DayBreak.AbilityWar.Utils.Messager;
import DayBreak.AbilityWar.Utils.Library.EffectLib;
import DayBreak.AbilityWar.Utils.Library.SoundLib;
import DayBreak.AbilityWar.Utils.Library.Item.EnchantLib;
import DayBreak.AbilityWar.Utils.Library.Item.MaterialLib;
import DayBreak.AbilityWar.Utils.Library.Packet.TitlePacket;
import DayBreak.AbilityWar.Utils.Math.NumberUtil;
import DayBreak.AbilityWar.Utils.Math.NumberUtil.NumberStatus;
import DayBreak.AbilityWar.Utils.VersionCompat.ServerVersion;

@AbilityManifest(Name = "?��?��", Rank = Rank.B)
public class TheEmpress extends AbilityBase {

	public static SettingObject<Integer> CooldownConfig = new SettingObject<Integer>(TheEmpress.class, "Cooldown", 70, 
			"# 쿨�??��") {
		
		@Override
		public boolean Condition(Integer value) {
			return value >= 0;
		}
		
	};
	
	public static SettingObject<Boolean> EasterEggConfig = new SettingObject<Boolean>(TheEmpress.class, "EasterEgg", true, 
			"# ?��?��?��?���? ?��?��?�� ?���?",
			"# false�? ?��?��?���? ?��?��?��?��그�? 발동?���? ?��?��?��?��.") {
		
		@Override
		public boolean Condition(Boolean value) {
			return true;
		}
		
	};
	
	public TheEmpress(Participant participant) {
		super(participant,
				ChatColor.translateAlternateColorCodes('&', "&f철괴�? ?��?���??���? ?��?�� 좌표?�� ?��?�� 버프 ?��?? ?��?��?��?�� ?��?��?��?��. " + Messager.formatCooldown(CooldownConfig.getValue())),
				ChatColor.translateAlternateColorCodes('&', "&fX &7: &a+&f, Y &7: &a+ &f?�� ?��   10�? | ?��카로?? IV ?��?��?�� �?"),
				ChatColor.translateAlternateColorCodes('&', "&fX &7: &a+&f, Y &7: &c- &f?�� ???�� 20�? | " + ((ServerVersion.getVersion() >= 9) ? "방패" : "거�?�줄")),
				ChatColor.translateAlternateColorCodes('&', "&fX &7: &c-&f, Y &7: &a+ &f?�� ?��?�� 30�? | 무한 ?��"),
				ChatColor.translateAlternateColorCodes('&', "&fX &7: &c-&f, Y &7: &c- &f?�� ?��?�� 20�? | ?��금사�?"));
	}
	
	boolean EasterEgg = !EasterEggConfig.getValue();
	
	CooldownTimer Cool = new CooldownTimer(this, CooldownConfig.getValue());
	
	@Override
	public boolean ActiveSkill(MaterialType mt, ClickType ct) {
		if(mt.equals(MaterialType.Iron_Ingot)) {
			if(ct.equals(ClickType.RightClick)) {
				if(!Cool.isCooldown()) {
					Location l = getPlayer().getLocation();
					
					NumberStatus X = NumberUtil.getNumberStatus(l.getX());
					NumberStatus Z = NumberUtil.getNumberStatus(l.getZ());
					
					Random random = new Random();
					boolean bool = random.nextBoolean();
					
					if(X.isPlus() && Z.isPlus()) {
						if(bool) {
							EffectLib.INCREASE_DAMAGE.addPotionEffect(getPlayer(), 200, 1, true);
						} else {
							ItemStack is = new ItemStack(Material.DIAMOND_SWORD);
							getPlayer().getInventory().addItem(EnchantLib.DAMAGE_ALL.addEnchantment(is, 4));
						}
					} else if(X.isPlus() && Z.isMinus()) {
						if(bool) {
							EffectLib.DAMAGE_RESISTANCE.addPotionEffect(getPlayer(), 400, 1, true);
						} else {
							if(ServerVersion.getVersion() >= 9) {
								getPlayer().getInventory().addItem(new ItemStack(Material.SHIELD));
							} else {
								getPlayer().getInventory().addItem(MaterialLib.COBWEB.getItem());
							}
						}
					} else if(X.isMinus() && Z.isPlus()) {
						if(bool) {
							EffectLib.SPEED.addPotionEffect(getPlayer(), 600, 1, true);
						} else {
							ItemStack is = new ItemStack(Material.BOW);
							getPlayer().getInventory().addItem(EnchantLib.ARROW_INFINITE.addEnchantment(is, 1));
						}
					} else if(X.isMinus() && Z.isMinus()) {
						if(bool) {
							EffectLib.REGENERATION.addPotionEffect(getPlayer(), 400, 1, true);
						} else {
							getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
						}
					} else if(X.isZero() && Z.isZero()) {
						if(!EasterEgg) {
							EasterEgg = true;
							TitlePacket title = new TitlePacket(ChatColor.translateAlternateColorCodes('&', "&a?��?��?�� �??��"),
									"?��?��?�� �??��?�� ?��?�� 모든 ?��?��?��?��?�� ?��?�� 쿨�??��?�� 초기?��?��?��?��?��?��.", 15, 80, 15);
							title.Broadcast();
							
							SoundLib.UI_TOAST_CHALLENGE_COMPLETE.broadcastSound();
							
							CooldownTimer.ResetCool();
						}
					}
					
					Cool.StartTimer();
					
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void PassiveSkill(Event event) {}

	@Override
	public void onRestrictClear() {}

	@Override
	public void TargetSkill(MaterialType mt, Entity entity) {}
	
}