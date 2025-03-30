package mediathek.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Bibliotheque {
    private static Bibliotheque instance;
    private Map<Integer, Abonne> abonnes = new HashMap<>();
    private Map<Integer, Document> documents = new HashMap<>();

    private Bibliotheque() {
        initialiser();
    }

    public static Bibliotheque getInstance() {
        if (instance == null) {
            instance = new Bibliotheque();
        }
        return instance;
    }

    private void initialiser() {
        abonnes.put(1, new Abonne(1, "Alice", LocalDate.of(2000, 1, 1)));
        abonnes.put(2, new Abonne(2, "Bob", LocalDate.of(2010, 5, 12))); // 14 ans

        documents.put(100, new Livre(100, "Les Misérables", 1200));
        documents.put(101, new DVD(101, "Inception", true));  // adulte
        documents.put(102, new DVD(102, "Shrek", false));
    }

    public Abonne getAbonne(int id) {
        return abonnes.get(id);
    }

    public Document getDocument(int id) {
        return documents.get(id);
    }
}
