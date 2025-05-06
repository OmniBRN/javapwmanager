package main.java.com.tudor;
import org.apache.commons.math3.random.MersenneTwister;

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
        MersenneTwister mt = new MersenneTwister(System.currentTimeMillis());
        String possibleLetters = letters;
        String resultedPassword = "";

        if(hasCapitalLetters) possibleLetters += capitalLetters;
        if(hasSpecialCharacters) possibleLetters += specialCharacters;
        if(hasDigits) possibleLetters += digits;

        // System.out.println(possibleLetters);
        int size = possibleLetters.length();
        
        for(int i=0; i<passwordLength; i++)
        {
            int result = mt.nextInt()%size;
            if(result < 0) result += size;
            resultedPassword += possibleLetters.charAt(result);
        }
        return resultedPassword;
    }
}
