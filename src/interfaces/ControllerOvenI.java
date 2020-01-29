package interfaces;

import Simulation.oven.OvenModel.Mode;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ControllerOvenI extends RequiredI{
	public Mode getOvenMode() throws Exception;
	public void setOvenMode(Mode mode) throws Exception;
}
