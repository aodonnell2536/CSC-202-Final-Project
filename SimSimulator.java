import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class SimSimulator extends Simulator {

	private CustomerCreator customerCreator;
	private Scanner in;
	
	public SimSimulator() {
		initialize();
	}
	
	protected void initialize() {

		time = 0;
		currentID = 1;
		customersDissatisfied = 0;
		customersSatisfied = 0;
		averageWaitTime = 0;
		totalUnusedTime = 0;
		totalUnusedTimeFull = 0;
		totalUnusedTimeSelf = 0;
		minUnusedFull = 0;
		minUnusedSelf = 0;
		minUnusedTotal = 0;
		averageFullWaitTime = 0;
		averageSelfWaitTime = 0;
		customers =  new Queue<Customer>();
		fullLaneQueues = new ArrayList<Queue<Customer>>();
		selfLaneQueue = new Queue<Customer>();
		customersCompleted = new TreeSet<Customer>();
		in = new Scanner(System.in);
		
		System.out.println("Minimum inter-arrival time : ");
		int minInterArrival = validateInput(1);
		System.out.println("Maximum inter-arrival time : ");
		int maxInterArrival = validateInput(minInterArrival);
		System.out.println("Minimum service time : ");
		int minService = validateInput(1);
		System.out.println("Maximum service time : ");
		int maxService = validateInput(minService);
		System.out.println("Number of customers : ");
		this.numCustomers = validateInput(1);
		System.out.println("Number of full lanes : ");
		this.numFullLanes = validateInput(1);
		System.out.println("Number of self lanes : ");
		this.numSelfLanes = validateInput(1);
		System.out.println("Percent slower for self checkout : ");
		this.selfPercentSlower = validateInput(0, 100);
		
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
	
	private int validateInput(int lowerBound,int ... higherBound) {

		
		boolean valid = false;
		int choice = lowerBound - 1;
		
		do {
			
			try {
				choice = in.nextInt();
				if (choice < lowerBound || (higherBound.length > 0 && choice > higherBound[0]))
					System.out.printf("Error : Input must be greater than %s%s\n",
							lowerBound - 1, (higherBound.length > 0) ? (" and less than " + (higherBound[0] + 1)) : "");
				else valid = true;
			}
			catch (NumberFormatException e) {
				System.out.println("Not a valid input.");
			}
			
		} while (!valid);
		
		return choice;
		
	}
	
}
