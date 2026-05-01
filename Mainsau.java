import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//  ─────────────────────────────────────────────
//  FILE CONSTANTS
//  ─────────────────────────────────────────────

class FileStore {
    static final String USERS_FILE     = "users1.txt";
    static final String EMPLOYERS_FILE = "employers1.txt";
    static final String JOBS_FILE      = "jobs.txt";

    static String applicantsFile(int jobId) {
        return "applicants_" + jobId + ".txt";
    }
}

// ─────────────────────────────────────────────
//  QUALIFICATION HELPER
// ─────────────────────────────────────────────

class QualificationHelper {
    // Degree hierarchy — index 0 = "None", higher index = higher qualification
    // "None" is a sentinel meaning "no qualification at all"
    static final String[] LEVELS = {
        "None",                                                         // index 0  (sentinel)
        "10th", "12th", "Diploma", "B.Sc", "B.Com", "B.A",            // 1–6
        "B.Tech", "BCA", "MCA", "M.Sc", "M.Tech", "MBA", "PhD"        // 7–13
    };

    // Returns index of degree in hierarchy, -1 if not found
    static int getLevel(String qualification) {
        for (int i = 0; i < LEVELS.length; i++) {
            if (LEVELS[i].equalsIgnoreCase(qualification.trim())) {
                return i;
            }
        }
        return -1;
    }

    // Print numbered list: 1..13 are real degrees, 14 = "None of the above"
    static void printOptions() {
        System.out.println("Available qualifications:");
        for (int i = 1; i < LEVELS.length; i++) {          // skip index 0 ("None")
            System.out.println("  " + i + ". " + LEVELS[i]);
        }
        // Option 14 maps to "None of the above"
        System.out.println("  " + LEVELS.length + ". None of the above");
    }

    // Let user pick qualification by number or type it directly
    static String selectQualification(Scanner sc) {
        printOptions();
        System.out.print("Enter qualification number or type it directly: ");
        String input = sc.nextLine().trim();

        try {
            int num = Integer.parseInt(input);

            if (num == LEVELS.length) {
                // User chose "None of the above" (option 14)
                return "None";
            } else if (num >= 1 && num < LEVELS.length) {
                // Valid degree option: 1 → "10th", 2 → "12th", ... 13 → "PhD"
                return LEVELS[num];
            } else {
                System.out.println("Invalid number. Please type your qualification directly.");
                System.out.print("Enter qualification: ");
                return sc.nextLine().trim();
            }
        } catch (NumberFormatException e) {
            // User typed degree name directly
            return input;
        }
    }
}

// ─────────────────────────────────────────────
//  BASE CLASS
// ─────────────────────────────────────────────
class Person {
    String name;
    String email;
    String password;

    boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    void changePassword(String newPass) {
        this.password = newPass;
        System.out.println("Password updated successfully.");
    }
}

// ─────────────────────────────────────────────
//  JOB
// ─────────────────────────────────────────────
class Job {
    int jobId;
    int employerId;
    String title;
    String company;
    double salary;
    String location;
    String requiredQualification;
    int requiredExperience;

    List<User> applicants = new ArrayList<>();

    Job(int id, int empId, String title, String company, double salary,
        String location, String reqQual, int reqExp) {
        this.jobId = id;
        this.employerId = empId;
        this.title = title;
        this.company = company;
        this.salary = salary;
        this.location = location;
        this.requiredQualification = reqQual;
        this.requiredExperience = reqExp;
    }

    void display() {
        System.out.println("Job ID: " + jobId +
                " | Employer ID: " + employerId +
                " | Title: " + title +
                " | Company: " + company +
                " | Salary: " + salary +
                " | Location: " + location +
                " | Min Qualification: " + requiredQualification +
                " | Min Experience: " + requiredExperience + " yrs");
    }

