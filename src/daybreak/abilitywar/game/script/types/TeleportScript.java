package daybreak.abilitywar.game.script.types;

import daybreak.abilitywar.game.games.standard.Game;
import daybreak.abilitywar.game.script.objects.AbstractScript;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static daybreak.abilitywar.utils.Validate.notNull;

public class TeleportScript extends AbstractScript {

	private final String WorldName;
	private final double X;
	private final double Y;
	private final double Z;
	private final float Yaw;
	private final float Pitch;

	public TeleportScript(String ScriptName, int Time, int LoopCount, String PreRunMessage, String RunMessage, Location location) {
		super(ScriptName, Time, LoopCount, PreRunMessage, RunMessage);
		this.WorldName = location.getWorld().getName();
		this.X = location.getX();
		this.Y = location.getY();
		this.Z = location.getZ();
		this.Yaw = location.getYaw();
		this.Pitch = location.getPitch();
	}

	@Override
	public void Execute(Game game) {
		try {
			Location l = new Location(notNull(Bukkit.getWorld(WorldName)), X, Y, Z, Yaw, Pitch);
			for (Player p : Bukkit.getOnlinePlayers()) p.teleport(l);
		} catch (NullPointerException ignore) {
		}
	}

}
