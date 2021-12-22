package Exceptions;

/**
* Classe que se encarrega de denotar que a password inserida não corresponde à do userusername inserido.
*/
public class WrongPassword extends Exception {

    public WrongPassword(String e){
        super(e);
    }
}