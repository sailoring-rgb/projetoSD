public class Viagem {
    private String origem;
    private String destino;
    private LocalDateTime partida;
    private LocalDateTime chegada;

    public Viagem(){
        this.origem = "";
        this.destino = "";
        this.partida = LocaDateTime.now();
        this.destino = LocalDateTime.now();
    }

    public Viagem(String origem, String destino, LocalDateTime partida, LocalDateTime chegada){
        this.origem = origem;
        this.destino = destino;
        this.partida = partida;
        this.chegada = chegada;
    }

    public viagem(viagem viagem){
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

    public User clone(){
        return new Viagem(this);
    }

    public String toString () {
        DateTimeFormatter dataformatada = DateTimeFormatter.ofPattern("dd-mm-yyyy");
        StringBuilder builder = new StringBuilder();
        builder.append(this.origem).append(";");
        builder.append(this.destino).append(";");
        builder.append(this.partida.format(dataFormatada)).append(";");
        builder.append(this.chegada.format(dataFormatada)).append(";");
        return builder.toString();
    }
}