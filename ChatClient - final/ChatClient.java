import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
	
	private Socket serverSocket;
	private boolean keepRunning = true;
	private String userName;
	
	public ChatClient(String[] args, String typeOfClient) {
		try {
			
			String addressInp = "localhost";
			int portInp = 14001;
			int numArgs = args.length;
			
			for(int i = 0; i < numArgs - 1; i++) {		// - 1 
				if(args[i].equals("-ccp")) {
					try {
						portInp = Integer.parseInt(args[i + 1]);
						System.out.println("Valid number");
					} catch(NumberFormatException e) {
						System.out.println("Not a valid number");
					}
				}
				
				else if(args[i].equals("-cca")) {
					addressInp = args[i + 1];
				}
			}
			
			userName = getCorrectClientName(typeOfClient);
			
			//userInput.close();
			serverSocket = new Socket(addressInp, portInp);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
	}
	
	private String getCorrectClientName(String typeOfClient) {
		String inputString = "";		//Will never happen ADDMORE DET
		if(typeOfClient.equals("Human")) {
			
			BufferedReader userInput;
			do {
				System.out.println("Please enter your name, it has to consist of at least a character and not contain a colon:\n");
				userInput = new BufferedReader(new InputStreamReader(System.in));
				try {
					inputString = userInput.readLine();
				} catch (IOException e) {
					inputString = "ThisUserHadAnError!!";
				}
				
			} while(inputString.equals("") || inputString.indexOf(':') != -1);
		}
		else if (typeOfClient.equals("chat_bot")) {
			inputString = "TherapyBot";
		}	
		
		return inputString;
	}
	
	private void keepRunningFlipper() {
		keepRunning =!keepRunning;
	}
	
	public void go() {
		System.out.println("Connected, chat log begins:");
		ChatInput chatter = new ChatInput(serverSocket, this, userName);
		ChatReceiver receiver = new ChatReceiver(serverSocket, this);
		
		Thread tChatter = new Thread(chatter);
		Thread tReceiver = new Thread(receiver);
		
		tChatter.start();
		tReceiver.start();
		

		try {
			while(keepRunning) {
				Thread.sleep(10);
			}
			
			tReceiver.interrupt();
			chatter.closeReader();
			
			tChatter.interrupt();
			tReceiver.join();			
			tChatter.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void disconnect() {
		keepRunningFlipper();
	}
	
	public static void main(String[] args) {
		
		ChatClient myEchoClient = new ChatClient(args, "Human");
		myEchoClient.go();
		
		try {
			myEchoClient.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\nProgram exited");
	}

}
