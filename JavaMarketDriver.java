import java.util.Scanner;

public class JavaMarketDriver {

	private static final Scanner in = new Scanner(System.in);
	private static CustomerCreator customerCreator;
	private static Simulator simulator;
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to the Java Market Simulator!\n"
				+ "Would you like a SIM run, or a TEST run? : ");
		
		// Ensures that the user's answer is a choice given
		String runType = null;
		boolean valid = false;
		do {
			runType = in.nextLine();
			if (!runType.equalsIgnoreCase("sim") && !runType.equalsIgnoreCase("test"))
				System.out.println("Sorry! That's not an option. Please enter SIM or TEST : ");
			else valid = true;
		} while (!valid);
		
		if (runType.equalsIgnoreCase("test")) 
			simulator = new TestSimulator();
		else
			simulator = new SimSimulator();
		
		simulator.run();
		
	}
	
}
