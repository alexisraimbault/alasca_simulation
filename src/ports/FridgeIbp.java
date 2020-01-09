package ports;

import Simulation.fridge.FridgeModel.Mode;
import Simulation.fridge.FridgeModel.State;
import components.Fridge;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.FridgeI;

public class FridgeIbp extends AbstractInboundPort implements FridgeI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				FridgeIbp(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, FridgeI.class, owner);
	}

	public				FridgeIbp(ComponentI owner)
	throws Exception
	{
		super(FridgeI.class, owner);
	}

	@Override
	public double getTemperature() throws Exception {
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Double>() {
					@Override
					public Double call() throws Exception {
						return ((Fridge)this.getServiceOwner()).getTemperature() ;
					}
				}) ;
	}

	@Override
	public Mode getState() throws Exception {
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Mode>() {
					@Override
					public Mode call() throws Exception {
						return ((Fridge)this.getServiceOwner()).getMode() ;
					}
				}) ;
	}

	@Override
	public void switchOn() throws Exception {
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).switchOn() ;
						return null;
					}
				}) ;
	}

	@Override
	public void switchOff() throws Exception {
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).switchOff() ;
						return null;
					}
				}) ;
	}

	@Override
	public void freeze() throws Exception {
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).freeze() ;
						return null;
					}
				}) ;
	}

	@Override
	public void rest() throws Exception {
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).rest() ;
						return null;
					}
				}) ;
	}

	@Override
	public void setHouseTemp(double temp) throws Exception {
			this.owner.handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((Fridge)this.getServiceOwner()).setHouseTemp(temp) ;
							return null;
						}
					}) ;
	}

	@Override
	public void setLowBattery(boolean isLow) throws Exception {
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).setLowBattery(isLow) ;
						return null;
					}
				}) ;
	}

}
