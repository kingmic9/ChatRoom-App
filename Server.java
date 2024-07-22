import java.net.*;
import java.io.*;

/**
 * The server class is the class that will actually run the server, and accept connections from users.
 * The server must be running before any connections are attempted to be made.
 *
 * USAGE WARNING: Only one instance of the server should be run on a given port number.
 * */
public class Server {

    // A ServerSocket object that will be used to accept all client connections.
    private final ServerSocket serverSocket;

    /** Initialize a new ChatRoom Server */
    public Server(ServerSocket serverSocket1){
        this.serverSocket = serverSocket1;
    }

    /** Start the server and allow clients to connect to the server.
     * Closes the ServerSocket if there are any issues with I/O. */
    public void startServer(){
        try{
            // Always have the server able to connect new clients
            while (!this.serverSocket.isClosed()){
                Socket socket = this.serverSocket.accept();

                // Message for the server only that a client has connected
                System.out.print("A client has connected.\n");

                // Make a runnable object to handle the client communication and actions
                ClientHandler clientHandler = new ClientHandler(socket);

                // Thread so that the clients can interact and use the app
                // without being blocked on the blocking accept call
                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e){closeServerSocket();}
    }

    /** Checks that the server socket is not null, before closing it. */
    public void closeServerSocket(){
        try {
            if (this.serverSocket != null){
               this.serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Runs the server. */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5678);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}