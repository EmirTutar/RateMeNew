package RateMe.LoginActivity;

/**
 * Das UserModel repräsentiert die Struktur eines Benutzerobjekts.
 * Es beinhaltet Eigenschaften wie Benutzername und E-Mail, die für die Verwaltung von Benutzerkonten verwendet werden.
 * Diese Klasse wird hauptsächlich für die Speicherung und Abfrage von Benutzerdaten in Firebase Firestore verwendet.
 */

public class UserModel {

    String username, email;

    public UserModel(){

    };
    public UserModel(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
