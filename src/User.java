import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String name;
    private String password;
    ReentrantLock lock = new ReentrantLock();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        try{
            lock.lock();
            return name;
        }
        finally {
            lock.unlock();
        }
    }

    public void setName(String name) {
        try {
            lock.lock();
            this.name = name;
        }
        finally {
            lock.unlock();
        }
    }

    public String getPassword() {
        try{
            lock.lock();
            return password;
        }
        finally {
            lock.unlock();
        }
    }

    public void setPassword(String password){
        try{
            lock.lock();
            this.password = password;
        finally {
            lock.unlock();
        }
    }

    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(";");
        builder.append(this.password).append(";");
        return builder.toString();
    }
}