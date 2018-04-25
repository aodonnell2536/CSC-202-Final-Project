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
	protected static int selfPercentSlower;
	protected static int numCustomers;
	protected int numFullLanes, numSelfLanes;
	protected int time;
	protected int currentID;
	protected boolean servicing;
	
	// Analytics
	protected int customersHelped;
	protected int customersSatisfied, customersDissatisfied;
	protected int totalUnusedTime, totalUnusedTimeFull, totalUnusedTimeSelf;
	protected int minUnusedFull, minUnusedSelf, minUnusedTotal;
	protected int[] fullLaneUnusedTime, selfLaneUnusedTime;		// Unused time for each lane
	protected int[] numCustFull, numCustSelf;					// Num customers serviced each lane
	protected TreeSet<Customer> customersCompleted;				// Set of completed customers in tree set for auto sorting by id
																
	
	public void run() {
		
		servicing = true;
		
		while (servicing) {
			
			System.out.printf("Time : %d\n", time);
			System.out.printf("\tDepartures and Arrivals : \n");
			
			if (customers.size() != 0 && time == customers.peek().getArrivalTime()) {
				
				Customer customer = customers.pop();
				customer.setID(currentID);
				currentID++;
				
				if (this instanceof SimSimulator) {
					if (Math.random() > 0.5)
						addCustomerToFull(customer);
					else
						addCustomerToSelf(customer);
				}
				else if (customer.isFull())
					addCustomerToFull(customer);
				else addCustomerToSelf(customer);
				
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
						System.out.printf("\t\tCustomer %d has begun service in full lane %d.\n", queue.peek().getID(), currentLane);
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
			if (selfLaneQueue.size() != 0) {
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
			
			// Logging the status of all the current lanes
			// and compiling unused lane time
			System.out.printf("\tLane Status : \n");
			
			boolean unusedFull = false, unused = false;;
			// Logging full lanes
			int currentQueueNum = 1;
			System.out.printf("\t\tFull Lanes : \n");
			for (Queue<Customer> queue : fullLaneQueues) {
				
				System.out.printf("\t\t\tFull lane %d : ", currentQueueNum);
				
				if (queue.size() != 0) {
					System.out.printf("Customer %d\n", queue.peek().getID());
				} else {
					System.out.printf("Empty\n");
					totalUnusedTimeFull++;
					fullLaneUnusedTime[currentQueueNum - 1]++;
					if (!unusedFull) {
						minUnusedFull++;
						minUnusedTotal++;
						unusedFull = true;
						unused = true;
					}
				}
				
				currentQueueNum++;
				
			}
			
			boolean unusedSelf = false;
			// Logging self lanes
			System.out.printf("\t\tSelf Lanes : \n");
			for (int i = 0; i < selfLanes.length; i++) {
				
				Customer customer = selfLanes[i];
				
				System.out.printf("\t\t\tSelf lane %d : ", i + 1);
				
				if (customer == null) {
					System.out.println("Empty");
					totalUnusedTimeSelf++;
					selfLaneUnusedTime[i]++;
					if (!unusedSelf) {
						minUnusedSelf++;
						if (!unused) {
							minUnusedTotal++;
							unused = true;
						}
						unusedSelf = true;
					}
				} else
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
				servicing = !allEmpty;	// All customers have been helped and sim is over if all are empty
			}
			
			time++;
			
		}
		
		totalUnusedTime = totalUnusedTimeSelf + totalUnusedTimeFull;
		
		/*
		 * 	SIMULATION IS DONE
		 *  ANALYTICS LOGGED THEN PROGRAM ENDS
		 */
		
		System.out.println("\nSIMULATION HAS ENDED\n");
		
		System.out.println("Customer analytics : ");
		System.out.printf("\tAverage wait time : %.2f min\n", averageWaitTime);
		System.out.printf("\tSatisfied Customers : %d\n", customersSatisfied);
		System.out.printf("\tDissatisfied Customers : %d\n", customersDissatisfied);
		System.out.printf("\tPercentage of customers that were satisfied : %.2f%%\n", (((double) customersSatisfied) / customersHelped) * 100);
		
		System.out.println("\nLane Analytics : ");
		System.out.printf("\tTotal time a full lane went unused : %d min\n", totalUnusedTimeFull);
		System.out.printf("\tTotal time a self lane went unused : %d min\n", totalUnusedTimeSelf);
		System.out.printf("\tTotal time lanes were unused : %d min\n", totalUnusedTime);
		// TODO: FIX THESE THESE ANALYTICS BELOW
		System.out.printf("\tPercent time at least one full lane went unused : %.2f%%\n", (((double)minUnusedFull) / time) * 100);
		System.out.printf("\tPercent time at least one self lane went unused : %.2f%%\n", (((double)minUnusedSelf) / time) * 100);
		System.out.printf("\tPercent time at least one lane went unused : %.2f%%\n", (((double)minUnusedTotal) / time) * 100);
		
		System.out.println("\nBREAKDOWN OF EACH LANE\n");
		System.out.printf("%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n"
				, "Lane Type", "Lane Num", "Custs served", "Unused time", "% time unused");
		for (int i = 0; i < numFullLanes; i++) {
			System.out.printf("%-10s\t%-10d\t%-10d\t%-10d\t%-10.2f\n"
					, "FULL", i + 1, numCustFull[i], fullLaneUnusedTime[i]
							, ((double)fullLaneUnusedTime[i] / time) * 100);
		}
		for (int i = 0; i < numSelfLanes; i++) {
			System.out.printf("%-10s\t%-10d\t%-10d\t%-10d\t%-10.2f\n"
					, "SELF", i + 1, numCustSelf[i], selfLaneUnusedTime[i]
							, ((double)selfLaneUnusedTime[i] / time) * 100);
		}
		
		System.out.println("\nBREAKDOWN OF EACH CUSTOMER'S EXPERIENCE\n");
		
		System.out.printf("%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n", "ID"
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
		
		// Finding the smallest sized queue
		int iMin = 0, minSize = Integer.MAX_VALUE;
		for (int i = 0; i < fullLaneQueues.size(); i++) 
			if (fullLaneQueues.get(i).size() < minSize) {
				minSize = fullLaneQueues.get(i).size();
				iMin = i;
			}
		
		// Setting the finish time
		int finishTime = (fullLaneQueues.get(iMin).size() == 0) ? 
				customer.getArrivalTime() + customer.getServiceTime() : 
				fullLaneQueues.get(iMin).rearPeek().getFinishTime() + customer.getServiceTime();
				
		// Setting fields of the customer being added and adding to the queue
		customer.setFull(true);
		customer.setFinishTime(finishTime);
		customer.setWaitTime();		// Necessary info resides within Customer class at this point
		customer.setLaneNumber(iMin + 1);
		fullLaneQueues.get(iMin).push(customer);
		
		if (fullLaneQueues.get(iMin).size() == 0)
			System.out.printf("\t\tCustomer %d has arrived for full service and began service instantly in lane %d", customer.getID(), iMin + 1);
		else
			System.out.printf("\t\tCustomer %d has arrived for full service and is queued in lane %d.\n", customer.getID(), iMin + 1);
		
	}
	
	private static void addCustomerToSelf(Customer customer) {
		
		// Adjusting the service time with 'selfPercentSlower'
		customer.setServiceTime(customer.getServiceTime() + (int)((double)selfPercentSlower/100)*customer.getServiceTime());
			
		// Setting the finish time
		int finishTime = (selfLaneQueue.size() == 0) ? 
				customer.getArrivalTime() + customer.getServiceTime() : 
				selfLaneQueue.rearPeek().getFinishTime() + customer.getServiceTime();
		
		// Setting fields of the customer being added and adding to the queue
		customer.setFull(false);
		customer.setFinishTime(finishTime);
		customer.setWaitTime();
		selfLaneQueue.push(customer);
		
		System.out.printf("\t\tCustomer %d has arrived for self service and entered the queue.\n", customer.getID());
		
	}
	
	private void recalcAverageWait(int nextWaitTime) {
		averageWaitTime = (((customersHelped - 1) * averageWaitTime) + nextWaitTime) / customersHelped;
	}
	
}
