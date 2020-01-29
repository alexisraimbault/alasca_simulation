package ports;

import Simulation.heater.HeaterModel.Mode;
import components.Heater;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.HeaterI;

public class HeaterIbp extends AbstractInboundPort implements HeaterI {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HeaterIbp(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, HeaterI.class, owner);
	}

	public HeaterIbp(ComponentI owner) throws Exception
	{
		super(HeaterI.class, owner);
	}

	@Override
	public double getTemperature() throws Exception {
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Double>() {
					@Override
					public Double call() throws Exception {
						return ((Heater)this.getServiceOwner()).getTemperature();
					}
				}) ;
	}

	@Override
	public Mode getMode() throws Exception {
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Mode>() {
					@Override
					public Mode call() throws Exception {
						return ((Heater)this.getServiceOwner()).getMode();
					}
				}) ;
	}

	@Override
	public void setLowBattery(boolean isLow) throws Exception {
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Heater)this.getServiceOwner()).setLowBattery(isLow);
						return null;
					}
				}) ;
	}

	@Override
	public void setAimedTemp(double temp) throws Exception {
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Heater)this.getServiceOwner()).setAimedTemp(temp);
						return null;
					}
				}) ;
	}

}
