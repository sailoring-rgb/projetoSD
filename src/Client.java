import java.net.Socket;

import java.net.Socket;
import java.io.*;

public class Client {
    private String user;
    private int isSpecial;
    private Demultiplexer multi;
    private Thread warning;

    public static void run() throws IOException, InterruptedException{
        System.out.println("Bem vindo!");
        warning.start();
        menu();
        warning.interrupt();
        multi.close();
        System.out.println("Adeus!");
    }

    public static void menu() throws InterruptedException, IOException {
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

    public static void login() throws InterruptedException{
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
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
                    isSpecial = Integer.parseInt(tokens[1]);
                    user = name;
                    if(isSpecial == -1){
                        funcionalidadesAdmin();
                    }else{
                        funcionalidadesBasicas();
                    }
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

    public static void registar() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try{
                isSpecial = -1;
                
                while(isSpecial == -1){
                    System.out.println("É um administrador? ");
                    System.out.println("1: Sim ");
                    System.out.println("2: Nao ");
                    System.out.println("Introduza o numero: ");

                    String option = stdin.readLine();
                    if(option.equals("1")) isSpecial = 1;
                    else{
                        if (option.equals("2")) isSpecial = 0;
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
                    if(isSpecial == -1){
                        funcionalidadesAdmin();
                    }else{
                        funcionalidadesBasicas();
                    }
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


public void funcionalidadesAdmin(){
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
                ystem.out.println("Insira intervalo de data possíveis (DD-MM-AAAA;DD-MM-AAAA): ");
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
        Socket socket = new Socket("localhost",12345);
        multi = new Demultiplexer(new TaggedConnection(socket));
        warning = new Thread(() -> {
            try{
                while(true){
                    byte[] reply = multi.receive(6);
                    System.out.println("\n\033[1;31m» " + new String(reply) + "\033[0m");
                }
            } catch (IOException | InterruptedException e) {

            }
        });
        multi.start();
        run();
    }

}
