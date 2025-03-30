package mediathek.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Abonne {
    private final int id;
    private final String nom;
    private final LocalDate dateNaissance;
    private LocalDateTime expirationReservation;

    // Brette Certif GERONIMO
    private LocalDate banniJusqu = null;

    public Abonne(int id, String nom, LocalDate dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.expirationReservation = null;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public void setExpirationReservation(LocalDateTime expiration) {
        this.expirationReservation = expiration;
    }

    public LocalDateTime getHeureExpirationReservation() {
        return expirationReservation;
    }

    // Brette Certif GERONIMO
    public boolean estBanni() {
        if (banniJusqu == null) return false;
        return LocalDate.now().isBefore(banniJusqu);
    }

    public void bannirPourUnMois() {
        this.banniJusqu = LocalDate.now().plusMonths(1);
    }

    public String getDateFinBannissementFormatee() {
        if (banniJusqu == null) return "non banni";
        return banniJusqu.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
