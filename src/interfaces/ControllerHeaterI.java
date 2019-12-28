package interfaces;

import Simulation.heater.HeaterModel.Mode;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ControllerHeaterI extends RequiredI{
	public double getHeaterTemperature() throws Exception;
	public Mode getHeaterMode() throws Exception;
}
