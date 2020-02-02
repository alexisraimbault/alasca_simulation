package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.BatteryOfferedI;
import interfaces.ControllerBatteryI;

public class ControllerBatteryConnector extends AbstractConnector implements ControllerBatteryI
{
	@Override
	public double getBatteryEnergy() throws Exception {
		return ((BatteryOfferedI)this.offering).getEnergy() ;
	}

	@Override
	public void setFridgeCons(double cons) throws Exception {
		((BatteryOfferedI)this.offering).setFridgeCons(cons) ;
	}

	@Override
	public void setHeaterCons(double cons) throws Exception {
		((BatteryOfferedI)this.offering).setHeaterCons(cons) ;
	}

	@Override
	public void setOvenCons(double cons) throws Exception {
		((BatteryOfferedI)this.offering).setOvenCons(cons) ;
	}

	@Override
	public void setSPPolicy(double policy) throws Exception {
		((BatteryOfferedI)this.offering).setSPPolicy(policy) ;
	}

	@Override
	public double getStorageCapacity() throws Exception {
		return ((BatteryOfferedI)this.offering).getStorageCapacity() ;
	}
}
