package WebAPI;

import java.sql.*;
import java.util.LinkedList;

import static java.lang.System.exit;

/**
 * SQLHandler's purpose is to provide functionality for connecting to and querying the database, throwing errors where
 * incorrect queries given to be handled by HTTPWebServer class
 *
 * written by MAL - Ma18533 - 1802882, jan 2020
 */
public class SQLHandler {
    private Connection connection = null; // Connection object to connect to server
    private Statement statement = null; // statement object to make the query to the connected server
    private String dbURL; // url of the server
    private String uName; // username to be provided or default used
    private String pwd; // password to be provided or default used

    /**
     * default no arg constructor for the Server
     */
   SQLHandler()
   {
       dbURL = "jdbc:mysql://localhost/carcollection";
       uName = "root";
       pwd = "ma216000";
   }

    /**
     * Three argument constructor for the server, containing socket, username, and password
     * @param url
     *      String, the socket address to connect to
     * @param uname
     *      String, the username to connect with
     * @param pass
     *      String, the password to connect with
     */
    SQLHandler(String url, String uname, String pass)
    {
        dbURL = url;
        uName = uname;
        pwd = pass;
    }

    /**
     * sqlConnect's main purpose is to connect to the server, or throw an error if connection error occurs
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    protected void sqlConnect () throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(dbURL, uName, pwd);
        statement = connection.createStatement();
    }

    /**
     * sqlClose's purpose is to close the server, or console log any errors
     */
    private void sqlClose()
    {
        try
        {
            connection.close();
            statement.close();
        }
        catch (SQLException e)
        {
            System.out.println("\nIssue closing connection");
            exit(1);
        }
    }

    /**
     * queryCar's purpose is to send a valid query for the carsURI, compiling Car object to create a JSON result string
     * to return
     * @param year
     *      String, the year to be queried, or 1 to exclude this term
     * @param priceFrom
     *      double, the priceFrom term to be queried, or negative to exclude this term
     * @param priceTo
     *      double, the price to term to be searched, or negative for this to be excluded
     * @param manufacturerID
     *      int, the id to be searched, or <=0 to be excluded
     * @return
     *      String, the formatted string version of the results
     * @throws MySQLException
     */
    public LinkedList<Car> queryCars(int year, double priceFrom, double priceTo, int manufacturerID) throws MySQLException
    {
        int hasResults = 0;

        // compile search statement
        String query = "SELECT * FROM CARS NATURAL JOIN manufacturers ";
        if (year >= 0 || priceFrom >=0 || priceTo >=0 || manufacturerID > 0)
        {
            boolean and = false;
            query += "WHERE ";
            if (year >= 0)
            {
                query += "year=" + year + " ";
                and = true;
            }
            if (priceFrom >= 0)
            {
                if (and) query += "AND ";
                query += "RetailPrice>=" + priceFrom + " ";
                and = true;
            }
            if (priceTo >= 0)
            {
                if (and) query += "AND ";
                query += "RetailPrice<=" + priceTo + " ";
                and = true;
            }
            if (manufacturerID > 0)
            {
                if (and) query += "AND ";
                query += "ManufacturerID=" + manufacturerID + " ";
            }
        }

        LinkedList<Car> result = new LinkedList<>();
        // fetch results, or throw error with invalid query
        try
        {
            sqlConnect();
            ResultSet rs = statement.executeQuery(query);
            System.out.println("/*---- Successful MySQL Query Made ----*/");
            System.out.println(query);
            while (rs.next())
            {
                result.push(new Car(rs.getInt("ModelID"), rs.getString("Name"),
                        rs.getString("Model"), rs.getString("BodyType"), rs.getInt("year"),
                        rs.getDouble("retailPrice")));
                hasResults +=1;
            }
            sqlClose();
        }
        catch (SQLException e)
        {
            System.out.println("/*---- Error with MySQL Query ----*/");
            System.out.println(query);
            throw new MySQLException(e.getMessage(), "SQL Query issue with '" + query + "'.");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("/*---- Error loading JDBC Driver ----*/");
            System.out.println("\nIssue with JDBC Driver");
        }
        return result;
    }

