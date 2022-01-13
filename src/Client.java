import java.net.Socket;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Client {
    private static String user;
    private static boolean isAdmin;
    private static Demultiplexer multi;
    private static Thread warning;

    public static void run() throws IOException, InterruptedException{
        System.out.print("\n\n\033[1;35mSeja Bem-vindo!\033[0m\n\n\n");
        warning.start();
        menu();
        warning.interrupt();
        multi.close();
    }

    /************************************************ MENUS ************************************************/

    public static void menu() throws InterruptedException, IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String username = null;
        while(username == null){
            System.out.print("-----------------MENU-----------------\n"
                    + "1: Login\n"
                    + "2: Registar\n\n"
                    + "0: Sair\n"
                    + "--------------------------------------\n"
                    + "Introduza a opção: ");
            String option = stdin.readLine();
            System.out.println("\n");
            if(option.equals("1"))
                login();
            else if (option.equals("2"))
                register();
            else if (option.equals("0"))
                logout(0);
            else{
                System.out.println("\n\033[0;31mOpção incorreta!\033[0m");
                System.out.println("\n\n");
            }
        }
    }


    public static void menuAdmin() throws IOException, InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        boolean res = true;
        while(res) {
            System.out.print("-----------------MENU-----------------\n"
                    + "1: Inserir informacao sobre voos \n"
                    + "2: Encerrar o dia \n"
                    + "3: Reservar viagem \n"
                    + "4: Cancelar reserva de uma viagem \n"
                    + "5: Consultar lista de voos existentes\n"
                    + "6: Consultar minhas reservas de voos\n\n"
                    + "0: Logout \n"
                    + "--------------------------------------\n");
            System.out.print("Introduza a opção: ");
            String option = stdin.readLine();
            if (option.equals("1"))
                insertInfo();
            else if (option.equals("2"))
                closeDay();
            else if (option.equals("3"))
                reserveFlight();
            else if (option.equals("4"))
                cancelReservation();
            else if (option.equals("5"))
                flightsList();
            else if(option.equals("6"))
                reservationsList();
            else if (option.equals("0")) {
                logout(1);
                res = false;
            } else System.out.println("\n\033[0;31mOpção incorreta!\033[0m");
            System.out.println("\n\n");
        }
    }

    public static void menuNormalUser() throws IOException, InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        boolean res = true;
        while(res) {
            System.out.print("-----------------MENU-----------------\n"
                    + "1: Reservar viagem \n"
                    + "2: Cancelar reserva de uma viagem \n"
                    + "3: Consultar lista de voos existentes\n"
                    + "4: Consultar minhas reservas de voos\n\n"
                    + "0: Logout \n"
                    + "--------------------------------------\n"
                    + "Introduza a opção: ");
            String option = stdin.readLine();
            if (option.equals("1"))
                reserveFlight();
            else if (option.equals("2"))
                cancelReservation();
            else if (option.equals("3"))
                flightsList();
            else if(option.equals("4"))
                reservationsList();
            else if (option.equals("0")){
                logout(1);
                res = false;
            }
            else System.out.println("\n\033[0;31mOpção incorreta!\033[0m");
            System.out.println("\n\n");
        }
    }

    /******************************************************************************************************/


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
                int error = Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(1);
                System.out.println("\n");
                if(error==0){
                    String[] tokens = new String(reply1).split("-");
                    System.out.print(tokens[0] + "\n\n\n");
                    user = name;
                    isAdmin = Boolean.parseBoolean(tokens[1]);
                    if(isAdmin) menuAdmin();
                    else menuNormalUser();
                }else
                    System.out.println("\033[0;31m" + new String(reply1) + ": Falha na autenticação" + "\n\n\033[0m");
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
                    if(isAdmin) menuAdmin();
                    else menuNormalUser();
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

    public static void logout(int option) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                if(option == 0) {
                    warning.interrupt();
                    multi.close();
                    System.out.print("\033[1;35mAté à Próxima!\033[0m\n\n");
                    System.exit(0);
                }
                else{
                    System.out.print("\n\n");
                    menu();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
    }

    public static void reserveFlight() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            int controlo = -1;
            String date1 = null;
            String date2 = null;
            try {
                System.out.print("\nInsira percurso completo (Origem->Destino): ");
                String trip = stdin.readLine();

                while(controlo == -1){
                    System.out.print("Insira intervalo de data possíveis (AAAA-MM-DD;AAAA-MM-DD): ");
                    String interval = stdin.readLine();
                    String[] tokens = interval.split(";");
                    date1 = isValid(tokens[0]);
                    date2 = isValid(tokens[1]);
                    if(date1 == null || date2 == null)
                        System.out.print("\n\033[0;31mO intervalo não está no formato correto!\033[0m\n\n");
                    else controlo = 0;
                }
                multi.send(3, (trip+";"+date1+";"+date2+";").getBytes());

                byte[] reply = multi.receive(3);
                int error = Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(3);
                System.out.print("\n\n");
                if (error == 0) System.out.println(new String(reply1));
                else System.out.print("\033[0;31m" + new String(reply1) + ": Reserva não efetuada!!" + "\n\033[0m");
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                    System.out.print(e.getMessage() + "\n\n");
            }
        });
        t.start();
        t.join();
    }

    public static void cancelReservation() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try{
                System.out.print("\nInsira o código da reserva: ");
                String codigo = stdin.readLine();

                multi.send(4, (codigo+" ").getBytes());

                byte[] reply = multi.receive(4);
                int error = Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(4);
                System.out.print("\n\n");
                if (error == 0) System.out.println(new String(reply1));
                else System.out.print("\033[0;31m" + new String(reply1) + ": Cancelamento não efetuado!!" + "\n\n\033[0m");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public static void flightsList() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                multi.send(5,(" ").getBytes());

                byte[] reply = multi.receive(5);
                System.out.print("\n\n\033[4;30mLista de voos (Origem->Destino):\033[0m");
                System.out.print("\n");
                System.out.println(new String(reply));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public static void insertInfo() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try{
                System.out.print("\nInsira a origem do voo: ");
                String origin = stdin.readLine();
                System.out.print("Insira o destino do voo: ");
                String destiny = stdin.readLine();
                System.out.print("Insira a capacidade do voo: ");
                String capacity = stdin.readLine();

                multi.send(6, (origin+";"+destiny+";"+capacity+";").getBytes());

                byte[] reply = multi.receive(6);
                System.out.println(new String(reply));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public static void closeDay() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            int controlo = -1;
            String day = null;
            try{
                while(controlo == -1){
                    System.out.print("\nInsira dia (AAAA-MM-DD): ");
                    day = isValid(stdin.readLine());
                    if(day == null)
                        System.out.print("\n\033[0;31mO intervalo não está no formato correto!\033[0m\n\n");
                    else controlo = 0;
                }

                multi.send(7, (day+" ").getBytes());

                byte[] reply = multi.receive(7);
                int error = Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(7);
                if (error == 0) System.out.println(new String(reply1));
                else System.out.print("\n\033[0;31m" + new String(reply1) + ": Encerramento sem efeito!!" + "\n\n\033[0m");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public static void reservationsList() throws InterruptedException{
        Thread t = new Thread(() -> {
            try {
                multi.send(8,(" ").getBytes());

                byte[] reply = multi.receive(8);
                System.out.print("\n\n\033[4;30mReservas de Voos (Origem->Destino):\033[0m");
                System.out.print("\n");
                System.out.println(new String(reply));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public static String isValid(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        try {
            LocalDateTime date = LocalDate.parse(input,formatter).atStartOfDay();
            return date.format(formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("localhost",12343);
        multi = new Demultiplexer(new TaggedConnection(socket));
        warning = new Thread(() -> {
            try{
                while(true){
                    byte[] reply = multi.receive(9);
                    System.out.print(new String(reply) + "\n\n");
                }
            } catch (IOException | InterruptedException e) {}
        });
        multi.start();
        run();
    }
}
