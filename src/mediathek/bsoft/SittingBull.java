package mediathek.bsoft;


import mediathek.model.Abonne;
import mediathek.model.Document;

public class SittingBull {

    public static void envoyerAlerte(Abonne ab, Document doc) {
        // On envoie un mail normalement mais on ^révient juste
        System.out.println("📨 Signal de fumée envoyé à " + ab.getNom() +
                " pour le document n°" + doc.numero());

    }
}
