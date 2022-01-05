import Exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public User getUser(String username){
        try {
            lock.lock();
            return credentials.get(username);
        }
        finally { lock.unlock(); }
    }

    public boolean registerUser(String username, String password, String name, Boolean isAdmin) throws UsernameAlreadyExists {
        try {
            lock.lock();
            if (credentials.containsKey(username)){
                throw new UsernameAlreadyExists("Este username não está disponível");
            } else{
                Map<Integer,Viagem> historic = new HashMap<>();
                User user = new User(username,password,name,isAdmin,historic);
                this.user = username;
                credentials.put(username,user.clone());
                return true;
            }
        }
        finally { lock.unlock(); }
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

    public int makeReservation(String route, String dates) throws ClosedDate{
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
                    flight.setCapacity(v.getCapacity()-1);
                    break;
                }
            }
            this.flights.add(flight);
            return this.getUser(user).addHistoric(flight);
        } finally { lock.unlock(); }
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

    public void cancelReservation(String codString) throws CodeNotExist, ClosedDate {
        try {
            lock.lock();
            int codigo = Integer.parseInt(codString);
            if (!this.getUser(this.user).getHistoric().containsKey(codigo))
                throw new CodeNotExist("Este código de reserva não existe");
            Viagem flight = this.getUser(this.user).getHistoric().get(codigo);
            if (closedDates.contains(flight.getDeparture())) throw new ClosedDate("Este dia foi encerrado");
            this.getUser(this.user).getHistoric().remove(codigo);
        } finally { lock.unlock(); }
    }

    public String flightsList() {
        try {
            lock.lock();
            String res = "";
            String res1 = "";
            for(Viagem v: this.flights){
                res = v.toString();
                System.out.print(res);
                res1 = String.join("\n",res1,res);
            }
            return res1;
        } finally { lock.unlock(); }
    }

    public void insertInf(String origin, String destiny, int capacity){
        try{
            lock.lock();
            Viagem flight = new Viagem(origin,destiny,capacity);
            this.flights.add(flight.clone());
        }finally{ lock.unlock(); }
    }

    //public void closeDay ()
}