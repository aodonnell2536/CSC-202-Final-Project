import java.util.ArrayList;
import java.util.TreeSet;

public abstract class Simulator {
	
	// Customer handling
	protected Queue<Customer> customers;
	protected static ArrayList<Queue<Customer>> fullLaneQueues;
	protected static Queue<Customer> selfLaneQueue;
	protected Customer[] selfLanes;
	protected double averageWaitTime;
	
	// Functionality
	protected static int selfPercentSlower, numCustomers;
	protected int numFullLanes, numSelfLanes;
	protected int time;
	protected int currentID;
	protected boolean servicing;
	
	// Analytics
	protected int customersHelped;
	protected int customersSatisfied, customersDissatisfied;
	protected int totalUnusedTime;
	protected TreeSet<Customer> customersCompleted;
	
	public void run() {
		
		// Initializing based off sim or test implementation
		initialize();
		
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
			int currentLane = 1;
			for (Queue<Customer> queue : fullLaneQueues) {
				
				if (queue.size() != 0 && time == queue.peek().getFinishTime()) {
					Customer customer = queue.pop();
					System.out.printf("\t\tCustomer %d has finished their service.\n", customer.getID());
					customersCompleted.add(customer);
					if (queue.size() != 0)
						System.out.printf("\t\tCustomer %d has begun service in full lane %d.\n", customer.getID(), currentLane);
				}
				currentLane++;
				
			}
			
			/*  
			 * Customer arrival for full service is 'handled' by having
			 * the current helped customer at the front of the queue
			 * as a little logical hack
			 */
			
			// Handling customer departure for self service
			for (int i = 0; i < selfLanes.length; i++) {
				
				Customer customer = selfLanes[i];
				
				if (customer != null && time == customer.getFinishTime()) {
					System.out.printf("\t\tCustomer %d has finished their service.\n", customer.getID());
					customersCompleted.add(customer);
					selfLanes[i] = null;
				}
				
			}
			
			// Handling customer arrival for self service
			if (selfLaneQueue.size() != 0 && time == selfLaneQueue.peek().getArrivalTime()) {
				for (int i = 0; i < selfLanes.length; i++) {
					if (selfLanes[i] == null) {
						Customer customer = selfLaneQueue.pop();
						customer.setLaneNumber(i + 1);
						selfLanes[i] = customer;
						System.out.printf("\t\tCustomer %d has begun service at self lane %d.\n", selfLanes[i].getID(), i + 1);
						break;
					}
				}
			}
			
			// Compiling the unused time for analytics
			for (Queue<Customer> queue : fullLaneQueues)
				if (queue.isEmpty()) totalUnusedTime++;
			for (Customer customer : selfLanes)
				if (customer == null) totalUnusedTime++;
			
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
		System.out.printf("\tAverage wait time : %f min \n", averageWaitTime);
		System.out.printf("\tSatisfied Customers : %d\n", customersSatisfied);
		System.out.printf("\tDissatisfied Customers : %d\n", customersDissatisfied);
		System.out.printf("\tPercentage of customers that were satisfied : %.2f%%\n", (((double) customersSatisfied) / customersHelped) * 100);
		System.out.printf("\tTotal time lanes were unused : %d min\n", totalUnusedTime);
//		System.out.printf("\tPercentage of time a lane went unused : %f%%\n", (((double)totalUnusedTime) / (time * (numFullLanes + numSelfLanes)) * 100));
		
		System.out.println("\nBREAKDOWN OF EACH CUSTOMER'S EXPERIENCE\n");
		
		System.out.printf("%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\n", "ID"
				, "Arrival", "Serv. Start", "Serv. Time", "Wait Time"
				, "Fin. Time", "Lane Type", "Lane #", "Satisfied?");
		
		for (Customer customer : customersCompleted) {
			
			System.out.printf("%-10d\t%-10d\t%-10d\t%-10d\t%-10d\t%-10d\t%-10s\t%-10d\t%-10s\n",
					customer.getID(), customer.getArrivalTime()
					, customer.getFinishTime() - customer.getServiceTime()
					, customer.getServiceTime(), customer.getWaitTime()
					, customer.getFinishTime() , (customer.isFull() ? "FULL" : "SELF")
					, customer.getLaneNumber(), (customer.getWaitTime() >= 5 ? "FALSE" : "TRUE"));
			
		}
		
		System.out.println("\nEND OF ANALYTICS\n");
		
		System.out.println("Thank you for using the Java Market Driver v3. Have a nice day!");
		
		
	}
	
	protected abstract void initialize();
	
	private static void addCustomerToFull(Customer customer) {
		
		customer.setFull(true);
		
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
		
		customer.setLaneNumber(iMin + 1);
		
		System.out.printf("\t\tCustomer %d has arrived for full service and is queued in lane %d.\n", customer.getID(), iMin + 1);
		
	}
	
	private static void addCustomerToSelf(Customer customer) {
		
		customer.setFull(false);
		
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
		
		averageWaitTime = (((customersHelped - 1) * averageWaitTime) + nextWaitTime) / customersHelped;
		
	}
	
}
