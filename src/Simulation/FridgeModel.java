package Simulation;

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
import events.SwitchOn;
import events.SwitchOff;
import events.Freeze;
import events.Rest;

@ModelExternalEvents(imported = {SwitchOn.class,
								 SwitchOff.class,
								 Freeze.class,
								 Rest.class})

public class FridgeModel
extends		AtomicHIOAwithEquations
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum State {
		OFF,
		FREEZE,
		REST
	}
	
	private static final String		SERIES = "temperature" ;
	
	public static final String		URI = "FridgeModel" ;
	
	protected XYPlotter tempPlotter ;
	
	protected State currentState;
	
	protected final Value<Double> currentTemp = new Value<Double>(this, 0.0, 0) ;
	
	protected EmbeddingComponentStateAccessI componentRef ;
	
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
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		// the hair dryer starts in mode OFF
		this.currentState = State.OFF ;
		// initialisation of the intensity plotter 
		this.tempPlotter.initialise() ;
		// show the plotter on the screen
		this.tempPlotter.showPlotter() ;

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
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

		super.initialiseVariables(startTime);
	}
	
	public Duration		timeAdvance()
	{
		if (this.componentRef == null) {
			// the model has no internal event, however, its state will evolve
			// upon reception of external events.
			return Duration.INFINITY ;
		} else {
			// This is to test the embedding component access facility.
			return new Duration(10.0, TimeUnit.SECONDS) ;
		}
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
		this.tempPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getTemp());

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
		this.tempPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getTemp());

		super.userDefinedExternalTransition(elapsedTime) ;
		if (this.hasDebugLevel(2)) {
			this.logMessage("FridgeModel::userDefinedExternalTransition 5") ;
		}
	}
	
	@Override
	public Vector<EventI>	output()
	{
		// the model does not export any event.
		return null ;
	}
	
	public double getTemp()
	{
		return currentTemp.v;
	}
	
	public void setState(State s)
	{
		this.currentState = s ;
		switch (s)
		{
			case OFF : 
				break ;
			case FREEZE :
				this.currentTemp.v += ((-3 - this.currentTemp.v )/13);
				break ;
			case REST :
				this.currentTemp.v = ((3 - this.currentTemp.v )/13) ;
		}
	}
	
	public State		getState()
	{
		return this.currentState ;
	}

}
