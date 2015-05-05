package at.antSim.eventSystem.events;

import at.antSim.eventSystem.Event;
import com.sun.istack.internal.NotNull;

/**
 * Created on 04.05.2015.
 * Represents a released Key.
 *
 * @author Clemens
 */
public class KeyReleasedEvent extends AbstractKeyEvent {

	public KeyReleasedEvent(@NotNull int key, @NotNull char keyCharacter) {
		super(key, keyCharacter);
	}

	@Override
	public Class<? extends Event> getType() {
		return KeyReleasedEvent.class;
	}
}
