package com.tudor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Vault {
    private String m_vaultName;
    private String m_hashedPassword;
    private String m_salt;
    private LocalDateTime m_creationDate;
    private List<Category> m_categories;
    private List<Entry> m_entries;


    public Vault(String vaultName, String password) throws Exception
    {


        SecureRandom random = new SecureRandom();
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] temp_salt = new byte[16];
        random.nextBytes(temp_salt);
        md.update(temp_salt);

        byte[] temp_hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        m_hashedPassword = Base64.getEncoder().encodeToString(temp_hashedPassword);
        m_salt = Base64.getEncoder().encodeToString(temp_salt);

        m_creationDate = LocalDateTime.now();
        m_vaultName = vaultName;
        m_categories = new ArrayList<Category>();
        m_entries = new ArrayList<Entry>();
    }

    
    public String getVaultName(){ return m_vaultName;};
    public String getHashedPassword() { return m_hashedPassword;};
    public LocalDateTime getCreationDate() { return m_creationDate;};
    public List<Category> getCategories() { return m_categories;};
    public List<Entry> getEntries() { return m_entries;};

    public void setVaultName(String value){ m_vaultName = value;};
    public void addCategory(Category value) {m_categories.add(value);};
    public void addEntry(Entry value){m_entries.add(value);};

    public void removeCategory(UUID categoryId)
    {
        for(int i=0; i<m_categories.size(); i++)
        {
            if(m_categories.get(i).getId() == categoryId)
            {
                m_categories.remove(i);
                return;
            }
        }
    }

    public void removeEntry(UUID entryId)
    {
        for(int i=0; i<m_entries.size(); i++)
        {
            if(m_entries.get(i).getId() == entryId)
            {
                m_entries.remove(i);
                return;
            }
        }
    }

    public boolean CheckPassword(String passwordTry) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] temp_salt = Base64.getDecoder().decode(m_salt);
        md.update(temp_salt);
        String passwordTryHash = Base64.getEncoder().encodeToString(md.digest(passwordTry.getBytes(StandardCharsets.UTF_8)));
        
        System.out.println(passwordTryHash);
        System.out.println(m_hashedPassword);

        return passwordTryHash.equals(m_hashedPassword);
        
    }
    
}
