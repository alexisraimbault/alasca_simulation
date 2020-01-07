package Simulation.fridge;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;

public class FridgeSimulatorPlugin 
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
		
		assert	m instanceof FridgeModel ;
		
		if (name.equals("state")) {
			return ((FridgeModel)m).getState() ;
		} else {
			if(name.equals("mode")) {
				return ((FridgeModel)m).getMode() ;
			}else
			{
				assert	name.equals("temperature") ;
				return ((FridgeModel)m).getTemp() ;
			}
		}
	}
	
	public void setHouseTemp(String modelURI, double temp) throws Exception
	{
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		
		assert	m instanceof FridgeModel ;
		((FridgeModel)m).setHouseTemp(temp);
	}
}
