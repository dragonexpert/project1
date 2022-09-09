##How to use Query Builder

This is strictly for Postgres. If your database is any other type, it will not work.

In order to map your classes to databases you will need to use annotations. Before the class
name you will first use @Entity and then the next line should be @Table(name="databaseTable").

Each of the fields should have an annotation of @Column(name="databaseColumnName"). Do
not use any other annotations, or it may mess up the query builder.

You will then need to create a ConnectionFactory object passing in the database
credentials. Once you create a ConnectionFactory object, you will invoke the
createNewConnection method which will return a Connection object. Now you are ready
to use the Query Builder.

When you create a QueryBuilder object, you will pass in the connection object and
the name of the class you are mapping to. Once you create the QueryBuilder object,
the rest of the methods return a QueryBuilder object so you can chain them. Call
the executeQuery method to perform any queries that should return result sets. If
you only need a single result, you can use the getOne method instead. Call executeOperation 
for any queries that do not return result sets.
