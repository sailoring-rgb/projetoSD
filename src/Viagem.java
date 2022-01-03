import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

public class Viagem {
    private String origem;
    private String destino;
    private LocalDateTime partida;
    private LocalDateTime chegada;
    ReentrantLock lock = new ReentrantLock();

    public Viagem(){
        this.origem = "";
        this.destino = "";
        this.partida = LocalDateTime.now();
        this.chegada = LocalDateTime.now();
    }

    public Viagem(String origem, String destino, LocalDateTime partida, LocalDateTime chegada){
        this.origem = origem;
        this.destino = destino;
        this.partida = partida;
        this.chegada = chegada;
    }

    public Viagem(Viagem viagem){
        this.origem = viagem.getOrigem();
        this.destino = viagem.getDestino();
        this.partida = viagem.getPartida();
        this.chegada = viagem.getChegada();
    }

    public String getOrigem() {
        try{
            lock.lock();
            return this.origem;
        }
        finally { lock.unlock(); }
    }

    public void setOrigem(String origem) {
        try {
            lock.lock();
            this.origem = origem;
        }
        finally { lock.unlock(); }
    }

    public String getDestino() {
        try{
            lock.lock();
            return this.destino;
        }
        finally { lock.unlock(); }
    }

    public void setDestino(String destino) {
        try {
            lock.lock();
            this.destino = destino;
        }
        finally { lock.unlock(); }
    }

    public LocalDateTime getPartida() {
        try{
            lock.lock();
            return this.partida;
        }
        finally { lock.unlock(); }
    }

    public void setPartida(LocalDateTime partida) {
        try {
            lock.lock();
            this.partida = partida;
        }
        finally { lock.unlock(); }
    }

    public LocalDateTime getChegada() {
        try{
            lock.lock();
            return this.chegada;
        }
        finally { lock.unlock(); }
    }

    public void setChegada(LocalDateTime chegada) {
        try {
            lock.lock();
            this.chegada = chegada;
        }
        finally { lock.unlock(); }
    }

    public Viagem clone(){
        return new Viagem(this);
    }

    public String toString () {
        DateTimeFormatter dataformatada = DateTimeFormatter.ofPattern("dd-mm-yyyy");
        StringBuilder builder = new StringBuilder();
        builder.append(this.origem).append(";");
        builder.append(this.destino).append(";");
        builder.append(this.partida.format(dataformatada)).append(";");
        builder.append(this.chegada.format(dataformatada)).append(";");
        return builder.toString();
    }
}