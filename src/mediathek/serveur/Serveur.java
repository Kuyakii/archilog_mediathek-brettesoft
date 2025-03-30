package mediathek.serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur implements Runnable {
    private final ServerSocket listenSocket;
        private final Class<? extends Runnable> serviceClass;

    public Serveur(int port, Class<? extends Runnable> serviceClass) throws IOException {
        this.listenSocket = new ServerSocket(port);
        this.serviceClass = serviceClass;
    }

    public void run() {
        System.out.println("Serveur lancé sur le port " + listenSocket.getLocalPort());
        while (true) {
            try {
                Socket clientSocket = listenSocket.accept();

                Runnable service = serviceClass
                        .getConstructor(Socket.class)
                        .newInstance(clientSocket);

                new Thread(service).start();
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement client : " + e);
            }
        }
    }
}
