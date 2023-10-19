import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketHandler implements Runnable{
	private Socket clientSocket;
	private ConnectionAccepter accepter;
	private String messageToPass;
	private boolean messageThere = false;		//Flag that means indicates whether there is a message to send or not
	private Integer socketID;
	
	public SocketHandler(Socket connectedSocket, ConnectionAccepter accepterIn, Integer socketIDIn){
		clientSocket = connectedSocket;
		accepter = accepterIn;
		socketID = socketIDIn;
		
	}
	
	private void updateMessage(String message) {
		messageToPass = message;
	}
	
	private String getMessage() {
		return messageToPass;
	}
	
	private void updateMessageFlag() {
		messageThere = !messageThere;
	}
	
	public void handleSocket() {
		SocketReciever reciever = null;
		Thread recieverThread = null;
		
		try {
			reciever = new SocketReciever(clientSocket, accepter, socketID);
			recieverThread = new Thread(reciever);		//Creates new thread to handle incoming messages
			recieverThread.start();
			
			//Forever loops, until there is a message to send, after doing so, waiting for a new message to send
			while(true) {
				Thread.sleep(20);		//The sleep allows this loop to be interrupted.
				if(messageThere == true) {
					sendMessageToClient(getMessage());
					updateMessageFlag();
				}
				
			}
		} catch(InterruptedException e) {
			
			sendMessageToClient("Server shutdown suddenly, forcibly quitting program...");
			reciever.closeReader();		
			recieverThread.interrupt();
			try {
				recieverThread.join();
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			Thread.currentThread().interrupt();
			return;
		}
	}
	
	public void newMessageAlert(String message) {
		updateMessage(message);
		updateMessageFlag();
	}
	
	private void sendMessageToClient(String message) {
		try {
			PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
			serverOut.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		handleSocket();
		System.out.println("ROGER");
	}
}
