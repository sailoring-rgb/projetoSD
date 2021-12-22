package Exceptions;

/**
* Classe que se encarrega de denotar que já existem um user com aquele username.
*/
public class UsernameAlreadyExists extends Exception {

    public UsernameAlreadyExists(String e){
        super(e);
    }
}