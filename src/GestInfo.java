import Exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class GestInfo {
    private String user;
    private ReentrantLock lock = new ReentrantLock();
    private Map<String,User> credentials;       // username; user desse username
    private Set<LocalDateTime> closedDates;
    private Map<String,Integer> flights;        // percurso do voo; capacidade do voo

    public GestInfo() {
        this.credentials = new HashMap<>();
        this.closedDates = new HashSet<>();
        this.flights = new HashMap<>();
    }

    public User getUser(String username){
        try {
            lock.lock();
            return credentials.get(username);
        }
        finally { lock.unlock(); }
    }

    public boolean signup(String username, String password, String name, Boolean isAdmin) throws UsernameAlreadyExists {
        try {
            lock.lock();
            if (credentials.containsKey(username)){
                throw new UsernameAlreadyExists("Este username não está disponível");
            } else {
                Map<Integer, Flight> historic = new HashMap<>();
                User user = new User(username,password,name,isAdmin,historic);
                this.user = username;
                credentials.put(this.user,user.clone());
                return true;
            }
        }
        finally { lock.unlock(); }
    }

    public boolean login(String user, String password) throws UserNotExist, WrongPass {
        try {
            lock.lock();
            if (!credentials.containsKey(user)) throw new UserNotExist("Este username não existe");
            String userPassword = credentials.get(user).getPassword();
            if (!userPassword.equals(password)) throw new WrongPass("Esta password está incorreta");
            this.user = user;
            return true;
        }
        finally { lock.unlock(); }
    }

    public int reserveFlight(String route,String date1,String date2) throws ClosedDate, FlightNotAvailable, FlightOverbooked{
        try {
            lock.lock();
            List<String> tokens1 = Arrays.asList(route.split("->"));  // escalas do percurso

            LocalDateTime date = pickDate(date1,date2);

            Flight flight;
            int size = tokens1.size();
            if(flightAvailable(tokens1,size)){
                flight = new Flight(tokens1,date);
                flight.lock.lock();

                // decrementar a capacidade de cada um dos voos
                for (int i = 1; i <= size-1; i++) {
                    String scale = tokens1.get(i-1) + "->" + tokens1.get(i);
                    int capacity = this.flights.get(scale);
                    this.flights.put(scale,capacity-1);
                }
                int code = this.getUser(user).addHistoric(flight);
                flight.lock.unlock();
                return code;
            }
            else throw new FlightNotAvailable("Este voo não está disponível");
        } finally { lock.unlock(); }
    }

    public LocalDateTime pickDate(String dateStr1, String dateStr2) throws ClosedDate{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        try {
            lock.lock();
            LocalDateTime date1 = LocalDate.parse(dateStr1, formatter).atStartOfDay();
            LocalDateTime date2 = LocalDate.parse(dateStr2, formatter).atStartOfDay();
            if(closedDates.isEmpty()) return date1;
            else{
                if (!closedDates.contains(date1)) return date1;
                else if (!closedDates.contains(date2)) return date2;
                else throw new ClosedDate("Estes dias foram encerrados");
            }
        } finally { lock.unlock(); }
    }

    public boolean flightAvailable(List<String> tokens, int size) throws FlightOverbooked{
        boolean exists = false;
        try{
            lock.lock();
            for (int i = 1; i <= size-1; i++) {
                String res2 = tokens.get(i-1) + "->" + tokens.get(i);
                if(this.flights.containsKey(res2)){
                    if(this.flights.get(res2) <= 0) throw new FlightOverbooked("A capacidade máxima já foi atingida");
                    exists = true;
                }
                else{
                    exists = false;
                    break;
                }
            }
            return exists;
        } finally { lock.unlock(); }
    }

    public void cancelReservation(String codString) throws CodeNotExist, ClosedDate {
        int code = Integer.parseInt(codString);
        try {
            lock.lock();
            if (!this.getUser(this.user).getHistoric().containsKey(code)) throw new CodeNotExist("Este código de reserva não existe");
            Flight flight = this.getUser(this.user).getHistoric().get(code);

            if (closedDates.contains(flight.getDeparture())) throw new ClosedDate("Este dia foi encerrado");
            this.getUser(this.user).removeHistoric(code);

            // incrementar a capacidade de cada um dos voos
            flight.lock.lock();
            List<String> route = flight.returnRoute();
            for(int i = 1; i <= route.size()-1; i++){
                String scale = route.get(i-1) + "->" + route.get(i);
                int capacity = this.flights.get(scale);
                this.flights.put(scale,capacity+1);
            }
            flight.lock.unlock();
        } finally { lock.unlock(); }
    }

    public void insertInfo(String origin, String destiny, String capString){
        try{
            lock.lock();
            int capacity = Integer.parseInt(capString);
            String res = origin + "->" + destiny;
            this.flights.put(res,capacity);
        } finally{ lock.unlock(); }
    }

    public void closeDay(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        LocalDateTime date1 = LocalDate.parse(date, formatter).atStartOfDay();
        try {
            lock.lock();
            closedDates.add(date1);
        } finally { lock.unlock(); }
    }

    public String flightsList() {
        try {
            lock.lock();
            String res1 = "";
            if(this.flights.isEmpty()) res1 = "Ainda não foi registado nenhum voo.";
            else
                for (String s: this.flights.keySet()) {
                    String newS = "Route: " + s;
                    res1 = String.join("\n", res1, newS);
                }
            return res1;
        } finally { lock.unlock(); }
    }

    public String reservationsList(){
        try{
            lock.lock();
            StringBuilder builder = new StringBuilder();
            if(this.getUser(this.user).getHistoric().isEmpty()) builder.append("Ainda não foram registadas reservas.");
            else{
                for(Map.Entry<Integer, Flight> v : this.getUser(this.user).getHistoric().entrySet()){
                    builder.append("\nCódigo: ").append(v.getKey()).append("\n");
                    builder.append(v.getValue().toString());
                }
            }
            return builder.toString();
        } finally{ lock.unlock(); }
    }
}
