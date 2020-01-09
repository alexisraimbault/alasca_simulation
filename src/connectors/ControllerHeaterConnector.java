package connectors;

import Simulation.heater.HeaterModel.Mode;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ControllerHeaterI;
import interfaces.HeaterI;

public class ControllerHeaterConnector extends AbstractConnector implements ControllerHeaterI{

	@Override
	public double getHeaterTemperature() throws Exception {
		return ((HeaterI)this.offering).getTemperature() ;
	}

	@Override
	public Mode getHeaterMode() throws Exception {
		return ((HeaterI)this.offering).getMode() ;
	}

	@Override
	public void setLowBattery(boolean isLow) throws Exception {
		((HeaterI)this.offering).setLowBattery(isLow) ;
	}

}
