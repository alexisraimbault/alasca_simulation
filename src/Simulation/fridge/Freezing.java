package Simulation.fridge;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class Freezing extends Event{
	private static final long serialVersionUID = 1L;
	
	public Freezing(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
}
