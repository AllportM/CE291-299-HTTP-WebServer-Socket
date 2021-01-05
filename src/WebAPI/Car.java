package WebAPI;

/**
 * Car's purpose is to provide a json printable entity object so that HTTPServer can send json string representation
 * of the car results. There will be two different types of cars, one for the manufacturers car model, and a second
 * for an owned car by a person with the addition of registration plate and purchase price
 *
 * created by Mal - ma18533, ma1802882, jan 2020
 */
public class Car {
    int id; // car id
    String manufacturerName; // manufacturers name
    String model; // model of the car
    String bodyType; // body type of the car
    int year; // year car was made
    double retailPrice; // retail price at time of manufacture

    /**
     * Constructor for a standard car
     * @params
     *      id, int, the id for this car model
     *      manName, String, the manufacturers name
     *      mod, String, the model of this car
     *      yr, int, the year the car was made
     *      rp, double, the cost of the car at time of manufacture
     * @returns
     *      none
     */
    Car(int id, String manName, String mod, String bod, int yr, double rp)
    {
        this.id = id;
        manufacturerName = manName;
        model = mod;
        bodyType = bod;
        year = yr;
        retailPrice = rp;
    }

    // Overrides the toString method to print valid json format string
    @Override
    public String toString()
    {
        return "{\"id\": \"" + id + "\", \"manufacturerName\": \"" + manufacturerName + "\", "
                + "\"model\": \"" + model + "\", \"bodyType\": \"" + bodyType + "\", \"year\": "
                + year + ", \"retailPrice\": " + retailPrice + "}";
    }
}
