package connectors;

import Simulation.oven.OvenModel.Mode;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ControllerOvenI;
import interfaces.OvenI;

public class ControllerOvenConnector  extends AbstractConnector implements ControllerOvenI{

	@Override
	public Mode getOvenMode() throws Exception {
		System.out.println("TEST GET OVEN MODE CONNECTOR");
		return ((OvenI)this.offering).getMode() ;
	}
}
