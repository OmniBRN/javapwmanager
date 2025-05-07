package main.java.com.tudor;
import java.security.SecureRandom;


public class PasswordGenerator {

    static private String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private String letters = "abcdefghijklmnopqrstuvwxyz";
    static private String specialCharacters = "!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~";
    static private String digits = "0123456789";

    static private boolean hasCapitalLetters = true;
    static private boolean hasSpecialCharacters = true;
    static private boolean hasDigits = true;
    static private int passwordLength = 16;

    static public void setHasCapitalLetters(boolean value){ hasCapitalLetters = value;};
    static public void setHasSpecialCharacters(boolean value){ hasSpecialCharacters = value;};
    static public void setHasDigits(boolean value){ hasDigits = value;};
    static public void setPasswordLength(int value){ passwordLength = value; };

    static public String GetPassword()
    {
        SecureRandom random = new SecureRandom();
        String possibleLetters = letters;
        StringBuilder resultedPassword = new StringBuilder(passwordLength);

        if(hasCapitalLetters) possibleLetters += capitalLetters;
        if(hasSpecialCharacters) possibleLetters += specialCharacters;
        if(hasDigits) possibleLetters += digits;

        // System.out.println(possibleLetters);
        int size = possibleLetters.length();
        
        for(int i=0; i<passwordLength; i++)
        {
            int result = random.nextInt(size);
            resultedPassword.append(possibleLetters.charAt(result));
        }
        return resultedPassword.toString();
    }
}
