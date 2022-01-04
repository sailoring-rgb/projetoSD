import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private String username;
    private String password;
    private String name;
    private boolean isAdministrador; /** false -> user normal ; true -> administrador */
    private Map<Integer,Viagem> historic; // código de reserva; viagem
    private ReadWriteLock lockRW = new ReentrantReadWriteLock();
    private Lock readlock = lockRW.readLock();
    private Lock writelock = lockRW.writeLock();
    //ReentrantLock lock = new ReentrantLock();

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
            readlock.lock();
            return this.username;
        }
        finally { readlock.unlock(); }
    }

    public String getPassword() {
        try{
            readlock.lock();
            return this.password;
        }
        finally { readlock.unlock(); }
    }

    public String getName() {
        try{
            readlock.lock();
            return this.name;
        }
        finally { readlock.unlock(); }
    }

    public boolean getIsAdministrador() {
        try{
            readlock.lock();
            return this.isAdministrador;
        }
        finally { readlock.unlock(); }
    }

    public Map<Integer,Viagem> getHistoric() {
        try{
            readlock.lock();
            return new HashMap<>(this.historic);
        }
        finally { readlock.unlock(); }
    }

    public int addHistoric(Viagem v) {
        try{
            writelock.lock();
            this.historic.put(this.historic.size()+1,v.clone());
            return this.historic.size();
        }
        finally { writelock.unlock(); }
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