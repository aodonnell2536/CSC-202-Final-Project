import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class SimSimulator extends Simulator {

	private CustomerCreator customerCreator;
	
	public SimSimulator() {
		initialize();
	}
	
	protected void initialize() {

		time = 0;
		currentID = 1;
		customersHelped = 0;
		customersDissatisfied = 0;
		customersSatisfied = 0;
		averageWaitTime = 0;
		totalUnusedTime = 0;
		totalUnusedTimeFull = 0;
		totalUnusedTimeSelf = 0;
		minUnusedFull = 0;
		minUnusedSelf = 0;
		minUnusedTotal = 0;
		customers =  new Queue<Customer>();
		fullLaneQueues = new ArrayList<Queue<Customer>>();
		selfLaneQueue = new Queue<Customer>();
		customersCompleted = new TreeSet<Customer>();
		
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
		
		fullLaneUnusedTime = new int[numFullLanes];
		selfLaneUnusedTime = new int[numSelfLanes];
		numCustFull = new int[numFullLanes];
		numCustSelf = new int[numSelfLanes];
		selfLanes = new Customer[numSelfLanes];
		for (int i = 0; i < numFullLanes; i++)
			fullLaneQueues.add(new Queue<Customer>());
		
		this.customerCreator = new CustomerCreator(minInterArrival, maxInterArrival, minService, maxService);
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
