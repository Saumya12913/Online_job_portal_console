import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== JOB PORTAL =====");
            System.out.println("1. Register User");
            System.out.println("2. Register Employer");
            System.out.println("3. User Login");
            System.out.println("4. Employer Login");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    User u = new User();
                    System.out.print("ID: ");
                    int uid = sc.nextInt(); sc.nextLine();
                    System.out.print("Name: ");
                    String uname = sc.nextLine();
                    System.out.print("Email: ");
                    String uemail = sc.nextLine();
                    System.out.print("Password: ");
                    String upass = sc.nextLine();

                    u.register(uid, uname, uemail, upass);
                    u.saveToDB();
                    break;

                case 2:
                    Employer e = new Employer();
                    System.out.print("ID: ");
                    int eid = sc.nextInt(); sc.nextLine();
                    System.out.print("Name: ");
                    String ename = sc.nextLine();
                    System.out.print("Email: ");
                    String eemail = sc.nextLine();
                    System.out.print("Password: ");
                    String epass = sc.nextLine();
                    System.out.print("Company: ");
                    String comp = sc.nextLine();

                    e.register(eid, ename, eemail, epass, comp);
                    e.saveToDB();
                    break;

                case 3:
                    System.out.print("Email: ");
                    String le = sc.nextLine();
                    System.out.print("Password: ");
                    String lp = sc.nextLine();

                    User user = User.login(le, lp);

                    if (user != null) {
                        int ch;
                        do {
                            System.out.println("\n1.View Jobs");
                            System.out.println("2.Apply Job");
                            System.out.println("3.Logout");
                            System.out.print("Choice: ");
                            ch = sc.nextInt();

                            if (ch == 1) Job.viewJobs();

                            else if (ch == 2) {
                                System.out.print("Job ID: ");
                                int jid = sc.nextInt();
                                Job.applyJob(jid, user.userId);
                            }

                        } while (ch != 3);
                    }
                    break;

                case 4:
                    System.out.print("Email: ");
                    String ee = sc.nextLine();
                    System.out.print("Password: ");
                    String ep = sc.nextLine();

                    Employer emp = Employer.login(ee, ep);

                    if (emp != null) {
                        int ch;
                        do {
                            System.out.println("\n1.Post Job");
                            System.out.println("2.Logout");
                            System.out.print("Choice: ");
                            ch = sc.nextInt();
                            sc.nextLine();

                            if (ch == 1) {
                                System.out.print("Job ID: ");
                                int jid = sc.nextInt(); sc.nextLine();
                                System.out.print("Title: ");
                                String t = sc.nextLine();
                                System.out.print("Salary: ");
                                double s = sc.nextDouble(); sc.nextLine();
                                System.out.print("Location: ");
                                String loc = sc.nextLine();
                                System.out.print("Qualification: ");
                                String q = sc.nextLine();
                                System.out.print("Experience: ");
                                int ex = sc.nextInt();

                                Job.postJob(jid, emp.empId, t, emp.company, s, loc, q, ex);
                            }

                        } while (ch != 2);
                    }
                    break;
            }

        } while (choice != 5);

        sc.close();
    }
}