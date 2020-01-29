package Simulation.energy;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class Charge extends AbstractBatteryEvent{

	private static final long serialVersionUID = 1L;
	private double energy;
	private double balance;
	
	public Charge(Time timeOfOccurrence, double energy, double balance)
	{
		super(timeOfOccurrence, null) ;
		this.energy = energy;
		this.balance = balance;
	}
	
	@Override
	public String eventAsString()
	{
		return "Test::Charge" ;
	}
	
	@Override
	public boolean hasPriorityOver(EventI e)
	{
		return true ;
	}
	
	@Override
	public void executeOn(AtomicModel model)
	{
		assert	model instanceof BatteryModel ;

		((BatteryModel)model).charge(energy, balance) ;
	}
}
