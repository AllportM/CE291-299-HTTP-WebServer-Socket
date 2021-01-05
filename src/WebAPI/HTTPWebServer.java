package WebAPI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * HTTPWebServer's purpose is to provide a hosting service as an API end point calling SQLHandler to retrieve json
 * string formatted results from the clients query and send them back as a HTTP response. Queries are seperated
 * by use of detecting the URI, and input validation is done backend on the parameters given. If any incorrect
 * request is made, specific error messages are thrown and given as response to front end
 */
public class HTTPWebServer {

    private HttpServer server; // the server

    /**
     * Default constructor for HTTPWebServer. Binds socket to http server and attaches a handler to handle
     * http queries
     * @params
     *      None
     * @returns
     *      None
     * @throws HTTPWebServerException
     *      would need to be handled on a deeper project through main
     */
    HTTPWebServer() throws HTTPWebServerException
    {
        try
        {
            InetSocketAddress addy = new InetSocketAddress("127.0.0.1", 8080);
            server = HttpServer.create(addy, 0);
            server.createContext("/", new MyHttpListener());
            server.setExecutor(null);
        }
        catch(IOException e)
        {
            throw new HTTPWebServerException("Binding to address 127.0.0.1:80");
        }
    }

    /**
     * starts the server, no params or args
     */
    public void start()
    {
        server.start();
        System.out.println("\n/** HTTP Server Running on 127.0.0.1:8080 **/");
    }

    /**
     * stops the server, no params or args
     */
    public void stop()
    {
        server.stop(0);
    }

