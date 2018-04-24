
public class Customer implements Comparable<Customer> {

	private int arrivalTime, serviceTime, finishTime, waitTime;
	private int id;
	private boolean full;		// True for FULL, false for SELF
	private int laneNumber;

	public Customer() {

	}

	public Customer(int arrivalTime, int serviceTime) {
		this.arrivalTime = arrivalTime;
		this.serviceTime = serviceTime;
	}

	public int getArrivalTime() { return arrivalTime; }
	public int getServiceTime() { return serviceTime; }
	public int getFinishTime() { return finishTime; }
	public int getWaitTime() { return waitTime; }
	public int getID() { return id; }
	public boolean isFull() { return full; }
	public int getLaneNumber() { return laneNumber; }

	public void setArrivalTime(int arrivalTime) { this.arrivalTime = arrivalTime; }
	public void setServiceTime(int serviceTime) { this.serviceTime = serviceTime; }
	public void setFinishTime(int finishTime) { this.finishTime = finishTime; }
	public void setWaitTime() { waitTime = finishTime - serviceTime - arrivalTime; }
	public void setID(int id) { this.id = id; }
	public void setFull(boolean full) { this.full = full; }
	public void setLaneNumber(int laneNumber) { this.laneNumber = laneNumber; }
	
	// Allows customers to be sorted in order of id
	public int compareTo(Customer customer) {
		return this.id - customer.getID();
	}

}
