package mediathek.serveur;


import mediathek.exception.ReservationException;
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
import java.time.format.DateTimeFormatter;

public class ServiceReservation implements Runnable {
    private final Socket socket;

    public ServiceReservation(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String requete = in.readLine();
            System.out.println("Requête reçue (réservation) : " + requete);

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

            // GRAND CHAMAN
            Abonne reservePar = doc.getReservePar();
            LocalDateTime expiration = doc.getExpirationReservation();

            if (reservePar != null && !reservePar.equals(ab) && expiration != null) {
                long secondesRestantes = Duration.between(LocalDateTime.now(), expiration).getSeconds();

                if (secondesRestantes > 0 && secondesRestantes <= 60) {
                    out.println("Le Grand Chaman chante... patience, le document sera bientôt libre (" + secondesRestantes + "s)");

                    try {
                        Thread.sleep(secondesRestantes * 1000); // Attente
                    } catch (InterruptedException e) {
                        out.println("ERREUR : la transe du chaman a été interrompue.");
                        return;
                    }

                    try {
                        doc.reserver(ab);
                        String heure = ab.getHeureExpirationReservation().format(DateTimeFormatter.ofPattern("HH'h'mm"));
                        out.println("Réservation confirmée par le Grand Chaman jusqu’à " + heure);
                        return;
                    } catch (ReservationException e) {
                        if((" " + e).contains("16 ans")) {
                            out.print(e.getMessage());
                            return;
                        }
                        out.println("ERREUR : un autre guerrier a été plus rapide après la transe du chaman.");
                        return;
                    }
                }
            }

            // SITTING BULL
            if (reservePar != null && !reservePar.equals(ab)) {
                String fin = expiration.format(DateTimeFormatter.ofPattern("HH'h'mm"));
                out.println("ERREUR : Ce document est réservé jusqu’à " + fin + ". Voulez-vous activer l’alerte Sitting Bull ? (oui/non)");

                String reponse = in.readLine();
                if (reponse != null && reponse.equalsIgnoreCase("oui")) {
                    doc.ajouterAlerte(ab);
                    out.println("Alerte activée ! Vous recevrez un signal de fumée au retour.");

                    try {
                        while(doc.getReservePar() != null || doc.getDateEmprunt() != null) {
                            Thread.sleep(500);
                        }
                    } catch (InterruptedException e) {
                        out.println("ERREUR : Sitting Bull a été interrompue.");
                        return;
                    }
                } else {
                    out.println("Alerte non activée.");
                }
                return;
            }

            // Réservation classique
            try {
                doc.reserver(ab);
                String heure = ab.getHeureExpirationReservation().format(DateTimeFormatter.ofPattern("HH'h'mm"));
                out.println("OK : Document réservé jusqu’à " + heure);
            } catch (ReservationException e) {
                out.println("ERREUR : " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Erreur réseau ServiceReservation : " + e);
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }
}