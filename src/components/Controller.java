package components;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Simulation.fridge.FridgeModel;
import Simulation.fridge.FridgeModel.State;
import Simulation.heater.HeaterModel;
import Simulation.oven.OvenModel.Mode;
import classes.PlanifiedTask;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.ControllerFridgeI;
import interfaces.ControllerOvenI;
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
import ports.ControllerOvenObp;
import ports.LaunchableIbp;

@OfferedInterfaces(offered = {LaunchableOfferedI.class})
@RequiredInterfaces(required = {ControllerFridgeI.class, ControllerOvenI.class, ControllerHeaterI.class, ControllerBatteryI.class, ControllerOndulatorI.class, ControllerEPI.class})
public class Controller extends AbstractComponent implements LaunchableOfferedI{
	
	private ControllerFridgeObp towardsFridge;
	private ControllerOvenObp towardsOven;
	private ControllerHeaterObp towardsHeater;
	private ControllerBatteryObp towardsBattery;
	private ControllerOndulatorObp towardsOndulator;
	private ControllerEPObp towardsEP;
	private LaunchableIbp launchIbp;
	private ArrayList<PlanifiedTask> tasks;

	protected Controller(String controllerURI, String obpURI, String obpURI2, String obpURI3,  String obpURI4,  String obpURI5,  String obpURI6, String launchUri) throws Exception {
		super(controllerURI,  1, 1) ;
		
		this.launchIbp = new LaunchableIbp(launchUri, this) ;
		this.launchIbp.publishPort() ;
		
		this.towardsFridge = new ControllerFridgeObp(obpURI, this) ;
		this.towardsFridge.localPublishPort() ;
		
		this.towardsOven = new ControllerOvenObp(obpURI6, this) ;
		this.towardsOven.localPublishPort() ;
		
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
	public FridgeModel.Mode getFridgeState() throws Exception
	{
		FridgeModel.Mode mode = this.towardsFridge.getFridgeState();
		//this.logMessage("fridge state : " + state);
		return mode;
	}
	
	public Mode getOvenMode() throws Exception
	{
		Mode mode = this.towardsOven.getOvenMode();
		//this.logMessage("fridge state : " + state);
		return mode;
	}
	
	public double getFridgeTemperature() throws Exception {
		double temp = this.towardsFridge.getFridgeTemperature();
		//this.logMessage("fridge temperature : " + temp);
		return temp;
	}
	
	public double getHeaterTemperature() throws Exception{
		return this.towardsHeater.getHeaterTemperature();
	}
	
	public HeaterModel.Mode getHeaterMode() throws Exception{
		return this.towardsHeater.getHeaterMode();
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
		this.controllOven();
		this.controllHeater();
		this.controlSPController();
	}
	
	public void controllFridge() throws Exception
	{
		FridgeModel.Mode s = getFridgeState();
		this.logMessage("fridge info...");
		this.logMessage("fridge temperature : " + getFridgeTemperature());
		this.logMessage("fridge state : " + s);
		
		if (s == FridgeModel.Mode.OFF) {
			this.towardsBattery.setFridgeCons(0) ;
		} else if (s == FridgeModel.Mode.REST) {
			this.towardsBattery.setFridgeCons(2) ;
		} else {
			assert	s == FridgeModel.Mode.FREEZE;
			this.towardsBattery.setFridgeCons(3) ;
		}
	}
	
	public void controllOven() throws Exception
	{
		Mode s = getOvenMode();
		this.logMessage("oven info...");
		this.logMessage("oven mode : " + s);
		
		if (s == Mode.OFF) {
			this.towardsBattery.setOvenCons(0) ;
		} else if (s == Mode.LOW) {
			this.towardsBattery.setOvenCons(2) ;
		} else {
			assert s == Mode.HIGH;
			this.towardsBattery.setOvenCons(3) ;
		}
	}
	
	public void controllHeater() throws Exception
	{
		double houseTemp = getHeaterTemperature();
		HeaterModel.Mode s = getHeaterMode();
		this.logMessage("heater info...");
		this.logMessage("heater temperature : " + houseTemp);
		this.logMessage("heater state : " + s);
		
		this.towardsFridge.setHouseTemp(houseTemp);
		
		if (s == HeaterModel.Mode.OFF) {
			this.towardsBattery.setHeaterCons(0) ;
		} else if (s == HeaterModel.Mode.LOW) {
			this.towardsBattery.setHeaterCons(3) ;
		} else {
			assert	s == HeaterModel.Mode.HIGH;
			this.towardsBattery.setHeaterCons(4) ;
		}
		
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

