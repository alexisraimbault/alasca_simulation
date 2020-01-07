package interfaces;

import Simulation.fridge.FridgeModel.Mode;
import Simulation.fridge.FridgeModel.State;
import fr.sorbonne_u.components.interfaces.OfferedI;

public interface FridgeI extends OfferedI {
	public double getTemperature() throws Exception;
	public Mode getState() throws Exception;
	public void setHouseTemp(double temp) throws Exception;
	public void switchOn() throws Exception;
	public void switchOff() throws Exception;
	public void freeze() throws Exception;
	public void rest() throws Exception;
}
