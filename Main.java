import java.sql.Connection;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        ConnectionFactory connectionFactory = new ConnectionFactory("postgres82322.c0rmfsc1zrep.us-east-2.rds.amazonaws.com", "postgres", 5432, "postgres", "password1234");
        Connection connection = connectionFactory.createNewConnection();
        QueryBuilder queryBuilder = new QueryBuilder(connection, "User");
        queryBuilder = queryBuilder.getColumns("*").fromTable("users");
        queryBuilder.viewSQL();
        ArrayList<Object> userList;
        userList = queryBuilder.executeQuery();
        System.out.println(userList.size());

        System.out.println("\n\n\n");
        QueryBuilder queryBuilder1 = new QueryBuilder(connection, "Account");
        queryBuilder1 = queryBuilder1.getColumns("*").fromTable("accounts");
        queryBuilder1.viewSQL();
        queryBuilder1.executeQuery();

        QueryBuilder queryBuilder2 = new QueryBuilder(connection, "Account");
        queryBuilder2.updateTable("accounts").setColumn("balance", 367.89).setColumn("first_name", "Jane").where("id", 7, "=").viewSQL();
        queryBuilder2.executeOperation();

        System.out.println("\n\n\n");
        queryBuilder1.where("id", 7, "=");
        queryBuilder1.executeQuery();
    }
}