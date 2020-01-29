package ports;

import Simulation.heater.HeaterModel.Mode;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ControllerFridgeI;
import interfaces.ControllerHeaterI;

public class ControllerHeaterObp extends AbstractOutboundPort implements ControllerHeaterI{
	
	public ControllerHeaterObp(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, ControllerHeaterI.class, owner);
		}

		public ControllerHeaterObp(ComponentI owner)
		throws Exception
		{
			super(ControllerHeaterI.class, owner);
		}

		@Override
		public double getHeaterTemperature() throws Exception {
			return ((ControllerHeaterI)this.connector).getHeaterTemperature() ;
		}

		@Override
		public Mode getHeaterMode() throws Exception {
			return ((ControllerHeaterI)this.connector).getHeaterMode() ;
		}

		@Override
		public void setLowBattery(boolean isLow) throws Exception {
			((ControllerHeaterI)this.connector).setLowBattery(isLow) ;
		}

		@Override
		public void setHeaterAimedTemp(double temp) throws Exception {
			((ControllerHeaterI)this.connector).setHeaterAimedTemp(temp) ;
		}
}
