package Simulation;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class FridgeTemp extends Event{
	
	public static class		Reading
	implements EventInformationI
	{
		private static final long serialVersionUID = 1L;
		public final double	value ;

		public Reading(double value)
		{
			super();
			this.value = value;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	public FridgeTemp(
			Time timeOfOccurrence,
			double fridgeTemp
			)
		{
			super(timeOfOccurrence, new Reading(fridgeTemp)) ;
			assert	timeOfOccurrence != null;
		}
}
