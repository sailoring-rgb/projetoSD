package Exceptions;

/**
* Classe que se encarrega de denotar que as credenciais inseridas não são válidas.
* Ou seja, o userusername inserido não existe.
*/
public class UsernameNotExist extends Exception {

    public UserusernameNotExist(String e){
        super(e);
    }
}