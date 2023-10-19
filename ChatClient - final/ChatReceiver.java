import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatReceiver implements Runnable {

	private Socket serverSocket;
	private ChatClient clientHub;
	
	public ChatReceiver(Socket socketToReceiveTo, ChatClient clientHubIn) {
		serverSocket = socketToReceiveTo;
		clientHub = clientHubIn;
	}
	
	public void receiveChat() {
		try {
			// Set up the ability to read the data from the server
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			
			while(true) {
				Thread.sleep(10);
				if(serverIn.ready()) {
					String serverResponse = serverIn.readLine();
					if(serverResponse.equals("Server shutdown suddenly, forcibly quitting program...")) {
						clientHub.disconnect();
					}
					System.out.println("\n" + serverResponse);
				}
			}
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
				
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void run() {
		receiveChat();
		System.out.println("Ba");
		
	}
}

