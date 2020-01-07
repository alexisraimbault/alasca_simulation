package Simulation.fridge;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import events.AbstractFridgeEvent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import events.Open;
import events.TriggerTempCheck;
import events.Close;

@ModelExternalEvents(imported = {Open.class,
								 Close.class,
			 					TriggerTempCheck.class})

public class FridgeModel
extends		AtomicHIOAwithEquations
{
	/**
	 * plaque temperature
	 * 
	 * on -> 3
	 * freeze -> -3
	 * 
	 * DOOR CLOSED :
	 * t(n+1) = t(n) + ((tPlaque - t(n) / 13))
	 * 
	 * DOOR OPEN :
	 * t(n+1) = t(n) + ((tPlaque - t(n) / 13)) + ((tOutside - t(n)) /13)
	 */
	
	private static final long serialVersionUID = 1L;

	public static enum State {
		OPEN,
		CLOSED
	}
	
	public static enum Mode {
		OFF,
		FREEZE,
		REST
	}
	
	private double outsideTemperature = 20.0;
	
	private static final String		SERIES = "temperature" ;
	private static final String		SERIES1 = "mode" ;
	
	public static final String		URI = "FridgeModel" ;
	
	protected XYPlotter tempPlotter ;
	protected XYPlotter modePlotter ;
	
	protected State currentState;
	
	protected Mode currentMode;
	
	protected final Value<Double> currentTemp = new Value<Double>(this, 0.0, 0) ;
	
	protected EmbeddingComponentStateAccessI componentRef ;
	
	protected Duration delay ;
	
	public FridgeModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			SimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// creation of a plotter to show the evolution of the intensity over
		// time during the simulation.
		PlotterDescription pd =
				new PlotterDescription(
						"Fridge Temperature",
						"Time (sec)",
						"Temperature (C)",
						100,
						0,
						600,
						400) ;
		this.tempPlotter = new XYPlotter(pd) ;
		this.tempPlotter.createSeries(SERIES) ;
		
		PlotterDescription pd1 =
				new PlotterDescription(
						"Fridge Mode",
						"Time (sec)",
						"Mode",
						100,
						400,
						600,
						400) ;
		this.modePlotter = new XYPlotter(pd1) ;
		this.modePlotter.createSeries(SERIES1) ;
		

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger()) ;
	}
	
	@Override
	public void setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// The reference to the embedding component
		this.componentRef =
			(EmbeddingComponentStateAccessI) simParams.get("componentRef") ;
		this.delay = new Duration(1.0, this.getSimulatedTimeUnit());
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		// the hair dryer starts in mode OFF
		this.currentState = State.CLOSED ;
		
		this.currentMode = Mode.REST;
		// initialisation of the intensity plotter 
		this.tempPlotter.initialise() ;
		// show the plotter on the screen
		this.tempPlotter.showPlotter() ;
		
		this.modePlotter.initialise() ;
		
		this.modePlotter.showPlotter() ;
		
		/*
		// Initialise to get the correct current time.
		super.initialiseState(initialTime) ;
		// Schedule the first SwitchOn event.
		Time t = this.getCurrentStateTime() ;
		this.scheduleEvent(new Open(t)) ;
		// Redo the initialisation to take into account the initial event
		// just scheduled.
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.nextTimeAdvance) ;
		*/

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		super.initialiseState(initialTime) ;
	}

	@Override
	protected void		initialiseVariables(Time startTime)
	{
		// as the hair dryer starts in mode OFF, its power consumption is 0
		this.currentTemp.v = 15.0 ;

		// first data in the plotter to start the plot.
		this.tempPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getTemp());
		
		this.modePlotter.addData(
				SERIES1,
				this.getCurrentStateTime().getSimulatedTime(),
				mode2int(currentMode));
		
		super.initialiseVariables(startTime);
	}
	
	public Duration		timeAdvance()
	{
		return this.delay;
	}
	
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if (this.componentRef != null) {
			// This is an example showing how to access the component state
			// from a simulation model; this must be done with care and here
			// we are not synchronising with other potential component threads
			// that may access the state of the component object at the same
			// time.
			try {
				this.logMessage("component state = " +
						componentRef.getEmbeddingComponentStateValue("state")) ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
	}
	
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		if (this.hasDebugLevel(2)) {
			this.logMessage("FridgeModel::userDefinedExternalTransition 1") ;
		}

		// get the vector of current external events
		Vector<EventI> currentEvents = this.getStoredEventAndReset() ;
		// when this method is called, there is at least one external event,
		// and for the hair dryer model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1 ;

		Event ce = (Event) currentEvents.get(0) ;
		assert	ce instanceof AbstractFridgeEvent ;
		if (this.hasDebugLevel(2)) {
			this.logMessage("FridgeModel::userDefinedExternalTransition 2 "
										+ ce.getClass().getCanonicalName()) ;
		}

		// the plot is piecewise constant; this data will close the currently
		// open piece
		/*this.tempPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getTemp());*/

		if (this.hasDebugLevel(2)) {
			this.logMessage("FridgeModel::userDefinedExternalTransition 3 "
															+ this.getState()) ;
		}

		// execute the current external event on this model, changing its state
		// and intensity level
		ce.executeOn(this) ;

		if (this.hasDebugLevel(1)) {
			this.logMessage("FridgeModel::userDefinedExternalTransition 4 "
															+ this.getState()) ;
		}

		// add a new data on the plotter; this data will open a new piece
		/*this.tempPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getTemp());*/
		super.userDefinedExternalTransition(elapsedTime) ;
		if (this.hasDebugLevel(2)) {
			this.logMessage("FridgeModel::userDefinedExternalTransition 5") ;
		}
	}
	
	@Override
	public Vector<EventI>	output()
	{
		this.updateTemperature();
		this.autoControll();
		return null ;
	}
	
	public double getTemp()
	{
		//System.out.println("TEST GET FRIDGE TEMP FRIDGE MODEL : " + currentTemp.v);
		return currentTemp.v;
	}
	
	public void setState(State s)
	{
		this.currentState = s ;
	}
	
	public void setMode(Mode m)
	{
		this.currentMode = m ;
	}
	
	public State		getState()
	{
		return this.currentState ;
	}
	
	public Mode		getMode()
	{
		return this.currentMode ;
	}
	
	public void autoControll()
	{
		if(this.currentMode == Mode.REST)
		{
			if(currentTemp.v > 7)
			{
				this.logMessage("FridgeModel::updateTemperature()::FREEZING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!") ;
				setMode(Mode.FREEZE);
			}
		}
		if(this.currentMode == Mode.FREEZE)
		{
			if(currentTemp.v < 3)
			{
				this.logMessage("FridgeModel::updateTemperature()::RESTING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!") ;
				setMode(Mode.REST);
			}
		}
		
		this.modePlotter.addData(
				SERIES1,
				this.getCurrentStateTime().getSimulatedTime(),
				mode2int(currentMode));
	}
	
	public void updateTemperature() {
		if(this.currentMode == Mode.REST)
		{
			if(this.currentState == State.CLOSED)
				currentTemp.v += ((3 - currentTemp.v)/13);
			else
			{
				currentTemp.v += ((outsideTemperature - currentTemp.v)/30);
				currentTemp.v += ((3 - currentTemp.v)/13);
			}
		}
		if(this.currentMode == Mode.FREEZE)
		{
			if(this.currentState == State.CLOSED)
				currentTemp.v += ((-3 - currentTemp.v)/13);
			else
			{
				currentTemp.v += ((outsideTemperature - currentTemp.v)/30);
				currentTemp.v += ((-3 - currentTemp.v)/13);
			}
				
		}
		this.tempPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getTemp());
	}

	
	public static int mode2int(Mode s)
	{
		assert	s != null ;

		if (s == Mode.OFF) {
			return 1 ;
		} else if (s == Mode.REST) {
			return 2 ;
		} else {
			assert	s == Mode.FREEZE;
			return 3 ;
		}
	}

	public void setHouseTemp(double temp) {
		this.outsideTemperature = temp;
	}

}