package se.chalmers.krogkollen.vendor;

/**
 * Created by Jonathan Nilsfors on 2014-08-25.
 */
public class Vendor implements IVendor {

    private String name;
    private double longitude;
    private double latitude;
    public Vendor(String name, double latitude, double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public double getLongitude() {
        return this.longitude;
    }

}
