package Simulation.fridge;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class Resting extends Event{
	private static final long serialVersionUID = 1L;
	
	public Resting(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}

}