    /**
     * MyHttpListener's purpose to handle http requests with information stored in a HttpExchange object
     * throwing error codes back to the requestee if any invalid inputs/requests are made, or responding with
     * valid queries as json objects
     */
    class MyHttpListener implements HttpHandler
    {
        /**
         * overrides the default handle method, detecting the request URI's, performs input validation on the
         * parameters given, and responds to specific queries through use of sendQuery function/s
         * @param ex
         *      ex, HttpExchange object containing http request details
         */
        @Override
        public void handle(HttpExchange ex)
        {
            Headers heads = ex.getRequestHeaders();
            URI uri = ex.getRequestURI();

            // debugging purposes, prints request to console
            System.out.println("\n/** Successful http request made **/");
            System.out.println("\n/** request type **/");
            System.out.println(ex.getRequestMethod());
            System.out.println("\n/** Headers **/");
            heads.entrySet().forEach(System.out::println);
            System.out.println("\n/** request **/");
            System.out.println(uri.getQuery());
            System.out.println("\n/** uri **/");
            System.out.println(uri.getPath());

            // creates a map of all of the key/value request pairs given & checks for invalid inputs
            Map<String, String> requests = new TreeMap<>();
            String errorMessage = "";
            if (uri.getQuery() != null)
            {
                String[] requestsStrings = uri.getQuery().split("&");
                for (String req : requestsStrings) {
                    String[] split = req.split("=");
                    requests.put(split[0], split[1]);
                }
                if (requests.size() > 0)
                {
                    for (Map.Entry<String, String> entry : requests.entrySet())
                    {
                        switch (entry.getKey())
                        {
                            case "year":
                            case "manufacturerID":
                            case "modelID":
                            case "carCount":
                                if (!(isInt(entry.getValue()))) addError(errorMessage, entry);
                                break;
                            case "priceFrom":
                            case "priceTo":
                            case "retailPrice":
                                if (!(isDbl(entry.getValue()))) addError(errorMessage, entry);
                                break;
                            case "model":
                            case "body":
                            case "manufacturerName":
                                if (containsEscapeChar(entry.getValue())) addError(errorMessage, entry);
                                break;
                            default:
                                errorMessage += "No such key for '" + entry.getKey() + "=" + entry.getValue()
                                        + "'\n";
                                break;
                        }
                    }
                }
            }

            if (!(errorMessage.equals("")))
            {
                sendResponse(ex, 400, errorMessage);
                return;
            }

            // filters URI and sends responses for the specific URI's
            try
            {
                SQLHandler sql = new SQLHandler();
                // sends response for root URI/java/css files
                String path = uri.getPath();
                if (path.equals("/css/myStyle.css") || path.equals("/js/webAPI.js") || path.equals("/"))
                {
                    if (path.equals("/")) path = "/index.html";
                    sendResponse(ex, 200,path, 2);
                    return;
                }

                // send 400 error if incorrect request type
                if (heads.get("Content-Type") == null || !heads.get("Content-Type").get(0).equals("application/json"))
                {
                    sendResponse(ex, 400, "Incorrect header - Content-Type, '"
                            + heads.get("Content-Type") + "'.");
                    return;
                }

                /*
                 * Cars URI response
                 */
                if (uri.getPath().equals("/cars"))
                {

                    /*
                     * response for GET method, being get results
                     */
                    if (ex.getRequestMethod().equals("GET"))
                    {
                        // returns error if too many request fields
                        if (requests.size() > 4) {
                            errorMessage += "Too many requests\n";
                            for (Map.Entry<String, String> entry : requests.entrySet())
                            {
                                errorMessage += entry.getKey() + "=" + entry.getValue() + "\n";
                            }
                            sendResponse(ex, 400, errorMessage);
                            return;
                        }
                        // assigns parameters from request or default values
                        int year = -1;
                        double priceFrom = -1;
                        double priceTo = -1;
                        int manufacturerID = -1;
                        for (Map.Entry<String, String> entry : requests.entrySet()) {
                            switch (entry.getKey()) {
                                case "year":
                                    year = Integer.parseInt(entry.getValue());
                                    break;
                                case "priceFrom":
                                    priceFrom = Double.parseDouble(entry.getValue());
                                    break;
                                case "priceTo":
                                    priceTo = Double.parseDouble(entry.getValue());
                                    break;
                                case "manufacturerID":
                                    manufacturerID = Integer.parseInt(entry.getValue());
                                    break;
                            }
                        }
                        // query and construct result string
                        LinkedList<Car> results = sql.queryCars(year, priceFrom, priceTo, manufacturerID);
                        String resultStr = "";
                        resultStr += "[";
                        int count = 0;
                        for (Car car: results)
                        {
                            resultStr += car;
                            if (count != results.size()-1) resultStr += ",";
                            count++;
                        }
                        resultStr += "]";
                        sendResponse(ex, 200, resultStr);
                    }

                    /*
                     * response for PUT method, being add results and insert into DB
                     */
                    else if (ex.getRequestMethod().equals("PUT"))
                    {
                        // returns error if too many request fields
                        if (requests.size() != 5)
                        {
                            errorMessage += "Too few requests\n";
                            for (Map.Entry<String, String> entry : requests.entrySet())
                            {
                                errorMessage += entry.getKey() + "=" + entry.getValue() + "\n";
                            }
                            errorMessage += "4 required";
                            sendResponse(ex, 400, errorMessage);
                            return;
                        }
                        // assigns parameters from request or default values
                        int year = -1;
                        double retailPrice = -1;
                        String model = "";
                        int manufacturerID = -1;
                        String bodyType = "";
                        for (Map.Entry<String, String> entry : requests.entrySet())
                        {
                            switch (entry.getKey()) {
                                case "year":
                                    year = Integer.parseInt(entry.getValue());
                                    break;
                                case "retailPrice":
                                    retailPrice = Double.parseDouble(entry.getValue());
                                    break;
                                case "manufacturerID":
                                    manufacturerID = Integer.parseInt(entry.getValue());
                                    break;
                                case "model":
                                    model = entry.getValue();
                                    break;
                                case "body":
                                    bodyType = entry.getValue();
                                    break;
                            }
                        }
                        //query and construct result string
                        LinkedList<Car> results = sql.insertCar(manufacturerID, model, bodyType, year, retailPrice);
                        String resultStr = "";
                        resultStr += "[";
                        int count = 0;
                        for (Car car: results)
                        {
                            resultStr += car;
                            if (count != results.size()-1) resultStr += ",";
                            count++;
                        }
                        resultStr += "]";
                        sendResponse(ex, 200, resultStr);
                    }
                }

                /*
                 * Car URI for a singular car given modelID given
                 */
                if (uri.getPath().equals("/car") && ex.getRequestMethod().equals("GET"))
                {
                    // returns error if too many request fields
                    if (requests.size() != 1) {
                        errorMessage += "Ivalid requests\n";
                        for (Map.Entry<String, String> entry : requests.entrySet())
                        {
                            errorMessage += entry.getKey() + "=" + entry.getValue() + "\n";
                        }
                        sendResponse(ex, 400, errorMessage);
                        return;
                    }
                    // attains results and sends response
                    int modelId = Integer.parseInt(requests.get("modelID"));
                    LinkedList<Car> results = sql.queryCarModel(modelId);
                    String resultStr = "";
                    resultStr += "[";
                    int count = 0;
                    for (Car car: results)
                    {
                        resultStr += car;
                        if (count != results.size()-1) resultStr += ",";
                        count++;
                    }
                    resultStr += "]";
                    sendResponse(ex, 200, resultStr);
                }


                /*
                 * Users URI response
                 */
                if (uri.getPath().equals("/users") && ex.getRequestMethod().equals("GET"))
                {
                    String req = requests.get("carCount");
                    // returns error if too many request fields
                    if (requests.size() > 1 && req == null) {
                        errorMessage += "Ivalid requests\n";
                        for (Map.Entry<String, String> entry : requests.entrySet())
                        {
                            errorMessage += entry.getKey() + "=" + entry.getValue() + "\n";
                        }
                        sendResponse(ex, 400, errorMessage);
                        return;
                    }
                    int carCount = (requests.size() > 0)? Integer.parseInt(requests.get("carCount")): -1;
                    LinkedList<Owner> results = sql.queryUsers(carCount);
                    String resultStr = "";
                    resultStr += "[";
                    int count = 0;
                    for (Owner owner: results)
                    {
                        resultStr += owner;
                        if (count != results.size()-1) resultStr += ",";
                        count++;
                    }
                    resultStr += "]";
                    sendResponse(ex, 200, resultStr);
                }

                /*
                 * Manufacturers URI response
                 */
                if (uri.getPath().equals("/manufacturer") && ex.getRequestMethod().equals("GET"))
                {
                    // returns error if too many request fields
                    if (requests.size() > 2 && (requests.get("manufacturerName") == null && requests.get("year") == null))
                    {
                        errorMessage += "Ivalid requests\n";
                        for (Map.Entry<String, String> entry : requests.entrySet())
                        {
                            errorMessage += entry.getKey() + "=" + entry.getValue() + "\n";
                        }
                        sendResponse(ex, 400, errorMessage);
                        return;
                    }
                    int year = (requests.get("year") != null)? Integer.parseInt(requests.get("year")): -1;
                    String manufacturerName = "";
                    if (requests.get("manufacturerName") != null)
                    {
                        char[] cs =  requests.get("manufacturerName").toCharArray();
                        for (char c: cs)
                        {
                            manufacturerName += Character.toLowerCase(c);
                        }
                    }
                    LinkedList<Manufacturer> results = sql.queryManufacturers(manufacturerName, year);
                    String resultStr = "";
                    resultStr += "[";
                    int count = 0;
                    for (Manufacturer manufacturer: results)
                    {
                        resultStr += manufacturer;
                        if (count != results.size()-1) resultStr += ",";
                        count++;
                    }
                    resultStr += "]";
                    sendResponse(ex, 200, resultStr);
                }

                errorMessage += "Resource not found '" + uri.getPath() + "'\n";
                for (Map.Entry<String, String> entry : requests.entrySet())
                {
                    errorMessage += entry.getKey() + "=" + entry.getValue() + "\n";
                }
                sendResponse(ex, 400, errorMessage);
            }
            catch (MySQLException e)
            {
                sendResponse(ex, 500, "Server error - invalid SQL query\n" + e.getMessage());
            }
        }

