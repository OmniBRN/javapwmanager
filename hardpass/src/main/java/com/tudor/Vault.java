package com.tudor;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public class Vault {
    private String m_vaultName;
    private String m_hashedPassword;
    private String m_salt;
    private LocalDateTime m_creationDate;
    private HashMap<Integer, Category> m_categories;
    private List<Entry> m_entries;

    static public Vault CreateVault()
    {
        System.out.println("Welcome to <name>\n"); 
        System.out.println("Enter a vault name:");
        String vaultName = Service.getLine();
        System.out.println("Enter a master password (Don't forget it, you won't be able to access the passwords)");
        String password = Service.getLine();
        try
        {
            Vault userVault = new Vault(vaultName, password);
            System.out.println("Vault created!\n");
            String query = "INSERT INTO vault (name, hashed_password, salt, creation_date) VALUES (?, ?, ?, ?)";
            try(Connection con = Database.getConnection();
                PreparedStatement stmt = con.prepareStatement(query);
            )
            {
                stmt.setString(1, userVault.getVaultName());
                stmt.setString(2, userVault.getHashedPassword());
                stmt.setString(3, userVault.getSalt());
                stmt.setObject(4, userVault.getCreationDate());
                stmt.executeUpdate();
            }

            return userVault;

        }
        catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        return null;
    }

    public Vault(String vaultName, String hashedPassword, String salt, LocalDateTime creationDate, HashMap<Integer, Category> categories, List<Entry> entries) {
        m_vaultName = vaultName;
        m_hashedPassword = hashedPassword;
        m_salt = salt;
        m_creationDate = creationDate;
        m_categories = categories;
        m_entries = entries;
    }
    public Vault(String vaultName, String password) throws Exception
    {
        SecureRandom random = new SecureRandom();
        byte[] temp_salt = new byte[16];
        random.nextBytes(temp_salt);
        m_salt = Base64.getEncoder().encodeToString(temp_salt);
        temp_salt = m_salt.getBytes(StandardCharsets.UTF_8);
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13)
            .withIterations(2)
            .withMemoryAsKB(65536)
            .withParallelism(2)
            .withSalt(temp_salt);

        Argon2BytesGenerator generate = new Argon2BytesGenerator();
        generate.init(builder.build());
        byte[] result = new byte [32];
        generate.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);

        Argon2BytesGenerator verifier = new Argon2BytesGenerator();
        verifier.init(builder.build());
        byte[] testHash = new byte[32];
        verifier.generateBytes(password.getBytes(StandardCharsets.UTF_8), testHash, 0, testHash.length);

        m_hashedPassword = Base64.getEncoder().encodeToString(result);

        m_creationDate = LocalDateTime.now();
        m_vaultName = vaultName;
        m_categories = new HashMap<Integer, Category>();
        m_entries = new ArrayList<Entry>();
    }

    
    public String getVaultName(){ return m_vaultName;};
    public String getHashedPassword() { return m_hashedPassword;};
    public String getSalt(){return m_salt;};
    public LocalDateTime getCreationDate() { return m_creationDate;}; 
    public HashMap<Integer, Category> getCategories() { return m_categories;};
    public Category getCategory(UUID categoryId) {
        for(int i=0;i<m_categories.size(); i++)
        {
            if(m_categories.get(i).getId() == categoryId)
                return m_categories.get(i);
            
        }
        return null;
    }
    public List<Entry> getEntries() { return m_entries;};

    public void setVaultName(String value){ m_vaultName = value;};
    public void addCategory(Category value) {
        int noCategories = m_categories.size();
        m_categories.put(noCategories, value);
    };
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

        
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13)
            .withIterations(2)
            .withMemoryAsKB(65536)
            .withParallelism(2)
            .withSalt(m_salt.getBytes(StandardCharsets.UTF_8));

        Argon2BytesGenerator generate = new Argon2BytesGenerator();
        generate.init(builder.build());
        byte[] result = new byte [32];
        generate.generateBytes(passwordTry.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
        
        String resultString = Base64.getEncoder().encodeToString(result);
        return resultString.equals(m_hashedPassword);
        
    }
    
}
