package Simulation.oven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import Simulation.oven.OvenModel.Mode;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(exported = {SetHigh.class,
		 						SetOff.class,
		 						SetLow.class})

public class OvenUserModel extends AtomicES_Model
{
	private static final long serialVersionUID = 1L ;
	public static final String	URI = "TestUserModel" ;
	
	private static final String		SERIES = "mode" ;
	
	/** initial delay before sending the first switch on event.				*/
	protected double	initialDelay ;
	/** delay between uses of the hair dryer from one day to another.		*/
	protected double	interdayDelay ;
	/** mean time between uses of the hair dryer in the same day.			*/
	protected double	meanTimeBetweenUsages ;
	/** during one use, mean time the hair dryer is at high temperature.	*/
	protected double	meanTimeAtHigh ;
	/** during one use, mean time the hair dryer is at low temperature.		*/
	protected double	meanTimeAtLow ;
	
	protected double	meanTimeBetweenTempUpdate ;
	/** next event to be sent.												*/
	protected ArrayList<Double> possibleCookingDurations;
	protected Class<?>	nextEvent ;
	
	protected final RandomDataGenerator		rg ;
	
	protected OvenModel.Mode fs ;
	
	protected boolean initialCall;
	
	
	public OvenUserModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			SimulatorI simulationEngine
			) throws Exception
		{
			super(uri, simulatedTimeUnit, simulationEngine) ;

			this.rg = new RandomDataGenerator() ;
			


			// create a standard logger (logging on the terminal)
			this.setLogger(new StandardLogger()) ;
		}
	
	@Override
	public void			initialiseState(Time initialTime) 
	{
		
		this.initialDelay = 10.0 ;
		this.interdayDelay = 100.0 ;
		this.meanTimeBetweenUsages = 10.0 ;
		this.meanTimeAtHigh = 4.0 ;
		this.meanTimeAtLow = 3.0 ;
		
		double[] durationsArray = { 6,0, 8.0};
		
		possibleCookingDurations = new ArrayList<Double>();
		for(double d : durationsArray)
			possibleCookingDurations.add(d);
		
		this.initialCall = true;
		
		this.meanTimeBetweenTempUpdate = 7.0;
		this.fs = OvenModel.Mode.OFF ;
		

		this.rg.reSeedSecure() ;

		// Initialise to get the correct current time.
		super.initialiseState(initialTime) ;

		// Schedule the first SwitchOn event.
		Duration d1 = new Duration(
							this.initialDelay,
							this.getSimulatedTimeUnit()) ;
		Duration d2 =
			new Duration(
					2.0 * this.meanTimeBetweenUsages *
											this.rg.nextBeta(1.75, 1.75),
					this.getSimulatedTimeUnit()) ;
		Time t = this.getCurrentStateTime().add(d1).add(d2) ;
		this.scheduleEvent(new SetHigh(t)) ;
		
		Collections.shuffle(possibleCookingDurations);
		Duration d3 = new Duration(this.possibleCookingDurations.get(0), this.getSimulatedTimeUnit());
		this.scheduleEvent(new SetOff(this.getCurrentStateTime().add(d1).add(d2).add(d3)));
		
		//this.scheduleEvent(new TriggerTempCheck(t)) ;

		// Redo the initialisation to take into account the initial event
		// just scheduled.
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.nextTimeAdvance) ;

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
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
		// This method implements a usage scenario for the hair dryer.
		// Here, we assume that the hair dryer is used once each cycle (day)
		// and then it starts in low mode, is set in high mode shortly after,
		// used for a while in high mode and then set back in low mode to
		// complete the drying.
		Duration d, d1 ;
		// See what is the type of event to be executed
		
		if (this.nextEvent.equals(SetHigh.class)) {
			applyState(OvenModel.Mode.HIGH);
			// when a switch on event has been issued, plan the next event as
			// a set high (the hair dryer is switched on in low mode
			
			
			
			//OPEN (- CLOSE?) ---------- OPEN (- CLOSE ?) ----------- OPEN - CLOSE
			
			// also, plan the next switch on for the next day
			d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
			if(Math.random() < 0.5)
			{
				this.scheduleEvent(new SetHigh(this.getCurrentStateTime().add(d))) ;
				
				Collections.shuffle(possibleCookingDurations);
				d1 = new Duration(this.possibleCookingDurations.get(0), this.getSimulatedTimeUnit());
				this.scheduleEvent(new SetOff(this.getCurrentStateTime().add(d).add(d1)));
			}
			else {
				this.scheduleEvent(new SetLow(this.getCurrentStateTime().add(d))) ;

				Collections.shuffle(possibleCookingDurations);
				d1 = new Duration(this.possibleCookingDurations.get(0), this.getSimulatedTimeUnit());
				this.scheduleEvent(new SetOff(this.getCurrentStateTime().add(d).add(d1)));
			}
				
		}else{
			if (this.nextEvent.equals(SetLow.class)) {
				applyState(OvenModel.Mode.LOW);
				// when a switch on event has been issued, plan the next event as
				// a set high (the hair dryer is switched on in low mode
				
				//OPEN (- CLOSE?) ---------- OPEN (- CLOSE ?) ----------- OPEN - CLOSE
				
				// also, plan the next switch on for the next day
				d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
				if(Math.random() < 0.5)
				{
					this.scheduleEvent(new SetHigh(this.getCurrentStateTime().add(d))) ;
					
					Collections.shuffle(possibleCookingDurations);
					d1 = new Duration(this.possibleCookingDurations.get(0), this.getSimulatedTimeUnit());
					this.scheduleEvent(new SetOff(this.getCurrentStateTime().add(d).add(d1)));
				}
				else {
					this.scheduleEvent(new SetLow(this.getCurrentStateTime().add(d))) ;

					Collections.shuffle(possibleCookingDurations);
					d1 = new Duration(this.possibleCookingDurations.get(0), this.getSimulatedTimeUnit());
					this.scheduleEvent(new SetOff(this.getCurrentStateTime().add(d).add(d1)));
				}
			}
			else {
				assert this.nextEvent.equals(SetOff.class);
				applyState(OvenModel.Mode.OFF);
			}
		}
	}
	
	
	public void applyState(Mode s)
	{
		assert	s != null ;

		if (s == Mode.OFF) {
			fs = Mode.OFF;
		} else {
			if(s == Mode.LOW) {
				fs = Mode.LOW;
			}
			else {
				assert	s == Mode.HIGH;
				fs = Mode.HIGH;
			}
		}
	}
	
}
