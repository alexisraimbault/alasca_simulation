package Simulation;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import Simulation.FridgeModel.State;
import events.Close;
import events.Open;
import events.TriggerTempCheck;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

@ModelExternalEvents(exported = {Open.class,
		 						Close.class,
		 						TriggerTempCheck.class})

public class FridgeUserModel extends AtomicES_Model
{
	private static final long serialVersionUID = 1L ;
	public static final String	URI = "FridgeUserModel" ;
	
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
	protected Class<?>	nextEvent ;
	
	protected final RandomDataGenerator		rg ;
	
	protected FridgeModel.State fs ;
	
	protected boolean initialCall;
	
	protected XYPlotter modePlotter ;
	
	public FridgeUserModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			SimulatorI simulationEngine
			) throws Exception
		{
			super(uri, simulatedTimeUnit, simulationEngine) ;

			this.rg = new RandomDataGenerator() ;
			
			PlotterDescription pd =
					new PlotterDescription(
							"Fridge State",
							"Time (sec)",
							"state",
							700,
							0,
							600,
							400) ;
			this.modePlotter = new XYPlotter(pd) ;
			this.modePlotter.createSeries(SERIES) ;

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
		
		this.initialCall = true;
		
		this.meanTimeBetweenTempUpdate = 7.0;
		this.fs = FridgeModel.State.CLOSED ;
		
		this.modePlotter.initialise() ;
		
		this.modePlotter.showPlotter() ;

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
		this.scheduleEvent(new Open(t)) ;
		
		//this.scheduleEvent(new TriggerTempCheck(t)) ;

		// Redo the initialisation to take into account the initial event
		// just scheduled.
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
		this.logMessage("FridgeUserModel::timeAdvance() 1 " + d +
									" " + this.eventListAsString()) ;
		this.modePlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				state2int(fs));
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

		this.logMessage("FridgeUserModel::output() " +
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
		Duration d ;
		// See what is the type of event to be executed
		
		if (this.nextEvent.equals(Open.class)) {
			applyState(State.OPEN);
			// when a switch on event has been issued, plan the next event as
			// a set high (the hair dryer is switched on in low mode
			d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75),
							 this.getSimulatedTimeUnit()) ;
			// compute the time of occurrence (in the future)
			Time t = this.getCurrentStateTime().add(d) ;
			// schedule the event
			if(Math.random() < 0.65)
				this.scheduleEvent(new Close(t)) ;
			
			//OPEN (- CLOSE?) ---------- OPEN (- CLOSE ?) ----------- OPEN - CLOSE
			
			// also, plan the next switch on for the next day
			d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
			this.scheduleEvent(
						new Open(this.getCurrentStateTime().add(d))) ;
			
			
			/*if(this.initialCall)
			{
				this.logMessage("test initial call...");
				d = new Duration(this.meanTimeBetweenTempUpdate, this.getSimulatedTimeUnit()) ;
				this.scheduleEvent(
							new TriggerTempCheck(this.getCurrentStateTime().add(d))) ;
				this.initialCall = false;
			}*/
		}else{
			assert this.nextEvent.equals(Close.class);
			applyState(State.CLOSED);
		}
	}
	
	
	public static int state2int(State s)
	{
		assert	s != null ;

		if (s == State.OPEN) {
			return 2 ;
		} else {
			assert	s == State.CLOSED;
			return 1 ;
		}
	}
	
	public void applyState(State s)
	{
		assert	s != null ;

		if (s == State.OPEN) {
			fs = State.OPEN;
		} else {
			assert	s == State.CLOSED;
			fs = State.CLOSED;
		}
		this.modePlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				state2int(fs));
	}
	
}
