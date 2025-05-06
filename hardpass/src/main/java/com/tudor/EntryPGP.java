package main.java.com.tudor;

public class EntryPGP extends Entry{
    private String m_publicKey;
    private String m_privateKey;

    public EntryPGP(String EntryName, String additionalNote, String publicKey, String privateKey)
    {
        super(EntryName, additionalNote);
        m_privateKey = privateKey;
        m_publicKey = publicKey;
    }

    public String DecryptMessage(String encryptedMessage)
    {
        
    }

}
