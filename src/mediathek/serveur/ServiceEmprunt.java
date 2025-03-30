package mediathek.serveur;

import java.io.*;
import java.net.*;

import mediathek.model.*;
import mediathek.exception.*;

public class ServiceEmprunt implements Runnable {
    private final Socket socket;

    public ServiceEmprunt(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String requete = in.readLine();
            System.out.println("Requête reçue (emprunt) : " + requete);

            String[] tokens = requete.trim().split(" ");
            if (tokens.length != 2) {
                out.println("ERREUR : format attendu '<idAbonne> <idDocument>'");
                return;
            }

            int idAbonne = Integer.parseInt(tokens[0]);
            int idDocument = Integer.parseInt(tokens[1]);

            Bibliotheque bib = Bibliotheque.getInstance();
            Abonne ab = bib.getAbonne(idAbonne);
            Document doc = bib.getDocument(idDocument);

            if (ab == null) {
                out.println("ERREUR : abonné introuvable");
                return;
            }
            if (doc == null) {
                out.println("ERREUR : document introuvable");
                return;
            }

            // GERONIMO
            if (ab.estBanni()) {
                out.println("ERREUR : vous êtes banni jusqu’au " + ab.getDateFinBannissementFormatee());
                return;
            }

            try {
                doc.emprunter(ab);
                out.println("OK : Document emprunté avec succès !");
            } catch (ReservationException e) {
                out.println("ERREUR : " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Erreur réseau ServiceEmprunt : " + e);
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }
}