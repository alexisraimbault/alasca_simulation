package Simulation.oven;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;

public class OvenSimulatorPlugin 
extends		AtomicSimulatorPlugin
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public Object getModelStateValue(String modelURI, String name)
	throws Exception
	{
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		
		assert	m instanceof OvenModel ;
		
		if(name.equals("mode")) {
			return ((OvenModel)m).getMode() ;
		}else
		{
			assert	name.equals("consumption") ;
			return ((OvenModel)m).getCons() ;
		}
		
	}
}
