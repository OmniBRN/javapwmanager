package com.tudor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

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
                            EntryLogin entryLogin = new EntryLogin(name, notes, password, username, email);
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

                            EntrySSH entrySSH = new EntrySSH(name, notes, SSHType, publicKey, privateKey);
                            encryptedEntries.add(entrySSH);
                            break;
                        }
                        case 3:
                        {
                            String privateKey = rs3.getString("private_key_pgp");
                            String publicKey = rs3.getString("public_key_pgp");
                            EntryPGP entryPGP = new EntryPGP(name, notes, privateKey, publicKey);
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
    static public void decryptVault(Vault userVault, String password)
    {
        Argon2 argon2 = Argon2Factory.create();
        password += userVault.getSalt();
        String key = argon2.hash(10, 65536, 1, password.toCharArray());


        
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
                if(rs.next() && rs.getInt("count") == 0)
                {
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
                if(rs.next() && rs.getInt("count") == 0)
                {
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
                        stmt.setInt(5, 2);
                        stmt.setString(6, t_entryPGP.getPublicKey());
                        stmt.setString(7, t_entryPGP.getPrivateKey());
                        stmt.executeUpdate();
                    }
                }

            }
        }

    }
}
