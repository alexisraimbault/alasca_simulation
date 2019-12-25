package ports;

import Simulation.oven.OvenModel.Mode;
import components.Oven;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.OvenI;

public class OvenIbp  extends AbstractInboundPort implements OvenI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				OvenIbp(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, OvenI.class, owner);
	}

	public				OvenIbp(ComponentI owner)
	throws Exception
	{
		super(OvenI.class, owner);
	}

	@Override
	public Mode getMode() throws Exception {
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Mode>() {
					@Override
					public Mode call() throws Exception {
						return ((Oven)this.getServiceOwner()).getMode() ;
					}
				}) ;
	}
}
