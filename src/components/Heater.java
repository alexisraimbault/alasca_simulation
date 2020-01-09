package components;

import java.util.concurrent.TimeUnit;

import Simulation.heater.HeaterModel.Mode;
import Simulation.heater.HeaterModel;
import Simulation.heater.HeaterSimulationComponent;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.ComponentEPI;
import interfaces.HeaterI;
import interfaces.LaunchableOfferedI;
import ports.ComponentEPObp;
import ports.HeaterIbp;
import ports.LaunchableIbp;

@OfferedInterfaces(offered = {HeaterI.class, LaunchableOfferedI.class})
@RequiredInterfaces(required = {ComponentEPI.class})
public class Heater extends AbstractComponent implements LaunchableOfferedI {
	
	private int power;
	private String state;
	private HeaterIbp ibp;
	private ComponentEPObp epObp;
	private LaunchableIbp launchIbp;
	private HeaterSimulationComponent ht;
	
	protected Heater(String heaterURI, String ibpURI, String epURI, String launchUri) throws Exception {
		super(heaterURI,  1, 1) ;
		
		this.ht = new HeaterSimulationComponent();

		this.launchIbp = new LaunchableIbp(launchUri, this) ;
		this.launchIbp.publishPort() ;
		
		this.ibp = new HeaterIbp(ibpURI, this) ;
		this.ibp.publishPort() ;
		
		this.epObp = new ComponentEPObp(epURI, this);
		this.epObp.publishPort() ;
		
		// remove ?
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.tracer.setTitle("heater") ;
		this.tracer.setRelativePosition(3, 1) ;
		
		this.power = 0;
		this.state = "off";
	}
	
	public int getPower() throws Exception {
		return 0;
	}

	public Mode getMode() throws Exception {
		return ht.getMode();
	}
	
	public double getTemperature() throws Exception {
		return ht.getTemperature();
	}


	public void switchOn() throws Exception {
		/*this.logMessage("switching on.") ;
		this.state = "on";
		EPDeclare();*/
	}

	public void switchOff() throws Exception {
		/*this.logMessage("switching off.") ;
		this.state = "off";
		EPDeclare();*/
	}

	public void setPower(int power) throws Exception {
		
	}
	
	public void EPRegister() throws Exception {
		
	}
	
	public void EPDeclare() throws Exception {
		
	}
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("starting heater component.") ;
		
		
	}



	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping heater component.") ;
		// This is the place where to clean up resources, such as
		// disconnecting and unpublishing ports that will be destroyed
		// when shutting down.
		// In static architectures like in this example, ports can also
		// be disconnected by the finalise method of the component
		// virtual machine.
		this.ibp.unpublishPort() ;
		this.epObp.unpublishPort() ;
		// This called at the end to make the component internal
		// state move to the finalised state.  15 + on 14
		super.finalise();
	}

	@Override
	public void launchTasks() throws Exception {
		
	}
	
	public void setLowBattery(boolean isLow) throws Exception 
	{
		ht.setLowBattery(isLow);
	}
	
}
