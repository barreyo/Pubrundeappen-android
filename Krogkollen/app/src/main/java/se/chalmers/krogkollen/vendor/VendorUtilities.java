package se.chalmers.krogkollen.vendor;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.krogkollen.backend.BackendHandler;
import se.chalmers.krogkollen.backend.BackendNotInitializedException;
import se.chalmers.krogkollen.backend.NoBackendAccessException;

/**
 * Created by Jonathan Nilsfors on 2014-08-25.
 */
public class VendorUtilities {
    List<IVendor> vendorList = new ArrayList<IVendor>();
    private static VendorUtilities instance = null;
    private VendorUtilities(){

    }

    public static VendorUtilities getInstance(){
        if(instance == null){
            instance = new VendorUtilities();
        }
        return instance;
    }

    public void loadVendorList() throws NoBackendAccessException, BackendNotInitializedException {

        vendorList = BackendHandler.getInstance().getAllVendors();
    }

    public List<IVendor> getVendorList() {
        return vendorList;
    }
}
