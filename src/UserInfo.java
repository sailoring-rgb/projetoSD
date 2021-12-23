import Exceptions.*;

import java.io.*;
import java.util.stream.Collectors;
import java.time.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class UserInfo {
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String,User> credentials;

    public UserInfo() {
        this.credentials = new HashMap<>();
    }

    public UserInfo(Map<String,User> credentials) {
        this.credentials = credentials.entrySet().stream().collect(Collectors.toMap(e->e.getKey(),e-> e.getValue().clone()));
    }
/*
    public User userParse(String userInput) throws IOException {
        String[] tokens = userInput.split(";");
        return new User(tokens[0],tokens[1],tokens[2],Integer.parseInt(tokens[3]));
    }
*/
    public String registerNewUser(User user) throws UsernameAlreadyExists {
        // User user = userParse(userInput);
        addNewUser(user.clone());
        try {
            user.lock.lock();
            return user.getUsername();
        }
        finally { user.lock.unlock(); }
    }

    public void addNewUser(User user) throws UsernameAlreadyExists {
        try {
            lock.lock();
            if (credentials.containsKey(user.getUsername())) throw new UsernameAlreadyExists("Este username não está disponível.");
            credentials.put(user.getUsername(),user.clone());
        }
        finally { lock.unlock(); }
    }

    public void removeUser(String username){
        try{
            lock.lock();
            credentials.remove(username);
        }
        finally{ lock.unlock(); }
    }

    public boolean validateUser(String username, String password) throws UsernameNotExist, WrongPassword {
        try {
            lock.lock();
            if (!credentials.containsKey(username)) throw new UsernameNotExist("Este username não existe.");
            String userPassword = credentials.get(username).getPassword();
            if (!userPassword.equals(password)) throw new WrongPassword("Esta password está incorreta.");
            return true;
        }
        finally { lock.unlock(); }
    }

    public boolean isAdministrador(String username){
        try{
            lock.lock();
            return credentials.get(username).getSpecialUser() == -1;
        } finally { lock.unlock(); }
    }

    public User getUser(String username){
        try {
            lock.lock();
            return credentials.get(username);
        }
        finally { lock.unlock(); }
    }
}