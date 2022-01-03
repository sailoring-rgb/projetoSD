import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class User {
    private String username;
    private String password;
    private String name;
    private boolean isAdministrador; /** false -> user normal ; true -> administrador */
    private List<Viagem> viagens;
    ReentrantLock lock = new ReentrantLock();

    public User(String username, String password, String name, boolean isSpecial, List<Viagem> viagens) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.isAdministrador = isSpecial;
        this.viagens = new ArrayList<>(viagens);
    }

    public User(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.isAdministrador = user.getIsAdministrador();
        this.viagens = user.getViagens();
    }

    public String getUsername() {
        try{
            lock.lock();
            return this.username;
        }
        finally { lock.unlock(); }
    }

    public void setUsername(String username) {
        try {
            lock.lock();
            this.username = username;
        }
        finally { lock.unlock(); }
    }

    public String getPassword() {
        try{
            lock.lock();
            return this.password;
        }
        finally { lock.unlock(); }
    }

    public void setPassword(String password){
        try {
            lock.lock();
            this.password = password;
        }
        finally { lock.unlock(); }
    }

    public String getName() {
        try{
            lock.lock();
            return this.name;
        }
        finally { lock.unlock(); }
    }

    public void setName(String name){
        try {
            lock.lock();
            this.name = name;
        }
        finally { lock.unlock(); }
    }

    public boolean getIsAdministrador() {
        try{
            lock.lock();
            return this.isAdministrador;
        }
        finally { lock.unlock(); }
    }

    public void setIsAdministrador(boolean isSpecial){
        try {
            lock.lock();
            this.isAdministrador = isSpecial;
        }
        finally { lock.unlock(); }
    }

    public List<Viagem> getViagens() {
        try{
            lock.lock();
            return new ArrayList<>(this.viagens);
        }
        finally { lock.unlock(); }
    }

    public User clone(){
        return new User(this);
    }

    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append(this.username).append(";");
        builder.append(this.password).append(";");
        builder.append(this.name).append(";");
        return builder.toString();
    }
}