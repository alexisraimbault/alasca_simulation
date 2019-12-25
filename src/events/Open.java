package events;

import Simulation.FridgeModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class Open 
extends		AbstractFridgeEvent
{
	private static final long serialVersionUID = 1L;
	
	public Open(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null) ;
	}
	
	@Override
	public String eventAsString()
	{
		return "Fridge::Open" ;
	}
	
	@Override
	public boolean hasPriorityOver(EventI e)
	{
		if(e instanceof Close)
			return false ;
		else
			return true;
	}
	
	@Override
	public void executeOn(AtomicModel model)
	{
		System.out.println("Open.executeOn : " + model.getClass());
		assert	model instanceof FridgeModel ;

		((FridgeModel)model).setState(FridgeModel.State.OPEN) ;
	}
}
