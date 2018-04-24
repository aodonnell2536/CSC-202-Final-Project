import java.util.ArrayList;

public abstract class Simulator {

	// TODO: LOGGING
	
	protected Queue<Customer> customers;
	protected static ArrayList<Queue<Customer>> fullLaneQueues;
	protected static Queue<Customer> selfLaneQueue;
	protected Customer[] selfLanes;
	protected double averageWaitTime;
	protected static int selfPercentSlower, numCustomers;
	protected int numFullLanes, numSelfLanes;
	protected int time, finalTime;
	protected int currentID;
	protected int customersHelped;
	protected int customersSatisfied, customersDissatisfied;
	
	
	public void run() {
		
		time = 0;
		currentID = 0;
		customersHelped = 0;
		customersDissatisfied = 0;
		customersSatisfied = 0;
		averageWaitTime = 0;
		customers =  new Queue<Customer>();
		fullLaneQueues = new ArrayList<Queue<Customer>>();
		selfLaneQueue = new Queue<Customer>();
		
		initialize();
		
		boolean servicing = true;
		
		while (servicing) {
			
			System.out.printf("Time : %d\n", time);
			System.out.printf("\tDepartures and Arrivals : \n");
			
			if (customers.size() != 0 && time == customers.peek().getArrivalTime()) {
				
				Customer customer = customers.pop();
				customer.setID(++currentID);
				
				if (Math.random() > 0.5)
					addCustomerToFull(customer);
				else
					addCustomerToSelf(customer);
				
				customersHelped++;
				recalcAverageWait(customer.getWaitTime());
				if (customer.getWaitTime() >= 5) customersDissatisfied++;
				else customersSatisfied++;
				
			}
			
			// Handling customer departure for full service
			for (Queue<Customer> queue : fullLaneQueues) {
				
				if (queue.size() != 0 && time == queue.peek().getFinishTime()) {
					System.out.printf("\t\tCustomer %d has finished their service.\n", queue.pop().getID());
				}
				
			}
			
			// Customer arrival for full service is 'handled' by having
			// the current helped customer at the front of the queue
			// as a little logic hack
			
			// Handling customer departure for self service
			for (int i = 0; i < selfLanes.length; i++) {
				
				Customer customer = selfLanes[i];
				
				if (customer != null && time == customer.getFinishTime()) {
					System.out.printf("\t\tCustomer %d has finished their service.\n", customer.getID());
					selfLanes[i] = null;
				}
				
			}
			
			// Handling customer arrival for self service
			if (selfLaneQueue.size() != 0 && time == selfLaneQueue.peek().getArrivalTime()) {
				for (int i = 0; i < selfLanes.length; i++) {
					if (selfLanes[i] == null) {
						selfLanes[i] = selfLaneQueue.pop();
						System.out.printf("\t\tCustomer %d has begun service at self lane %d.\n", selfLanes[i].getID(), i + 1);
						break;
					}
				}
			}
			
			// Logging the status of all the current lanes
			System.out.printf("\tLane Status : \n");
			
			// Logging full lanes
			int currentQueueNum = 1;
			System.out.printf("\t\tFull Lanes : \n");
			for (Queue<Customer> queue : fullLaneQueues) {
				
				System.out.printf("\t\t\tFull lane %d : ", currentQueueNum++);
				
				if (queue.size() != 0)
					System.out.printf("Customer %d\n", queue.peek().getID());
				else
					System.out.printf("Empty\n");
				
			}
			
			// Logging self lanes
			System.out.printf("\t\tSelf Lanes : \n");
			for (int i = 0; i < selfLanes.length; i++) {
				
				Customer customer = selfLanes[i];
				
				System.out.printf("\t\t\tSelf lane %d : ", i + 1);
				
				if (customer == null)
					System.out.println("Empty");
				else
					System.out.printf("Customer %d\n", customer.getID());
				
			}
			
			// Checking to see if all customers have been serviced
			if (customers.size() == 0) {
				boolean allEmpty = true;
				for (Queue<Customer> queue : fullLaneQueues) 
					if (queue.size() != 0)
						allEmpty = false;
				for (Customer customer : selfLanes)
					if (customer != null)
						allEmpty = false;
				servicing = !allEmpty;			// All customers have been helped and sim is over
			}
			
			time++;
			
		}
		
		// Logging Analytics
		System.out.println("\nSIMULATION HAS ENDED\n");
		
		System.out.println("Analytics : ");
		System.out.printf("Average wait time : %f\n", averageWaitTime);
		System.out.printf("Satisfied Customers : %d\n", customersSatisfied);
		System.out.printf("Dissatisfied Customers : %d\n", customersDissatisfied);
		
		
		
	}
	
	protected abstract void initialize();
	
	private static void addCustomerToFull(Customer customer) {
		
		int iMin = 0, minSize = Integer.MAX_VALUE;
		for (int i = 0; i < fullLaneQueues.size(); i++) 
			if (fullLaneQueues.get(i).size() < minSize) {
				minSize = fullLaneQueues.get(i).size();
				iMin = i;
			}
		
		
		int finishTime;
		if (fullLaneQueues.get(iMin).size() == 0)
			finishTime = customer.getArrivalTime() + customer.getServiceTime();
		else
			finishTime = fullLaneQueues.get(iMin).rearPeek().getFinishTime() + customer.getServiceTime();
		
		customer.setFinishTime(finishTime);
		customer.setWaitTime();		// Necessary info resides within Customer class at this point
		
		fullLaneQueues.get(iMin).push(customer);
		
		System.out.printf("\t\tCustomer %d has arrived for full service and is queued in lane %d.\n", customer.getID(), iMin + 1);
		
		
	}
	
	private static void addCustomerToSelf(Customer customer) {
		
		customer.setServiceTime(customer.getServiceTime() + selfPercentSlower*customer.getServiceTime());
		
		int finishTime;
		if (selfLaneQueue.size() == 0)
			finishTime = customer.getArrivalTime() + customer.getServiceTime();
		else
			finishTime = selfLaneQueue.rearPeek().getFinishTime() + customer.getServiceTime();
			
		customer.setFinishTime(finishTime);
		customer.setWaitTime();
		
		selfLaneQueue.push(customer);
		
		System.out.printf("\t\tCustomer %d has arrived for self service and entered the queue.\n", customer.getID());
		
	}
	
	private void recalcAverageWait(int nextWaitTime) {
		
		averageWaitTime = ((customersHelped - 1) * averageWaitTime + nextWaitTime) / customersHelped;
		
	}
	
}
