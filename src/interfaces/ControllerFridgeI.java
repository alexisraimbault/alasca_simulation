package interfaces;

import Simulation.fridge.FridgeModel.State;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ControllerFridgeI extends RequiredI{
	public double getFridgeTemperature() throws Exception;
	public void switchFridgeOn() throws Exception;
	public void switchFridgeOff() throws Exception;
	State getFridgeState() throws Exception;
	void freezeFridge() throws Exception;
	void restFridge() throws Exception;
}
