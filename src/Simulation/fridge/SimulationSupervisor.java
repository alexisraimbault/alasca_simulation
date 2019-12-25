package Simulation.fridge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import events.Close;
import events.Open;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.utils.PlotterDescription;

public class SimulationSupervisor extends AbstractComponent
{
	
	/** the supervisor plug-in attached to the component.					*/
	protected SupervisorPlugin		sp ;
	/** maps from URIs of models to URIs of the reflection inbound ports
	 *  of the components that hold them.									*/
	protected Map<String,String>	modelURIs2componentURIs ;
	
	protected 			SimulationSupervisor(
			Map<String,String> modelURIs2componentURIs
			) throws Exception
	{
		super(2, 0) ;

		assert	modelURIs2componentURIs != null ;
		this.initialise(modelURIs2componentURIs) ;
	}
	
	protected 			SimulationSupervisor(
			String reflectionInboundPortURI,
			Map<String,String> modelURIs2componentURIs
			) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0) ;

		this.initialise(modelURIs2componentURIs) ;
	}
	
	@SuppressWarnings("unchecked")
	protected void		initialise(
			Map<String,String> modelURIs2componentURIs
			) throws Exception
	{
		this.modelURIs2componentURIs = modelURIs2componentURIs ;

		this.tracer.setTitle("SupervisorComponent") ;
		this.tracer.setRelativePosition(0, 4) ;
		this.toggleTracing() ;

		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		
		atomicModelDescriptors.put(
				FridgeCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						FridgeCoupledModel.URI,
						(Class<? extends EventI>[])
							new Class<?>[]{
								Open.class,
								Close.class
							},
							null,
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(FridgeCoupledModel.URI))) ;
		
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>() ;
		
		Set<String> submodels3 = new HashSet<String>() ;
		submodels3.add(FridgeCoupledModel.URI) ;
		
		Map<EventSource,EventSink[]> connections3 =
				new HashMap<EventSource,EventSink[]>() ;
				
				
		/*ComponentModelArchitecture architecture =
				new ComponentModelArchitecture(
						MoleneModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS) ;*/
		//TODO
	
		//this.sp = new SupervisorPlugin(architecture) ;
		sp.setPluginURI("supervisor") ;
		this.installPlugin(this.sp) ;
		this.logMessage("Supervisor plug-in installed...") ;
		
	}
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.logMessage("supervisor component begins execution.") ;

		sp.createSimulator() ;

		Thread.sleep(1000L);
		this.logMessage("SupervisorComponent#execute 1") ;

		Map<String, Object> simParams = new HashMap<String, Object>() ;
		
		String modelURI = FridgeModel.URI ;
		/*simParams.put(
				modelURI + ":" +
						"fridge-temp-plotting",
				new PlotterDescription(
						"Fridge Model - temp",
						"Time (sec)",
						"temp (C)",
						SimulationMain.ORIGIN_X +
					  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y,
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;*/
		//TODO
		
		this.logMessage("SupervisorComponent#execute 2") ;

		sp.setSimulationRunParameters(simParams) ;

		this.logMessage("SupervisorComponent#execute 3") ;

		this.logMessage("supervisor component begins simulation.") ;
		long start = System.currentTimeMillis() ;
		sp.doStandAloneSimulation(0, 5000L) ;
		long end = System.currentTimeMillis() ;
		this.logMessage("supervisor component ends simulation. " + (end - start)) ;
	}
}