    void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FileStore.JOBS_FILE, true))) {
            pw.println("jobId:" + jobId);
            pw.println("employerId:" + employerId);
            pw.println("title:" + title);
            pw.println("company:" + company);
            pw.println("salary:" + salary);
            pw.println("location:" + location);
            pw.println("qualification:" + requiredQualification);
            pw.println("experience:" + requiredExperience);
            pw.println("applicantsCount:" + applicants.size());
            pw.println("---");
        } catch (IOException e) {
            System.out.println("Error saving job: " + e.getMessage());
        }
    }

    static void rewriteAllJobs(List<Job> jobs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FileStore.JOBS_FILE, false))) {
            for (Job j : jobs) {
                pw.println("jobId:" + j.jobId);
                pw.println("employerId:" + j.employerId);
                pw.println("title:" + j.title);
                pw.println("company:" + j.company);
                pw.println("salary:" + j.salary);
                pw.println("location:" + j.location);
                pw.println("qualification:" + j.requiredQualification);
                pw.println("experience:" + j.requiredExperience);
                pw.println("applicantsCount:" + j.applicants.size());
                pw.println("---");
            }
        } catch (IOException e) {
            System.out.println("Error rewriting jobs file: " + e.getMessage());
        }
    }

    static List<Job> loadAllJobs() {
        List<Job> jobs = new ArrayList<>();
        File f = new File(FileStore.JOBS_FILE);
        if (!f.exists()) return jobs;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int jobId = 0, empId = 0, reqExp = 0;
            String title = "", company = "", location = "", reqQual = "";
            double salary = 0;

            while ((line = br.readLine()) != null) {
                if      (line.startsWith("jobId:"))         jobId    = Integer.parseInt(line.substring(6));
                else if (line.startsWith("employerId:"))    empId    = Integer.parseInt(line.substring(11));
                else if (line.startsWith("title:"))         title    = line.substring(6);
                else if (line.startsWith("company:"))       company  = line.substring(8);
                else if (line.startsWith("salary:"))        salary   = Double.parseDouble(line.substring(7));
                else if (line.startsWith("location:"))      location = line.substring(9);
                else if (line.startsWith("qualification:")) reqQual  = line.substring(14);
                else if (line.startsWith("experience:"))    reqExp   = Integer.parseInt(line.substring(11));
                else if (line.equals("---")) {
                    // Sanitize: if stored qualification is not in the standard list, reset to "10th"
                    if (QualificationHelper.getLevel(reqQual) == -1) {
                        System.out.println("Notice: Job ID " + jobId + " had an invalid qualification ('"
                                + reqQual + "'). Reset to '10th'. Employer should update it.");
                        reqQual = "10th";
                    }
                    jobs.add(new Job(jobId, empId, title, company, salary, location, reqQual, reqExp));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading jobs: " + e.getMessage());
        }
        return jobs;
    }

    void addApplicant(User u, List<Job> allJobs) {
        applicants.add(u);

        try (PrintWriter pw = new PrintWriter(
                new FileWriter(FileStore.applicantsFile(jobId), true))) {
            pw.println("userId:" + u.userId);
            pw.println("name:" + u.name);
            pw.println("email:" + u.email);
            pw.println("---");
        } catch (IOException e) {
            System.out.println("Error saving applicant: " + e.getMessage());
        }

        Job.rewriteAllJobs(allJobs);
    }

    void loadApplicants(List<User> allUsers) {
        File f = new File(FileStore.applicantsFile(jobId));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int uid = -1;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("userId:")) {
                    uid = Integer.parseInt(line.substring(7));
                } else if (line.equals("---")) {
                    for (User u : allUsers) {
                        if (u.userId == uid) {
                            applicants.add(u);
                            break;
                        }
                    }
                    uid = -1;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading applicants: " + e.getMessage());
        }
    }

    void viewApplicants() {
        if (applicants.isEmpty()) {
            System.out.println("No applicants yet.");
            return;
        }
        System.out.println("----- Applicants -----");
        for (User u : applicants) {
            System.out.println("User ID: " + u.userId +
                    " | Name: " + u.name +
                    " | Email: " + u.email);
        }
    }
}

// ─────────────────────────────────────────────
//  USER
// ─────────────────────────────────────────────
class User extends Person {
    int userId;

