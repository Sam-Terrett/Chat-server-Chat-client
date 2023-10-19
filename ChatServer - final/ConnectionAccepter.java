import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class ConnectionAccepter implements Runnable {
	private ServerSocket mySocket;
	//Initialise lists that will store the connected sockets class, thread and thread.
	private ArrayList<SocketHandler> connectedSockets;
	private ArrayList<Thread> connectedSocketsThreads;
	private ArrayList<Integer> connectedSocketsID;
	private int currentSocketID = 0;
	
	public ConnectionAccepter(ServerSocket acceptingSocket) {
		mySocket = acceptingSocket;
		connectedSockets = new ArrayList<>();
		connectedSocketsThreads = new ArrayList<>();
		connectedSocketsID = new ArrayList<>();
	}
	
	public void acceptingConnections() {
		try {
			while(true) {
				Thread.sleep(10);
				currentSocketID++;		//Generates new ID
				Integer tempNumID = Integer.valueOf(currentSocketID);
				
				SocketHandler newConnection = new SocketHandler(getMySocket().accept(), this, tempNumID);	//Blocks here until new connection request is made, then creates a new instance of SocketHandler for that connection
				System.out.println("New client detected, assigned ID: " + tempNumID);
				Thread newConnectionThread = new Thread(newConnection);
				newConnectionThread.start();
				
				//Adds new connection, thread and ID to list to the same index in each list 
				addConnectedSocketID(tempNumID);
				addConnectedSocketThread(newConnectionThread);
				addConnectedSocket(newConnection);
				
				LocalDateTime now = LocalDateTime.now();		//Gets current time
				newMessage("[" + now.getHour() + ":" + now.getMinute() + "] " + "New user connected");
				
			}
			
			
		} catch(InterruptedException e) {
			disconnectAllClients();
			Thread.currentThread().interrupt();		
			return;
			
		} catch(IOException e) {
			disconnectAllClients();
			Thread.currentThread().interrupt();		
			return;
		}
	}

	public ServerSocket getMySocket() {
		return mySocket;
	}
	
	private void addConnectedSocketID(Integer newID) {
		connectedSocketsID.add(newID);
	}
	
	private void addConnectedSocket(SocketHandler newSocket) {
		connectedSockets.add(newSocket);
	}
	
	private void addConnectedSocketThread(Thread newSocketThread) {
		connectedSocketsThreads.add(newSocketThread);
	}
	
	
	
	private void removeClientConnection(int index) {
		System.out.println("\nRemoving client " + getSocketIDs().get(index) + "...");
		connectedSockets.remove(index);
		connectedSocketsID.remove(index);
		
		connectedSocketsThreads.get(index).interrupt();
		try {
			connectedSocketsThreads.get(index).join();

		} catch (InterruptedException e) {
			;
		}
		System.out.println("Removed\n");
		
		connectedSocketsThreads.remove(index);
	}
	
	private ArrayList<Integer> getSocketIDs() {
		return connectedSocketsID;
	}
	
	public synchronized void disconnectClient(Integer ID) {
		int index = getSocketIDs().indexOf(ID);
		removeClientConnection(index);
	}
	
	public synchronized void newMessage(String message) {
		ArrayList<SocketHandler> copyOfSocketList = new ArrayList<SocketHandler>(connectedSockets);
		for(int i = 0; i < copyOfSocketList.size(); i++) {
			copyOfSocketList.get(i).newMessageAlert(message);
		}
	}
	
	public synchronized void disconnectAllClients() {
		while (!getSocketIDs().isEmpty()){
			removeClientConnection(0);
		}
	}
	
	public void closeServerSocket() {
		try {
			mySocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		acceptingConnections();
	}

}
