package pandusland;

public class Pandus implements Comparable {
    public int id;
    public double longitude;
    public double latitude;
    public String address;
    public String type;
    public String comment;
    public String imagepath;
    public int user_id;
    public String category;

    public double distance;
    public int dist;

    public String file;
    public String anon;
    public String uname;

    public Pandus(int id, double longitude, double latitude, String address, String type, String comment, String imagepath, int user_id, String category) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.type = type;
        this.comment = comment;
        this.imagepath = imagepath;
        this.user_id = user_id;
        this.category = category;
    }

    @Override
    public int compareTo(Object another) {
        double distance = ((Pandus) another).distance;

        // ascending order
        return (int) (this.distance - distance);

        // descending order
        //return (int) (distance - this.distance);
    }
}
