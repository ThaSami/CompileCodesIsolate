import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Assignment {

    static void print(Process p1, Process p2,Statement stmt,long hash) throws IOException, SQLException {
        String s = null;
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p1.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p1.getErrorStream()));

        BufferedReader stdInput2 = new BufferedReader(new
                InputStreamReader(p2.getInputStream()));

        BufferedReader stdError2 = new BufferedReader(new
                InputStreamReader(p2.getErrorStream()));
        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        while ((s = stdInput2.readLine()) != null) {
            System.out.println(s);
            stmt.execute("INSERT INTO codes (hash, output) VALUES (\""+String.valueOf(hash)+"\",\""+s+"\")");

        }



        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        while ((s = stdError2.readLine()) != null) {
            System.out.println(s);
        }
    }

    static void print(Process p1,Statement stmt,long hash) throws IOException, SQLException {
        String s = null;
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p1.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p1.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
            stmt.execute("INSERT INTO codes (hash, output) VALUES (\""+String.valueOf(hash)+"\",\""+s+"\");");

        }


        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }


    }

    static String readFile(String fileName) throws IOException {
        Scanner sc = new Scanner(new File(fileName));
        StringBuilder str = new StringBuilder();
        while (sc.hasNextLine()) {
            str.append(sc.nextLine());
        }
        return str.toString();
    }

    public static void main(String[] args) {


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "user", "password");
            Statement stmt = con.createStatement();
            long hash = Objects.hashCode(readFile(args[0]));
            ResultSet resultSet = stmt.executeQuery("select * from codes where hash=" +"\""+String.valueOf(hash)+"\"");

           if(resultSet.next()){
               System.out.println("File output is : "+ resultSet.getString("output"));
               System.exit(0);
            }

            if (args[0].endsWith(".cpp")) {
                Process p = Runtime.getRuntime().exec("g++ " + args[0] + " -o out");
                Process p2 = Runtime.getRuntime().exec("./out");

                print(p, p2,stmt,hash);
            } else if (args[0].endsWith(".py")) {
                Process p = Runtime.getRuntime().exec("python " + args[0]);
                print(p,stmt,hash);
            }
            con.close();

        } catch (Exception e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
