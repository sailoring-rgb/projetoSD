import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    public static Map<String,User> parse() throws IOException {
        List<String> lines = readFile("dados.txt");
        Map<String,User> credentials = new HashMap<>(); // username, user
        String[] token;
        for (String l : lines) {
            token = l.split(";", 4);
            User u = new User(token[0],token[1],token[2],Integer.parseInt(token[3]));
            credentials.put(token[0],u.clone());
        }
        return credentials;
    }

    public static List<String> readFile(String file) {
        List<String> lines;
        try { lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8); }
        catch(IOException exc) { lines = new ArrayList<>(); }
        return lines;
    }
}

