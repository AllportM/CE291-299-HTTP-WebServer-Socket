package WebAPI;

/**
 * Main's function is to simple start the server
 */

public class Main {
    public static void main(String[] args) throws HTTPWebServerException{
        HTTPWebServer serv = new HTTPWebServer();
        serv.start();
    }
}
