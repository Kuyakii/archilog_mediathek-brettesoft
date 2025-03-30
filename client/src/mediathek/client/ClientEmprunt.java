package mediathek.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientEmprunt {

    public static void main(String[] args) {
        String serveur = "localhost";
        int port = 3000;

        try (
                Socket socket = new Socket(serveur, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Client d'emprunt connecté au serveur.");

            System.out.print("Numéro de l’abonné : ");
            int idAbonne = scanner.nextInt();

            System.out.print("Numéro du document : ");
            int idDocument = scanner.nextInt();

            String requete = idAbonne + " " + idDocument;
            out.println(requete);

            String reponse = in.readLine();
            System.out.println("Réponse du serveur : " + reponse);

        } catch (IOException e) {
            System.err.println("Erreur client emprunt : " + e);
        }
    }
}
