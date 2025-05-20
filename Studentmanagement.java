import java.util.*;

public class Studentmanagement {

    static class Student {
        int id;
        String name;
        int attendanceCount = 0;
        int totalClasses = 0;
        Map<String, Double> grades = new HashMap<>();
        List<String> messages = new ArrayList<>();

        Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        double getAttendancePercentage() {
            if (totalClasses == 0) return 0;
            return (attendanceCount * 100.0) / totalClasses;
        }

        double getGPA() {
            if (grades.isEmpty()) return 0;
            double sum = 0;
            for (double g : grades.values()) sum += g;
            return sum / grades.size();
        }
    }

    static Map<Integer, Student> students = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static int studentIdCounter = 1;

    static Map<String, String> users = new HashMap<>();
    static boolean isAuthenticated = false;

    public static void main(String[] args) {
        setupUsers();
        authenticateUser();

        if (!isAuthenticated) {
            System.out.println("Authentication failed. Exiting...");
            return;
        }

        System.out.println("Welcome to Student Management System!");

        boolean running = true;

        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Enroll Student");
            System.out.println("2. Mark Attendance for All Students");
            System.out.println("3. Add Grade");
            System.out.println("4. View Student Details");
            System.out.println("5. Send Message to Student");
            System.out.println("6. View Student Messages");
            System.out.println("7. Send Bulk Message to All Students");
            System.out.println("8. Attendance Report");
            System.out.println("9. Grade Report (GPA)");
            System.out.println("10. Exit");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> enrollStudent();
                case 2 -> markAttendanceForAll();
                case 3 -> addGrade();
                case 4 -> viewStudentDetails();
                case 5 -> sendMessage();
                case 6 -> viewMessages();
                case 7 -> sendBulkMessage();
                case 8 -> attendanceReport();
                case 9 -> gradeReport();
                case 10 -> {
                    running = false;
                    System.out.println("Exiting. Goodbye!");
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    static int readInt() {
        while (true) {
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    static void setupUsers() {
        System.out.println("Setup users for authentication.");
        while (true) {
            System.out.print("Add a user? (Y/N): ");
            String ans = scanner.nextLine().trim().toUpperCase();
            if (!ans.equals("Y")) break;

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            users.put(username, password);
            System.out.println("User added.");
        }

        if (users.isEmpty()) {
            System.out.println("No users added, adding default admin/admin123.");
            users.put("admin", "admin123");
        }
    }

    static void authenticateUser() {
        System.out.println("Please login:");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            isAuthenticated = true;
            System.out.println("Login successful as " + username);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    static void enrollStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        Student student = new Student(studentIdCounter++, name);
        students.put(student.id, student);
        System.out.println("Student enrolled with ID: " + student.id);
    }

    static void markAttendanceForAll() {
        if (students.isEmpty()) {
            System.out.println("No students enrolled.");
            return;
        }
        System.out.println("Mark attendance for each student. Enter Y for present, N for absent.");
        for (Student student : students.values()) {
            System.out.print("Is " + student.name + " present? (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            student.totalClasses++;
            if ("Y".equals(input)) {
                student.attendanceCount++;
            }
        }
        System.out.println("Attendance marked for all students.");
    }

    static void addGrade() {
        System.out.print("Enter student ID to add grade: ");
        int id = readInt();

        Student student = students.get(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter subject name: ");
        String subject = scanner.nextLine();

        System.out.print("Enter grade (0-100): ");
        double grade;
        while (true) {
            try {
                grade = Double.parseDouble(scanner.nextLine());
                if (grade < 0 || grade > 100) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid grade. Enter a number between 0 and 100: ");
            }
        }

        student.grades.put(subject, grade);
        System.out.println("Grade added for " + student.name + " in " + subject);
    }

    static void viewStudentDetails() {
        System.out.print("Enter student ID to view details: ");
        int id = readInt();

        Student student = students.get(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("Student ID: " + student.id);
        System.out.println("Name: " + student.name);
        System.out.printf("Attendance: %d/%d (%.2f%%)\n", student.attendanceCount, student.totalClasses, student.getAttendancePercentage());

        if (student.grades.isEmpty()) {
            System.out.println("No grades recorded.");
        } else {
            System.out.println("Grades:");
            for (var entry : student.grades.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
            System.out.printf("GPA: %.2f\n", student.getGPA());
        }
    }

    static void sendMessage() {
        System.out.print("Enter student ID to send message: ");
        int id = readInt();

        Student student = students.get(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter message: ");
        String message = scanner.nextLine();

        student.messages.add(message);
        System.out.println("Message sent to " + student.name);
    }

    static void viewMessages() {
        System.out.print("Enter student ID to view messages: ");
        int id = readInt();

        Student student = students.get(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        if (student.messages.isEmpty()) {
            System.out.println("No messages for " + student.name);
        } else {
            System.out.println("Messages for " + student.name + ":");
            for (String msg : student.messages) {
                System.out.println(" - " + msg);
            }
        }
    }

    static void sendBulkMessage() {
        if (students.isEmpty()) {
            System.out.println("No students enrolled.");
            return;
        }

        System.out.print("Enter bulk message to send to all students: ");
        String message = scanner.nextLine();

        for (Student student : students.values()) {
            student.messages.add("[Bulk] " + message);
        }

        System.out.println("Bulk message sent to all students.");
    }

    static void attendanceReport() {
        if (students.isEmpty()) {
            System.out.println("No students enrolled.");
            return;
        }

        System.out.println("Attendance Report:");
        for (Student student : students.values()) {
            double attendancePercent = student.getAttendancePercentage();
            System.out.printf("ID: %d, Name: %s, Attendance: %d/%d (%.2f%%)\n",
                student.id, student.name, student.attendanceCount, student.totalClasses, attendancePercent);

            if (attendancePercent < 75.0 && student.totalClasses > 0) {
                String warning = "Warning: Your attendance is below 75%. Please attend classes regularly.";
                student.messages.add("[Auto] " + warning);
                System.out.println("-> Sent warning message to " + student.name);
            }
        }
    }

    static void gradeReport() {
        if (students.isEmpty()) {
            System.out.println("No students enrolled.");
            return;
        }

        System.out.println("Grade Report (GPA):");
        for (Student student : students.values()) {
            System.out.printf("ID: %d, Name: %s, GPA: %.2f\n",
                student.id, student.name, student.getGPA());
        }
    }
}
