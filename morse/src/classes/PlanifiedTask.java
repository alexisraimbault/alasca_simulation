package classes;

public class PlanifiedTask {
	
	public PlanifiedTask(Equipments equipment, int iterations, Operations operation, int priority) {
		super();
		this.equipment = equipment;
		this.iterations = iterations;
		this.operation = operation;
		this.priority = priority;
	}
	public int priority;//0<1<2<3<4<5<6<...
	public Equipments equipment;
	public int iterations;
	public Operations operation;
	
	
}
