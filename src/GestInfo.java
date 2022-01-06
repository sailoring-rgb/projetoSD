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
    private Set<LocalDateTime> closedDates;
    private Map<String,Integer> flights;

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
                Map<Integer,Viagem> historic = new HashMap<>();
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

    public int makeReservation(String route, String dates) throws ClosedDate, FlightNotAvailable{
        try {
            lock.lock();
            List<String> tokens1 = Arrays.asList(route.split("->"));  // escalas do percurso
            String[] tokens2 = dates.split(";");   // datas do intervalo

            LocalDateTime date = pickDate(tokens2);

            Viagem flight;
            int size = tokens1.size();
            if(flightAvailable(tokens1,size)){
                flight = new Viagem(tokens1.get(0),tokens1.get(size-1),date);
                return this.getUser(user).addHistoric(flight);
            }
            else throw new FlightNotAvailable("Este voo não está disponível");

            // NOTA: A CAPACIDADE DO VOO NÃO É DECREMENTADA, BEM COMO, QUANDO É CANCELADA, NÃO É INCREMENTADA.

        } finally { lock.unlock(); }
    }

    public LocalDateTime pickDate(String[] tokens) throws ClosedDate {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        try {
            lock.lock();
            LocalDateTime date1 = LocalDate.parse(tokens[0], formatter).atStartOfDay();
            LocalDateTime date2 = LocalDate.parse(tokens[1], formatter).atStartOfDay();
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
            this.getUser(this.user).removeHistoric(codigo);
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

    public boolean flightAvailable(List<String> tokens, int size){
        boolean exists = false;
        System.out.println(tokens);
        try{
            lock.lock();
            for (int i = 1; i <= size-1; i++) {
                String res2 = tokens.get(i-1) + "->" + tokens.get(i);
                if(this.flights.containsKey(res2) && this.flights.get(res2) > 0){
                    exists = true;
                    break;
                }
                else exists = false;
            }
            return exists;
        } finally { lock.unlock(); }
    }

    public void insertInf(String origin, String destiny, String capString){
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

    public String reservationsList(){
        try{
            lock.lock();
            StringBuilder builder = new StringBuilder();
            if(this.getUser(this.user).getHistoric().isEmpty()) builder.append("Ainda não foram registadas reservas.");
            else{
                for(Map.Entry<Integer,Viagem> v : this.getUser(this.user).getHistoric().entrySet()){
                    builder.append("\nCódigo: ").append(v.getKey()).append("\n");
                    builder.append(v.getValue().toString());
                }
            }
            return builder.toString();
        }finally{ lock.unlock();}
    }
}
