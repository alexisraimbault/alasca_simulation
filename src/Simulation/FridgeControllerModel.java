package Simulation;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import Simulation.FridgeModel.Mode;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

@ModelExternalEvents(
		imported = {FridgeTemp.class},
		exported = {Oning.class, Ofing.class, Resting.class, Freezing.class})
public class FridgeControllerModel extends AtomicModel{
	public enum Decision
	{
		FREEZE,
		REST
	}
	
	public static class	DecisionPiece
	{
		public final double		first ;
		public final double		last ;
		public final Decision	d ;

		public			DecisionPiece(
			double first,
			double last,
			Decision d
			)
		{
			super();
			this.first = first;
			this.last = last;
			this.d = d;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "(" + this.first + ", " + this.last + ", " + this.d + ")" ;
		}
	}
	
	private static final long	serialVersionUID = 1L ;
	private static final String	SERIES = "mode/last decision" ;
	
	public static final String	URI =
			"FridgerControllerModel" ;
	
	public static double hotThreshold = 6.0;
	
	public static double notHotThreshold = 3.0;
	
	protected Decision triggeredDecision ;
	
	protected boolean mustTransmitDecision ;
	
	protected double currentFridgeTemp ;
	
	protected Mode currentMode ;
	
	protected Decision lastDecision ;
	
	protected double lastDecisionChangeTime ;
	
	protected final Vector<DecisionPiece> decisionFunction ;
	
	protected XYPlotter plotter ;
	
	public				FridgeControllerModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			SimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.decisionFunction = new Vector<DecisionPiece>() ;

		this.setLogger(new StandardLogger()) ;
//			this.setDebugLevel(1);
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
		this.plotter.createSeries(SERIES) ;

	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;
		this.triggeredDecision = Decision.REST;
		this.currentMode = Mode.REST;
		this.lastDecision = Decision.REST;
		this.mustTransmitDecision = false;
		this.lastDecisionChangeTime = initialTime.getSimulatedTime() ;
		this.decisionFunction.clear() ;
		this.currentFridgeTemp = 15.0;
		
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
			this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.stateToInteger(this.currentMode)) ;
		}
	}
	
	protected int		stateToInteger(Mode s)
	{
		assert	s != null ;

		if (s == Mode.OFF) {
			return 1 ;
		} else if (s == Mode.REST) {
			return 2 ;
		} else {
			assert	s == Mode.FREEZE ;
			return 3 ;
		}
	}
	
	@Override
	public Vector<EventI>	output()
	{
		Vector<EventI> ret = null ;
		if (this.triggeredDecision == Decision.FREEZE) {
			ret.add(new Freezing(this.getCurrentStateTime(), null)) ;
		} else {
			assert	this.triggeredDecision == Decision.REST ;
			ret.add(new Resting(this.getCurrentStateTime(), null)) ;
		}
		
		this.decisionFunction.add(
				new DecisionPiece(this.lastDecisionChangeTime,
							  this.getCurrentStateTime().getSimulatedTime(),
							  this.lastDecision)) ;

		this.lastDecision = this.triggeredDecision ;
		this.lastDecisionChangeTime =
							this.getCurrentStateTime().getSimulatedTime() ;
		this.mustTransmitDecision = false ;
		return ret ;
	}
	
	@Override
	public Duration		timeAdvance()
	{
		if (this.mustTransmitDecision) {
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return Duration.INFINITY ;
		}
	}
	
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		Vector<EventI> current = this.getStoredEventAndReset() ;
		for (int i = 0 ; i < current.size() ; i++) {
			if (current.get(i) instanceof FridgeTemp) {
				this.currentFridgeTemp =
						((FridgeTemp.Reading)
							((FridgeTemp)current.get(i)).
											getEventInformation()).value ;
			}
		}
		Mode oldMode = this.currentMode;
		
		if(this.currentMode == Mode.OFF) {
			// Do nothing
		}else if(this.currentMode == Mode.REST){
			if(this.currentFridgeTemp >= hotThreshold)
			{
				this.triggeredDecision = Decision.FREEZE;
				this.currentMode = Mode.FREEZE;
				this.mustTransmitDecision = true;
			}
		}else{
			assert this.currentMode == Mode.FREEZE;
			if(this.currentFridgeTemp >= notHotThreshold)
			{
				this.triggeredDecision = Decision.REST;
				this.currentMode = Mode.REST;
				this.mustTransmitDecision = true;
			}
		}
		
		if (this.plotter != null && oldMode != this.currentMode) {
			this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.stateToInteger(oldMode)) ;
			this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.stateToInteger(this.currentMode)) ;

		}
	}
	
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		if (this.plotter != null) {
			this.plotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.stateToInteger(this.currentMode)) ;
		}
		super.endSimulation(endTime) ;
	}
}
