package classes;

public class PlanifiedTask {
	
	public PlanifiedTask(int value, int iterations, Operations operation, int priority) {
		super();
		this.value = value;
		this.iterations = iterations;
		this.operation = operation;
		this.priority = priority;
	}
	public int priority;//0<1<2<3<4<5<6<...
	public int value;//duration, aimed temp, ...
	public int iterations;
	public Operations operation;
}
