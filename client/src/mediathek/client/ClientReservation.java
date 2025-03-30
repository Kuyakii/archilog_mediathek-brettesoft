package mediathek.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientReservation {

    public static void main(String[] args) {
        String serveur = "localhost";
        int port = 2000;

        try (
                Socket socket = new Socket(serveur, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Client de réservation connecté au serveur.");

            System.out.print("Numéro de l’abonné : ");
            int idAbonne = scanner.nextInt();

            System.out.print("Numéro du document : ");
            int idDocument = scanner.nextInt();

            String requete = idAbonne + " " + idDocument;
            out.println(requete);

            System.out.println("Réponses du serveur :");
            String reponse;
            // grand Chaman
            while ((reponse = in.readLine()) != null) {
                System.out.println("> " + reponse);

                // Sitting Bull
                if (reponse.contains("Sitting Bull")) {
                    Scanner sc = new Scanner(System.in);
                    System.out.print("> ");
                    String choix = sc.nextLine();
                    out.println(choix);
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur client réservation : " + e);
        }
    }
}
