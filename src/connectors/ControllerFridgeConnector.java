package connectors;

import Simulation.FridgeModel.State;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ControllerFridgeI;
import interfaces.FridgeI;

public class ControllerFridgeConnector extends AbstractConnector implements ControllerFridgeI{

	@Override
	public State getFridgeState() throws Exception {
		return ((FridgeI)this.offering).getState() ;
	}

	@Override
	public void freezeFridge() throws Exception {
		((FridgeI)this.offering).freeze() ;
	}

	@Override
	public void restFridge() throws Exception {
		((FridgeI)this.offering).rest() ;
	}

	@Override
	public double getFridgeTemperature() throws Exception {
		System.out.println("TEST GET FRIDGE TEMP CONNECTOR");
		return ((FridgeI)this.offering).getTemperature() ;
	}

	@Override
	public void switchFridgeOn() throws Exception {
		((FridgeI)this.offering).switchOn() ;
	}

	@Override
	public void switchFridgeOff() throws Exception {
		((FridgeI)this.offering).switchOff() ;
	}

}