    /**
     * queryCarModel's purpose is to query a singular car, construct a Car object and return a JSON formatted string
     * of that object, or throw an error is invalid search
     * @param modelID
     *      int, the model IF to be searched
     * @return
     * @throws MySQLException
     */
    public LinkedList<Car> queryCarModel(int modelID) throws MySQLException
    {
        int hasResults = 0;

        // compile search statement
        String query = "SELECT * FROM CARS NATURAL JOIN manufacturers WHERE ModelID=" + modelID;

        // fetch results, or throw error with invalid query
        LinkedList<Car> result = new LinkedList<>();
        try
        {
            sqlConnect();
            ResultSet rs = statement.executeQuery(query);
            System.out.println("/*---- Successful MySQL Query Made ----*/");
            System.out.println(query);
            while (rs.next())
            {
                result.push(new Car(rs.getInt("ModelID"), rs.getString("Name"),
                        rs.getString("Model"), rs.getString("BodyType"), rs.getInt("year"),
                        rs.getFloat("retailPrice")));
                hasResults +=1;
            }
            sqlClose();
        }
        catch (SQLException e)
        {
            System.out.println("/*---- Error with MySQL Query ----*/");
            System.out.println(query);
            throw new MySQLException(e.getMessage(), "SQL Query issue with '" + query + "'.");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("/*---- Error loading JDBC Driver ----*/");
            System.out.println("\nIssue with JDBC Driver");
        }
        return result;
    }

    /**
     * insertCar's purpose is to insert a car into the database, constructing a car object of the result and returning
     * its string value
     * @param manID
     * @param model
     * @param body
     * @param year
     * @param retail
     * @return
     * @throws MySQLException
     */
    public LinkedList<Car> insertCar(int manID, String model, String body, int year, double retail) throws MySQLException
    {
        // compile search statement
        String query = "INSERT INTO cars (ManufacturerID, Model, BodyType, Year, RetailPrice) VALUES ("
                + manID +", \"" + model + "\", \"" + body + "\", " + year + ", " + retail + ")";

        // fetch results, or throw error with invalid query
        LinkedList<Car> result = new LinkedList<>();
        try
        {
            result = new LinkedList<>();
            sqlConnect();
            statement.executeUpdate(query);
            System.out.println("/*---- Successful MySQL Query Made ----*/");
            System.out.println(query);
            query = "SELECT * FROM cars  NATURAL JOIN manufacturers WHERE ManufacturerID=" + manID + " AND year="
                    + year + " AND Model=\"" + model + "\"";
            ResultSet rs = statement.executeQuery(query);
            System.out.println("/*---- Successful MySQL Query Made ----*/");
            System.out.println(query);
            while (rs.next())
            {
                result.push(new Car(rs.getInt("ModelID"), rs.getString("Name"),
                        rs.getString("Model"), rs.getString("BodyType"), rs.getInt("year"),
                        rs.getDouble("retailPrice")));
            }
            sqlClose();
        }
        catch (SQLException e)
        {
            System.out.println("/*---- Error with MySQL Query ----*/");
            System.out.println(query);
            throw new MySQLException(e.getMessage(), "Issue with SQL statement '" + query + "'.");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("/*---- Error loading JDBC Driver");
            System.out.println("\nIssue with JDBC Driver");
        }
        return result;
    }