        /**
         * sendResponse's purpose is to send a response to request made, being either an error or a json formatted
         * string. This version of the response is for either error, or application/json responses
         * @param ex
         *      HttpExchange object, object containing request and response access methods
         * @param type
         *      the HTTP status code to be replied with
         * @param response
         *      the response to be send
         */
        private void sendResponse(HttpExchange ex, int type, String response)
        {
            try
            {
                Headers heads = ex.getResponseHeaders();
                if (type == 200) heads.add("Content-Type", "application/json");
                else heads.add("Error-Message", response);
                ex.sendResponseHeaders(type, response.getBytes().length);
                OutputStream os = ex.getResponseBody();
                os.write(response.getBytes());
                os.close();
                System.out.println("/*---- Successful response to client:");
                System.out.println("/*---- Headers ----*/");
                for (Map.Entry<String, List<String> > entry: heads.entrySet())
                {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
                System.out.println("/*---- Response ----*/");
                System.out.println(response);
            }
            catch (IOException ignore){}
        }

        /**
         * The overloaded sendResponse's purpose is for text/html responses i.e index.html, style.css files
         * @param ex
         *      HttpExchange object, object containing request and response access methods
         * @param type
         *      the HTTP status code to be replied with
         * @param response
         *      the response to be send
         * @param ind
         *      just used to oveer an alternative method
         */
        private void sendResponse(HttpExchange ex, int type, String response, int ind)
        {
            try
            {
                // adds headers
                Headers heads = ex.getResponseHeaders();
                heads.add("Content-Type", "text/html");
                File file = new File("." + response);
                FileInputStream index = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(index));
                String respond = "";
                String line;
                while ((line = br.readLine()) != null)
                {
                    respond += line;
                }
                ex.sendResponseHeaders(type, respond.getBytes().length);
                OutputStream os = ex.getResponseBody();
                os.write(respond.getBytes());
                os.close();
                System.out.println("/*---- Successful response to client:");
                System.out.println("/*---- Headers ----*/");
                for (Map.Entry<String, List<String> > entry: heads.entrySet())
                {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
                System.out.println("/*---- Response ----*/");
                System.out.println(response);
            }
            catch (IOException ignore){}
        }

        /**
         * convertInt's main purpose is to return an integer from a string, or null, used for input validation
         * @param intStr
         *      number to be checked
         * @return
         *      boolean, true if the string input is a valid int, false otherwise
         */
        private boolean isInt(String intStr)
        {
            try
            {
                Integer x = Integer.parseInt(intStr);
                return true;
            }
            catch (NumberFormatException ignore)
            {
                return false;
            }
        }

        /**
         * convertDouble's main purpose is to return an integer from a string, or null
         * @param dbl
         *      String, string to be checked
         * @return
         *      boolean, true if string is a valid double, false otherwise
         */
        private boolean isDbl(String dbl)
        {
            try
            {
                Double x = Double.parseDouble(dbl);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        /**
         * Check string for SQL escape characters (prevent injection)
         * @param test
         *      String, string to be checked
         * @return
         *      boolean, true if any characters are mysql escape characters, false otherwise
         */
        private boolean containsEscapeChar(String test)
        {
            char[] chars = test.toCharArray();
            for (int i = 0; i < chars.length; i++)
            {
                if (chars[i] == '\\' && i != chars.length && (chars[i+1] == 'b' || chars[i+1] == 'n'
                || chars[i+1] == 'r' || chars[i+1] == 't' || chars[i+1] == 't' || chars[i+1] == '0')
                || chars[i] == ';' || chars[i] == '\'' || chars[i] == '\"')
                    return true;
                if (chars[i] == '\\') return true;
            }
            return false;
        }

        /**
         * addError's main purpose is to take a map entry value and add it to the string in formatted way
         * @param message
         *      String, string for map entry to be added to
         * @param entry
         *      String, string with entry added
         */
        private void addError(String message, Map.Entry<String, String> entry)
        {
            message += "Invalid input for '"
                    + entry.getKey() + "=" + entry.getValue() + "'\n";
        }
    }
}
