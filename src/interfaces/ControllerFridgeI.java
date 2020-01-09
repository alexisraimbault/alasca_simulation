package interfaces;

import Simulation.fridge.FridgeModel.Mode;
import Simulation.fridge.FridgeModel.State;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ControllerFridgeI extends RequiredI{
	public double getFridgeTemperature() throws Exception;
	public void switchFridgeOn() throws Exception;
	public void switchFridgeOff() throws Exception;
	Mode getFridgeState() throws Exception;
	public void setHouseTemp(double temp) throws Exception;
	public void setLowBattery(boolean isLow) throws Exception ;
	void freezeFridge() throws Exception;
	void restFridge() throws Exception;
}
