package events;

import Simulation.FridgeModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SwitchOff extends		AbstractFridgeEvent
{
	private static final long serialVersionUID = 1L;
	
	public SwitchOff(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null) ;
	}
	
	@Override
	public String eventAsString()
	{
		return "Fridge::SwitchOff" ;
	}
	
	@Override
	public boolean hasPriorityOver(EventI e)
	{
		return false ;
	}
	
	@Override
	public void executeOn(AtomicModel model)
	{
		assert	model instanceof FridgeModel ;

		((FridgeModel)model).setState(FridgeModel.State.OFF) ;
	}
}
