package components;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Simulation.FridgeModel.State;
import classes.PlanifiedTask;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.ControllerFridgeI;
import interfaces.ControllerHeaterI;
import interfaces.ControllerBatteryI;
import interfaces.ControllerOndulatorI;
import interfaces.LaunchableOfferedI;
import interfaces.ControllerEPI;
import ports.ControllerBatteryObp;
import ports.ControllerEPObp;
import ports.ControllerFridgeObp;
import ports.ControllerHeaterObp;
import ports.ControllerOndulatorObp;
import ports.LaunchableIbp;

@OfferedInterfaces(offered = {LaunchableOfferedI.class})
@RequiredInterfaces(required = {ControllerFridgeI.class, ControllerHeaterI.class, ControllerBatteryI.class, ControllerOndulatorI.class, ControllerEPI.class})
public class Controller extends AbstractComponent implements LaunchableOfferedI{
	
	private ControllerFridgeObp towardsFridge;
	private ControllerHeaterObp towardsHeater;
	private ControllerBatteryObp towardsBattery;
	private ControllerOndulatorObp towardsOndulator;
	private ControllerEPObp towardsEP;
	private LaunchableIbp launchIbp;
	private ArrayList<PlanifiedTask> tasks;

	protected Controller(String controllerURI, String obpURI, String obpURI2, String obpURI3,  String obpURI4,  String obpURI5, String launchUri) throws Exception {
		super(controllerURI,  1, 1) ;
		
		this.launchIbp = new LaunchableIbp(launchUri, this) ;
		this.launchIbp.publishPort() ;
		
		this.towardsFridge = new ControllerFridgeObp(obpURI, this) ;
		this.towardsFridge.localPublishPort() ;
		
		this.towardsHeater = new ControllerHeaterObp(obpURI4, this) ;
		this.towardsHeater.localPublishPort() ;
		
		this.towardsBattery = new ControllerBatteryObp(obpURI2, this) ;
		this.towardsBattery.localPublishPort() ;
		
		this.towardsOndulator = new ControllerOndulatorObp(obpURI3, this) ;
		this.towardsOndulator.localPublishPort() ;
		
		this.towardsEP = new ControllerEPObp(obpURI5, this) ;
		this.towardsEP.localPublishPort() ;
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.tasks = new ArrayList<PlanifiedTask>();
		
		this.tracer.setTitle("controller") ;
		this.tracer.setRelativePosition(1, 0) ;

	}
	public State getFridgeState() throws Exception
	{
		State state = this.towardsFridge.getFridgeState();
		//this.logMessage("fridge state : " + state);
		return state;
	}
	
	public double getFridgeTemperature() throws Exception {
		double temp = this.towardsFridge.getFridgeTemperature();
		//this.logMessage("fridge temperature : " + temp);
		return temp;
	}
	
	public double getBatteryEnergy() throws Exception {
		double temp = this.towardsBattery.getBatteryEnergy();
		this.logMessage("battery energy : " + temp);
		return temp;
	}

	public void switchFridgeOn() throws Exception {
		this.logMessage("switching fridge on...");
		this.towardsFridge.switchFridgeOn();
	}

	public void switchFridgeOff() throws Exception {
		this.logMessage("switching fridge off...");
		this.towardsFridge.switchFridgeOff();
	}
	
	public void freezeFridge() throws Exception {
		this.logMessage("freezing fridge ...");
		this.towardsFridge.freezeFridge();
	}
	
	public void restFridge() throws Exception {
		this.logMessage("resting fridge ...");
		this.towardsFridge.restFridge();
	}
	
	public void switchHeaterOn() throws Exception {
		this.logMessage("switching heater on...");
		this.towardsHeater.switchHeaterOn();
	}

	public void switchHeaterOff() throws Exception {
		this.logMessage("switching heater off...");
		this.towardsHeater.switchHeaterOff();
	}
	
	public void setOndulatorPolicy(String policy) throws Exception {
		this.logMessage("setting SP controller policy to : " + policy + "...");
		this.towardsOndulator.setOndulatorPolicy(policy);
	}
	
	public void getEPConsommation() throws Exception{
		double cons = this.towardsEP.getTotalConsommation();
		this.logMessage("total electric panel consommation : " + cons);
	}
	
