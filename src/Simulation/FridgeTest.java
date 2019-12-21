package Simulation;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import Simulation.FridgeModel.State;
import components.Fridge;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.cyphy.examples.sg.equipments.hairdryer.components.HairDryerSimulatorPlugin;
import fr.sorbonne_u.cyphy.examples.sg.equipments.hairdryer.models.HairDryerCoupledModel;
import fr.sorbonne_u.cyphy.examples.sg.equipments.hairdryer.models.HairDryerModel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import interfaces.FridgeI;
import interfaces.LaunchableOfferedI;
import ports.ComponentEPObp;
import ports.FridgeIbp;
import ports.LaunchableIbp;

public class FridgeTest 
extends		AbstractCyPhyComponent
implements	EmbeddingComponentStateAccessI{
	
	protected FridgeSimulatorPlugin		asp ;
	

	public			FridgeTest() throws Exception
	{
		// 2 threads to be able to execute tasks and requests while executing
		// the DEVS simulation.
		super(2, 0) ;
		this.initialise() ;
		

	}

	protected FridgeTest(String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		this.initialise() ;
	}
	
	protected void		initialise() throws Exception
	{
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new FridgeSimulatorPlugin() ;
		// Set the URI of the plug-in, using the URI of its associated
		// simulation model.
		this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		// Set the simulation architecture.
		this.asp.setSimulationArchitecture(localArchitecture) ;
		// Install the plug-in on the component, starting its own life-cycle.
		this.installPlugin(this.asp) ;

		// Toggle logging on to get a log on the screen.
		this.toggleLogging() ;
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		return this.asp.getModelStateValue(FridgeModel.URI, "state") + " " + this.asp.getModelStateValue(FridgeModel.URI, "mode") + " " + this.asp.getModelStateValue(FridgeModel.URI, "temperature");
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return FridgeCoupledModel.build() ;
	}
	
	@Override
	public void			execute() throws Exception
	{
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
		// To give an example of the embedding component access facility, the
		// following lines show how to set the reference to the embedding
		// component or a proxy responding to the access calls.
		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		simParams.put("componentRef", this) ;
		this.asp.setSimulationRunParameters(simParams) ;
		// Start the simulation.
		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							asp.doStandAloneSimulation(0.0, 1000000L) ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}) ;
		Thread.sleep(10L) ;
		// During the simulation, the following lines provide an example how
		// to use the simulation model access facility by the component.
		for (int i = 0 ; i < 100 ; i++) {
			this.logMessage("Fridge " +
				this.asp.getModelStateValue(FridgeModel.URI, "state") + " " +
				this.asp.getModelStateValue(FridgeModel.URI, "temperature")) ;
			Thread.sleep(5L) ;
		}
	}
	
	public double getTemperature() throws Exception {
		System.out.println("TEST GET FRIDGE TEMP FRIDGE : " + this.asp.getModelStateValue(FridgeModel.URI, "temperature"));
		return (double) this.asp.getModelStateValue(FridgeModel.URI, "temperature");
	}
	
	public State getState() throws Exception {
		System.out.println("TEST GET FRIDGE STATE FRIDGE : " + this.asp.getModelStateValue(FridgeModel.URI, "state"));
		return (State) this.asp.getModelStateValue(FridgeModel.URI, "state");
	}
	
}