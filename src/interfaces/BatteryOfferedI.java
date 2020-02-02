package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface BatteryOfferedI extends OfferedI{
	public void store(double power)throws Exception;
	double getEnergy() throws Exception;
	public void setFridgeCons(double cons) throws Exception;
	
	public void setHeaterCons(double cons) throws Exception;
	
	public void setOvenCons(double cons) throws Exception;
	
	public void setSPPolicy(double policy) throws Exception;
	
	public double getStorageCapacity() throws Exception;
}