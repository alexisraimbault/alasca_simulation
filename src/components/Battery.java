package components;

import Simulation.energy.EnergySimulationComponent;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.BatteryOfferedI;
import interfaces.BatteryRequiredI;
import interfaces.LaunchableOfferedI;
import ports.BatteryIbp;
import ports.LaunchableIbp;

@OfferedInterfaces(offered = {BatteryOfferedI.class, LaunchableOfferedI.class})
@RequiredInterfaces(required = {BatteryRequiredI.class})
public class Battery extends AbstractComponent implements LaunchableOfferedI {
	
	private BatteryIbp ibp;
	private LaunchableIbp launchIbp;
	private EnergySimulationComponent ft;
	
	protected Battery(String batteryURI, String ibpURI, String launchUri) throws Exception {
		super(batteryURI,  1, 1) ;

		this.launchIbp = new LaunchableIbp(launchUri, this) ;
		this.launchIbp.publishPort() ;
		
		this.ibp = new BatteryIbp(ibpURI, this) ;
		this.ibp.publishPort() ;
		
		
		this.ft = new EnergySimulationComponent();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.tracer.setTitle("Battery") ;
		this.tracer.setRelativePosition(1, 2) ;
		
	}

	public void store(double power) {
		
	}

	public Double getEnergy() throws Exception {
		return ft.getBattery();
	}
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("starting Battery component.") ;
		
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping Battery component.") ;
		// This is the place where to clean up resources, such as
		// disconnecting and unpublishing ports that will be destroyed
		// when shutting down.
		// In static architectures like in this example, ports can also
		// be disconnected by the finalise method of the component
		// virtual machine.
		this.ibp.unpublishPort() ;

		// This called at the end to make the component internal
		// state move to the finalised state.
		super.finalise();
	}

	@Override
	public void launchTasks() throws Exception {
		// nothing
		
	}
	
	public void setFridgeCons(double cons) throws Exception{
		ft.setFridgeCons(cons);
	}
	
	public void setHeaterCons(double cons) throws Exception{
		ft.setHeaterCons(cons);
	}
	
	public void setOvenCons(double cons) throws Exception{
		ft.setOvenCons(cons);
	}
	
	public void setSPPolicy(double policy) throws Exception{
		ft.setSPPolicy(policy);
	}
	
	public double getStorageCapacity() throws Exception{
		return ft.getStorageCapacity();
	}
}
