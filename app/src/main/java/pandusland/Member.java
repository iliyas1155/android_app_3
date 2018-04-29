package pandusland;

public class Member {
    private String id;
    private String sname;
    private String email;
    private int rating;

    public Member(String id, String sname, String email, int rating) {
        this.id = id;
        this.sname = sname;
        this.email = email;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getSname() {
        return sname;
    }

    public String getEmail() {
        return email;
    }

    public int getRating() { return rating; }

}