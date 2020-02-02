package Simulation.energy;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


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

@ModelExternalEvents(imported = { Charge.class, Consume.class })

public class BatteryModel extends AtomicHIOAwithEquations {
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
	
	private int nbBatteries;
	private double singleBatteryCapacity;
	private double batteryCost;


	private static final String SERIES = "battery";
	private static final String SERIES1 = "balance";

	public static final String URI = "BatteryModel";

	protected XYPlotter batteryPlotter;
	protected XYPlotter balancePlotter;

	protected final Value<Double> currentBattery = new Value<Double>(this, 0.0, 0) ;
	protected final Value<Double> currentBalance = new Value<Double>(this, 0.0, 0) ;

	protected EmbeddingComponentStateAccessI componentRef;

	protected Duration delay;
	
	protected double fridgeConsumption;
	protected double ovenConsumption;
	protected double heaterConsumption;

	public BatteryModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		
		nbBatteries = 1;
		singleBatteryCapacity = 50;
		batteryCost = 100;
		currentBalance.v = 0.0;
		
		this.fridgeConsumption = 2;
		this.ovenConsumption = 2;
		this.heaterConsumption = 2;

		PlotterDescription pd = new PlotterDescription("Remaining Battery", "Time (sec)", "kw", 700, 800, 600, 400);
		this.batteryPlotter = new XYPlotter(pd);
		this.batteryPlotter.createSeries(SERIES);
		
		PlotterDescription pd1 = new PlotterDescription("Remaining Balance", "Time (sec)", "euros", 1400, 800, 600, 400);
		this.balancePlotter = new XYPlotter(pd1);
		this.balancePlotter.createSeries(SERIES1);


		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get("componentRef1");
		this.delay = new Duration(1.0, this.getSimulatedTimeUnit());
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.batteryPlotter.initialise();
		this.batteryPlotter.showPlotter();
		
		this.balancePlotter.initialise();
		this.balancePlotter.showPlotter();



		try {
			this.setDebugLevel(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {

		this.batteryPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), currentBattery.v);
		this.balancePlotter.addData(SERIES1, this.getCurrentStateTime().getSimulatedTime(), currentBalance.v);

		super.initialiseVariables(startTime);
	}

	public Duration timeAdvance() 
	{
		return this.delay;
	}
	
	public void tryBuyBattery()
	{
		
		if(currentBalance.v > batteryCost)
		{
			System.out.println("TEST : BUYING NEW BATTERY !!!!!!");
			currentBalance.v -= batteryCost;
			nbBatteries ++;
		}
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {

			try {
				this.logMessage("component state = " + componentRef.getEmbeddingComponentStateValue("mode"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("TestModel::userDefinedExternalTransition 1");
		}


		Vector<EventI> currentEvents = this.getStoredEventAndReset();

		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractBatteryEvent;
		if (this.hasDebugLevel(2)) {
			this.logMessage("TestModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}





		ce.executeOn(this);
		
		this.currentBattery.v -= this.fridgeConsumption;
		
		this.currentBattery.v -= this.heaterConsumption;
		
		this.currentBattery.v -= this.ovenConsumption;


		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("TestModel::userDefinedExternalTransition 5");
		}
	}

	@Override
	public Vector<EventI> output() {
		this.tryBuyBattery();
		this.updateBattery();
		this.autoControll();
		
		return null;
	}

	public void autoControll() {
		//do nothing
	}

	public void updateBattery() {

		this.batteryPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), currentBattery.v);
		this.balancePlotter.addData(SERIES1, this.getCurrentStateTime().getSimulatedTime(), currentBalance.v);
	}

	public void charge(double energy, double balance)
	{
		this.currentBattery.v = Math.min(this.currentBattery.v + energy, nbBatteries*singleBatteryCapacity );
		this.currentBalance.v += balance;
	}
	
	public void consume(double energy)
	{
		this.currentBattery.v -= energy;
	}
	
	public double getBattery() {
		return currentBattery.v;
	}
	
	public void setFridgeConsumption(double cons) {
		this.fridgeConsumption = cons;
	}
	
	public void setOvenConsumption(double cons) {
		this.ovenConsumption = cons;
	}
	
	public void setHeaterConsumption(double cons) {
		this.heaterConsumption = cons;
	}

	public double getStorageCapacity() {
		return this.nbBatteries*this.singleBatteryCapacity;
	}

}