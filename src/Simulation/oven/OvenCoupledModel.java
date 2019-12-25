package Simulation.oven;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class OvenCoupledModel extends CoupledModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L ;
	/** URI of the unique instance of this class (in this example).			*/
	public static final String	URI = "TestCoupledModel" ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public OvenCoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine,
		ModelDescriptionI[] submodels,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
			  imported, reexported, connections,
			  importedVars, reexportedVars, bindings);
	}
	
	public static Architecture	build() throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;

		atomicModelDescriptors.put(
				OvenModel.URI,
				AtomicHIOA_Descriptor.create(
						OvenModel.class,
						OvenModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		atomicModelDescriptors.put(
				OvenUserModel.URI,
				AtomicModelDescriptor.create(
						OvenUserModel.class,
						OvenUserModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<String,CoupledModelDescriptor>() ;

		Set<String> submodels = new HashSet<String>() ;
		submodels.add(OvenModel.URI) ;
		submodels.add(OvenUserModel.URI) ;

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>() ;
		EventSource from1 =
				new EventSource(OvenUserModel.URI, SetOff.class) ;
		EventSink[] to1 =
				new EventSink[] {
						new EventSink(OvenModel.URI, SetOff.class)} ;
		connections.put(from1, to1) ;
		EventSource from2 =
				new EventSource(OvenUserModel.URI, SetHigh.class) ;
		EventSink[] to2 = new EventSink[] {
				new EventSink(OvenModel.URI, SetHigh.class)} ;
		connections.put(from2, to2) ;
		
		EventSource from3 =
				new EventSource(OvenUserModel.URI, SetLow.class) ;
		EventSink[] to3 = new EventSink[] {
				new EventSink(OvenModel.URI, SetLow.class)} ;
		connections.put(from3, to3) ;

		coupledModelDescriptors.put(
					OvenCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							OvenCoupledModel.class,
							OvenCoupledModel.URI,
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
						OvenCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS);
	}
}