import java.io.*;
import java.net.*;
import java.util.ArrayList;

/** A ClientHandler Class which manages all ClientHandlers, which allows a thread to run / manage each client.*/
public class ClientHandler implements Runnable{

    // A static array list to hold all the clients for a given server socket
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();

    // The individual socket associated with the client
    private Socket socket;

    // Reader and Writer for Each Client, buffered to improve efficiency
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // Client username information, to tell other users who a message is from
    private String clientUsername;

    // Keep track of if the client has been greeted to the server
    private boolean greeted;

    /** Create a ClientHandler, add it to the list of client handlers.
     * Reads in the client username, as read from the client
     * Additionally, broadcasts to the server that a new user has joined.
     *
     * Closes the client socket, reader and writer if there is any I/O errors*/
    public ClientHandler(Socket socket){
        try{
            // Setup client socket, and readers from the client's socket
            this.socket = socket;
            this.greeted = false;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();

            // Add this client to the list of clients
            clientHandlers.add(this);

            // Broadcast to all other users that a user has joined
            broadcast("Server: " + this.clientUsername + " has entered the chatroom.");

        } catch (IOException e){
            closeEverything(socket, this.bufferedWriter, this.bufferedReader);
        }
    }

    /** Method to make the broadcast a client's messages to other active users*/
    @Override
    public void run() {
    String messageFromClient;

    // Process messages as long as the client is connected
    while (this.socket.isConnected()) {
        try{
            messageFromClient = this.bufferedReader.readLine();
            broadcast(messageFromClient);
        } catch (IOException e) {
            closeEverything(socket, this.bufferedWriter, this.bufferedReader);
            break;
        }
    }
    }

    /** Send message to all other users. */
    public void broadcast(String messageToSend){
        for (ClientHandler clientHandler : clientHandlers){
            try{
                // Broadcast message to all other users
                if (!clientHandler.clientUsername.equals(this.clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    // Ensure that buffer does not fill up
                    clientHandler.bufferedWriter.flush();
                } else if (!this.greeted){
                    //Greet the current client iff this is the first thing they type (i.e. their name)
                    clientHandler.bufferedWriter.write("Welcome to the conversation " + this.clientUsername + "!");
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                    int otherUsersOnline = clientHandlers.size() - 1; // Exclude yourself
                    clientHandler.bufferedWriter.write("There are currently " + otherUsersOnline + " other people online in this chatroom.");
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                    this.greeted = true; // Do not greet the client again after they type something
                    // Only greet them after they type their name
                }
            } catch (IOException e){
                closeEverything(socket, this.bufferedWriter, this.bufferedReader);
            }
        }
    }

    /** Removes client handler from list of handlers, and tells other users that they left */
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcast("SERVER: " + this.clientUsername + " has left the chatroom!");
    }

    /** close the socket, reader and writer for a client */
    public void closeEverything(Socket socket, BufferedWriter bw, BufferedReader br){
        removeClientHandler();
        try {
            if (bw != null) {bw.close();}
            if (br != null) {br.close();}
            if (socket != null) {socket.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
