package Simulation.heater;

import Simulation.fridge.FridgeModel;
import events.AbstractFridgeEvent;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class Close extends		AbstractFridgeEvent
{
	private static final long serialVersionUID = 1L;
	
	public Close(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null) ;
	}
	
	@Override
	public String eventAsString()
	{
		return "Fridge::Close" ;
	}
	
	@Override
	public boolean hasPriorityOver(EventI e)
	{
		return true ;
	}
	
	@Override
	public void executeOn(AtomicModel model)
	{
		assert	model instanceof HeaterModel ;

		((HeaterModel)model).setState(HeaterModel.State.CLOSED) ;
	}
}
