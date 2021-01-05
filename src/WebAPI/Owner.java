package WebAPI;

import java.util.LinkedList;

/**
 * Owner's purpose is to contain member data variables required for an owner and override the toString method for
 * JSON formatted string
 *
 * written by MAL - Ma18533 - 1802882, jan 2020
 */
public class Owner {
    private int ID; // owners ID
    private String fullName; // their full name
    private String postCode; // postal code
    private LinkedList<OwnedCar> carsOwned; // list of Car objects that they own

    /**
     * default constructor for this data type, initializes data member variables
     * @param name
     * @param pc
     * @param ID
     */
    Owner(String name, String pc, int ID)
    {
        this.ID = ID;
        fullName = name;
        postCode = pc;
        carsOwned = new LinkedList<>();
    }

    public void addCar(OwnedCar car)
    {
        carsOwned.push(car);
    }

    public int carsOwned()
    {
        return carsOwned.size();
    }

    @Override
    public String toString()
    {
        String me =  "{\"id\": \"" + ID + "\", \"fullname\": \"" + fullName + "\", \"postCode\": \""
                + postCode + "\", \"cars\": [";
        int count = 0;
        for (OwnedCar car: carsOwned)
        {
            me += car;
            if (count != carsOwned.size() - 1) me += ", ";
            count +=1;
        }
        me += "]}";
        return me;
    }

    public int getID() {
        return ID;
    }
}
