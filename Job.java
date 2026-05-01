import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Job {

    public static void postJob(int jobId, int empId, String title, String company,
                               double salary, String location,
                               String qualification, int experience) {

        try {
            Connection con = DBConnection.getConnection();

            String q = "INSERT INTO jobs VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(q);

            ps.setInt(1, jobId);
            ps.setInt(2, empId);
            ps.setString(3, title);
            ps.setString(4, company);
            ps.setDouble(5, salary);
            ps.setString(6, location);
            ps.setString(7, qualification);
            ps.setInt(8, experience);

            ps.executeUpdate();
            System.out.println("Job Posted!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewJobs() {
        try {
            Connection con = DBConnection.getConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM jobs");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("job_id") + " | " +
                        rs.getString("title") + " | " +
                        rs.getString("company") + " | " +
                        rs.getDouble("salary")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyJob(int jobId, int userId) {
        try {
            Connection con = DBConnection.getConnection();

            String q = "INSERT INTO applicants(job_id,user_id) VALUES (?,?)";
            PreparedStatement ps = con.prepareStatement(q);

            ps.setInt(1, jobId);
            ps.setInt(2, userId);

            ps.executeUpdate();
            System.out.println("Applied Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}