package main.java.com.tudor;

public class EntryLogin extends Entry {

    private String m_username;
    private String m_email;
    private String m_password;

    public EntryLogin(String EntryName, String additionalNote, String password, String username, String email)
    {
        super(EntryName, additionalNote);
        m_password = password;
        m_username = username;
        m_email = email;
    }


}
