import java.net.Socket;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    private static String user;
    private static int admin;

    Socket socket = new Socket("localhost",12345);
    Demultiplexer multi = new Demultiplexer(new Connection(s));

    //HashSet<Thread> alarms = new HashSet<>();

    m.start();

    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    String userusername = null;

    while(userusername == null){
        System.out.println(" Introduza a sua escolha: "
                        + "1: Login \n"
                        + "2: Registar \n"
                        + "3: Sair"
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

    public void login() throws InterruptedException{
        Thread t = new Thread(()-> {
            try{
                System.out.println("----------Login----------"
                                + "\n"
                                + " Inserir nome: ");
                String nome = stdin.readLine();

                System.out.println("Inserir palavra-passe: ");
                String password = stdin.readLine();

                multi.send(1, (nome+" "+password+" ").getBytes());

                byte[] reply = multi.receive(1);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(1);   // ?????????????????????
                if(error==0){  //??????????????????????????????
                    String[] tokens = new String(reply1).split(":");  
                    System.out.println("\033[1;36m"+ tokens[0] +"\033[0m"); 
                    System.out.println(tokens[1]);
                    admin = Integer.parseInt(tokens[1]);
                    user = nome;
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

                System.out.println("Inserir nome de utilizador: ");
                String nome = stdin.readLine();
                System.out.println("Inserir password: ");
                String password = stdin.readLine();

                multi.send(2, (nome+" "+password+" ").getBytes());

                byte[] reply = multi.receive(2);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(2);   // ?????????????????????
                if(error==0){  //??????????????????????????????
                    System.out.println("\033[1;36m"+ new String(reply1) +"\033[0m"); 
                    user = nome;
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

}
