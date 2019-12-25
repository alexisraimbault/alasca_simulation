package Simulation.fridge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import events.Close;
import events.Open;
import events.TriggerTempCheck;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;

public class FridgeComponent extends AbstractCyPhyComponent {
	
	protected			FridgeComponent() throws Exception
	{
		super(1, 0) ;
		this.initialise() ;
	}
	
	protected			FridgeComponent(String reflectionInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		this.initialise() ;
	}
	
	protected void		initialise() throws Exception
	{
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new AtomicSimulatorPlugin() ;
		this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		this.asp.setSimulationArchitecture(localArchitecture) ;
		this.installPlugin(this.asp) ;
	}
	
	@Override
	protected Architecture	createLocalArchitecture(String architectureURI)
	throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;

		atomicModelDescriptors.put(
				FridgeModel.URI,
				AtomicHIOA_Descriptor.create(
						FridgeModel.class,
						FridgeModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		atomicModelDescriptors.put(
				FridgeUserModel.URI,
				AtomicModelDescriptor.create(
						FridgeUserModel.class,
						FridgeUserModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<String,CoupledModelDescriptor>() ;

		Set<String> submodels = new HashSet<String>() ;
		submodels.add(FridgeModel.URI) ;
		submodels.add(FridgeUserModel.URI) ;

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>() ;
		EventSource from1 =
				new EventSource(FridgeUserModel.URI, Open.class) ;
		EventSink[] to1 =
				new EventSink[] {
						new EventSink(FridgeModel.URI, Open.class)} ;
		connections.put(from1, to1) ;
		EventSource from2 =
				new EventSource(FridgeUserModel.URI, Close.class) ;
		EventSink[] to2 = new EventSink[] {
				new EventSink(FridgeModel.URI, Close.class)} ;
		connections.put(from2, to2) ;
		
		EventSource from3 =
				new EventSource(FridgeUserModel.URI, TriggerTempCheck.class) ;
		EventSink[] to3 = new EventSink[] {
				new EventSink(FridgeModel.URI, TriggerTempCheck.class)} ;
		connections.put(from3, to3) ;

		coupledModelDescriptors.put(
					FridgeCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							FridgeCoupledModel.class,
							FridgeCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE,
							null,
							null,
							null)) ;

		return new Architecture(
						FridgeCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS);
	}
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.logMessage("Fridge component begins execution.") ;
	}
}
