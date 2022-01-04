import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public class Viagem {
    private String origin;
    private String destiny;
    private LocalDateTime departure;
    //private LocalDateTime arrival;
    private int capacity;
    ReentrantLock lock = new ReentrantLock();

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
            lock.lock();
            return this.origin;
        }
        finally { lock.unlock(); }
    }

    public String getDestiny() {
        try{
            lock.lock();
            return this.destiny;
        }
        finally { lock.unlock(); }
    }

    public LocalDateTime getDeparture() {
        try{
            lock.lock();
            return this.departure;
        }
        finally { lock.unlock(); }
    }
/*
    public LocalDateTime getArrival() {
        try{
            lock.lock();
            return this.arrival;
        }
        finally { lock.unlock(); }
    }
*/
    public int getCapacity(){
        try{
            lock.lock();
            return this.capacity;
        }
        finally { lock.unlock(); }
    }

    public void setCapacity(int capacity){
        try{
            lock.lock();
            this.capacity = capacity;
        }
        finally { lock.unlock(); }
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