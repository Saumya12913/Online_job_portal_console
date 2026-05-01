import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Employer {

    int empId;
    String name, email, password, company;

    public void register(int id, String name, String email, String password, String company) {
        this.empId = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.company = company;
    }

    public void saveToDB() {
        try {
            Connection con = DBConnection.getConnection();

            String q = "INSERT INTO employers(emp_id,name,email,password,phone,designation,company,website) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(q);

            ps.setInt(1, empId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);

            // default values for extra columns
            ps.setString(5, "NA");
            ps.setString(6, "NA");
            ps.setString(7, company);
            ps.setString(8, "NA");

            ps.executeUpdate();
            System.out.println("Employer Registered!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Employer login(String email, String password) {
        try {
            Connection con = DBConnection.getConnection();

            String q = "SELECT * FROM employers WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(q);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Employer e = new Employer();
                e.register(
                        rs.getInt("emp_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("company")
                );
                System.out.println("Employer Login Successful!");
                return e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}