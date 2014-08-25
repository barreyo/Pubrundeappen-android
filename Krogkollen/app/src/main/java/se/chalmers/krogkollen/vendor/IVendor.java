package se.chalmers.krogkollen.vendor;

/**
 * Created by Jonathan Nilsfors on 2014-08-25.
 */
public interface IVendor {

    /**
     *
     * @return the name of the vendor
     */
    public String getName();

    /**
     *
     * @return the latitude of the vendor
     */
    public double getLatitude();

    /**
     *
     * @return the longitude of the vendor
     */
    public double getLongitude();
}
