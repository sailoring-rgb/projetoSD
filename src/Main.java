import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main{
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);

        Map<String,User> credentials = Parser.parse();
        System.out.println("Username: ");
        String username = s.nextLine();
        if(credentials.containsKey(username)){
            System.out.println("exists");
            String rightPass = credentials.get(username).getPassword();
            System.out.println("Password: ");
            String password = s.nextLine();
            System.out.println("password: " + rightPass);
        }
        else System.out.println("doesn't exist");
    }
}