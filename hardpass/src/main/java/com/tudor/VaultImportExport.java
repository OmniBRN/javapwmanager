package com.tudor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VaultImportExport {
    static public void exportToFile(Vault userVault) throws Exception
    {

        StringBuilder data = new StringBuilder();
        data.append("VAULT_NAME:" + userVault.getVaultName() + "\n");
        data.append("HASHED_PASSWORD:" + userVault.getHashedPassword() + "\n");
        data.append("SALT:" + userVault.getSalt() + "\n");
        data.append("CREATION_DATE:" + userVault.getCreationDate() + "\n");
        data.append("CATEGORIES:\n");
        List<Category> categories = userVault.getCategories();
        for(int i=0; i<categories.size(); i++)
        {
            data.append("#" + "ID:" + categories.get(i).getId() + "/" + categories.get(i).getCategoryName() + "\n");
        }
        data.append("ENTRIES:\n");
        List<Entry> entries = userVault.getEntries();
        for(int i=0; i<entries.size(); i++)
        {
            String entryType = null;
            StringBuilder specificStuff = new StringBuilder();
            Entry currentEntry = entries.get(i);
            if(currentEntry instanceof EntryLogin)
            {
                entryType = "EntryLogin";
                specificStuff.append("/USERNAME:" + ((EntryLogin)currentEntry).getUsername() + "/EMAIL:" + ((EntryLogin)currentEntry).getEmail() + "/PASSWORD:" + ((EntryLogin)currentEntry).getPassword() + "\n");
            }
            if(currentEntry instanceof EntrySSH)
            {
                entryType = "EntrySSH";
                specificStuff.append("/SSHTYPE:" + ((EntrySSH)currentEntry).getSSHType() + "/PRIVATE_KEY:" + ((EntrySSH)currentEntry).getPrivateKey() + "/PUBLIC_KEY:" + ((EntrySSH)currentEntry).getPublicKey() + "\n");
            }
            if(currentEntry instanceof EntryPGP)
            {
                entryType = "EntryPGP";
                specificStuff.append("/PRIVATE_KEY" + ((EntryPGP)currentEntry).getPrivateKey() + "/PUBLIC_KEY" + ((EntryPGP)currentEntry).getPublicKey() + "\n");
            }
            data.append("#[" + entryType + "]ID:" + currentEntry.getId() + "/ENTRY_NAME:" + currentEntry.getEntryName() + "/CATEGORY_ID:" + currentEntry.getCategoryId() + "/ADDITIONAL_NOTE:" + currentEntry.getAdditionalNote() + specificStuff.toString());

        }
        System.out.println(data.toString());
        byte[] byteArray = data.toString().getBytes(StandardCharsets.UTF_8);
        
        Path file = Paths.get("test");
        Files.write(file, byteArray);
          
    }
}
