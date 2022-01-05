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
                                if (info.validateUser(tokens[0],tokens[1])) {
                                    System.out.print("A validar as credenciais...\n\n");
                                    connections.put(tokens[0],c);
                                    c.send(frame.tag, String.valueOf(cond).getBytes());
                                    c.send(frame.tag, String.valueOf(info.getUser(tokens[0]).getIsAdministrador()).getBytes());
                                    c.send(frame.tag, ("Login efetuado com sucesso!!").getBytes());
                                }
                            }
                            else if (frame.tag == 2) {
                                String[] tokens = data.split(" ");
                                if(info.registerUser(tokens[0],tokens[1],tokens[2],Boolean.parseBoolean(tokens[3]))) {
                                    System.out.print("A registar o novo cliente...\n\n");
                                    connections.put(tokens[0], c);
                                    c.send(frame.tag, String.valueOf(cond).getBytes());
                                    c.send(frame.tag, "Registo efetuado com sucesso!!".getBytes());
                                }
                            }
                            else if (frame.tag == 3) {
                                String[] tokens = data.split(" ");
                                System.out.print("A reservar viagem...\n\n");
                                int codigo = info.makeReservation(tokens[0],tokens[1]);
                                c.send(frame.tag,String.valueOf(cond).getBytes());
                                c.send(frame.tag,("Reserva efetuada com sucesso!!: Código "+codigo).getBytes());
                            }
                            else if (frame.tag == 4) {
                                String[] tokens = data.split(" ");
                                System.out.print("A cancelar reserva...\n\n");
                                info.cancelReservation(tokens[0]);
                                c.send(frame.tag,String.valueOf(cond).getBytes());
                                c.send(frame.tag,"Cancelamento efetuado com sucesso!!".getBytes());
                            }
                            else if (frame.tag == 5){
                                System.out.print("A carregar lista de voos...\n\n");
                                String list = info.flightsList();
                                c.send(frame.tag,list.getBytes());
                            }
                            else if (frame.tag == 6){
                                String[] tokens = data.split(" ");
                                System.out.print("A inserir informação sobre voo...\n\n");
                                info.insertInf(tokens[0],tokens[1],tokens[2]);
                                c.send(frame.tag,"\n\nInformação inserida com sucesso!!".getBytes());
                            }
                            else if (frame.tag == 7){
                                String[] tokens = data.split(" ");
                                System.out.print("A encerrar dia...\n\n");
                                info.closeDay(tokens[0]);
                                c.send(frame.tag,"\n\nEncerramento efetuado com sucesso!!".getBytes());
                            }

                            else if(frame.tag == 8){
                                System.out.print("A carregar reservas de voos...\n\n");
                                String list = info.flightsReservations();
                                c.send(frame.tag,list.getBytes());
                            }

                        } catch ( UsernameAlreadyExists | UsernameNotExist | WrongPassword | ClosedDate | CodeNotExist e) {
                            cond = 1;
                            c.send(frame.tag, String.valueOf(cond).getBytes());
                            c.send(frame.tag, e.getMessage().getBytes());
                        }
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            };

            new Thread(worker).start();
        }
    }
        /*
        try {
            ServerSocket ss = new ServerSocket(12345);
            info = new UserInfo();

            while (true) {
                System.out.println("À espera de pedidos de clientes...\n");
                Socket socket = ss.accept();
                TaggedConnection c = new TaggedConnection(socket);
                //criação de uma thread de forma a tratar cada um dos clientes que querem fazer pedidos
                Thread t = new Thread(new ServerWorker(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() throws IOException {
    }


    static class ServerWorker implements Runnable {
        private Socket s;

        public ServerWorker(Socket socket) {
            this.s = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream());

                out.flush();

                s.shutdownOutput();
                s.shutdownInput();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    */

}