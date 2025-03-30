package mediathek.model;

import mediathek.exception.ReservationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Document {
    int numero();
    void reserver(Abonne ab) throws ReservationException;
    void emprunter(Abonne ab) throws ReservationException;
    void retourner();

    // Pour Grand Chaman
    Abonne getReservePar();
    LocalDateTime getExpirationReservation();

    // Pour Géronimo
    Abonne getDernierEmprunteur();
    LocalDateTime getDateEmprunt();
    void setDateEmprunt(LocalDateTime dateEmprunt); // Pour tester le banissement en cas de retard

    // Sitting Bull
    void ajouterAlerte(Abonne ab);
    void notifierAlertes();
}
