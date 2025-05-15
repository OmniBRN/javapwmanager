package com.tudor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import static org.junit.Assert.assertTrue;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Vault {
    private String m_vaultName;
    private String m_hashedPassword;
    private String m_salt;
    private LocalDateTime m_creationDate;
    private List<Category> m_categories;
    private List<Entry> m_entries;


    public Vault(String vaultName, String password) throws Exception
    {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        SecureRandom random = new SecureRandom();
        byte[] temp_salt = new byte[16];
        random.nextBytes(temp_salt);
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

        assertTrue(Arrays.equals(result,testHash));


        // MessageDigest md = MessageDigest.getInstance("SHA-256");
        // md.update(temp_salt);

        // byte[] temp_hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        // m_hashedPassword = Base64.getEncoder().encodeToString(temp_hashedPassword);
        m_salt = Base64.getEncoder().encodeToString(temp_salt);

        m_creationDate = LocalDateTime.now();
        m_vaultName = vaultName;
        m_categories = new ArrayList<Category>();
        m_entries = new ArrayList<Entry>();
    }

    
    public String getVaultName(){ return m_vaultName;};
    public String getHashedPassword() { return m_hashedPassword;};
    public String getSalt(){return m_salt;};
    public LocalDateTime getCreationDate() { return m_creationDate;};
    public List<Category> getCategories() { return m_categories;};
    public Category getCategory(UUID categoryId)
    {
        for(int i=0;i<m_categories.size(); i++)
        {
            if(m_categories.get(i).getId() == categoryId)
                return m_categories.get(i);
            
        }
        return null;
    }
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
