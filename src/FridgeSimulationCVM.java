import Simulation.fridge.FridgeSimulationComponent;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;


public class			FridgeSimulationCVM
extends		AbstractCVM
{
	public				FridgeSimulationCVM() throws Exception
	{
		super() ;
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		@SuppressWarnings("unused")
		String componentURI =
				AbstractComponent.createComponent(
						FridgeSimulationComponent.class.getCanonicalName(),
						new Object[]{}) ;

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			FridgeSimulationCVM c = new FridgeSimulationCVM() ;
			c.startStandardLifeCycle(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
//------------------------------------------------------------------------------