    /**
     * queryUser's purpose is to provide sql query functionality for the users query, constructing owners and cars to
     * return a JSON formatted string of the result, or throw errors if incorrect sql query occurs
     * @param carCount
     * @return
     * @throws MySQLException
     */
    public LinkedList<Owner> queryUsers(int carCount) throws MySQLException
    {
        // compile search statement
        String query = "SELECT * FROM owners";
        // fetch results, or throw error with invalid query
        LinkedList<Owner> owners = new LinkedList<>();
        LinkedList<Owner> result = new LinkedList<>();
        try
        {
            // populates owners
            owners = new LinkedList<>();
            sqlConnect();
            ResultSet rs = statement.executeQuery(query);
            System.out.println("/*---- Successful MySQL Query Made ----*/");
            System.out.println(query);
            while (rs.next())
            {
                owners.push(new Owner(rs.getString("FullName"), rs.getString("PostCode"),
                        rs.getInt("OwnerID")));
            }

            // populates owners cars adding owner to result if cars owned # >= carCount
            for (Owner owner: owners)
            {
                query = "SELECT * from cars_owned NATURAL JOIN (SELECT * FROM cars NATURAL JOIN manufacturers) t WHERE "
                        + "ownerID=" + owner.getID();
                rs = statement.executeQuery(query);
                System.out.println("/*---- Successful MySQL Query Made ----*/");
                System.out.println(query);
                int count = 0;
                while (rs.next())
                {
                    owner.addCar(new OwnedCar(rs.getInt("ModelID"), rs.getString("Name"),
                            rs.getString("Model"), rs.getString("BodyType"), rs.getInt("year"),
                            rs.getDouble("retailPrice"), rs.getDouble("Purchase"), rs.getString("PlateNo")));
                    count++;
                }
                if (count == carCount || carCount < 0)
                {
                    result.push(owner);
                }
            }
            sqlClose();
        }
        catch (SQLException e)
        {
            System.out.println("/*---- Error with MySQL Query ----*/");
            System.out.println(query);
            throw new MySQLException(e.getMessage(), "SQL Query issue with '" + query + "'.");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("\nIssue with JDBC Driver");
        }
        return result;
    }

    public LinkedList<Manufacturer> queryManufacturers(String manufacturerName, int year) throws MySQLException
    {
        int hasResults = 0;

        // compile search statement
        String query = "SELECT * FROM manufacturers";

        // fetch manufacturers, or throw error with invalid query
        LinkedList<Manufacturer> manufacturers = new LinkedList<>();
        LinkedList<Manufacturer> result = new LinkedList<>();
        try
        {
            sqlConnect();
            // fetch manufacturers
            ResultSet rs = statement.executeQuery(query);
            System.out.println("/*---- Successful MySQL Query Made ----*/");
            System.out.println(query);
            while (rs.next())
            {
                manufacturers.push(new Manufacturer(rs.getString("Name"), rs.getString("Country"),
                        rs.getInt("ManufacturerID")));
                hasResults +=1;
            }

            // filter manufacturers by name or year and add to results
            for (Manufacturer man: manufacturers)
            {
                // adds cars to manufacturer
                if (manufacturerName.toLowerCase().equals(man.getName().toLowerCase()) || manufacturerName.equals(""))
                {
                    query = "SELECT * FROM manufacturers NATURAL JOIN cars WHERE name='" + man.getName() + "' ";
                    if (year >= 0)
                    {
                        query += "AND year=" + year;
                    }
                    System.out.println(query);
                    rs = statement.executeQuery(query);
                    System.out.println("/*---- Successful MySQL Query Made ----*/");
                    System.out.println(query);
                    while (rs.next())
                    {
                        man.addCar(new Car(rs.getInt("ModelID"), rs.getString("Name"),
                                rs.getString("Model"), rs.getString("BodyType"),
                                rs.getInt("Year"), rs.getDouble("RetailPrice")));
                    }
                    result.push(man);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("/*---- Error with MySQL Query ----*/");
            System.out.println(query);
            throw new MySQLException(e.getMessage(), "SQL Query issue with '" + query + "'.");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("/*---- Error loading JDBC Driver ----*/");
            System.out.println("\nIssue with JDBC Driver");
        }
        return result;
    }
}

