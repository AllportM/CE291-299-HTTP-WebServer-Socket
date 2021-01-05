package WebAPI;

import org.junit.Test;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class SQLHandlerTest {
    HttpURLConnection connection;
    String localHost = "http://127.0.0.1:8080";

    @Test
    public void testQueryCarsQuery1() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.queryCars(-1, -1, -1, 0);
        assertTrue(result.size() > 0);
        System.out.println("\n/** SQL Cars Test Query 1 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test
    public void testQueryCarsQuery2() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.queryCars(-1, 200000, -1, 0);
        assertTrue(result.size() > 0);
        System.out.println("\n/** SQL Cars Test Query 2 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test
    public void testQueryCarsQuery3() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.queryCars(-1, 200000, -1, 1);
        assertFalse(result.size() > 0);
        System.out.println("/** SQL Cars Test Query 3 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test
    public void testQueryCarModel1() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.queryCarModel(6);
        assertFalse(result.size() <= 0);
        System.out.println("/** SQL CarModel Test Query 1 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test
    public void testQueryCarModel2() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.queryCarModel(20);
        assertFalse(result.size() > 0);
        System.out.println("/** SQLCarModel  Test Query 2 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test
    public void testQueryCarModel3() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.queryCarModel(-1);
        assertFalse(result.size() > 0);
        System.out.println("/** SQLCarModel  Test Query 3 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test
    public void testQueryInsertCar1()
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = new LinkedList<>();
        try
        {
            result = sh.insertCar(1, "Test", "Test", 2100, 1);
        }
        catch (MySQLException e)
        {
            System.out.println(e.getMessage());
        }
        assertFalse(result.size() <= 0);
        System.out.println("/** SQL InsertCar Test Query 1 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test (expected = MySQLException.class)
    public void testQueryInsertCar2() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.insertCar(0, "a900", "Test", 2100, 1);
        assertFalse(result.size() <= 0);
        System.out.println("/** SQL InsertCar Test Query 2 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test (expected = MySQLException.class)
    public void testQueryInsertCar3() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Car> result = sh.insertCar(8, "a900", "Test", 2100, 1);
        assertFalse(result.size() <= 0);
        System.out.println("/** SQL InsertCar Test Query 3 **/");
        for (Car car: result)
        {
            System.out.println("\n" + car);
        }
    }

    @Test
    public void testQueryUsersQuery1() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Owner> result = sh.queryUsers(-1);
        assertTrue(result.size() == 5);
        System.out.println("\n/** SQL User Test Query 1 **/\n[");
        int count = 0;
        for (Owner owner: result)
        {
            System.out.println(owner);
            if (count != result.size()-1) System.out.println(", ");
            count++;
        }
        System.out.println("]");
    }

    @Test
    public void testQueryUsersQuery2() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Owner> result = sh.queryUsers(3);
        assertTrue(result.size() == 2);
        System.out.println("\n/** SQL User Test Query 2 **/\n[");
        int count = 0;
        for (Owner owner: result)
        {
            System.out.println(owner);
            if (count != result.size()-1) System.out.println(", ");
            count++;
        }
        System.out.println("]");
    }

    @Test
    public void testQueryUsersQuery3() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Owner> result = sh.queryUsers(8);
        assertTrue(result.size() == 0);
        System.out.println("\n/** SQL User Test Query 1 **/\n[");
        int count = 0;
        for (Owner owner: result)
        {
            System.out.println(owner);
            if (count != result.size()-1) System.out.println(", ");
            count++;
        }
        System.out.println("]");
    }

    @Test
    public void testQueryManufacturersQuery1() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Manufacturer> result = sh.queryManufacturers("", -1);
        assertTrue(result.size() == 7);
        System.out.println("\n/** SQL Manufacturer Test Query 1 **/\n[");
        int count = 0;
        for (Manufacturer man: result)
        {
            System.out.println(man);
            if (count != result.size()-1) System.out.println(", ");
            count++;
        }
        System.out.println("]");
    }

    @Test
    public void testQueryManufacturersQuery2() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Manufacturer> result = sh.queryManufacturers("Audi", -1);
        assertTrue(result.size() == 1);
        assertTrue(result.getFirst().carCount() == 6);
        System.out.println("\n/** SQL Manufacturer Test Query 2 **/\n[");
        int count = 0;
        for (Manufacturer man: result)
        {
            System.out.println(man);
            if (count != result.size()-1) System.out.println(", ");
            count++;
        }
        System.out.println("]");
    }
    @Test
    public void testQueryManufacturersQuery3() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Manufacturer> result = sh.queryManufacturers("Bentley", 2019);
        assertTrue(result.size() == 1);
        assertTrue(result.getFirst().carCount() == 1);
        System.out.println("\n/** SQL Manufacturer Test Query 2 **/\n[");
        int count = 0;
        for (Manufacturer man: result)
        {
            System.out.println(man);
            if (count != result.size()-1) System.out.println(", ");
            count++;
        }
        System.out.println("]");
    }
    @Test
    public void testQueryManufacturersQuery4() throws MySQLException
    {
        SQLHandler sh = new SQLHandler();
        LinkedList<Manufacturer> result = sh.queryManufacturers("Furang", 1);
        assertTrue(result.size() == 0);
        System.out.println("\n/** SQL Manufacturer Test Query 2 **/\n[");
        int count = 0;
        for (Manufacturer man: result)
        {
            System.out.println(man);
            if (count != result.size()-1) System.out.println(", ");
            count++;
        }
        System.out.println("]");
    }

    @Test (expected = SQLException.class)
    public void testQueryCarsExcConnectionIssue() throws Exception
    {
        SQLHandler sh = new SQLHandler("htht", "hehe", "hehe");
        sh.sqlConnect();
    }
}