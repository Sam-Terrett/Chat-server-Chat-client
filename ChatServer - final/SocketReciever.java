import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReciever implements Runnable {
	
	private Socket clientSocket;
	private ConnectionAccepter accepter;
	private Integer socketID;
	private BufferedReader clientIn;
	
	public SocketReciever(Socket connectedSocket, ConnectionAccepter accepterIn, Integer socketIDIn) {
		clientSocket = connectedSocket;
		accepter = accepterIn;
		socketID = socketIDIn;
	}
	
	public void readMessages() {
		try {
			clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			while(true) {
				Thread.sleep(100);
				if(clientIn.ready()) {
					String clientMessage = clientIn.readLine();
					
					if(clientMessage.equals("EXIT")) {
						accepter.disconnectClient(socketID);		//Tells the ConnectionAccepter to delete......... 
						//Thread.currentThread().interrupt();
					}
					else {
						accepter.newMessage(clientMessage);
					}
				}
			}
			
		} catch(IOException e) {
			Thread.currentThread().interrupt();
			return;
			
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}
	}
	
	public void closeReader() {
		try {
			clientIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		readMessages();
		System.out.println("ROGER");
		
	}

}
