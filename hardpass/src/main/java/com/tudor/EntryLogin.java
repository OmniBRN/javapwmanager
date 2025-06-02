package com.tudor;

import java.util.UUID;

public class EntryLogin extends Entry {

    private String m_username;
    private String m_email;
    private String m_password;

    public EntryLogin(UUID id, String EntryName, String additionalNote, String password, String username, String email)
    {

        super(id, EntryName, additionalNote);
        if(password.equals(""))
            generatePassword(true,true, true, 16);
        else m_password = password;
        m_username = username;
        m_email = email;
    }
    public EntryLogin(String EntryName, String additionalNote, String password, String username, String email)
    {
        super(EntryName, additionalNote);
        if(password.equals(""))
            generatePassword(true,true, true, 16);
        else m_password = password;
        m_username = username;
        m_email = email;
    }

    public String getUsername() {return m_username;};
    public String getEmail() {return m_email;};
    public String getPassword() {return m_password;};

    public void setUsername(String value){m_username = value;};
    public void setEmail(String value){m_email = value;};
    public void setPassword(String value){m_password = value;};

    public void generatePassword(boolean hasCapitalLetters, boolean hasSpecialCharacters, boolean hasDigits, int length)
    {
        PasswordGenerator.setHasCapitalLetters(hasDigits);
        PasswordGenerator.setHasCapitalLetters(hasCapitalLetters);
        PasswordGenerator.setHasSpecialCharacters(hasSpecialCharacters);
        PasswordGenerator.setPasswordLength(length);
        m_password = PasswordGenerator.GetPassword();


    }

    


}
