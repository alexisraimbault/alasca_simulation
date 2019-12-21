package components;


import java.util.concurrent.TimeUnit;

import Simulation.FridgeModel.State;
import Simulation.FridgeTest;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import interfaces.FridgeI;
import interfaces.LaunchableOfferedI;
import interfaces.ComponentEPI;
import ports.ComponentEPObp;
import ports.FridgeIbp;
import ports.LaunchableIbp;

@OfferedInterfaces(offered = {FridgeI.class, LaunchableOfferedI.class})
@RequiredInterfaces(required = {ComponentEPI.class})
public class Fridge extends AbstractComponent implements LaunchableOfferedI {
	private FridgeIbp ibp;
	private FridgeTest ft;
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
	
	
	protected Fridge(String fridgeURI, String ibpURI, String epURI, String launchUri) throws Exception {
		super(fridgeURI,  1, 1) ;

		this.ft = new FridgeTest();
		
		this.launchIbp = new LaunchableIbp(launchUri, this) ;
		this.launchIbp.publishPort() ;
		
		//toléré de 0 - 7
		//préférence 4 - 5
		
		this.ibp = new FridgeIbp(ibpURI, this) ;
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
					getImplementedInterface().equals(FridgeI.class) :
					new PostconditionException("The component must have a "
							+ "port with implemented interface URIProviderI") ;
		assert	this.findPortFromURI(ibpURI).isPublished() :
					new PostconditionException("The component must have a "
							+ "port published with URI " + ibpURI) ;
		
		this.tracer.setTitle("fridge") ;
		this.tracer.setRelativePosition(3, 0) ;
		
	}

	public double getTemperature() throws Exception {
		return ft.getTemperature();
	}
	
	public State getState() throws Exception {
		return ft.getState();
	}
	
	public void switchOn() throws Exception {
			
	}

	public void switchOff() {
			
	}

	public void freeze() throws Exception {
		
	}

	public void rest() throws Exception {
		
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
		this.logMessage("starting fridge component.") ;
		
		// Schedule the first service method invocation in one second.

		

	}



	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping fridge component.") ;
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
//-----------------------------------------------------------------------------


