import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Viagem {
    private String origin;
    private String destiny;
    private List<String> escalas;
    private LocalDateTime departure;
    private ReadWriteLock lockRW = new ReentrantReadWriteLock();
    private Lock readlock = lockRW.readLock();
    private Lock writelock = lockRW.writeLock();

    public Viagem(List<String> route, LocalDateTime departure){
        this.origin = route.get(0);
        this.destiny = route.get(route.size()-1);
        this.escalas = defineEscalas(route);
        this.departure = departure;
    }

    public Viagem(Viagem viagem){
        this.origin = viagem.getOrigin();
        this.destiny = viagem.getDestiny();
        this.escalas = viagem.getEscalas();
        this.departure = viagem.getDeparture();
    }

    public String getOrigin() {
        try{
            readlock.lock();
            return this.origin;
        } finally { readlock.unlock(); }
    }

    public String getDestiny() {
        try{
            readlock.lock();
            return this.destiny;
        } finally { readlock.unlock(); }
    }

    public LocalDateTime getDeparture() {
        try{
            readlock.lock();
            return this.departure;
        } finally { readlock.unlock(); }
    }

    public List<String> defineEscalas(List<String> route){
        try{
            writelock.lock();
            List<String> escalas = new ArrayList<>(route);
            escalas.remove(0);
            escalas.remove(escalas.size()-1);
            return new ArrayList<>(escalas);
        } finally { writelock.unlock(); }
    }

    public List<String> getEscalas(){
        try{
            readlock.lock();
            return new ArrayList<>(this.escalas);
        } finally { readlock.unlock(); }
    }

    public List<String> returnRoute(){
        try{
            writelock.lock();
            List<String> route = new ArrayList<>();
            route.add(this.origin);
            route.addAll(this.escalas);
            route.add(this.destiny);
            return new ArrayList<>(route);
        } finally { writelock.unlock(); }
    }

    public Viagem clone(){
        return new Viagem(this);
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        StringBuilder builder = new StringBuilder();
        builder.append("*** Route: ").append(this.origin + "->" + String.join("->", this.escalas) + "->" + this.destiny).append("\n");
        builder.append("*** Departure: ").append(this.departure.format(formatter)).append("\n");
        return builder.toString();
    }
}