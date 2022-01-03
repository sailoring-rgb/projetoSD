import Exceptions.UsernameAlreadyExists;
import Exceptions.UsernameNotExist;
import Exceptions.WrongPassword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private static UserInfo info;
    private static Map<String,TaggedConnection> connections = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12343);
        info = new UserInfo();

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

                        } catch ( UsernameAlreadyExists | UsernameNotExist | WrongPassword e) {
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