package components;

import Simulation.oven.OvenSimulationComponent;
import Simulation.heater.HeaterSimulationComponent;
import Simulation.oven.OvenModel.Mode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import interfaces.ComponentEPI;
import interfaces.LaunchableOfferedI;
import interfaces.OvenI;
import ports.ComponentEPObp;
import ports.OvenIbp;
import ports.LaunchableIbp;

@OfferedInterfaces(offered = {OvenI.class, LaunchableOfferedI.class})
@RequiredInterfaces(required = {ComponentEPI.class})

public class Oven extends AbstractComponent implements LaunchableOfferedI {

	private OvenIbp ibp;
	private OvenSimulationComponent ft;
	private LaunchableIbp launchIbp;

	/*
	 * plaque temperature
	 * 
	 * on -> 3
	 * off -> ambiante
	 * freeze -> -3
	 * 
	 * t(n+1) = t(n) + (tPlaque - t(n) / 13)
	 * 
	 */
	
	private ComponentEPObp epObp;
	
	
	protected Oven(String ovenURI, String ibpURI, String epURI, String launchUri) throws Exception {
		super(ovenURI,  1, 1) ;

		this.ft = new OvenSimulationComponent();
		
		this.launchIbp = new LaunchableIbp(launchUri, this) ;
		this.launchIbp.publishPort() ;
		
		//toléré de 0 - 7
		//préférence 4 - 5
		
		this.ibp = new OvenIbp(ibpURI, this) ;
		this.ibp.publishPort() ;
		
		this.epObp = new ComponentEPObp(epURI, this);
		this.epObp.publishPort() ;
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		assert	this.isPortExisting(ibpURI) :
			new PostconditionException("The component must have a "
					+ "port with URI " + ibpURI) ;
		assert	this.findPortFromURI(ibpURI).
					getImplementedInterface().equals(OvenI.class) :
					new PostconditionException("The component must have a "
							+ "port with implemented interface URIProviderI") ;
		assert	this.findPortFromURI(ibpURI).isPublished() :
					new PostconditionException("The component must have a "
							+ "port published with URI " + ibpURI) ;
		
		this.tracer.setTitle("Oven") ;
		this.tracer.setRelativePosition(3, 2) ;
		
	}
	
	
	public Mode getMode() throws Exception {
		return ft.getMode();
	}

	
	public void step() throws Exception
	{
		
	}
	

	
	public void EPRegister() throws Exception {
		
	}
	
	public void EPDeclare() throws Exception {
		
	}
	
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("starting Oven component.") ;
		
		// Schedule the first service method invocation in one second.

		

	}



	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping Oven component.") ;
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


}
