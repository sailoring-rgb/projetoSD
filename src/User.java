import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String username;
    private String password;
    private String name;
    private int isSpecial; /** 0 -> user normal ; -1 -> administrador */
    ReentrantLock lock = new ReentrantLock();

    public User(String username, String password, String name, int isSpecial) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.isSpecial = isSpecial;
    }

    public User(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.isSpecial = user.getSpecialUser();
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

    public int getSpecialUser() {
        try{
            lock.lock();
            return this.isSpecial;
        }
        finally { lock.unlock(); }
    }

    public void setSpecialUser(int isSpecial){
        try {
            lock.lock();
            this.isSpecial = isSpecial;
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