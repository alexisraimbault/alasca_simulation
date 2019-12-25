package Simulation.oven;

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

@ModelExternalEvents(imported = { SetOff.class, SetHigh.class, SetLow.class })

public class OvenModel extends AtomicHIOAwithEquations {
	/**
	 * plaque temperature
	 * 
	 * on -> 3 freeze -> -3
	 * 
	 * DOOR CLOSED : t(n+1) = t(n) + ((tPlaque - t(n) / 13))
	 * 
	 * DOOR OPEN : t(n+1) = t(n) + ((tPlaque - t(n) / 13)) + ((tOutside - t(n)) /13)
	 */

	private static final long serialVersionUID = 1L;

	public static enum Mode {
		OFF, LOW, HIGH
	}

	private static final String SERIES = "temperature";
	private static final String SERIES1 = "mode";

	public static final String URI = "TestModel";

	protected XYPlotter tempPlotter;
	protected XYPlotter modePlotter;

	protected Mode currentMode;


	protected EmbeddingComponentStateAccessI componentRef;

	protected Duration delay;

	public OvenModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

// creation of a plotter to show the evolution of the intensity over
// time during the simulation.
		PlotterDescription pd = new PlotterDescription("Oven Consumption", "Time (sec)", "kwh", 700, 0, 600,
				400);
		this.tempPlotter = new XYPlotter(pd);
		this.tempPlotter.createSeries(SERIES);

		PlotterDescription pd1 = new PlotterDescription("Oven Mode", "Time (sec)", "Mode", 700, 400, 600, 400);
		this.modePlotter = new XYPlotter(pd1);
		this.modePlotter.createSeries(SERIES1);

// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
// The reference to the embedding component
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get("componentRef1");
		this.delay = new Duration(1.0, this.getSimulatedTimeUnit());
	}

	@Override
	public void initialiseState(Time initialTime) {
// the hair dryer starts in mode OFF

		this.currentMode = Mode.OFF;
// initialisation of the intensity plotter 
		this.tempPlotter.initialise();
// show the plotter on the screen
		this.tempPlotter.showPlotter();

		this.modePlotter.initialise();

		this.modePlotter.showPlotter();

		/*
		 * // Initialise to get the correct current time.
		 * super.initialiseState(initialTime) ; // Schedule the first SwitchOn event.
		 * Time t = this.getCurrentStateTime() ; this.scheduleEvent(new Open(t)) ; //
		 * Redo the initialisation to take into account the initial event // just
		 * scheduled. this.nextTimeAdvance = this.timeAdvance() ; this.timeOfNextEvent =
		 * this.getCurrentStateTime().add(this.nextTimeAdvance) ;
		 */

		try {
// set the debug level triggering the production of log messages.
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
// as the hair dryer starts in mode OFF, its power consumption is 0

// first data in the plotter to start the plot.
		this.tempPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), getCons(currentMode));

		this.modePlotter.addData(SERIES1, this.getCurrentStateTime().getSimulatedTime(), mode2int(currentMode));

		super.initialiseVariables(startTime);
	}

	public Duration timeAdvance() {
		return this.delay;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {
// This is an example showing how to access the component state
// from a simulation model; this must be done with care and here
// we are not synchronising with other potential component threads
// that may access the state of the component object at the same
// time.
			try {
				this.logMessage("component state = " + componentRef.getEmbeddingComponentStateValue("state"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("TestModel::userDefinedExternalTransition 1");
		}

// get the vector of current external events
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
// when this method is called, there is at least one external event,
// and for the hair dryer model, there will be exactly one by
// construction.
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractFridgeEvent;
		if (this.hasDebugLevel(2)) {
			this.logMessage("TestModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

// the plot is piecewise constant; this data will close the currently
// open piece
		/*
		 * this.tempPlotter.addData( SERIES,
		 * this.getCurrentStateTime().getSimulatedTime(), this.getTemp());
		 */

		if (this.hasDebugLevel(2)) {
			this.logMessage("TestModel::userDefinedExternalTransition 3 " + this.getMode());
		}

// execute the current external event on this model, changing its state
// and intensity level
		ce.executeOn(this);

		if (this.hasDebugLevel(1)) {
			this.logMessage("TestModel::userDefinedExternalTransition 4 " + this.getMode());
		}

// add a new data on the plotter; this data will open a new piece
		/*
		 * this.tempPlotter.addData( SERIES,
		 * this.getCurrentStateTime().getSimulatedTime(), this.getTemp());
		 */
		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("TestModel::userDefinedExternalTransition 5");
		}
	}

	@Override
	public Vector<EventI> output() {
		
		this.updateTemperature();
		this.autoControll();
		
		return null;
	}


	public void setMode(Mode m) {
		this.currentMode = m;
	}

	public Mode getMode() {
		return this.currentMode;
	}

	public void autoControll() {

		this.modePlotter.addData(SERIES1, this.getCurrentStateTime().getSimulatedTime(), mode2int(currentMode));
	}

	public void updateTemperature() {

		this.tempPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), getCons(currentMode));
	}

	public static int mode2int(Mode s) {
		assert s != null;

		if (s == Mode.OFF) {
			return 1;
		} else if (s == Mode.LOW) {
			return 2;
		} else {
			assert s == Mode.HIGH;
			return 3;
		}
	}

	public static double getCons(Mode s) {
		assert s != null;

		if (s == Mode.OFF) {
			return 0.0;
		} else if (s == Mode.LOW) {
			return 0.7;
		} else {
			assert s == Mode.HIGH;
			return 1.0;
		}
	}
	
	public double getCons() {
		return getCons(currentMode);
	}

}