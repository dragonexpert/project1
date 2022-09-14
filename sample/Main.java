import java.sql.Connection;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        ConnectionFactory connectionFactory = new ConnectionFactory("HOST", "databaseName", "port", "databaseUser", "password");
        Connection connection = connectionFactory.createNewConnection();

        // Generate a list example
        QueryBuilder queryBuilder = new QueryBuilder(connection, "User");
        queryBuilder = queryBuilder.getColumns("*").fromTable("users");
        queryBuilder.viewSQL();
        ArrayList<Object> userList;
        userList = queryBuilder.executeQuery();
        System.out.println("Rows: " + userList.size());
        System.out.println(userList.toString());
        System.out.println("\n\n\n");

        // Another example using a different table
        QueryBuilder queryBuilder1 = new QueryBuilder(connection, "Account");
        queryBuilder1 = queryBuilder1.getColumns("*").fromTable("accounts");
        queryBuilder1.viewSQL();
        queryBuilder1.executeQuery();



        // Update query example
        QueryBuilder queryBuilder2 = new QueryBuilder(connection, "Account");
        queryBuilder2.updateTable("accounts").setColumn("balance", 367.89).setColumn("first_name", "Jane").where("id", 7, "=").viewSQL();
        queryBuilder2.executeOperation();

        System.out.println("\n\n\n");
        queryBuilder1.where("id", 7, "=");
        System.out.println(queryBuilder1.executeQuery().toString());

        // Drop column example
        //QueryBuilder queryBuilder3 = new QueryBuilder(connection, "Account");
        //queryBuilder3.alterTable("accounts").dropColumn("newColumn").executeOperation();
        //System.out.println("Operation performed");

        // Insert user example
        QueryBuilder queryBuilder4 = new QueryBuilder(connection, "User");
        queryBuilder4.insertTable("users").setColumn("username", "newUser").setColumn("password", "newPassword").setColumn("email", "newEmail@example.com").executeOperation();

        // Delete user example
        QueryBuilder queryBuilder5 = new QueryBuilder(connection, "User");
        queryBuilder5.deleteTable("users").where("username", "newUser", "=").executeOperation();

        // Get a single record example
        QueryBuilder queryBuilder6 = new QueryBuilder(connection, "User");
        User user = (User) queryBuilder6.getColumns().fromTable("users").getOne(3);
        System.out.println(user.toString());
    }
}
