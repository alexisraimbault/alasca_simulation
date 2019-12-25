package interfaces;

import Simulation.oven.OvenModel.Mode;
import fr.sorbonne_u.components.interfaces.OfferedI;

public interface OvenI extends OfferedI {
	public Mode getMode() throws Exception;
}
