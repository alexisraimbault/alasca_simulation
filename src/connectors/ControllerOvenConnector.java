package connectors;

import Simulation.oven.OvenModel.Mode;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ControllerOvenI;
import interfaces.OvenI;

public class ControllerOvenConnector  extends AbstractConnector implements ControllerOvenI{

	@Override
	public Mode getOvenMode() throws Exception {
		return ((OvenI)this.offering).getMode() ;
	}

	@Override
	public void setOvenMode(Mode mode) throws Exception {
		((OvenI)this.offering).setMode(mode) ;
	}
}
