package pl.edu.uwr.projekt.musicreviewer2;

public class Review {
    private final String artist;
    private final String title;
    private final String year;
    private final String genre;
    private final String type;
    private final String score;
    private final String review;
    private final String account;

    public Review(String artist, String title, String year, String genre,
                  String score, String type, String review, String account){
        this.artist = artist;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.score = score;
        this.type = type;
        this.review = review;
        this.account = account;
    }

    public String getArtist() { return artist; }

    public String getTitle() { return title; }

    public String getYear() { return year; }

    public String getGenre() { return genre; }

    public String getType() { return type; }

    public String getScore() { return score; }

    public String getReview() { return review; }

    public String getAccount() {return account; }
}
