import java.net.Socket;

import java.net.Socket;
import java.io.*;

public class Client {
    private static String user;
    private static boolean isAdmin;
    private static Demultiplexer multi;
    private static Thread warning;

    public static void run() throws IOException, InterruptedException{
        System.out.print("\n\n\033[1;35mSeja Bem-vindo!\033[0m\n\n");
        warning.start();
        menu();
        warning.interrupt();
        multi.close();
        System.out.print("\n\n\033[1;35mAté à Próxima!\033[0m\n\n");
    }

    public static void menu() throws InterruptedException, IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String username = null;
        while(username == null){
            System.out.print("---------------MENU---------------\n"
                            + "1: Login\n"
                            + "2: Registar\n\n"
                            + "0: Sair\n"
                            + "----------------------------------\n"
                            + "Introduza a opção: ");
            String option = stdin.readLine();
            System.out.println("\n");
            if(option.equals("1"))
                login();
            else if (option.equals("2"))
                register();
            else if (option.equals("0"))
                logout();
            else{
                System.out.println("\033[0;31mOpção incorreta!\033[0m");
                System.out.println("\n\n");
            }
        }
    }

    public static void logout() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                warning.interrupt();
                multi.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
    }

    public static void login() throws InterruptedException{
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(()-> {
            try{
                System.out.print("Inserir username: ");
                String name = stdin.readLine();
                System.out.print("Inserir palavra-passe: ");
                String password = stdin.readLine();

                multi.send(1, (name+" "+password+" ").getBytes());

                byte[] reply = multi.receive(1);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(1);
                System.out.println("\n");
                if(error==0){
                    System.out.print(new String(reply1) + "\n\n");
                    user = name;
                    if(isAdmin) funcionalidadesAdmin();
                    else funcionalidadesBasicas();
                }else
                    System.out.println("\n\033[0;31m" + new String(reply1) + ": Falha na autenticação" + "\033[0m");
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                System.out.print(e.getMessage() + "\n\n");
            }
        });
        t.start();
        t.join();
    }

    public static void register() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try{
                int controlo = -1;
                
                while(controlo == -1){
                    System.out.print("É um administrador?\n");
                    System.out.print("1: Sim\n");
                    System.out.print("2: Não\n\n");
                    System.out.print("Introduza o número: ");
                    String option = stdin.readLine();
                    if(option.equals("1")){
                        isAdmin = true;
                        controlo = 0;
                    }
                    else if (option.equals("2")){
                            isAdmin = false;
                            controlo = 0;
                        }
                    else System.out.print("\n\033[0;31mOpção incorreta!\033[0m\n\n");
                }

                System.out.print("\nInserir username: ");
                String username = stdin.readLine();
                System.out.print("Inserir password: ");
                String password = stdin.readLine();
                System.out.print("Inserir nome: ");
                String name = stdin.readLine();

                multi.send(2, (username+" "+password+" "+name+" "+isAdmin+" ").getBytes());

                byte[] reply = multi.receive(2);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(2);
                System.out.println("\n");
                if(error==0){
                    System.out.println(new String(reply1) + "\n\n");
                    user = username;
                    if(isAdmin) funcionalidadesAdmin();
                    else funcionalidadesBasicas();
                }else{
                    System.out.print("\033[0;31m" + new String(reply1) + ": Registo não efetuado!!" + "\n\n\033[0m");
                }
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                System.out.print(e.getMessage() + "\n\n");
            }
        });
        t.start();
        t.join();
    }

    public static void funcionalidadesAdmin() throws IOException, InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("-----------------MENU-----------------\n"
                       + "1: Inserir informacao sobre voos \n"
                       + "2: Encerrar o dia \n"
                       + "3: Reservar viagem \n"
                       + "4: Cancelar reserva de uma viagem \n"
                       + "5: Lista de voos \n\n"
                       + "0: Logout \n"
                       + "--------------------------------------\n");
        System.out.print("Introduza a opção: ");
        String option = stdin.readLine();
        if(option.equals("1"));
            //insereInformacao();
        else if (option.equals("2"));
            //encerrarDia();
        else if (option.equals("3"))
            reservaViagem();
        else if (option.equals("4"));
            //cancelarReseva();
        else if (option.equals("5"))
            listaVoos();
        else if (option.equals("0"))
            logout();
        else System.out.println("\033[0;31mOpção incorreta!\033[0m");
        System.out.println("\n\n");
    }

    public static void funcionalidadesBasicas() throws IOException, InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("-----------------MENU-----------------\n"
                         + "1: Reservar viagem \n"
                         + "2: Cancelar reserva de uma viagem \n"
                         + "3: Lista de voos\n\n"
                         + "0: Logout \n"
                         + "--------------------------------------\n\n"
                         + "Introduza a opção: ");
        String option = stdin.readLine();
        if(option.equals("1"))
            reservaViagem();
        else if (option.equals("2"));
            //cancelarReseva();
        else if (option.equals("3"))
            listaVoos();
        else if (option.equals("0"))
            logout();
        else System.out.println("\033[0;31mOpção incorreta!\033[0m");
        System.out.println("\n\n");
    }

    public static void reservaViagem() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try {
                //System.out.println("Lista de voos (origem -> destino): ");
                //listaVoos();
                System.out.print("Insira percurso completo (A->B): ");
                String trip = stdin.readLine();
                System.out.print("Insira intervalo de data possíveis (DD-MM-AAAA;DD-MM-AAAA): ");
                String date = stdin.readLine();

                multi.send(3, (user+" "+trip+" "+date+" ").getBytes());

                byte[] reply = multi.receive(3);
                int error = Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(3);
                if (error == 0) System.out.println("\033[1;36m" + new String(reply1) + "\033[0m");
                else System.out.print("\033[0;31m" + new String(reply1) + ": Reserva não efetuada!!" + "\n\n\033[0m");
                System.out.println("\n");
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                    System.out.print(e.getMessage() + "\n\n");
            }
        });
        t.start();
        t.join();
    }

    public void cancelarReserva(){
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira User Name: ");
                String name = stdin.readLine();
                System.out.println("Insira o codigo da reserva: ");
                String cod = stdin.readLine();

                //cancelar reserva
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
    }

    public static void listaVoos(){
        Thread t = new Thread(() -> {
            System.out.println("Lista de voos (origem -> destino): ");

            //apresentar lista
        });
    }

    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("localhost",12343);
        multi = new Demultiplexer(new TaggedConnection(socket));
        warning = new Thread(() -> {
            try{
                while(true){
                    byte[] reply = multi.receive(6);
                    System.out.print(new String(reply) + "\n\n");
                }
            } catch (IOException | InterruptedException e) {}
        });
        multi.start();
        run();
    }

}
