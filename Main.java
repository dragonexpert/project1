import javax.persistence.Column;
import javax.persistence.Entity;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws IllegalAccessException
    {
        QueryBuilder queryBuilder = new QueryBuilder("User");
        queryBuilder = queryBuilder.getColumns("*").fromTable("users");
        queryBuilder.viewSQL();
        queryBuilder.executeQuery();


        Class annotatedClass = User.class;
        User newUser = new User(1, "user1", "pass1", "email1@example.com");
        Method[] methods = annotatedClass.getMethods();
        Field[] fields = annotatedClass.getDeclaredFields();
        Constructor[] constructors = annotatedClass.getConstructors();
        Constructor realConstructor;
        User customUser = new User();
        ArrayList<Object> values = new ArrayList<>();
        values.add(1);
        values.add("user1");
        values.add("pass1");
        values.add("email1");
        /*
        for(int x=0; x < fields.length; x++)
        {
            fields[x].setAccessible(true);
            System.out.println("Field Name: " + fields[x].getName());
            fields[x].set(customUser, values.get(x));
            Annotation[] annotations = fields[x].getDeclaredAnnotations();
            for(Annotation annotation : annotations)
            {
                String annotationParts[] = annotation.toString().split(", ");
                String keyVals[] = annotationParts[3].split("=");
                System.out.println("Annotation: " + annotationParts[3]);
            }
        }
        */
        System.out.println(customUser.toString());
    }
}
