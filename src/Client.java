import java.net.Socket;

import java.net.Socket;
import java.io.*;

public class Client {
    private static String user;
    private static boolean isAdmin;
    private static Demultiplexer multi;
    private static Thread warning;

    public static void run() throws IOException, InterruptedException{
        System.out.print("\n\nBem vindo!\n\n");
        warning.start();
        menu();
        warning.interrupt();
        multi.close();
        System.out.print("\n\nAdeus!\n\n");
    }

    public static void menu() throws InterruptedException, IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String username = null;
        while(username == null){
            System.out.print("1: Login\n"
                            + "2: Registar\n\n"
                            + "Introduza a sua escolha: ");
            String option = stdin.readLine();
            System.out.println("\n\n");
            switch (option) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                default:
                    break;
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
                System.out.print("----------Login----------\n"
                                + "Inserir username: ");
                String name = stdin.readLine();
                System.out.print("Inserir palavra-passe: ");
                String password = stdin.readLine();
                System.out.print("------------------------\n\n");

                multi.send(1, (name+" "+password+" ").getBytes());

                byte[] reply = multi.receive(1);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(1);   // ?????????????????????
                if(error==0){
                    String[] tokens = new String(reply1).split(":");  
                    System.out.println(tokens[0]);
                    System.out.println(tokens[1]);
                    user = name;
                    if(isAdmin == true){
                        funcionalidadesAdmin();
                    }else{
                        funcionalidadesBasicas();
                    }
                }else{
                    System.out.println(new String(reply1) + ": Falha na autenticação");
                }
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                //menu.printExcecao(e.getMessage());
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
                    System.out.print("----------Registar----------\n"
                                     + "É um administrador?\n"
                                     + "1: Sim\n"
                                     + "2: Nao\n"
                                     + "\nIntroduza o numero: ");
                    String option = stdin.readLine();
                    if(option.equals("1")){
                        isAdmin = true;
                        controlo = 0;
                    }
                    else{
                        if (option.equals("2")){
                            isAdmin = false;
                            controlo = 0;
                        }
                        else System.out.println("Opção incorreta!");
                    }
                }

                System.out.print("\nInserir username: ");
                String username = stdin.readLine();
                System.out.print("Inserir password: ");
                String password = stdin.readLine();
                System.out.print("Inserir nome: ");
                String name = stdin.readLine();
                System.out.print("----------------------------\n\n\n");

                multi.send(2, (username+" "+password+" "+name+" "+isAdmin+" ").getBytes());

                byte[] reply = multi.receive(2);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(2);
                if(error==0){
                    System.out.println(new String(reply1));
                    user = username;
                    if(isAdmin == true){
                        funcionalidadesAdmin();
                    }else{
                        funcionalidadesBasicas();
                    }
                }else{
                    System.out.println(new String(reply1) + ": Registo não efetuado!!");
                }
            }
            catch(NullPointerException | IOException | InterruptedException e){
                //menu.printExcecao(e.getMessage());
            }
        });
        t.start();
        t.join();
    }

public static void funcionalidadesAdmin() throws IOException {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("1: Inserir informacao sobre voos \n" 
                       + "2: Encerrar o dia \n"
                       + "3: Reservar viagem \n"
                       + "4: Cancelar reserva de uma viagem \n"
                       + "5: Lista de voos \n"
                       + "6: Logout \n"
                       + "Introduza o numero: ");
    String option = stdin.readLine();
    switch(option){
        case "1":
            //insereInformacao();
            break;
        case "2":
            //encerrarDia();
            break;
        case "3":
            reservaViagem();
            break;
        case "4":
            //cancelarReseva();
            break;
        case "5":
            listaVoos();
            break;
        case "6":
            //logout();
            break;
    }
}

    public static void funcionalidadesBasicas() throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("1: Reservar viagem \n"
                         + "2: Cancelar reserva de uma viagem \n"
                         + "3: Lista de voos \n"
                         + "4: Logout \n"
                         + "Introduza o numero: ");
        String option = stdin.readLine();
        switch(option){
            case "1":
                reservaViagem();
                break;
            case "2":
                //cancelarReseva();
                break;
            case "3":
                listaVoos();
                break;
            case "4":
                //logout();
                break;
        }
    }

    public static void reservaViagem(){
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try{
                System.out.println("Lista de voos (origem -> destino): ");
                listaVoos();
                System.out.println("Insira percurso completo: ");
                String trip = stdin.readLine();
                System.out.println("Insira intervalo de data possíveis (DD-MM-AAAA;DD-MM-AAAA): ");
                String date1 = stdin.readLine();

                //calcular intervalo, reservar e devolver codigo de reserva
                //se tivermos com a lista de viagens existentes, o servidor colhe a data mais proxima dentro do intervalo
                //se não o servior calcula uma media do intervalo
            } catch (IOException e) {
            e.printStackTrace();
            }
        });
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
                    System.out.println(new String(reply));
                }
            } catch (IOException | InterruptedException e) {}
        });
        multi.start();
        run();
    }

}
