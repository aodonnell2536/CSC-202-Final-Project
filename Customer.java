
public class Customer {

	private int arrivalTime, serviceTime, finishTime, waitTime;
	
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
	
	public void setArrivalTime(int arrivalTime) { this.arrivalTime = arrivalTime; }
	public void setServiceTime(int serviceTime) { this.serviceTime = serviceTime; }
	public void setFinishTime(int finishTime) { this.finishTime = finishTime; }
	public void setWaitTime() { waitTime = serviceTime - arrivalTime; }
	
}
