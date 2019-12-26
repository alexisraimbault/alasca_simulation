package Simulation.oven;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SetOff extends		AbstractOvenEvent
{
	private static final long serialVersionUID = 1L;
	
	public SetOff(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null) ;
	}
	
	@Override
	public String eventAsString()
	{
		return "Test::SetOff" ;
	}
	
	@Override
	public boolean hasPriorityOver(EventI e)
	{
		return false ;
	}
	
	@Override
	public void executeOn(AtomicModel model)
	{
		if(model instanceof OvenModel)
			((OvenModel)model).setMode(OvenModel.Mode.OFF) ;
	}
}
