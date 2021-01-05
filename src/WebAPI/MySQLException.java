package WebAPI;

public class MySQLException extends Exception{
    public MySQLException(String origMess, String myMess)
    {
        super(myMess + "\n" + origMess);
    }
}
