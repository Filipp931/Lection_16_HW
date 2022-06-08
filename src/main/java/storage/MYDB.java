package storage;


import java.io.*;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.function.Predicate;

public class MYDB extends Source{
   // private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cache";
    private static final String USER = "root";
    private static final String PASS = "admin";
    private static MYDB MYDB;
    private static Connection connection;
    private MYDB() {
        System.out.println("Connecting to DB...");
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static MYDB getInstance(){
        if(MYDB == null){
            synchronized (MYDB.class){
                if ((MYDB ==null)) {
                    MYDB = new MYDB();
                }
            }
        }
        return MYDB;
    }

    public Map<Method, HashMap<List<Object>, Object>> getCacheFromTable(List<Method> methods) throws SQLException {
        Map<Method, HashMap<List<Object>, Object>> result = new HashMap<>();
        String statement = "SELECT * FROM cache";
        try (Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(statement);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String method = resultSet.getString("method");
                Object[] args = (Object[]) readObject(resultSet.getBlob("args"));
                Object value = readObject(resultSet.getBlob("value"));
                putToCache(result, methods, method, args, value);
            }
        }
        return result;
    }

    private void putToCache(Map<Method, HashMap<List<Object>, Object>> cache, List<Method> methods, String method,  Object[] args, Object value) {
        Method realMethod = methods.stream().filter(m -> m.toString().equals(method)).findAny().orElse(null);
        if(cache.containsKey(realMethod)){
            cache.get(realMethod).put(Arrays.asList(args), value);
        } else {
            cache.put(realMethod, new HashMap<>(){{put(Arrays.asList(args), value);}});
        }
    }
    private Object readObject(Blob blob){
        Object result = null;
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(blob.getBinaryStream()))) {
            result = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void storeToBase(Method method, Object args, Object value) throws SQLException {
        String statement = "INSERT INTO cache(method, args, value) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(statement)) {
            stmt.setString(1, method.toString());
            stmt.setBlob(2, writeToBlob(args));
            stmt.setBlob(3, writeToBlob(value));
            stmt.executeUpdate();
        }
    }

    /**
     * получение сериализованного объекта в виде blob
     * @param obj
     * @return
     */
    private Blob writeToBlob(Object obj) throws SQLException {
        Blob result = connection.createBlob();
        try (ObjectOutputStream ous = new ObjectOutputStream(new BufferedOutputStream(result.setBinaryStream(1)))) {
            ous.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
