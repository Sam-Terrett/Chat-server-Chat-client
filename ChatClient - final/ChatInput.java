import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class ChatInput implements Runnable {
	
	private Socket serverSocket;
	private ChatClient clientHub;
	private BufferedReader userInput;
	private String name;
	
	public ChatInput(Socket socketToSendTo, ChatClient clientHubIn, String nameIn) {
		serverSocket = socketToSendTo;
		clientHub = clientHubIn;
		name = nameIn;
	}
	
	public void readInputs() {
		
		try {
			userInput = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
			
			while(true) {
				Thread.sleep(5);
				if(userInput.ready()) {
					String userInputString = userInput.readLine();
					if(userInputString.equals("EXIT")) {
						serverOut.println(userInputString);
						clientHub.disconnect();
					}
					
					else {
						LocalDateTime now = LocalDateTime.now();
						
						serverOut.println("[" + now.getHour() + ":" + now.getMinute() + "] " + name + ": " + userInputString);
					}	
				}
			}
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
			
		} catch (IOException e) {
			Thread.currentThread().interrupt();
			return;
		}

	}
	
	public void closeReader() {
		try {
			userInput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		readInputs();
		System.out.println("Hey");
		
	}
}
