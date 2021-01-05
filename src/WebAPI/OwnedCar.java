package WebAPI;

/**
 * OwnedCar's purpose is an extension of the information stored within the Car class, containing the addition
 * of purchase price and plate number
 *
 * written by Mal - Ma18533 - 1802882, jan 2020
 */
public class OwnedCar extends Car{
    double purchasePrice; // purchase price of car
    String plate; // registration plate number

    /**
     * default constructor for this type of owned car, instantiates member variables by calling super and allocating
     * itself
     *
     * params = member variables to be initialized
     * @param id
     * @param manName
     * @param mod
     * @param bod
     * @param yr
     * @param rp
     * @param pp
     * @param plate
     */
    public OwnedCar(int id, String manName, String mod, String bod, int yr, double rp, double pp, String plate)
    {
        super(id, manName, mod, bod, yr, rp);
        purchasePrice = pp;
        this.plate = plate;
    }

    @Override
    public String toString()
    {
        return "{\"id\": \"" + id + "\", \"plateNumber\": \"" + plate + "\", \"manufacturerName\": "
                + "\"" + manufacturerName + "\", \"model\": \"" + model + "\", \"bodyType\": \""
                + bodyType + "\", \"year\": " + year + ", \"purchasePrice\": " + purchasePrice
                + ", \"retailPrice\": " + String.format("%.2f", retailPrice) + "}";
    }
}
