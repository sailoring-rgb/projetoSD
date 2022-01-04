import Exceptions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class GestInfo {
    private String user;
    private ReentrantLock lock = new ReentrantLock();
    private Map<String,User> credentials;
    private List<Date> closedDates;
    private List<Viagem> flights;

    public GestInfo() {
        this.credentials = new HashMap<>();
        this.closedDates = new ArrayList<>();
        this.flights = new ArrayList<>();
    }

    public GestInfo(Map<String,User> credentials, List<Date> closedDates, List<Viagem> flights) {
        this.user = "";
        this.credentials = credentials.entrySet().stream().collect(Collectors.toMap(e->e.getKey(),e-> e.getValue().clone()));
        this.closedDates = new ArrayList<>(closedDates);
        this.flights = new ArrayList<>(flights);
    }

    public boolean registerUser(String username, String password, String name, Boolean isAdmin) throws UsernameAlreadyExists {
        try {
            lock.lock();
            if (credentials.containsKey(username)){
                throw new UsernameAlreadyExists("Este username não está disponível");
            }
            else{
                Map<Integer,Viagem> historic = new HashMap<>();
                User user = new User(username,password,name,isAdmin,historic);
                this.user = username;
                credentials.put(username,user.clone());
                return true;
            }
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
            if (!credentials.containsKey(username)) throw new UsernameNotExist("Este username não existe");
            String userPassword = credentials.get(username).getPassword();
            if (!userPassword.equals(password)) throw new WrongPassword("Esta password está incorreta");
            this.user = username;
            return true;
        }
        finally { lock.unlock(); }
    }

    public boolean isAdministrador(String username){
        try{
            lock.lock();
            return credentials.get(username).getIsAdministrador();
        } finally { lock.unlock(); }
    }

    public void addReservation(String route, String dates) throws ClosedDate{
        try {
            lock.lock();
            String[] tokens1 = route.split("->");
            String[] tokens2 = dates.split(";");
            int size = tokens1.length;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
            LocalDateTime date1 = LocalDate.parse(tokens2[0], formatter).atStartOfDay();
            LocalDateTime date2 = LocalDate.parse(tokens2[1], formatter).atStartOfDay();
            LocalDateTime date = pickDate(date1, date2);

            Viagem flight = new Viagem(tokens1[0], tokens1[size - 1], date);
            // FALTA IMPLEMENTAR A QUESTÃO DAS ESCALAS
            for (Viagem v : this.flights) {
                if (v.getOrigin().equals(flight.getOrigin()) && v.getDestiny().equals(flight.getDestiny()) && v.getDeparture().equals(flight.getDeparture())) {
                    v.setCapacity(v.getCapacity() - 1);
                    this.getUser(user).getHistoric().put(this.getUser(user).getHistoric().size() + 1, v.clone());
                    break;
                }
            }
            this.flights.add(flight);
            this.getUser(user).getHistoric().put(this.getUser(user).getHistoric().size() + 1, flight.clone());
        } finally { lock.unlock(); }
    }

    public void cancelarReserva(String Username, String codR){
            try{
                lock.lock();
                credentials.get(Username).removeViagem(codR);

            }finally {
                lock.unlock();
            }
    }

    public LocalDateTime pickDate(LocalDateTime date1, LocalDateTime date2) throws ClosedDate {
        try {
            lock.lock();
            if(closedDates.isEmpty()) return date1;
            else{
                if (!closedDates.contains(date1)) return date1;
                else if (!closedDates.contains(date2)) return date2;
                else throw new ClosedDate("Estes dias foram encerrados");
            }
        } finally {
            lock.unlock();
        }
    }

    public List<Viagem> getFlights() {
        try {
            lock.lock();
            return flights;
        }finally {
            lock.unlock();
        }

    }

    public User getUser(String username){
        try {
            lock.lock();
            return credentials.get(username);
        }
        finally { lock.unlock(); }
    }
}