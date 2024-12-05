import java.io.*;
import java.net.*;

public class NfsClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 4093;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            String command;
            while (true) {
                System.out.print("Digite um comando (readdir, create <nome>, rename <antigo> <novo>, remove <nome>, exit): ");
                command = console.readLine();
                output.println(command);

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Encerrando o cliente...");
                    break;
                }

                
                String response;
                while ((response = input.readLine()) != null) {
                    if (response.equals("END")) break;  
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
