import java.sql.Connection;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws IllegalAccessException
    {
        ConnectionFactory connectionFactory = new ConnectionFactory("postgres82322.c0rmfsc1zrep.us-east-2.rds.amazonaws.com", "postgres", 5432, "postgres", "password1234");
        Connection connection = connectionFactory.createNewConnection();
        QueryBuilder queryBuilder = new QueryBuilder(connection, "User");
        queryBuilder = queryBuilder.getColumns("*").fromTable("users");
        queryBuilder.viewSQL();
        ArrayList<Object> userList = new ArrayList<>();
        userList = queryBuilder.executeQuery();
        System.out.println(userList.size());

        System.out.println("\n\n\n");
        QueryBuilder queryBuilder1 = new QueryBuilder(connection, "Account");
        queryBuilder1 = queryBuilder1.getColumns("*").fromTable("accounts");
        queryBuilder1.viewSQL();
        queryBuilder1.executeQuery();
    }
}