package daybreak.abilitywar.utils;

import daybreak.abilitywar.AbilityWar;
import daybreak.abilitywar.utils.versioncompat.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

/**
 * FallingBlock을 더욱 편하게 사용하기 위해 만든 유틸입니다.
 *
 * @author Daybreak 새벽
 */
public abstract class FallBlock implements Listener {

	private Object data = null;
	private Byte byteData = null;
	private Location location;
	private World world;
	private Vector vector = new Vector(0, 0, 0);

	/**
	 * Fallblock의 기본 생성자입니다.
	 *
	 * @param data     생성할 FallingBlock의 종류
	 * @param location 생성할 위치
	 */
	public FallBlock(Material data, Location location) {
		if (ServerVersion.getVersion() >= 13) {
			this.data = data.createBlockData();
		} else {
			try {
				this.data = Class.forName("org.bukkit.material.MaterialData").getConstructor(Material.class).newInstance(data);
			} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
			}
		}
		this.location = location;
		this.world = location.getWorld();
	}

	/**
	 * Fallblock의 기본 생성자입니다.
	 *
	 * @param Data     생성할 FallingBlock의 종류
	 * @param location 생성할 위치
	 * @param vector   생성할 때 적용할 벡터
	 */
	public FallBlock(Material Data, Location location, Vector vector) {
		this(Data, location);
		this.vector = vector;
	}

	/**
	 * FallinBlock을 스폰합니다.
	 *
	 * @return 스폰한 FallingBlock
	 * FallingBlock를 스폰하지 못했을 경우 null 반환
	 */
	@SuppressWarnings("deprecation")
	public FallingBlock Spawn() {
		final FallingBlock fallingBlock;
		if (ServerVersion.getVersion() >= 13) {
			BlockData bd = (BlockData) data;
			fallingBlock = world.spawnFallingBlock(location, bd);
		} else {
			if (byteData != null) {
				fallingBlock = world.spawnFallingBlock(location, ((MaterialData) data).getItemType(), byteData);
			} else {
				fallingBlock = world.spawnFallingBlock(location, (MaterialData) data);
			}
		}

		Bukkit.getPluginManager().registerEvents(this, AbilityWar.getPlugin());

		fallingBlock.setGlowing(glowing);
		fallingBlock.setInvulnerable(true);
		fallingBlock.setDropItem(false);
		fallingBlock.setVelocity(vector);

		this.fallingBlock = fallingBlock;

		return fallingBlock;
	}

	@Deprecated
	public FallBlock setByteData(byte byteData) {
		this.byteData = byteData;
		return this;
	}

	public FallBlock toggleSetBlock(boolean bool) {
		this.setBlock = bool;
		return this;
	}

	public FallBlock toggleGlowing(boolean bool) {
		this.glowing = bool;
		return this;
	}

	private boolean glowing = false;
	private boolean setBlock = false;
	private FallingBlock fallingBlock = null;

	/**
	 * 스폰한 FallingBlock 엔티티가 땅에 떨어져 블록으로 변환되었을 때 호출됩니다.
	 */
	public abstract void onChangeBlock(FallingBlock block);

	/**
	 * FallingBlock이 땅에 떨어졌을 때 블록 설치 캔슬 및 onChangeBlock() 호출을 위해 사용됩니다.
	 */
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if (e.getEntity().equals(fallingBlock)) {
			onChangeBlock((FallingBlock) e.getEntity());
			if (!setBlock) {
				e.setCancelled(true);
				e.getEntity().remove();
			}

			HandlerList.unregisterAll(this);
		}
	}

}
