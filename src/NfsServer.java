import java.io.*;
import java.net.*;
import java.util.*;

public class NfsServer {
    private static final int PORT = 4093;
    private List<String> arquivos = new ArrayList<>();

    public static void main(String[] args) {
        new NfsServer().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(String.format("==== Servidor NFS iniciado  na porta %s =====" , PORT) );

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                String command;
                while ((command = input.readLine()) != null) {
                    System.out.println("Comando recebido: " + command);
                    processCommand(command);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processCommand(String command) {
            String[] parts = command.split(" ");
            String action = parts[0];

            switch (action) {
                case "readdir":
                    readdir();
                    break;
                case "create":
                    create(parts[1]);
                    break;
                case "rename":
                    rename(parts[1], parts[2]);
                    break;
                case "remove":
                    remove(parts[1]);
                    break;
                default:
                    output.println("Comando não encontrado: " + action);
            }
        }

        private void readdir() {
            if (arquivos.isEmpty()) {
                output.println("Nenhum arquivo encontrado.");
            } else {
                for (String arquivo : arquivos) {
                    output.println(arquivo);
                }
            }
            output.println("END");
        }

        private void create(String fileName) {
            if (!arquivos.contains(fileName)) {
                arquivos.add(fileName);
                output.println("Arquivo " + fileName + " criado.");
            } else {
                output.println("Arquivo " + fileName + " já existe.");
            }
            output.println("END");
        }

        private void rename(String oldName, String newName) {
            if (arquivos.contains(oldName)) {
                arquivos.remove(oldName);
                arquivos.add(newName);
                output.println("Arquivo " + oldName + " renomeado para " + newName);
            } else {
                output.println("Arquivo " + oldName + " não encontrado.");
            }
            output.println("END");
        }

        private void remove(String fileName) {
            if (arquivos.remove(fileName)) {
                output.println("Arquivo " + fileName + " removido.");
            } else {
                output.println("Arquivo " + fileName + " não encontrado.");
            }
            output.println("END");
        }
    }
}
