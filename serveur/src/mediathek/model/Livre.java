package mediathek.model;

import mediathek.bsoft.SittingBull;
import mediathek.exception.ReservationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Livre implements Document {
    private final int numero;
    private final String titre;
    private final int nbPages;

    private Abonne reservePar = null;
    private boolean emprunte = false;

    // GERONIMO
    private Abonne dernierEmprunteur = null;
    private LocalDateTime dateEmprunt = null;

    // Sitting Bull
    private List<Abonne> alertes = new ArrayList<>();

    public Livre(int numero, String titre, int nbPages) {
        this.numero = numero;
        this.titre = titre;
        this.nbPages = nbPages;
    }

    @Override
    public int numero() {
        return numero;
    }

    @Override
    public synchronized void reserver(Abonne ab) throws ReservationException {
        if (reservePar != null && reservePar.getHeureExpirationReservation().isBefore(LocalDateTime.now().plusSeconds(3))) {
            reservePar = null;
        }
        if (emprunte)
            throw new ReservationException("Ce livre est déjà emprunté.");
        if (reservePar != null)
            throw new ReservationException("Ce livre est déjà réservé.");

        reservePar = ab;
        ab.setExpirationReservation(java.time.LocalDateTime.now().plusHours(1));
        // ab.setExpirationReservation(java.time.LocalDateTime.now().plusSeconds(70)); // Test pour Sitting Bull et GRAND CHAMAN
    }

    @Override
    public synchronized void emprunter(Abonne ab) throws ReservationException {
        if (emprunte)
            throw new ReservationException("Ce livre est déjà emprunté.");
        if (reservePar != null && reservePar != ab)
            throw new ReservationException("Ce livre est réservé pour un autre abonné.");

        emprunte = true;
        reservePar = null;
        dernierEmprunteur = ab;
        dateEmprunt = LocalDateTime.now(); // GERONIMO
    }

    @Override
    public synchronized void retourner() {
        emprunte = false;
        reservePar = null;
        dateEmprunt = null;
        notifierAlertes(); // Sitting Bull
    }

    // GERONIMO
    @Override
    public Abonne getDernierEmprunteur() {
        return dernierEmprunteur;
    }
    @Override
    public LocalDateTime getDateEmprunt() {
        return dateEmprunt;
    }
    @Override
    public void setDateEmprunt(LocalDateTime dateEmprunt) { // Pour tester le banissement en cas de retard
        this.dateEmprunt = dateEmprunt;
    }

    // GRAND CHAMAN
    @Override
    public Abonne getReservePar() {
        return reservePar;
    }

    @Override
    public LocalDateTime getExpirationReservation() {
        return reservePar != null ? reservePar.getHeureExpirationReservation() : null;
    }

    // Sitting Bull
    @Override
    public void ajouterAlerte(Abonne ab) {
        if (!alertes.contains(ab)) {
            alertes.add(ab);
        }
    }

    @Override
    public void notifierAlertes() {
        for (Abonne ab : alertes) {
            System.out.println("💨 Envoi d’un signal de fumée à " + ab.getNom());
            SittingBull.envoyerAlerte(ab, this);
        }
        alertes.clear();
    }
}
