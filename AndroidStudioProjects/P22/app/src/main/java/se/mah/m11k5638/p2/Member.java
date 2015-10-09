package se.mah.m11k5638.p2;

/**
 * Created by Jonas on 08/10/15.
 */
public class Member {

    private String name;
    private double longitude, latitude;

    public Member(String name, double longitude, double latitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
