package interfaces;

import Simulation.heater.HeaterModel.Mode;
import fr.sorbonne_u.components.interfaces.OfferedI;

public interface HeaterI extends OfferedI {
	
	public double getTemperature() throws Exception;
	public Mode getMode() throws Exception;
}
