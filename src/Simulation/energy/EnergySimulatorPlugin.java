package Simulation.energy;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;

public class EnergySimulatorPlugin extends		AtomicSimulatorPlugin
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public Object getModelStateValue(String modelURI, String name)
	throws Exception
	{
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		
		assert	m instanceof BatteryModel ;
		
		assert (name.equals("battery"));
		return ((BatteryModel)m).getBattery() ;
		
	}
	
	public void setFridgeConsumption(String modelURI, double cons) throws Exception{
		
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		
		assert	m instanceof BatteryModel ;
		((BatteryModel)m).setFridgeConsumption(cons);
	}
	
	public void setHeaterConsumption(String modelURI, double cons) throws Exception{
		
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		
		assert	m instanceof BatteryModel ;
		((BatteryModel)m).setHeaterConsumption(cons);
	}

	public void setOvenConsumption(String modelURI, double cons) throws Exception{
		
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		
		assert	m instanceof BatteryModel ;
		((BatteryModel)m).setOvenConsumption(cons);
	}
	
	public void setSPPolicy(String modelURI, double policy) throws Exception{
		
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		
		assert	m instanceof SolarPanelModel ;
		((SolarPanelModel)m).setStoragePolicy(policy);
	}
}
