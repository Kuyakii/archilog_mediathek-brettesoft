package mediathek.serveur;

public class AppServeur {

    public static void main(String[] args) {
        try {
            new Thread(new Serveur(2000, ServiceReservation.class)).start();
            new Thread(new Serveur(3000, ServiceEmprunt.class)).start();
            new Thread(new Serveur(4000, ServiceRetour.class)).start();

            System.out.println("Serveurs de la médiathèque lancés !");
        } catch (Exception e) {
            System.err.println("Erreur au lancement des serveurs : " + e);
        }
    }
}
