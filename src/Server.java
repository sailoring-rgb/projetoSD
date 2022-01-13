import Exceptions.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private static GestInfo info;
    private static Map<String,TaggedConnection> connections = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12343);
        info = new GestInfo();

        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);

            Runnable worker = () -> {
                try {
                    for (;;) {
                        int cond = 0;
                        TaggedConnection.Frame frame = c.receive();
                        String data = new String(frame.data);
                        try {
                            if (frame.tag == 1) {
                                String[] tokens = data.split(" ");
                                if (info.login(tokens[0],tokens[1])) {
                                    System.out.print("A validar as credenciais...\n");
                                    connections.put(tokens[0],c);
                                    c.send(frame.tag, String.valueOf(cond).getBytes());
                                    c.send(frame.tag, ("Login efetuado com sucesso!!"+"-"+info.getUser(tokens[0]).getIsAdministrador()+"-").getBytes());
                                }
                            }
                            else if (frame.tag == 2){
                                String[] tokens = data.split(" ");
                                if(info.signup(tokens[0],tokens[1],tokens[2],Boolean.parseBoolean(tokens[3]))) {
                                    System.out.print("A registar o novo cliente...\n");
                                    connections.put(tokens[0], c);
                                    c.send(frame.tag, String.valueOf(cond).getBytes());
                                    c.send(frame.tag, "Registo efetuado com sucesso!!".getBytes());
                                }
                            }
                            else if (frame.tag == 3) {
                                String[] tokens = data.split(";");
                                System.out.print("A reservar viagem...\n");
                                int code = info.reserveFlight(tokens[0],tokens[1],tokens[2]);
                                c.send(frame.tag,String.valueOf(cond).getBytes());
                                c.send(frame.tag,("Reserva efetuada com sucesso!!: Código "+code).getBytes());
                            }
                            else if (frame.tag == 4) {
                                String[] tokens = data.split(" ");
                                System.out.print("A cancelar reserva...\n");
                                info.cancelReservation(tokens[0]);
                                c.send(frame.tag,String.valueOf(cond).getBytes());
                                c.send(frame.tag,"Cancelamento efetuado com sucesso!!".getBytes());
                            }
                            else if (frame.tag == 5){
                                System.out.print("A carregar lista de voos...\n");
                                String list = info.flightsList();
                                c.send(frame.tag,list.getBytes());
                            }
                            else if (frame.tag == 6){
                                String[] tokens = data.split(";");
                                System.out.print("A inserir informação sobre voo...\n");
                                info.insertInfo(tokens[0],tokens[1],tokens[2]);
                                c.send(frame.tag,"\n\nInformação inserida com sucesso!!".getBytes());
                            }
                            else if (frame.tag == 7){
                                String[] tokens = data.split(" ");
                                System.out.print("A encerrar dia...\n");
                                info.closeDay(tokens[0]);
                                c.send(frame.tag,String.valueOf(cond).getBytes());
                                c.send(frame.tag,"\n\nEncerramento efetuado com sucesso!!".getBytes());
                            }
                            else if(frame.tag == 8){
                                System.out.print("A carregar reservas de voos...\n");
                                String list = info.reservationsList();
                                c.send(frame.tag,list.getBytes());
                            }

                        } catch ( UserNotExist | WrongPass | UsernameAlreadyExists | ClosedDate |
                                  CodeNotExist | FlightNotAvailable | FlightOverbooked | DayAlreadyCancelled e) {
                            cond = 1;
                            c.send(frame.tag, String.valueOf(cond).getBytes());
                            c.send(frame.tag, e.getMessage().getBytes());
                        }
                        System.out.print("Processo terminado.\n\n");
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            };

            new Thread(worker).start();
        }
    }
}