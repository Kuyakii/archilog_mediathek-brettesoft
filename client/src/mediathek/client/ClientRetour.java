package mediathek.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientRetour {

    public static void main(String[] args) {
        String serveur = "localhost";
        int port = 4000;

        try (
                Socket socket = new Socket(serveur, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Client de retour connecté au serveur.");

            System.out.print("Numéro du document à retourner : ");
            int idDocument = scanner.nextInt();

            out.println(idDocument);

            String reponse = in.readLine();
            System.out.println("Réponse du serveur : " + reponse);

        } catch (IOException e) {
            System.err.println("Erreur client retour : " + e);
        }
    }
}
