import java.util.Scanner;

public class SimSimulator extends Simulator {

	private CustomerCreator customerCreator;
	
	public SimSimulator() {
		
	}
	
	protected void initialize() {
		
		Scanner in = new Scanner(System.in);
		System.out.println("Minimum inter-arrival time : ");
		int minInterArrival = in.nextInt();
		System.out.println("Maximum inter-arrival time : ");
		int maxInterArrival = in.nextInt();
		System.out.println("Minimum service time : ");
		int minService = in.nextInt();
		System.out.println("Maximum service time : ");
		int maxService = in.nextInt();
		System.out.println("Number of customers : ");
		this.numCustomers = in.nextInt();
		System.out.println("Number of full lanes : ");
		this.numFullLanes = in.nextInt();
		System.out.println("Number of self lanes : ");
		this.numSelfLanes = in.nextInt();
		System.out.println("Percent slower for self checkout : ");
		this.selfPercentSlower = in.nextInt();
		
		this.customerCreator = new CustomerCreator(minInterArrival, maxInterArrival, minService, maxService);
		fullLanes = new boolean[numFullLanes];
		selfLanes = new boolean[numSelfLanes];
		createCustomers();
		
	}
	
	private void createCustomers() {
		
		int lastArrival = 0;
		
		for (int i = 0; i < numCustomers; i++) {
			Customer customer = customerCreator.nextCustomer(lastArrival);
			customers.push(customer);
			lastArrival = customer.getArrivalTime();
		}
		
	}
	
}
