package daybreak.abilitywar.game.events.participant;

import daybreak.abilitywar.game.games.mode.AbstractGame;
import org.bukkit.event.player.PlayerEvent;

import static daybreak.abilitywar.utils.Validate.notNull;

public abstract class ParticipantEvent extends PlayerEvent {

	public ParticipantEvent(AbstractGame.Participant participant) {
		super(notNull(participant).getPlayer());
		this.participant = participant;
	}

	private final AbstractGame.Participant participant;

	public AbstractGame.Participant getParticipant() {
		return participant;
	}

}
