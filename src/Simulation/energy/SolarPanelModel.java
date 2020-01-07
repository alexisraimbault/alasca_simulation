package Simulation.energy;

import java.util.Vector;
import java.util.concurrent.TimeUnit;


import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(exported = { Charge.class, Consume.class })

public class SolarPanelModel extends AtomicES_Model
{
	private static final long serialVersionUID = 1L ;
	public static final String	URI = "SolarPanelModel" ;
	
	protected double	initialDelay ;
	protected double	interdayDelay ;
	protected double	meanTimeBetweenUsages ;
	
	protected double	meanTimeBetweenTempUpdate ;
	/** next event to be sent.												*/
	protected Class<?>	nextEvent ;
	
	protected double energyProduced;
	
	protected boolean initialCall;
	
	
	public SolarPanelModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			SimulatorI simulationEngine
			) throws Exception
		{
			super(uri, simulatedTimeUnit, simulationEngine) ;
			


			// create a standard logger (logging on the terminal)
			this.setLogger(new StandardLogger()) ;
		}
	
	@Override
	public void			initialiseState(Time initialTime) 
	{
		
		this.initialDelay = 10.0 ;
		this.interdayDelay = 100.0 ;
		this.meanTimeBetweenUsages = 10.0 ;
		
		this.initialCall = true;
		
		this.meanTimeBetweenTempUpdate = 7.0;
		
		this.energyProduced = 7.0;//TODO 

		super.initialiseState(initialTime) ;

		Duration d1 = new Duration(
							this.initialDelay,
							this.getSimulatedTimeUnit()) ;
		Time t = this.getCurrentStateTime().add(d1);
		this.scheduleEvent(new Charge(t, this.energyProduced)) ;
		

		
		//this.scheduleEvent(new TriggerTempCheck(t)) ;
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.nextTimeAdvance) ;

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
	@Override
	public Duration			timeAdvance()
	{
		// This is just for debugging purposes; the time advance for an ES
		// model is given by the earliest time among the currently scheduled
		// events.
		Duration d = super.timeAdvance() ;
		this.logMessage("TestUserModel::timeAdvance() 1 " + d +
									" " + this.eventListAsString()) ;

		return d ;
	}
	
	@Override
	public Vector<EventI>	output()
	{
		// output is called just before executing an internal transition
		// in ES models, this corresponds to having at least one event in
		// the event list which time of occurrence corresponds to the current
		// simulation time when performing the internal transition.

		// when called, there must be an event to be executed and it will
		// be sent to other models when they are external events.
		assert	!this.eventList.isEmpty() ;
		// produce the set of such events by calling the super method
		Vector<EventI> ret = super.output() ;
		// by construction, there will be only one such event
		assert	ret.size() == 1 ;

		// remember which external event was sent (in ES model, events are
		// either internal or external, hence an external event is removed
		// from the event list to be sent and it will not be accessible to
		// the internal transition method; hence, we store the information
		// to keep it for the internal transition)
		this.nextEvent = ret.get(0).getClass() ;

		this.logMessage("TestUserModel::output() " +
									this.nextEvent.getCanonicalName()) ;
		return ret ;
	}
	
	@Override
	public void				userDefinedInternalTransition(
		Duration elapsedTime
		)
	{

		Duration d;

		
		if (this.nextEvent.equals(Charge.class)) {
			
			// also, plan the next switch on for the next day
			d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
			this.scheduleEvent(new Charge(this.getCurrentStateTime().add(d), this.energyProduced)) ;
		}
	}
	

}