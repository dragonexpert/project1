import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;

public class QueryBuilder
{
    Connection connection;
    private String url;
    boolean whereClauseSet;
    boolean orderClauseSet;
    String sql;
    String columns;
    String fromTable;
    String whereClause;
    String orderClause;
    String limitClause;
    ArrayList<Object> whereColumnValues;
    ArrayList<String> columnType;
    String className;

    /**
     * Generic constructor.
     */
    public QueryBuilder(String className)
    {
        super();
        try
        {

            String databaseName = "postgres";
            String host = "postgres82322.c0rmfsc1zrep.us-east-2.rds.amazonaws.com";
            int port = 5432;
            url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

            String databaseUser = "postgres";
            String databasePassword = "password1234";
            connection = DriverManager.getConnection(url, databaseUser, databasePassword);
             System.out.println("Connection Success!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Connection Failure: " + url);
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.sql = "";
        this.columns = "";
        this.fromTable = "";
        this.whereClause = "";
        this.whereClauseSet = false;
        this.orderClauseSet = false;
        this.orderClause = "";
        this.limitClause = "";
        this.whereColumnValues = new ArrayList<>();
        this.columnType = new ArrayList<>();
        this.className = className;
    }

    public boolean isWhereClauseSet()
    {
        return whereClauseSet;
    }

    public void setWhereClauseSet(boolean whereClauseSet)
    {
        this.whereClauseSet = whereClauseSet;
    }

    public boolean isOrderClauseSet()
    {
        return orderClauseSet;
    }

    public void setOrderClauseSet(boolean orderClauseSet)
    {
        this.orderClauseSet = orderClauseSet;
    }

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    public void setColumns(String columns)
    {
        this.columns = columns;
    }

    public String getFromTable()
    {
        return fromTable;
    }

    public void setFromTable(String fromTable)
    {
        this.fromTable = fromTable;
    }

    public String getWhereClause()
    {
        return whereClause;
    }

    public void setWhereClause(String whereClause)
    {
        this.whereClause = whereClause;
    }

    public String getOrderClause()
    {
        return orderClause;
    }

    public void setOrderClause(String orderClause)
    {
        this.orderClause = orderClause;
    }

    public ArrayList<Object> getWhereColumnValues()
    {
        return whereColumnValues;
    }

    public void setWhereColumnValues(ArrayList<Object> whereColumnValues)
    {
        this.whereColumnValues = whereColumnValues;
    }

    public ArrayList<String> getColumnType()
    {
        return columnType;
    }

    public void setColumnType(ArrayList<String> columnType)
    {
        this.columnType = columnType;
    }

    /**
     * This allows you get columns separated by a comma.
     * @param columns CSV list of columns. * to get all columns.
     * @return QueryBuilder
     */
    public QueryBuilder getColumns(String columns)
    {
        this.columns = columns;
        return this;
    }

    /**
     * This allows you to do any number of column arguments where each column is a separate parameter.
     * @param columns Each individual column you are pulling.
     * @return QueryBuilder
     */
    public QueryBuilder getColumns(String... columns)
    {
        String comma = "";
        for(String c : columns)
        {
            this.columns += comma + c;
            comma = ", ";
        }
        return this;
    }

    /**
     * This selects all columns in the given table.
     * @return QueryBuilder
     */
    public QueryBuilder getColumns()
    {
        this.columns = "*";
        return this;
    }

    /**
     * This is used to determine which table to get data from.
     * @param table The name of the table.
     * @return QueryBuilder
     */
    public QueryBuilder fromTable(String table)
    {
        this.fromTable = table;
        return this;
    }

    /**
     * Adds a String column to the WHERE clause.
     * @param column The name of the column.
     * @param value The value of the column.
     * @param comparisonOperator The comparison to use. <,>,=,<=,>=</=,>
     * @return QueryBuilder
     */
    public QueryBuilder where(String column, Object value, String comparisonOperator)
    {
        if(!isWhereClauseSet())
        {
            this.whereClause = " WHERE ";
            setWhereClauseSet(true);
        }
        this.whereClause += " " + column + " " + comparisonOperator + " ? ";
        this.whereColumnValues.add(value);
        this.columnType.add("String");
        return this;
    }

    /**
     * Generates an ORDER BY clause
     * @param column The name of the column to sort.
     * @param order Sorting ordering.
     * @return QueryBuilder
     */
    public QueryBuilder order(String column, String order)
    {

        if(order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc"))
        {
            if(!this.orderClause.equals(""))
            {
                this.orderClause += ", ";
            }
            if(!isOrderClauseSet())
            {
                this.orderClause = " ORDER BY ";
                setOrderClauseSet(true);
            }
            this.orderClause += column + " " + order;
        }
        return this;
    }

    public QueryBuilder limit(int start, int rows)
    {
        if(this.limitClause.length() == 0)
        {
            this.limitClause = " LIMIT " + start + " , " + rows;
        }
        return this;
    }

    public void viewSQL()
    {
        sql = "SELECT " + this.columns + " FROM " + this.fromTable + this.whereClause + this.orderClause + this.limitClause;
        System.out.println("SQL: " + sql);
    }

    /**
     * This will return an ArrayList of results.
     * The objects are attempted to be converted to proper objects based on table name.
     * This should not be used for queries that do not return results.
     * Instead, use the executeOperation method.
     * @return ArrayList<Object>
     */
    public ArrayList<Object> executeQuery()
    {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        sql = "SELECT " + this.columns + " FROM " + this.fromTable + this.whereClause + this.orderClause + this.limitClause;

        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            int x = 1;
            for(Object column : whereColumnValues)
            {
                preparedStatement.setObject(x, column);
                ++x;
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<>();
            for(int i = 1; i <= columnCount; i++)
            {
                columnNames.add(resultSetMetaData.getColumnName(i));
            }
            objectArrayList = new ArrayList<>();
            while(resultSet.next())
            {
                Class annotatedClass = Class.forName(this.className);
                Object obj = new User();
                Constructor constructors[] = annotatedClass.getConstructors();

                System.out.println(annotatedClass);
                Field[] fields = annotatedClass.getDeclaredFields();
                for(Field field : fields)
                {
                    field.setAccessible(true);
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    for(Annotation annotation : annotations)
                    {
                        String[] annotationParts = annotation.toString().split(", ");
                        String[] pairs = annotationParts[3].split("=");
                        field.set(obj, resultSet.getObject(pairs[1].replace("\"", "")));
                    }
                }
                System.out.println(obj.toString());
                objectArrayList.add(obj);
            }
        }
        catch (SQLException | ClassNotFoundException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return objectArrayList;
    }
}