	public void step() throws Exception
	{
		this.logMessage("STEPPING...");
		this.getEPConsommation();
		this.updateTasks();
		this.controllFridge();
		this.controllHeater();
		this.controlSPController();
	}
	
	public void controllFridge() throws Exception
	{
		this.logMessage("fridge info...");
		this.logMessage("fridge temperature : " + getFridgeTemperature());
		this.logMessage("fridge state : " + getFridgeState());
	}
	
	public void controllHeater() throws Exception
	{
		//TODO
	}
	
	public void controlSPController() throws Exception
	{
		//TODO
	}
	
	public void updateTasks() 
	{
		for(PlanifiedTask t : tasks)
		{
			if(t.iterations-- <= 0 )
			{
				//TODO
			}
			
		}
	}
	
	public void addPlanifiedTask(PlanifiedTask task)
	{
		this.tasks.add(task);
	}
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("starting controller component.") ;
		
		// Schedule the first service method invocation in one second.
		/*this.scheduleTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((Controller)this.getTaskOwner()).switchFridgeOn();
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			},
			1000, TimeUnit.MILLISECONDS);*/
		
		/*this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Controller)this.getTaskOwner()).switchHeaterOn();
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				},
				1000, TimeUnit.MILLISECONDS);*/
		
		/*this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Controller)this.getTaskOwner()).setOndulatorPolicy("default");
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				},
				1000, TimeUnit.MILLISECONDS);*/
		
		/*this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Controller)this.getTaskOwner()).getFridgeTemperature();
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				},
				2000, TimeUnit.MILLISECONDS);*/
		
		/*this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Controller)this.getTaskOwner()).getBatteryEnergy();
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				},
				3000, TimeUnit.MILLISECONDS);*/
		
		this.scheduleTaskWithFixedDelay(		
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Controller)this.getTaskOwner()).step();
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}, 1000, 1000 // délai entre la fin d'une exécution et la suivante, à modifier 
				,TimeUnit.MILLISECONDS) ;
		
		
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping controller component.") ;
		// This is the place where to clean up resources, such as
		// disconnecting and unpublishing ports that will be destroyed
		// when shutting down.
		// In static architectures like in this example, ports can also
		// be disconnected by the finalise method of the component
		// virtual machine.
		this.towardsFridge.unpublishPort() ;
		this.towardsBattery.unpublishPort() ;
		this.towardsOndulator.unpublishPort() ;
		this.towardsHeater.unpublishPort() ;
		this.towardsEP.unpublishPort() ;
		// This called at the end to make the component internal
		// state move to the finalised state.
		super.finalise();
	}
	
	@Override
	public void launchTasks() throws Exception {
		// Schedule the first service method invocation in one second.
				this.scheduleTask(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((Controller)this.getTaskOwner()).switchFridgeOn();
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					1000, TimeUnit.MILLISECONDS);
				
				this.scheduleTask(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((Controller)this.getTaskOwner()).switchHeaterOn();
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						},
						1000, TimeUnit.MILLISECONDS);
				
				this.scheduleTask(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((Controller)this.getTaskOwner()).setOndulatorPolicy("default");
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						},
						1000, TimeUnit.MILLISECONDS);
				
				this.scheduleTask(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((Controller)this.getTaskOwner()).getFridgeTemperature();
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						},
						2000, TimeUnit.MILLISECONDS);
				
				this.scheduleTask(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((Controller)this.getTaskOwner()).getBatteryEnergy();
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						},
						3000, TimeUnit.MILLISECONDS);
				
				this.scheduleTaskWithFixedDelay(		
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((Controller)this.getTaskOwner()).controllFridge();
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						}, 4000, 1000 // délai entre la fin d'une exécution et la suivante, à modifier 
						,TimeUnit.MILLISECONDS) ;
				
				this.scheduleTaskWithFixedDelay(		
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((Controller)this.getTaskOwner()).getEPConsommation();
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						}, 1000, 4000 // délai entre la fin d'une exécution et la suivante, à modifier 
						,TimeUnit.MILLISECONDS) ;
	}
}
//-----------------------------------------------------------------------------

