import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User {

    int userId;
    String name, email, password;

    public void register(int id, String name, String email, String password) {
        this.userId = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void saveToDB() {
        try {
            Connection con = DBConnection.getConnection();

            String q = "INSERT INTO users VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(q);

            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);

            ps.executeUpdate();
            System.out.println("User Registered!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User login(String email, String password) {
        try {
            Connection con = DBConnection.getConnection();

            String q = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(q);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.register(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                System.out.println("User Login Successful!");
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}