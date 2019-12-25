
import Simulation.oven.OvenSimulationComponent;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class OvenSimulationCVM extends		AbstractCVM
{
	public				OvenSimulationCVM() throws Exception
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
						OvenSimulationComponent.class.getCanonicalName(),
						new Object[]{}) ;

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			OvenSimulationCVM c = new OvenSimulationCVM() ;
			c.startStandardLifeCycle(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
