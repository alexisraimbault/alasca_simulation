package ports;

import Simulation.oven.OvenModel.Mode;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ControllerOvenI;

public class ControllerOvenObp extends AbstractOutboundPort implements ControllerOvenI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControllerOvenObp(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, ControllerOvenI.class, owner);
		}

		public ControllerOvenObp(ComponentI owner)
		throws Exception
		{
			super(ControllerOvenI.class, owner);
		}

	@Override
	public Mode getOvenMode() throws Exception {
		return ((ControllerOvenI)this.connector).getOvenMode() ;
	}

	@Override
	public void setOvenMode(Mode mode) throws Exception {
		((ControllerOvenI)this.connector).setOvenMode(mode) ;
	}
}
