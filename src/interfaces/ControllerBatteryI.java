package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ControllerBatteryI extends RequiredI{
	public double getBatteryEnergy() throws Exception;
	public void setFridgeCons(double cons) throws Exception;
	
	public void setHeaterCons(double cons) throws Exception;
	
	public void setOvenCons(double cons) throws Exception;
	
	public void setSPPolicy(double policy) throws Exception;
	public double getStorageCapacity() throws Exception;
}
