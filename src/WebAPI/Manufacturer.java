package WebAPI;

import java.util.LinkedList;

/**
 * Manufacturer's purpose is to provide object representation of a manufacturer result, containing member
 * variables for this aswell as a list of car models they manufacturer.
 * toString is overrided to provide JSON formatted output
 *
 * created by Mal - Ma18522 - 1802882, Jan 2020
 */
public class Manufacturer {
    private String name; // name of manufacturer
    private String country; // country of origin
    private int id; // manufacturer's id
    private LinkedList<Car> cars; // linked list of cars that they manufacture

    /**
     * Default constructor for this type, instantiates variables
     * @param name
     *      String, name of manufacturer
     * @param country
     *      String, country of origin
     * @param id
     *      int, manufacturer's id
     */
    Manufacturer(String name, String country, int id)
    {
        this.name = name;
        this.country = country;
        this.id = id;
        cars = new LinkedList<>();
    }

    /**
     * Simply adds a car object to the manufacturers member list
     * @param car
     *      Car, car object to be added
     */
    void addCar(Car car)
    {
        cars.push(car);
    }

    /**
     * Returns manufacturer's name
     * @return
     *      String, name
     */
    String getName() {
        return name;
    }

    /**
     * returns the number of cars the manufacturer makes
     * @return
     *      int, length of car list
     */
    int carCount()
    {
        return cars.size();
    }

    /**
     * Overrides the toString method into a JSON formattable string
     */
    @Override
    public String toString()
    {
        String me = "{\"id\": \"" + id + "\", \"name\": \"" + name + "\", \"country\": "
                + "\"" + country + "\", \"cars\": [";
        int count = 0;
        for (Car car: cars)
        {
            me += car;
            if (count != cars.size() -1) me += ", ";
            count++;
        }
        me += "]}";
        return me;
    }
}
