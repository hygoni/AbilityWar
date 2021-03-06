package daybreak.abilitywar.game.script.objects;

import daybreak.abilitywar.game.games.mode.AbstractGame;
import daybreak.abilitywar.game.games.standard.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class AbstractScript {

	private final String scriptType;
	private final String name;
	private final int period;
	private final int loopCount;
	private final String preMessage;
	private final String runMessage;
	private transient AbstractGame.TimerBase timer = null;

	public AbstractScript(String name, int period, int loopCount, String preMessage, String runMessage) {
		this.scriptType = this.getClass().getName();
		this.name = name;
		this.period = period;
		this.loopCount = loopCount;
		this.preMessage = preMessage;
		this.runMessage = runMessage;
	}

	public void Start(Game game) {
		if (timer == null || !timer.isRunning()) {
			timer = game.new TimerBase() {
				int count = loopCount;

				@Override
				public void onStart() {
					if (count > 0) count--;
				}

				@Override
				protected void onProcess(int count) {
					String msg = getPreRunMessage(count);

					if (!msg.equalsIgnoreCase("none")) {
						if (count == (this.getMaxCount() / 2)) {
							Bukkit.broadcastMessage(msg);
						} else if (count <= 5 && count >= 1) {
							Bukkit.broadcastMessage(msg);
						}
					}
				}

				@Override
				public void onEnd() {
					Execute(game);

					String msg = getRunMessage();
					if (!msg.equalsIgnoreCase("none")) {
						Bukkit.broadcastMessage(msg);
					}

					if (isLoop()) {
						if (count > -1) {
							if (count > 0) {
								this.startTimer();
							}
						} else {
							this.startTimer();
						}
					}
				}
			};
			timer.startTimer();
		}
	}

	public String getType() {
		return scriptType;
	}

	public String getName() {
		return name;
	}

	protected boolean isLoop() {
		return loopCount != 0;
	}

	protected AbstractGame.TimerBase getTimer() {
		return timer;
	}

	private String getPreRunMessage(Integer Time) {
		return ChatColor.translateAlternateColorCodes('&',
				preMessage.replaceAll("%Time%", Time.toString()).replaceAll("%ScriptName%", name));
	}

	private String getRunMessage() {
		return ChatColor.translateAlternateColorCodes('&', runMessage.replaceAll("%ScriptName%", name));
	}

	protected abstract void Execute(Game game);

}
