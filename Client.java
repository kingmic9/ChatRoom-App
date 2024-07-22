import java.net.*;
import java.io.*;
import java.util.Scanner;


/** A class to manage all client actions like send / receive a message*/
public class Client {

    // Socket for the client to be read or written from
    private Socket socket;
    // reader and writer for the socket
    private BufferedReader br;
    private BufferedWriter bw;
    //client username
    private String username;

    /** Initialize a client with a given username, and socket*/
    public Client(Socket socket, String username){
        try {
            this.username = username;
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, bw, br);
        }
    }

    /** Send a message to handler to be broadcast*/
    public void sendMessage() {
        try {
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bw.write(username + ": " + messageToSend);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bw, br);
        }
    }

    /** Listen for a message from other users i.e. received by client handler */
    public void listenForMessage(){
        new Thread(() -> {
            try{
            String messageFromGC;

            while (socket.isConnected()){
                messageFromGC = br.readLine();
                System.out.print(messageFromGC + "\n");

            }
            } catch (IOException e) {
                closeEverything(socket, bw, br);
            }
        }).start();
    }

    /** close the socket, reader and writer */
    public void closeEverything(Socket socket, BufferedWriter bw, BufferedReader br){
        try {
            if (bw != null) {bw.close();}
            if (br != null) {br.close();}
            if (socket != null) {socket.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Run a client (Connect to Server and Start Chatting) */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the chatroom: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("192.168.68.76", 5678);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();


    }
}
