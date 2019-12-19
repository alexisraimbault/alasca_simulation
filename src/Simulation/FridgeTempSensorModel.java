package Simulation;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

@ModelExternalEvents(imported = {TicEvent.class},
					exported = {FridgeTemp.class})
public class FridgeTempSensorModel extends AtomicHIOAwithEquations
{
	private static final long				serialVersionUID = 1L ;
	/** an URI to be used when create an instance of the model.				*/
	public static final String				URI = "FridgeTempSensorModel" ;

	/** true when a external event triggered a reading.						*/
	protected boolean						triggerReading ;
	/** the last value emitted as a reading of the battery level.		 	*/
	protected double						lastReading ;
	/** the simulation time at the last reading.							*/
	protected double						lastReadingTime ;
	/** history of readings, for the simulation report.						*/
	protected final Vector<FridgeTemp>	readings ;

	/** frame used to plot the battery level readings during the
	 *  simulation.															*/
	protected XYPlotter						plotter ;
	
	@ImportedVariable(type = Double.class)
	protected Value<Double>					fridgeTemp ;
	
	public				FridgeTempSensorModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			SimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.setLogger(new StandardLogger()) ;
		this.readings = new Vector<FridgeTemp>() ;
		this.lastReading = -1.0 ;
	}
	
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname =
			this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pd) ;
		this.plotter.createSeries("standard") ;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.triggerReading = false ;
		this.lastReadingTime = initialTime.getSimulatedTime() ;
		this.readings.clear() ;
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
		}

		super.initialiseState(initialTime) ;
	}
	
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		this.fridgeTemp.v =15.0 ;

		super.initialiseVariables(startTime);
	}
	
	@Override
	public Duration		timeAdvance()
	{
		if (this.triggerReading) {
			// immediate internal event when a reading is triggered.
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return Duration.INFINITY ;
		}
	}
	
	@Override
	public Vector<EventI>	output()
	{
		if (this.triggerReading) {
			// Plotting, plays no role in the simulation
			if (this.plotter != null) {
				this.plotter.addData(
						"standard",
						this.lastReadingTime,
						this.fridgeTemp.v) ;
				this.plotter.addData(
						"standard",
						this.getCurrentStateTime().getSimulatedTime(),
						this.fridgeTemp.v) ;
			}
			this.lastReading = this.fridgeTemp.v ;
			this.lastReadingTime =
					this.getCurrentStateTime().getSimulatedTime() ;

			// Create and emit the battery level event.
			Vector<EventI> ret = new Vector<EventI>(1) ;
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
			FridgeTemp bl = new FridgeTemp(t, this.fridgeTemp.v) ;
			ret.add(bl) ;

			// Memorise the reading for the simulation report.
			this.readings.add(bl) ;


			// The reading that was triggered has now been processed.
			this.triggerReading = false ;
			return ret ;
		} else {
			return null ;
		}
	}
	
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;

	}
	
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;

		Vector<EventI> current = this.getStoredEventAndReset() ;
		boolean	ticReceived = false ;
		for (int i = 0 ; !ticReceived && i < current.size() ; i++) {
			if (current.get(i) instanceof TicEvent) {
				ticReceived = true ;
			}
		}
		if (ticReceived) {
			this.triggerReading = true ;
		}
	}
}