    void register(int id, String name, String email, String password) {
        this.userId   = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FileStore.USERS_FILE, true))) {
            pw.println("userId:" + userId);
            pw.println("name:" + name);
            pw.println("email:" + email);
            pw.println("password:" + password);
            pw.println("---");
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    static void rewriteAllUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FileStore.USERS_FILE, false))) {
            for (User u : users) {
                pw.println("userId:" + u.userId);
                pw.println("name:" + u.name);
                pw.println("email:" + u.email);
                pw.println("password:" + u.password);
                pw.println("---");
            }
        } catch (IOException e) {
            System.out.println("Error rewriting users file: " + e.getMessage());
        }
    }

    static List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        File f = new File(FileStore.USERS_FILE);
        if (!f.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int userId = 0;
            String name = "", email = "", password = "";

            while ((line = br.readLine()) != null) {
                if      (line.startsWith("userId:"))   userId   = Integer.parseInt(line.substring(7));
                else if (line.startsWith("name:"))     name     = line.substring(5);
                else if (line.startsWith("email:"))    email    = line.substring(6);
                else if (line.startsWith("password:")) password = line.substring(9);
                else if (line.equals("---")) {
                    User u = new User();
                    u.register(userId, name, email, password);
                    users.add(u);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    @Override
    boolean login(String email, String password) {
        if (super.login(email, password)) {
            System.out.println("User login successful.");
            return true;
        }
        return false;
    }

    void viewJobs(List<Job> jobs) {
        if (jobs.isEmpty()) {
            System.out.println("No jobs available.");
            return;
        }
        System.out.println("----- Available Jobs -----");
        for (Job job : jobs) {
            job.display();
        }
    }

    void applyJob(List<Job> jobs, int jobId) {
        Scanner sc = new Scanner(System.in);
        for (Job job : jobs) {
            if (job.jobId == jobId) {
                System.out.println("Enter your qualification:");
                String uqual = QualificationHelper.selectQualification(sc);

                // ── "None of the above" chosen by user ──────────────────────
                if ("None".equalsIgnoreCase(uqual)) {
                    System.out.println("Not eligible: You selected 'None of the above'.");
                    System.out.println("You must hold at least one recognized qualification to apply.");
                    return;
                }

                System.out.print("Enter your experience (years): ");
                int uexp = sc.nextInt();
                sc.nextLine();

                int userLevel     = QualificationHelper.getLevel(uqual);
                int requiredLevel = QualificationHelper.getLevel(job.requiredQualification);

                // ── Eligibility check ────────────────────────────────────────
                boolean qualOk = userLevel >= requiredLevel;
                boolean expOk  = uexp >= job.requiredExperience;

                if (qualOk && expOk) {
                    job.addApplicant(this, jobs);
                    System.out.println("Eligible! Applied successfully.");
                } else {
                    System.out.println("Not eligible for this job.");
                    if (!qualOk) {
                        System.out.println("  Reason: Your qualification '" + uqual +
                                "' is below the required '" + job.requiredQualification + "'.");
                    }
                    if (!expOk) {
                        System.out.println("  Reason: Your experience (" + uexp +
                                " yr(s)) is below the required " + job.requiredExperience + " yr(s).");
                    }
                }
                return;
            }
        }
        System.out.println("Job not found.");
    }
}

// ─────────────────────────────────────────────
//  EMPLOYER
// ─────────────────────────────────────────────
class Employer extends Person {
    int empId;
    String phone;
    String designation;
    String company;
    String website;

    void register(int id, String name, String email, String password,
                  String phone, String designation,
                  String company, String website) {
        this.empId       = id;
        this.name        = name;
        this.email       = email;
        this.password    = password;
        this.phone       = phone;
        this.designation = designation;
        this.company     = company;
        this.website     = website;
    }

    void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FileStore.EMPLOYERS_FILE, true))) {
            pw.println("empId:" + empId);
            pw.println("name:" + name);
            pw.println("email:" + email);
            pw.println("password:" + password);
            pw.println("phone:" + phone);
            pw.println("designation:" + designation);
            pw.println("company:" + company);
            pw.println("website:" + website);
            pw.println("---");
        } catch (IOException e) {
            System.out.println("Error saving employer: " + e.getMessage());
        }
    }

    static void rewriteAllEmployers(List<Employer> employers) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FileStore.EMPLOYERS_FILE, false))) {
            for (Employer e : employers) {
                pw.println("empId:" + e.empId);
                pw.println("name:" + e.name);
                pw.println("email:" + e.email);
                pw.println("password:" + e.password);
                pw.println("phone:" + e.phone);
                pw.println("designation:" + e.designation);
                pw.println("company:" + e.company);
                pw.println("website:" + e.website);
                pw.println("---");
            }
        } catch (IOException e) {
            System.out.println("Error rewriting employers file: " + e.getMessage());
        }
    }

    static List<Employer> loadAllEmployers() {
        List<Employer> employers = new ArrayList<>();
        File f = new File(FileStore.EMPLOYERS_FILE);
        if (!f.exists()) return employers;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int empId = 0;
            String name = "", email = "", password = "", phone = "",
                   desig = "", company = "", website = "";

            while ((line = br.readLine()) != null) {
                if      (line.startsWith("empId:"))        empId    = Integer.parseInt(line.substring(6));
                else if (line.startsWith("name:"))         name     = line.substring(5);
                else if (line.startsWith("email:"))        email    = line.substring(6);
                else if (line.startsWith("password:"))     password = line.substring(9);
                else if (line.startsWith("phone:"))        phone    = line.substring(6);
                else if (line.startsWith("designation:"))  desig    = line.substring(12);
                else if (line.startsWith("company:"))      company  = line.substring(8);
                else if (line.startsWith("website:"))      website  = line.substring(8);
                else if (line.equals("---")) {
                    Employer e = new Employer();
                    e.register(empId, name, email, password, phone, desig, company, website);
                    employers.add(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading employers: " + e.getMessage());
        }
        return employers;
    }

    @Override
    boolean login(String email, String password) {
        if (super.login(email, password)) {
            System.out.println("Employer login successful.");
            return true;
        }
        return false;
    }

    void viewProfile() {
        System.out.println("\n----- Employer Profile -----");
        System.out.println("ID          : " + empId);
        System.out.println("Name        : " + name);
        System.out.println("Email       : " + email);
        System.out.println("Phone       : " + phone);
        System.out.println("Designation : " + designation);
        System.out.println("Company     : " + company);
        System.out.println("Website     : " + website);
    }

    void postJob(List<Job> jobs, Scanner sc) {
        System.out.print("Enter Job ID: ");
        int jobId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Job Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Salary: ");
        double salary = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Location: ");
        String location = sc.nextLine();
        System.out.println("Select Required Qualification:");
        String rq = QualificationHelper.selectQualification(sc);
        System.out.print("Enter Required Experience (years): ");
        int rexp = sc.nextInt();
        sc.nextLine();

        Job job = new Job(jobId, this.empId, title, this.company,
                salary, location, rq, rexp);
        jobs.add(job);
        job.saveToFile();
        System.out.println("Job posted successfully with Job ID: " + jobId);
    }

    void updateJob(List<Job> jobs, Scanner sc) {
        System.out.print("Enter Job ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        Job target = null;
        for (Job job : jobs) {
            if (job.jobId == id && job.employerId == this.empId) {
                target = job;
                break;
            }
        }

        if (target == null) {
            System.out.println("Job not found or you don't have permission to update it.");
            return;
        }

        int choice;
        do {
            System.out.println("\n--- Update Job Menu ---");
            System.out.println("1. Update Job ID");
            System.out.println("2. Update Job Title");
            System.out.println("3. Update Salary");
            System.out.println("4. Update Location");
            System.out.println("5. Update Qualification");
            System.out.println("6. Update Experience");
            System.out.println("7. Exit");
            System.out.print("Choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter new Job ID: ");
                    target.jobId = sc.nextInt();
                    sc.nextLine();
                    break;
                case 2:
                    System.out.print("Enter new Title: ");
                    target.title = sc.nextLine();
                    break;
                case 3:
                    System.out.print("Enter new Salary: ");
                    target.salary = sc.nextDouble();
                    sc.nextLine();
                    break;
                case 4:
                    System.out.print("Enter new Location: ");
                    target.location = sc.nextLine();
                    break;
                case 5:
                    System.out.println("Select new Qualification:");
                    target.requiredQualification = QualificationHelper.selectQualification(sc);
                    break;
                case 6:
                    System.out.print("Enter new Experience (years): ");
                    target.requiredExperience = sc.nextInt();
                    sc.nextLine();
                    break;
                case 7:
                    System.out.println("Exiting update...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 7);

        Job.rewriteAllJobs(jobs);
        System.out.println("Job updated and saved.");
    }
}

// ─────────────────────────────────────────────
//  MAIN
// ─────────────────────────────────────────────
public class Mainsau {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        List<User>     users     = User.loadAllUsers();
        List<Employer> employers = Employer.loadAllEmployers();
        List<Job>      jobs      = Job.loadAllJobs();

        for (Job job : jobs) {
            job.loadApplicants(users);
        }

        int userIdCounter = users.isEmpty()     ? 1 : users.get(users.size() - 1).userId + 1;
        int empIdCounter  = employers.isEmpty() ? 1 : employers.get(employers.size() - 1).empId + 1;

        System.out.println("Data loaded. Users: " + users.size() +
                           ", Employers: " + employers.size() +
                           ", Jobs: " + jobs.size());

        int choice;

        do {
            System.out.println("\n===== ONLINE JOB PORTAL =====");
            System.out.println("1. Register Employer");
            System.out.println("2. Register User");
            System.out.println("3. Employer Login");
            System.out.println("4. User Login");
            System.out.println("5. Exit");
            System.out.print("Choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter Name: ");
                    String ename = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String eemail = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String epass = sc.nextLine();
                    System.out.print("Enter Phone: ");
                    String phone = sc.nextLine();
                    System.out.print("Enter Designation: ");
                    String desig = sc.nextLine();
                    System.out.print("Enter Company Name: ");
                    String comp = sc.nextLine();
                    System.out.print("Enter Company Website: ");
                    String web = sc.nextLine();

                    Employer emp = new Employer();
                    emp.register(empIdCounter++, ename, eemail, epass,
                            phone, desig, comp, web);
                    employers.add(emp);
                    emp.saveToFile();
                    System.out.println("Employer registered successfully.");
                    break;

                case 2:
                    System.out.print("Enter Name: ");
                    String uname = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String uemail = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String upass = sc.nextLine();

                    User user = new User();
                    user.register(userIdCounter++, uname, uemail, upass);
                    users.add(user);
                    user.saveToFile();
                    System.out.println("User registered successfully.");
                    break;

                case 3:
                    System.out.print("Email: ");
                    String lemail = sc.nextLine();
                    System.out.print("Password: ");
                    String lpass = sc.nextLine();

                    boolean empFound = false;
                    for (Employer e : employers) {
                        if (e.login(lemail, lpass)) {
                            empFound = true;
                            int ech;
                            do {
                                System.out.println("\n--- Employer Menu ---");
                                System.out.println("1. View Profile");
                                System.out.println("2. Post Job");
                                System.out.println("3. Update Job");
                                System.out.println("4. View Applicants");
                                System.out.println("5. Change Password");
                                System.out.println("6. Logout");
                                System.out.print("Choice: ");
                                ech = sc.nextInt();
                                sc.nextLine();

                                switch (ech) {
                                    case 1:
                                        e.viewProfile();
                                        break;
                                    case 2:
                                        e.postJob(jobs, sc);
                                        break;
                                    case 3:
                                        e.updateJob(jobs, sc);
                                        break;
                                    case 4:
                                        System.out.print("Enter Job ID to view applicants: ");
                                        int vid = sc.nextInt();
                                        sc.nextLine();
                                        boolean found = false;
                                        for (Job job : jobs) {
                                            if (job.jobId == vid && job.employerId == e.empId) {
                                                job.viewApplicants();
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) System.out.println("Job not found.");
                                        break;
                                    case 5:
                                        System.out.print("Enter new password: ");
                                        String np = sc.nextLine();
                                        e.changePassword(np);
                                        Employer.rewriteAllEmployers(employers);
                                        break;
                                    case 6:
                                        System.out.println("Logged out.");
                                        break;
                                    default:
                                        System.out.println("Invalid choice.");
                                }
                            } while (ech != 6);
                            break;
                        }
                    }
                    if (!empFound) System.out.println("Invalid email or password.");
                    break;

                case 4:
                    System.out.print("Email: ");
                    String ulemail = sc.nextLine();
                    System.out.print("Password: ");
                    String ulpass = sc.nextLine();

                    boolean userFound = false;
                    for (User u : users) {
                        if (u.login(ulemail, ulpass)) {
                            userFound = true;
                            int uch;
                            do {
                                System.out.println("\n--- User Menu ---");
                                System.out.println("1. View Jobs");
                                System.out.println("2. Apply for Job");
                                System.out.println("3. Change Password");
                                System.out.println("4. Logout");
                                System.out.print("Choice: ");
                                uch = sc.nextInt();
                                sc.nextLine();

                                switch (uch) {
                                    case 1:
                                        u.viewJobs(jobs);
                                        break;
                                    case 2:
                                        System.out.print("Enter Job ID to apply: ");
                                        int applyId = sc.nextInt();
                                        sc.nextLine();
                                        u.applyJob(jobs, applyId);
                                        break;
                                    case 3:
                                        System.out.print("Enter new password: ");
                                        String unp = sc.nextLine();
                                        u.changePassword(unp);
                                        User.rewriteAllUsers(users);
                                        break;
                                    case 4:
                                        System.out.println("Logged out.");
                                        break;
                                    default:
                                        System.out.println("Invalid choice.");
                                }
                            } while (uch != 4);
                            break;
                        }
                    }
                    if (!userFound) System.out.println("Invalid email or password.");
                    break;

                case 5:
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 5);

        sc.close();
    }
}