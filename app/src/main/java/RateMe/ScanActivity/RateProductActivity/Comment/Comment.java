package RateMe.ScanActivity.RateProductActivity.Comment;

/**
 * Die Klasse "Comment" repräsentiert ein Kommentarobjekt, das in der RateProduct-Aktivität verwendet wird.
 * Sie dient als Datenmodell für jeden Kommentar, der in der Firebase Firestore-Datenbank gespeichert wird.
 */

public class Comment {
    private String id;
    private String userName;
    private String text;
    private String userEmail;

    public Comment(String id, String userName, String text, String userEmail) {
        this.id = id;
        this.userName = userName;
        this.text = text;
        this.userEmail = userEmail;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmail() {
        return userEmail;
    }

    public void setEmail(String userEmail) { this.userEmail = userEmail;}
}
