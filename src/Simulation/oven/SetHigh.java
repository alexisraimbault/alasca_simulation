package Simulation.oven;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SetHigh extends		AbstractOvenEvent
{
	private static final long serialVersionUID = 1L;
	
	public SetHigh(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null) ;
	}
	
	@Override
	public String eventAsString()
	{
		return "Test::SetHigh" ;
	}
	
	@Override
	public boolean hasPriorityOver(EventI e)
	{
		return true ;
	}
	
	@Override
	public void executeOn(AtomicModel model)
	{
		System.out.println("SetHigh.executeOn : " + model.getClass());
		assert	model instanceof OvenModel ;

		((OvenModel)model).setMode(OvenModel.Mode.HIGH) ;
	}
}
