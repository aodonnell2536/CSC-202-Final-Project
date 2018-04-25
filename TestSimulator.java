import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class TestSimulator extends Simulator {
	
	public TestSimulator() { initialize(); }
	
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
		customers =  new Queue<Customer>();
		fullLaneQueues = new ArrayList<Queue<Customer>>();
		selfLaneQueue = new Queue<Customer>();
		customersCompleted = new TreeSet<Customer>();
		
		try { readData(); } 
		catch (IOException e) { e.printStackTrace(); }
		
		fullLaneUnusedTime = new int[numFullLanes];
		selfLaneUnusedTime = new int[numSelfLanes];
		numCustFull = new int[numFullLanes];
		numCustSelf = new int[numSelfLanes];
		selfLanes = new Customer[numSelfLanes];
		for (int i = 0; i < numFullLanes; i++)
			fullLaneQueues.add(new Queue<Customer>());
		
	}
	
	private void readData() throws IOException {
	
		System.out.println("Please select the file to read the test data from.");
		Frame f = new Frame();
		f.setVisible(true);
		FileDialog fd = new FileDialog(f);
		fd.setVisible(true);
		File file = new File(fd.getDirectory() + fd.getFile());
		f.setVisible(false);
		
		
		Scanner in = new Scanner(file);
		
		numFullLanes = in.nextInt();
		numSelfLanes = in.nextInt();
		selfPercentSlower = in.nextInt();
		
		numCustomers = in.nextInt();
		for (int i = 0; i < numCustomers; i++) {
			int arrival = in.nextInt();
			int service = in.nextInt();
			boolean full = in.nextBoolean();
			Customer customer = new Customer(arrival, service);
			customer.setFull(full);
			customers.push(customer);
		}
		
		in.close();
		
		
	}
	
}
