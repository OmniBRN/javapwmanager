package com.tudor;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class VaultImportExport {
    static public Vault importUserVault() throws Exception
    {
        String encryptedVaultName = null;
        String hashedPassword = null; 
        String salt = null;
        LocalDateTime creationDate = null;
        HashMap<Integer, Category> encryptedCategories = new HashMap<>();
        List<Entry> encryptedEntries = new ArrayList<>();

        String vaultQuery = "SELECT name, hashed_password, salt, creation_date from vault";
        String categoryQuery = "SELECT id, name FROM category";
        String entryQuery = "SELECT id, name, category_id, notes, entry_type, username, email, password, public_key_ssh, private_key_ssh, ssh_type, public_key_pgp, private_key_pgp from Entry";


        try(Connection con = Database.getConnection())
        {
            String countQuery = "SELECT COUNT(*) as vaultCount from vault";
            // Verificam daca exista vault-uri
            Statement stmt= con.createStatement();
            boolean isVault = true;
            try(ResultSet rs = stmt.executeQuery(countQuery))
            {
                if(rs.next())
                {
                    int getCount = rs.getInt("vaultCount");
                    if(getCount == 0)
                        isVault = false;
                }
            }
            if(isVault == false)
                return null;

            // Get Vault
            try(ResultSet rs2 = stmt.executeQuery(vaultQuery))
            {

                while(rs2.next())
                {
                    encryptedVaultName = rs2.getString("name");
                    hashedPassword = rs2.getString("hashed_password");
                    // creationDate = rs2.getObject("creation_date", LocalDateTime.class);
                    salt = rs2.getString("salt");
                }
            }

            // Get Entries

            try(ResultSet rs3 = stmt.executeQuery(entryQuery))
            {

                while(rs3.next())
                {
                    UUID entryId = rs3.getObject("id", UUID.class);
                    int entryType = rs3.getInt("entry_type");
                    String name = rs3.getString("name");
                    UUID category_id = rs3.getObject("category_id", UUID.class);
                    String notes = rs3.getString("notes");
                    switch(entryType)
                    {
                        case 1:
                        {
                            String username = rs3.getString("username");
                            String email = rs3.getString("email");
                            String password = rs3.getString("password");
                            EntryLogin entryLogin = new EntryLogin(entryId, name, notes, password, username, email);
                            entryLogin.setCategoryId(category_id);
                            encryptedEntries.add(entryLogin);
                            break;
                        }
                        case 2:
                        {
                            String publicKey = rs3.getString("public_key_ssh");
                            String privateKey = rs3.getString("private_key_ssh");
                            SSHType SSHType = null;
                            int intSSHType = rs3.getInt("ssh_type");
                            if(intSSHType == 1)
                                SSHType = com.tudor.SSHType.RSA; 
                            if(intSSHType == 2)
                                SSHType = com.tudor.SSHType.ECDSA; 
                            if(intSSHType == 3)
                                SSHType = com.tudor.SSHType.Ed25519; 

                            EntrySSH entrySSH = new EntrySSH(entryId, name, notes, SSHType, publicKey, privateKey);
                            entrySSH.setCategoryId(category_id);
                            encryptedEntries.add(entrySSH);
                            break;
                        }
                        case 3:
                        {
                            String privateKey = rs3.getString("private_key_pgp");
                            String publicKey = rs3.getString("public_key_pgp");
                            EntryPGP entryPGP = new EntryPGP(entryId, name, notes, privateKey, publicKey);
                            entryPGP.setCategoryId(category_id);
                            encryptedEntries.add(entryPGP);
                            break;
                        }
                    }
                }
            }

            // Get Categories 
            try(ResultSet rs4 = stmt.executeQuery(categoryQuery))
            {
                while(rs4.next())
                {
                    UUID categoryID = rs4.getObject("id", UUID.class);
                    String name = rs4.getString("name");
                    Category t_Category = new Category(categoryID, name);
                    encryptedCategories.put(encryptedCategories.size(), t_Category);
                }
            }
            
        }
        return new Vault(encryptedVaultName, hashedPassword, salt, creationDate, encryptedCategories, encryptedEntries);
            
        
    }

    static public void deleteEntry(UUID id) throws SQLException
    {
        String query = "DELETE FROM entry WHERE id = ?";
        try(Connection con = Database.getConnection();
            PreparedStatement stmt = con.prepareStatement(query))
            {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            }
    }

    static public void deleteCategory(UUID id) throws SQLException
    {
        String query = "DELETE FROM category WHERE id = ?";
        try(Connection con = Database.getConnection();
            PreparedStatement stmt = con.prepareStatement(query))
            {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            }
    }

    static private String encryptMessage(String message, String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {

        SecureRandom secureRandom = new SecureRandom();
        int iterations = 100000;
        int length = 256;
        byte[] saltByte = Base64.getDecoder().decode(salt);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltByte, iterations, length);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        // Salt generat in general, IV generat de fiecare data
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        byte[] combinate = new byte[16+16+cipherText.length];
        System.arraycopy(saltByte, 0, combinate, 0, saltByte.length);
        System.arraycopy(iv, 0, combinate, saltByte.length, iv.length);
        System.arraycopy(cipherText, 0, combinate, saltByte.length + iv.length, cipherText.length);
        
        return Base64.getEncoder().encodeToString(combinate);
        
    }

    static private String decryptMessage(String encryptedMessage, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {

        byte[] combined = Base64.getDecoder().decode(encryptedMessage);
        
        int iterations = 100000;
        int length = 256;
        
        byte[] salt = new byte[16];
        byte[] iv = new byte[16];
        byte[] ciphertext = new byte[combined.length - 32];

        System.arraycopy(combined, 0, salt, 0, salt.length);
        System.arraycopy(combined, salt.length, iv, 0, iv.length);
        System.arraycopy(combined, salt.length + iv.length, ciphertext, 0, ciphertext.length);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, length);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedMessage = cipher.doFinal(ciphertext);
        
        return new String(decryptedMessage, "UTF-8");

    }

    static public Vault encryptVault(Vault userVault, String password, String salt) throws Exception 
    {
        String encryptedVaultName = encryptMessage(userVault.getVaultName(), password, userVault.getSalt()) ;
        HashMap<Integer, Category> encryptedCategories = new HashMap<Integer, Category>();
        List<Entry> encryptedEntries = new ArrayList<>();

        HashMap<Integer, Category> Categories = userVault.getCategories();
        List<Entry> Entries = userVault.getEntries();
        int categoriesCount = Categories.size(), entriesCount = Entries.size();
        
        for(int i=0; i<categoriesCount; i++)
        {
            Category currentCategory = Categories.get(i);
            UUID categoryId = currentCategory.getId();
            String encryptedCategoryName = encryptMessage(currentCategory.getCategoryName(), password, userVault.getSalt());

            Category tempCategory = new Category(categoryId, encryptedCategoryName);
            encryptedCategories.put(encryptedCategories.size(), tempCategory);
        }

        for(int i=0; i<entriesCount; i++)
        {
            Entry currentEntry = Entries.get(i);
            String encryptedEntryName = encryptMessage(currentEntry.getEntryName(), password, userVault.getSalt());
            String encryptedAdditionalNote = encryptMessage(currentEntry.getAdditionalNote(), password, userVault.getSalt());
            if(currentEntry instanceof EntryLogin)
            {
                EntryLogin currentEntryLogin = (EntryLogin) currentEntry;
                String encryptedUsername = encryptMessage(currentEntryLogin.getUsername(), password, userVault.getSalt());
                String encryptedPassword = encryptMessage(currentEntryLogin.getPassword(), password, userVault.getSalt());
                String encryptedEmail = encryptMessage(currentEntryLogin.getEmail(), password, userVault.getSalt());
                EntryLogin encryptedCurrentEntryLogin = new EntryLogin(currentEntry.getId(), encryptedEntryName, encryptedAdditionalNote, encryptedPassword, encryptedUsername, encryptedEmail);
                encryptedCurrentEntryLogin.setCategoryId(currentEntryLogin.getCategoryId());
                encryptedEntries.add(encryptedCurrentEntryLogin);
            }
            if(currentEntry instanceof EntrySSH)
            {
                EntrySSH currentEntrySSH = (EntrySSH) currentEntry;
                String encryptedPublicKey = encryptMessage(currentEntrySSH.getPublicKey(), password, userVault.getSalt());
                String encryptedPrivateKey = encryptMessage(currentEntrySSH.getPrivateKey(), password, userVault.getSalt());
                EntryLogin encryptedCurrentEntrySSH = new EntryLogin(currentEntry.getId(), encryptedEntryName, encryptedAdditionalNote, currentEntrySSH.getSSHType(), encryptedPublicKey, encryptedPrivateKey);
                encryptedCurrentEntrySSH.setCategoryId(currentEntrySSH.getCategoryId());
                encryptedEntries.add(encryptedCurrentEntrySSH);
            }
            if(currentEntry instanceof EntryPGP)
            {
                EntryPGP currentEntryPGP = (EntryPGP) currentEntry;
                String encryptedPrivateKey = encryptMessage(currentEntryPGP.getPrivateKey(), password, userVault.getSalt());
                String encryptedPublicKey = encryptMessage(currentEntryPGP.getPublicKey(), password, userVault.getSalt());
                EntryPGP encryptedCurrentEntryPGP = new EntryPGP(currentEntry.getId(), encryptedEntryName, encryptedAdditionalNote, encryptedPrivateKey, encryptedPublicKey);
                encryptedCurrentEntryPGP.setCategoryId(currentEntryPGP.getCategoryId());
                encryptedEntries.add(encryptedCurrentEntryPGP);
            }
        }
        Vault encryptedUserVault = new Vault(encryptedVaultName, userVault.getHashedPassword(), userVault.getSalt(), userVault.getCreationDate(), encryptedCategories, encryptedEntries);

        return encryptedUserVault;
        
    }

    static public Vault decryptVault(Vault encryptedUserVault, String password) throws Exception
    {
        String decryptedVaultName = decryptMessage(encryptedUserVault.getVaultName(), password);
        HashMap<Integer, Category> decryptedCategories = new HashMap<Integer, Category>();
        List<Entry> decryptedEntries = new ArrayList<>();

        HashMap<Integer, Category> Categories = encryptedUserVault.getCategories();
        List<Entry> Entries = encryptedUserVault.getEntries();
        int categoriesCount = Categories.size(), entriesCount = Entries.size();

        for(int i=0; i<categoriesCount; i++)
        {
            Category currentCategory = Categories.get(i);
            UUID categoryId = currentCategory.getId();
            String encryptedCategoryName = decryptMessage(currentCategory.getCategoryName(), password);

            Category tempCategory = new Category(categoryId, encryptedCategoryName);
            decryptedCategories.put(decryptedCategories.size(), tempCategory);
        }

        for(int i=0; i<entriesCount; i++)
        {
            Entry currentEntry = Entries.get(i);
            String decryptedEntryName = decryptMessage(currentEntry.getEntryName(), password);
            String decryptedAdditionalNote = decryptMessage(currentEntry.getAdditionalNote(), password);
            if(currentEntry instanceof EntryLogin)
            {
                EntryLogin currentEntryLogin = (EntryLogin) currentEntry;
                String decryptedUsername = decryptMessage(currentEntryLogin.getUsername(), password);
                String decryptedPassword = decryptMessage(currentEntryLogin.getPassword(), password);
                String decryptedEmail = decryptMessage(currentEntryLogin.getEmail(), password);
                EntryLogin decryptedCurrentEntryLogin = new EntryLogin(currentEntry.getId(), decryptedEntryName, decryptedAdditionalNote, decryptedPassword, decryptedUsername, decryptedEmail);
                decryptedCurrentEntryLogin.setCategoryId(currentEntryLogin.getCategoryId());
                decryptedEntries.add(decryptedCurrentEntryLogin);
            }
            if(currentEntry instanceof EntrySSH)
            {
                EntrySSH currentEntrySSH = (EntrySSH) currentEntry;
                String decryptedPublicKey = decryptMessage(currentEntrySSH.getPublicKey(), password);
                String decryptedPrivateKey = decryptMessage(currentEntrySSH.getPrivateKey(), password);
                EntryLogin decryptedCurrentEntrySSH = new EntryLogin(currentEntry.getId(), decryptedEntryName, decryptedAdditionalNote, currentEntrySSH.getSSHType(), decryptedPublicKey, decryptedPrivateKey);
                decryptedCurrentEntrySSH.setCategoryId(currentEntrySSH.getCategoryId());
                decryptedEntries.add(decryptedCurrentEntrySSH);
            }
            if(currentEntry instanceof EntryPGP)
            {
                EntryPGP currentEntryPGP = (EntryPGP) currentEntry;
                String decryptedPrivateKey = decryptMessage(currentEntryPGP.getPrivateKey(), password);
                String decryptedPublicKey = decryptMessage(currentEntryPGP.getPublicKey(), password);
                EntryPGP decryptedCurrentEntryPGP = new EntryPGP(currentEntry.getId(), decryptedEntryName, decryptedAdditionalNote, decryptedPrivateKey, decryptedPublicKey);
                decryptedCurrentEntryPGP.setCategoryId(currentEntryPGP.getCategoryId());
                decryptedEntries.add(decryptedCurrentEntryPGP);
            }
        }
        Vault decryptedUserVault = new Vault(decryptedVaultName, encryptedUserVault.getHashedPassword(), encryptedUserVault.getSalt(), encryptedUserVault.getCreationDate(), decryptedCategories, decryptedEntries);
        return decryptedUserVault;
    }
    static public void exportToDB(Vault userVault) throws Exception {
        // Vault can't really change
        // Trecem prin fiecare entry si modificam/adaugam

        HashMap<Integer, Category> Categories = userVault.getCategories();
        int categoriesCount = Categories.size();
        List<Entry> Entries = userVault.getEntries();
        int entriesCount = Entries.size();
        for(int i=0; i<categoriesCount; i++)
        {
            Category t_category = Categories.get(i);
            boolean insert = false;
            //Verificam daca este update sau insert
            String query1 = "SELECT COUNT(*) as count from category WHERE id = ?";
            try(Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query1))
            {
                stmt.setObject(1, t_category.getId());
                ResultSet rs = stmt.executeQuery();
                if(rs.next())
                { 
                    if(rs.getInt("count") == 0)
                        insert = true;
                }
            }
            if(insert)
            {

                String query2 = "INSERT INTO category (id, name) VALUES (?, ?)";
                try(Connection conn = Database.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query2))
                {
                    stmt.setObject(1, t_category.getId());
                    stmt.setString(2, t_category.getCategoryName());
                    stmt.executeUpdate();
                }
            }

            else
            {

                String query2 = "UPDATE category SET name = ? WHERE id = ?";
                try(Connection conn = Database.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query2))
                {
                    stmt.setString(1, t_category.getCategoryName());
                    stmt.setObject(2, t_category.getId());
                    stmt.executeUpdate();
                }
            }
        }
        for(int i=0; i<entriesCount; i++)
        {
            Entry t_entry = Entries.get(i);
            boolean insert = false;
            //Verificam daca este update sau insert
            String query1 = "SELECT COUNT(*) as count from entry WHERE id = ?";
            try(Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query1))
            {
                stmt.setObject(1, t_entry.getId());
                ResultSet rs = stmt.executeQuery();
                if(rs.next())
                { 
                    if(rs.getInt("count") == 0)
                        insert = true;
                }
            }

            // Daca insert = true -> Este un rand nou
            // Daca insert = false -> Este un rand vechi/posibil updatat (de asemenea doar EntryLogin poate sa fie modificat, la SSH sau PGP nu are sens)

            if(!insert)
            {
                if(t_entry instanceof EntryLogin)
                {
                    EntryLogin t_entryLogin = (EntryLogin) t_entry;
                    String query2 = "UPDATE entry SET name = ?, category_id = ?, notes = ?, username = ?, email = ?, password = ? WHERE id = ?";
                    try(Connection conn = Database.getConnection();
                        PreparedStatement stmt = conn.prepareStatement(query2))
                    {
                        stmt.setString(1, t_entryLogin.getEntryName());
                        stmt.setObject(2, t_entryLogin.getCategoryId());
                        stmt.setString(3, t_entryLogin.getAdditionalNote());
                        stmt.setString(4, t_entryLogin.getUsername());
                        stmt.setString(5, t_entryLogin.getEmail());
                        stmt.setString(6, t_entryLogin.getPassword());
                        stmt.setObject(7, t_entryLogin.getId());
                        stmt.executeUpdate();
                    }
                }
            }
            else
            {
                if(t_entry instanceof EntryLogin)
                {
                    EntryLogin t_entryLogin = (EntryLogin) t_entry;
                    String query2 = "INSERT INTO entry (id, name, category_id, notes, entry_type, username, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; 
                    try(Connection conn = Database.getConnection();
                        PreparedStatement stmt = conn.prepareStatement(query2))
                    {
                        stmt.setObject(1, t_entryLogin.getId());
                        stmt.setString(2, t_entryLogin.getEntryName());
                        stmt.setObject(3, t_entryLogin.getCategoryId());
                        stmt.setString(4, t_entryLogin.getAdditionalNote());
                        stmt.setInt(5, 1);
                        stmt.setString(6, t_entryLogin.getUsername());
                        stmt.setString(7, t_entryLogin.getEmail());
                        stmt.setString(8, t_entryLogin.getPassword());
                        stmt.executeUpdate();
                    }
                }
                if(t_entry instanceof EntrySSH)
                {
                    EntrySSH t_entrySSH = (EntrySSH) t_entry;
                    String query3 = "INSERT INTO entry (id, name, category_id, notes, entry_type, public_key_ssh, private_key_ssh, ssh_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; 
                    try(Connection conn = Database.getConnection();
                        PreparedStatement stmt = conn.prepareStatement(query3))
                    {
                        stmt.setObject(1, t_entrySSH.getId());
                        stmt.setString(2, t_entrySSH.getEntryName());
                        stmt.setObject(3, t_entrySSH.getCategoryId());
                        stmt.setString(4, t_entrySSH.getAdditionalNote());
                        stmt.setInt(5, 2);
                        stmt.setString(6, t_entrySSH.getPublicKey());
                        stmt.setString(7, t_entrySSH.getPrivateKey());
                        String t_SshType = t_entrySSH.getSSHType();
                        if(t_SshType.equals("RSA"))
                            stmt.setInt(8, 1);
                        if(t_SshType.equals("ECDSA"))
                            stmt.setInt(8, 2);
                        if(t_SshType.equals("Ed25519"))
                            stmt.setInt(8, 3);
                        stmt.executeUpdate();
                    }

                }
                if(t_entry instanceof EntryPGP)
                {

                    EntryPGP t_entryPGP = (EntryPGP) t_entry;
                    String query4 = "INSERT INTO entry (id, name, category_id, notes, entry_type, public_key_pgp, private_key_pgp) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
                    try(Connection conn = Database.getConnection();
                        PreparedStatement stmt = conn.prepareStatement(query4))
                    {
                        stmt.setObject(1, t_entryPGP.getId());
                        stmt.setString(2, t_entryPGP.getEntryName());
                        stmt.setObject(3, t_entryPGP.getCategoryId());
                        stmt.setString(4, t_entryPGP.getAdditionalNote());
                        stmt.setInt(5, 3);
                        stmt.setString(6, t_entryPGP.getPublicKey());
                        stmt.setString(7, t_entryPGP.getPrivateKey());
                        stmt.executeUpdate();
                    }
                }

            }
        }

    }
}
