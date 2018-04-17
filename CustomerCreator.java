import java.util.Random;

public class CustomerCreator {

	private Random random;
	private int minInterArrival, maxInterArrival;
	private int minService, maxService;
	
	public CustomerCreator() {}
	
	public CustomerCreator(int minInterArrival, int maxInterArrival, int minService, int maxService) {
		this.minInterArrival = minInterArrival;
		this.maxInterArrival = maxInterArrival;
		this.minService = minService;
		this.maxService = maxService;
		random = new Random();
	}
	
	public Customer nextCustomer(int lastArrival) {
		
		int arrivalTime = lastArrival + random.nextInt(maxInterArrival - minInterArrival + 1) + minInterArrival;
		int serviceTime = random.nextInt(maxService - minService + 1) + minService;
		
		return new Customer(arrivalTime, serviceTime);
		
	}
	
}
