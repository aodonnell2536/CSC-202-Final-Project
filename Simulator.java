import java.util.ArrayList;

public abstract class Simulator {

	protected Queue<Customer> customers;
	protected ArrayList<Queue<Customer>> fullLaneQueues;
	protected Queue<Customer> selfLaneQueue;
	protected boolean[] selfLanes, fullLanes;
	protected double averageWaitTime;
	protected int selfPercentSlower, numCustomers;
	protected int numFullLanes, numSelfLanes;
	protected int time;
	protected int customersHelped;
	
	public void run() {
		
		time = 0;
		customersHelped = 0;
		averageWaitTime = 0;
		
		initialize();
		
		while (!customers.isEmpty()) {
			
			// A new customer has arrived and needs to be added to a queue
			if (customers.peek().getArrivalTime() == time) {
				
				Customer customer = customers.pop();
				
				if (Math.random() > 0.5)
					addCustomerToFull(customer);
				else
					addCustomerToSelf(customer);
				
				recalcAverageWait(customer.getWaitTime());
				
			}
			
			// Handling the current queues
			for (int i = 0; i < selfLanes.length; i++) {
				
				
				
			}
			
		}
		
	}
	
	protected abstract void initialize();
	
	private void addCustomerToFull(Customer customer) {
		
		int iMin = 0, minSize = Integer.MAX_VALUE;
		for (int i = 0; i < fullLaneQueues.size(); i++) 
			if (fullLaneQueues.get(i).size() < minSize) {
				minSize = fullLaneQueues.get(i).size();
				iMin = i;
			}
		
		Queue<Customer> addQueue = fullLaneQueues.get(iMin);
		
		int finishTime;
		if (addQueue.size() == 0)
			finishTime = customer.getArrivalTime() + customer.getServiceTime();
		else
			finishTime = addQueue.rearPeek().getFinishTime() + customer.getServiceTime();
		
		customer.setFinishTime(finishTime);
		customer.setWaitTime();		// Necessary info resides within Customer class at this point
		
		addQueue.push(customer);
		
		
	}
	
	private void addCustomerToSelf(Customer customer) {
		
		int finishTime;
		if (selfLaneQueue.size() == 0)
			finishTime = customer.getArrivalTime() + customer.getServiceTime();
		else
			finishTime = selfLaneQueue.rearPeek().getFinishTime() + customer.getServiceTime();
			
		customer.setFinishTime(finishTime);
		customer.setWaitTime();
		
		selfLaneQueue.push(customer);
		
	}
	
	private void recalcAverageWait(int nextWaitTime) {
		
		averageWaitTime = ((customersHelped - 1) * averageWaitTime + nextWaitTime) / customersHelped;
		
	}
	
}
