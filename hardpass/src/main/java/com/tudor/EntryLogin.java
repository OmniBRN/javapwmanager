package com.tudor;

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

    public String getUsername() {return m_username;};
    public String getEmail() {return m_email;};
    public String getPassword() {return m_password;};

    public void setUsername(String value){m_username = value;};
    public void setEmail(String value){m_email = value;};
    public void setPassword(String value){m_password = value;};

    


}
