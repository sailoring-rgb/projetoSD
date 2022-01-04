import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String username;
    private String password;
    private String name;
    private boolean isAdministrador; /** false -> user normal ; true -> administrador */
    private Map<Integer,Viagem> historic; // código de reserva; viagem
    ReentrantLock lock = new ReentrantLock();

    public User(String username, String password, String name, boolean isSpecial, Map<Integer,Viagem> historic) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.isAdministrador = isSpecial;
        this.historic = new HashMap<>(historic);
    }

    public User(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.isAdministrador = user.getIsAdministrador();
        this.historic = user.getHistoric();
    }

    public void removeViagem(String codReserva){
        try{
            lock.lock();
            this.historic.remove(Integer.parseInt(codReserva));
        }finally {
            lock.unlock();
        }
    }
    public String getUsername() {
        try{
            lock.lock();
            return this.username;
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

    public String getName() {
        try{
            lock.lock();
            return this.name;
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

    public Map<Integer,Viagem> getHistoric() {
        try{
            lock.lock();
            return new HashMap<>(this.historic);
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
        builder.append(this.historic.toString()).append(";");
        return builder.toString();
    }
}