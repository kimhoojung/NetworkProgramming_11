package application;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private AtomicInteger playerCount = new AtomicInteger(1);
    private boolean isGameStarted = false;  // 게임이 시작되었는지 여부를 추적하는 플래그

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port: " + port);
    }

    public void acceptClients() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                String playerName = "Player" + playerCount.getAndIncrement();
                ClientHandler clientHandler = new ClientHandler(socket, this, playerName);
                clients.add(clientHandler);
                broadcastMessage("[Server]: " + playerName + " has connected.");
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcastMessage("[Server]: " + client.getPlayerName() + " has disconnected.");
    }

    public void processMessage(String message, ClientHandler client) {
        if ("start_game".equals(message.trim().toLowerCase())) {
            if (!isGameStarted) {
                isGameStarted = true;  // 게임 시작 플래그 설정
                broadcastMessage("disable_start_button");  // 모든 클라이언트에 버튼 비활성화 명령 전송
                performCountdown();  // 게임 시작 카운트다운
                broadcastMessage("[Server]: Game has started!");
            }
        } else {
            broadcastMessage(client.getPlayerName() + ": " + message);
        }
    }

    private void performCountdown() {
        for (int i = 3; i > 0; i--) {
            broadcastMessage("Countdown: " + i);
            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, Failed to complete countdown");
            }
        }
        broadcastMessage("게임이 시작되었습니다!");
    }

    public static void main(String[] args) {
        int port = 1234;
        try {
            Server server = new Server(port);
            server.acceptClients();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;

    public ClientHandler(Socket socket, Server server, String playerName) {
        this.socket = socket;
        this.server = server;
        this.playerName = playerName;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                server.processMessage(message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getPlayerName() {
        return playerName;
    }
}