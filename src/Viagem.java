import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Viagem {
    private String origin;
    private String destiny;
    private LocalDateTime departure;
    //private LocalDateTime arrival;
    private int capacity;
    private ReadWriteLock lockRW = new ReentrantReadWriteLock();
    private Lock readlock = lockRW.readLock();
    private Lock writelock = lockRW.writeLock();
    //ReentrantLock lock = new ReentrantLock();

    public Viagem(){
        this.origin = "";
        this.destiny = "";
        this.departure = LocalDateTime.now();
        this.capacity = 0;
    }

    public Viagem(String origin, String destiny, LocalDateTime departure){
        this.origin = origin;
        this.destiny = destiny;
        this.departure = departure;
        this.capacity = 400;
        //this.arrival = arrival;
    }

    public Viagem(Viagem viagem){
        this.origin = viagem.getOrigin();
        this.destiny = viagem.getDestiny();
        this.departure = viagem.getDeparture();
        //this.arrival = viagem.getArrival();
        this.capacity = viagem.getCapacity();
    }

    public String getOrigin() {
        try{
            readlock.lock();
            return this.origin;
        }
        finally { readlock.unlock(); }
    }

    public String getDestiny() {
        try{
            readlock.lock();
            return this.destiny;
        }
        finally { readlock.unlock(); }
    }

    public LocalDateTime getDeparture() {
        try{
            readlock.lock();
            return this.departure;
        }
        finally { readlock.unlock(); }
    }

    public int getCapacity(){
        try{
            readlock.lock();
            return this.capacity;
        }
        finally { readlock.unlock(); }
    }

    public void setCapacity(int capacity){
        try{
            writelock.lock();
            this.capacity = capacity;
        }
        finally { writelock.unlock(); }
    }

    public Viagem clone(){
        return new Viagem(this);
    }

    public String toString () {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd",Locale.US);
        StringBuilder builder = new StringBuilder();
        builder.append(this.origin).append(";");
        builder.append(this.destiny).append(";");
        builder.append(this.departure.format(formatter)).append(";");
        //builder.append(this.arrival.format(dataformatada)).append(";");
        return builder.toString();
    }
}