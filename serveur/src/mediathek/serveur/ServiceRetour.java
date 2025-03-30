package mediathek.serveur;


import mediathek.model.Abonne;
import mediathek.model.Bibliotheque;
import mediathek.model.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class ServiceRetour implements Runnable {
    private final Socket socket;

    public ServiceRetour(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String requete = in.readLine();
            System.out.println("Requête reçue (retour) : " + requete);

            int idDocument;
            try {
                idDocument = Integer.parseInt(requete.trim());
            } catch (NumberFormatException e) {
                out.println("ERREUR : identifiant de document invalide");
                return;
            }

            Bibliotheque bib = Bibliotheque.getInstance();
            Document doc = bib.getDocument(idDocument);

            if (doc == null) {
                out.println("ERREUR : document introuvable");
                return;
            }
            if (doc.getDateEmprunt() == null) {
                out.println("ERREUR : Document non emprunté");
                return;
            }
            doc.retourner();

            // GERONIMO
            Random rand = new Random();
            boolean degrade = rand.nextDouble() < 0.3; // test au hasard document dégradé
            // doc.setDateEmprunt(LocalDateTime.now().minusHours(3)); // test retard + bannissement GERONIMO

            LocalDateTime dateEmprunt = doc.getDateEmprunt();
            boolean retard = false;

            if (dateEmprunt != null) {
                Duration duree = Duration.between(dateEmprunt, LocalDateTime.now());
                retard = duree.toHours() > 1;
                System.out.println(doc.getDernierEmprunteur());
                System.out.println("Délai d'emprunt : " + duree.toMinutes() + " minutes");
                System.out.println(retard);
            }

            Abonne ab = doc.getDernierEmprunteur();
            if (ab != null && (degrade || retard)) {
                ab.bannirPourUnMois();
                out.println("⚠️ Abonné " + ab.getNom() + " banni jusqu’au " +
                        ab.getDateFinBannissementFormatee() + " (cause : " +
                        (retard ? "retard" : "") + (retard && degrade ? " + " : "") +
                        (degrade ? "dégradation" : "") + ")");
            }
            out.println("OK : Document retourné avec succès");

        } catch (IOException e) {
            System.err.println("Erreur réseau ServiceRetour : " + e);
        } finally {
            try { socket.close(); } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du socket" + e);
            }
        }
    }
}