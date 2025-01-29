import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // Constructor
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Send message to the server
    public void sendMessage() {
        try {
            bufferedWriter.write(username); // Write the username
            bufferedWriter.newLine();        // Add new line
            bufferedWriter.flush();          // Flush the stream

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + " : " + messageToSend); // Format message
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Listen for incoming messages from the server
    public void ListenForMessage() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        if (msgFromGroupChat != null) {
                            System.out.println(msgFromGroupChat);
                        }
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    // Close all resources
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat ! : ");
        String username = scanner.nextLine();
        try {
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket, username);
            client.ListenForMessage();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
