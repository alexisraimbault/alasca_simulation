package events;

import Simulation.FridgeModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class Rest extends		AbstractFridgeEvent
{
	private static final long serialVersionUID = 1L;
	
	public Rest(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null) ;
	}
	
	@Override
	public String eventAsString()
	{
		return "Fridge::Rest" ;
	}
	
	@Override
	public boolean hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOn || e instanceof Freeze) {
			return false ;
		} else {
			return true ;
		}
	}
	
	@Override
	public void executeOn(AtomicModel model)
	{
		assert	model instanceof FridgeModel ;

		((FridgeModel)model).setState(FridgeModel.State.REST) ;
	}
}
