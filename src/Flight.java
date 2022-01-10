import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public class Flight {
    private String origin;
    private String destiny;
    private List<String> escalas;
    private LocalDateTime departure;
    ReentrantLock lock = new ReentrantLock();

    public Flight(List<String> route, LocalDateTime departure){
        this.origin = route.get(0);
        this.destiny = route.get(route.size()-1);
        this.escalas = defineEscalas(route);
        this.departure = departure;
    }

    public Flight(Flight viagem){
        this.origin = viagem.getOrigin();
        this.destiny = viagem.getDestiny();
        this.escalas = viagem.getEscalas();
        this.departure = viagem.getDeparture();
    }

    public String getOrigin() {
        try{
            lock.lock();
            return this.origin;
        } finally { lock.unlock(); }
    }

    public String getDestiny() {
        try{
            lock.lock();
            return this.destiny;
        } finally { lock.unlock(); }
    }

    public LocalDateTime getDeparture() {
        try{
            lock.lock();
            return this.departure;
        } finally { lock.unlock(); }
    }

    public List<String> defineEscalas(List<String> route){
        try{
            lock.lock();
            List<String> escalas = new ArrayList<>(route);
            escalas.remove(0);
            escalas.remove(escalas.size()-1);
            return new ArrayList<>(escalas);
        } finally { lock.unlock(); }
    }

    public List<String> getEscalas(){
        try{
            lock.lock();
            return new ArrayList<>(this.escalas);
        } finally { lock.unlock(); }
    }

    public List<String> returnRoute(){
        try{
            lock.lock();
            List<String> route = new ArrayList<>();
            route.add(this.origin);
            route.addAll(this.escalas);
            route.add(this.destiny);
            return new ArrayList<>(route);
        } finally { lock.unlock(); }
    }

    public Flight clone(){
        return new Flight(this);
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        StringBuilder builder = new StringBuilder();
        builder.append("*** Route: ").append(this.origin + "->" + String.join("->", this.escalas) + "->" + this.destiny).append("\n");
        builder.append("*** Departure: ").append(this.departure.format(formatter)).append("\n");
        return builder.toString();
    }
}