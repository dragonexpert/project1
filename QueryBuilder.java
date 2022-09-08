import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

// Alter column syntax : Alter table tableName Alter column columnName TYPE type
public class QueryBuilder
{
    private final Connection connection;
    private boolean whereClauseSet;
    private boolean orderClauseSet;
    private String sql;
    private String columns;
    private String fromTable;
    private String whereClause;
    private String orderClause;
    private String limitClause;
    private ArrayList<Object> whereColumnValues;
    private ArrayList<String> columnType;
    private String className;
    private HashMap<String, Object> updateFields;
    private String queryType;
    private ResultSetMetaData resultSetMetaData;


    /**
     * Generic constructor.
     */
    public QueryBuilder(Connection connection, String className)
    {
        super();
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
        this.connection = connection;
        this.updateFields = new HashMap<>();
        this.queryType = "select";
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

    public HashMap<String, Object> getUpdateFields()
    {
        return updateFields;
    }

    public void setUpdateFields(HashMap<String, Object> updateFields)
    {
        this.updateFields = updateFields;
    }

    public String getQueryType()
    {
        return queryType;
    }

    public void setQueryType(String queryType)
    {
        this.queryType = queryType;
    }

    public ResultSetMetaData getResultSetMetaData()
    {
        return resultSetMetaData;
    }

    public void setResultSetMetaData(ResultSetMetaData resultSetMetaData)
    {
        this.resultSetMetaData = resultSetMetaData;
    }

    /**
     * This allows you get columns separated by a comma.
     * @param columns CSV list of columns. * to get all columns.
     * @return QueryBuilder
     */
    public QueryBuilder getColumns(String columns)
    {
        this.columns = columns;
        setQueryType("select");
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
        setQueryType("select");
        return this;
    }

    /**
     * This selects all columns in the given table.
     * @return QueryBuilder
     */
    public QueryBuilder getColumns()
    {
        this.columns = "*";
        setQueryType("select");
        return this;
    }

    // Generates the SQL for select statements.
    private void selectQuery()
    {
        sql = "SELECT " + this.columns + " FROM " + this.fromTable + this.whereClause + this.orderClause + this.limitClause;
        setSql(sql);
    }

    // Generates the SQL for update queries.
    private void updateQuery()
    {
        String sql = "UPDATE " + getFromTable() + " SET ";
        String comma = "";
        for(String key : updateFields.keySet())
        {
            sql += comma + key + " = ?";
            comma = ", ";
        }
        sql += getWhereClause();
        setSql(sql);
    }

    // Generates the SQL for insert queries
    private void insertQuery()
    {
        String sql = "INSERT INTO " + getFromTable() + "( ";
        String comma = "";
        for(String key : updateFields.keySet())
        {
            sql += comma + key;
            comma = ", ";
        }
        comma = "";
        sql += ") VALUES (";
        for(int x = 0; x < updateFields.size(); x++)
        {
            sql += comma + "?";
            comma = ", ";
        }
        sql += ")";
        setSql(sql);
    }

    // Generate the SQL for delete queries
    private void deleteQuery()
    {
        String sql = "DELETE FROM " + getFromTable() + getWhereClause();
        setSql(sql);
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
     * Sets the table you are updating.
     * @param table The name of the table.
     * @return QueryBuilder
     */
    public QueryBuilder updateTable(String table)
    {
        setFromTable(table);
        setQueryType("update");
        return this;
    }

    /**
     * Sets the table you are inserting values into.
     * @param table The name of the table
     * @return QueryBuilder
     */
    public QueryBuilder insertTable(String table)
    {
        setQueryType("insert");
        setFromTable(table);
        return this;
    }

    /**
     * This assigns a single column to a single value.
     * @param key The name of the column.
     * @param value The value to set.
     * @return QueryBuilder
     */
    public QueryBuilder setColumn(String key, Object value)
    {
        this.updateFields.put(key, value);
        return this;
    }

    /**
     * This assigns multiple columns at one time.
     * @param map Map in the form of key => value pairs.
     * @return QueryBuilder
     */
    public QueryBuilder setColumn(HashMap<String, Object> map)
    {
        this.updateFields.putAll(map);
        return this;
    }

    /**
     * Adds a String column to the WHERE clause.
     * @param column The name of the column.
     * @param value The value of the column.
     * @param comparisonOperator The comparison to use. <,>,=,<=,>=,>
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
     * Used for handling queries with multiple where conditions.
     * @param column The name of the column to check.
     * @param value The value to check.
     * @param comparisonOperator The comparison operator to use.
     * @return QueryBuilder
     */
    public QueryBuilder and(String column, Object value, String comparisonOperator)
    {
        if(!isWhereClauseSet())
        {
            // Fallback for people who call things out of sequence.
            this.whereClause = " WHERE " + column + " " + comparisonOperator + " ? ";
            this.whereColumnValues.add(value);
            this.columnType.add("String");
            setWhereClauseSet(true);
            return this;
        }
        this.whereClause += " AND " + column + " " + comparisonOperator + " ? ";
        this.whereColumnValues.add(value);
        this.columnType.add("String");
        return this;
    }

    /**
     * Used for handling queries with multiple where conditions.
     * @param column The name of the column to check.
     * @param value The value to check.
     * @param comparisonOperator The comparison operator to use.
     * @return QueryBuilder
     */
    public QueryBuilder or(String column, Object value, String comparisonOperator)
    {
        if(!isWhereClauseSet())
        {
            // Fallback for people who call things out of sequence.
            this.whereClause = " WHERE " + column + " " + comparisonOperator + " ? ";
            this.whereColumnValues.add(value);
            this.columnType.add("String");
            setWhereClauseSet(true);
            return this;
        }
        this.whereClause += " OR " + column + " " + comparisonOperator + " ? ";
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
        switch (this.queryType)
        {
            case "select":
                selectQuery();
                break;
            case "update":
                updateQuery();
                break;
            case "delete":
                deleteQuery();
                break;
            case "insert":
                insertQuery();
                break;
            default:
                break;
        }
        System.out.println("SQL: " + sql);
    }


    /**
     * This performs operations on a database that are not of the select variety.
     * @return The number of rows affected. This will not return a row insert id.
     * @throws Exception If the query type is select or not supported.
     */
    public int executeOperation() throws Exception
    {
        try
        {
            switch (queryType)
            {
                case "update":
                    updateQuery();
                    break;
                case "insert":
                    insertQuery();
                    break;
                case "delete":
                    deleteQuery();
                    break;
                case "select":
                    throw new Exception("Must call executeQuery for select statements.");
                default:
                    throw new Exception("Invalid Query Type: " + getQueryType());
            }
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            int x = 1;
            for(String key : updateFields.keySet())
            {
                try
                {
                    preparedStatement.setObject(x, this.updateFields.get(key));
                }
                catch (Exception e)
                {
                    // Currency fixing.
                    String myNumberFormat = NumberFormat.getCurrencyInstance(Locale.US).format(this.updateFields.get(x));
                    System.out.println(myNumberFormat);
                    //Number newNumber = myNumberFormat.parse((String) this.updateFields.get(key));
                    preparedStatement.setString(x, myNumberFormat);
                }
                ++x;
            }
            for (Object column : whereColumnValues)
            {
                preparedStatement.setObject(x, column);
                x++;
            }
            int rowsAffected = preparedStatement.executeUpdate();
            setResultSetMetaData(preparedStatement.getMetaData());
            return rowsAffected;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
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
        selectQuery();

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
                Class<?> annotatedClass = Class.forName(this.className);
                Field[] fields = annotatedClass.getDeclaredFields();
                Object obj = null;
                Constructor[] constructors = annotatedClass.getConstructors();
                for(Constructor constructor : constructors)
                {
                    if(constructor.getParameterCount() == 0)
                    {
                        obj = constructor.newInstance();
                    }
                }
                System.out.println(annotatedClass);
                for(Field field : fields)
                {
                    field.setAccessible(true);
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    for(Annotation annotation : annotations)
                    {
                        String[] annotationParts = annotation.toString().split(", ");
                        String[] pairs = annotationParts[3].split("=");
                        String resultSetKey = pairs[1].replace("\"", "");
                        String type = field.getType().toString();
                        switch(type)
                        {
                            case "int":
                                field.set(obj, resultSet.getInt(resultSetKey));
                                break;
                            case "double":
                                try
                                {
                                    field.set(obj, (Double) resultSet.getObject(resultSetKey));
                                }
                                catch (Exception e)
                                {
                                    // Might be money format
                                    Object originalValue = (String) resultSet.getString(resultSetKey);
                                    BigDecimal bigDecimal = QueryBuilder.parseCurrencyColumn(originalValue.toString(), Locale.US);
                                    field.set(obj, bigDecimal.doubleValue());
                                }
                                break;
                            case "String":
                                field.set(obj, resultSet.getString(resultSetKey));
                                break;
                            default:
                                field.set(obj, resultSet.getObject(resultSetKey));
                                break;
                        }
                    }
                }
                assert obj != null;
                System.out.println(obj.toString());
                objectArrayList.add(obj);
            }
        }
        catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException | ParseException e)
        {
            e.printStackTrace();
        }
        return objectArrayList;
    }


    private static BigDecimal parseCurrencyColumn(String amount, Locale locale) throws ParseException
    {
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat)
        {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]",""));
    }
}
