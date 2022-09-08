import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory
{

    private String host;

    private String databaseName;

    private int port;

    private String databaseUser;

    private String databasePassword;

    public ConnectionFactory(String host, String databaseName, int port, String databaseUser, String databasePassword)
    {
        this.host = host;
        this.databaseName = databaseName;
        this.port = port;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    public Connection createNewConnection()
    {
        String url = "";
        try
        {
            url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

            Connection connection = DriverManager.getConnection(url, databaseUser, databasePassword);
            System.out.println("Connection Success!");
            return connection;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Connection Failure: " + url);
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
