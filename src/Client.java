import java.net.Socket;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private static String user;
    private static int admin;
    private static Demultiplexer multi;
    private static Thread warning;

    public static void run() throws IOException, InterruptedException{
        System.out.println("Bem vindo!");
        warning.start();
        menu();
        warning.interrupt();
        multi.close();
        System.out.Println("Adeus!");
    }

    public static void menu() throws InterruptedException{
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        String username = null;

        while(username == null){
            System.out.println(" Introduza a sua escolha: " 
                            + "1: Login \n"
                            + "2: Registar \n"
                            + "\n");
            String option = stdin.readLine();
            switch (option) {
                case "1":
                    login();
                    break;
            
                case "2":
                    registar();
                    break;
        
                default:
                    break;
            }
        }
    }

    public void login() throws InterruptedException{
        Thread t = new Thread(()-> {
            try{
                System.out.println("----------Login----------"
                                + "\n"
                                + " Inserir User Name: ");
                String name = stdin.readLine();

                System.out.println("Inserir palavra-passe: ");
                String password = stdin.readLine();

                multi.send(1, (name+" "+password+" ").getBytes());

                byte[] reply = multi.receive(1);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(1);   // ?????????????????????
                if(error==0){  //??????????????????????????????
                    String[] tokens = new String(reply1).split(":");  
                    System.out.println("\033[1;36m"+ tokens[0] +"\033[0m"); 
                    System.out.println(tokens[1]);
                    admin = Integer.parseInt(tokens[1]);
                    user = name;
                    funcionalidades();
                }else{
                    System.out.println(new String(reply1) + ": Falha na autenticação"); // ?????? rever
                }
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                //menu.printExcecao(e.getMessage());
            }
        });
        t.start();
        t.join();
    }

    public void registar() throws InterruptedException {
        Thread t = new Thread(() -> {
            try{
                admin = -1;
                
                while(admin == -1){
                    System.out.println("É um administrador? ");
                    System.out.println("1: Sim ");
                    System.out.println("2: Nao ");
                    System.out.println("Introduza o numero: ");

                    String option = stdin.readLine();
                    if(option.equals("1")) admin = 1;
                    else{
                        if (option.equals("2")) admin = 0;
                        else System.out.println("\033[1;31m" + "Opção incorreta!" + "\033[0m");
                    }
                }

                System.out.println("Inserir User Name: ");
                String name = stdin.readLine();
                System.out.println("Inserir password: ");
                String password = stdin.readLine();

                multi.send(2, (name+" "+password+" ").getBytes());

                byte[] reply = multi.receive(2);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(2);   // ?????????????????????
                if(error==0){  //??????????????????????????????
                    System.out.println("\033[1;36m"+ new String(reply1) +"\033[0m"); 
                    user = name;
                    funcionalidades();
                }else{
                    System.out.println(new String(reply1) + ": Registo nao efetuado"); // ?????? rever
                }
            }
            catch(NullPointerException | IOException | InterruptedException e){
                //menu.printExcecao(e.getMessage());
            }
        });
        t.start();
        t.join();
    }

    public void funcionalidades(){
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
                insereInformacao();
                break;
            case "2":
                encerrarDia();
                break;
            case "3":
                reservaViagem();
                break;
            case "4":
                cancelarReseva();
                break;
            case "5":
                listaVoos();
                break;
            case "6":
                logout();
                break;
        }
    }

    public void reservaViagem(){
        Thread t = new Thread(() -> {
            try{
                System.out.println("Lista de voos (origem -> destino): ");
                listaVoos();
                System.out.println("Insira percurso completo: ");
                String trip = stdin.readILine();
                System.out.println("Insira possível data de partida(DD-MM-AAAA): ");
                String date1 = stdin.readLine();
                System.out.println("Insira possível data de chegada(DD-MM-AAAA): ");
                String date2 = stdin.readLine();

                //calcular intervalo, reservar e devolver codigo de reserva
            }
        })
    }


    public void cancelarReserva(){
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira User Name: ");
                String name = stdin.readLine();
                System.out.println("Insira o codigo da reserva: "); 
                String cod = stdin.readLine();

                //cancelar reserva
            }
        });
        
    }

    public void listaVoos(){
        Thread t = new Thread(() -> {
            try{
                System.out.println("Lista de voos (origem -> destino): ");

                //apresentar lista
            }
        })
    }

    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("localhost",12345);
        multi = new Demultiplexer(new TaggedConnection(s));
        warning = new Thread(() -> {
            try{
                while(true){
                    byte[] reply = multi.receive(6);
                    System.out.println("\n\033[1;31m» " + new String(reply) + "\033[0m");
                }
            }catch (IOException | InterruptedException e){

            }
        });
        multi.start();
        run();
    }

}
