import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class ChatServer {
	private ServerSocket mySocket;
	private ConnectionAccepter serverAccepter;
	
	public ChatServer(int portNo)	{
		try {
			mySocket = new ServerSocket(portNo);		//Sets up server on socket 14001
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void go() {
		System.out.println("Server listening...");
		
		//This initialises and starts the connection accepter thread, which will manage the connecting, disconnecting and general management of the server.
		serverAccepter = new ConnectionAccepter(mySocket);		
		Thread serverAccepterThread = new Thread(serverAccepter);		
		serverAccepterThread.start();
		
		//This then starts to take user input, waiting for 'EXIT' to be entered to shutdown the program
		BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
		String consoleInputString = ("");
		while (!consoleInputString.equals("EXIT")) {
			try {
				consoleInputString = consoleInput.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Shutting down server...");
		serverAccepter.closeServerSocket();				//After breaking the while loop, the server begins shutdown procedure
		try {
			serverAccepterThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		int portNumInp = 14001;
		int numArgs = args.length;
		
		for(int i = 0; i < numArgs - 1; i++) {		// - 1 
			if(args[i].equals("-csp")) {
				try {
					portNumInp = Integer.parseInt(args[i + 1]);
					System.out.println("Valid number");
				} catch(NumberFormatException e) {
					System.out.println("Not a valid number");
				}
			}
		}
		
		ChatServer myChatServer = new ChatServer(portNumInp);
		myChatServer.go();
		try {
			myChatServer.mySocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server closed");
	}

}
