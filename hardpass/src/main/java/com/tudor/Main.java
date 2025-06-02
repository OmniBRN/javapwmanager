package com.tudor;

import java.util.concurrent.TimeUnit;


public class Main {
    
    public static void main(String[] args) throws Exception {
        // Momentan nu o sa memorez nimic pana nu vad ca merge totul ;-;
        Vault userVault = VaultImportExport.importUserVault();
        if(userVault == null) 
        {
            
            userVault = Vault.CreateVault();
            Audit.AddToAudit("Created Vault");
            if(userVault == null)
            {
                Audit.AddToAudit("Failed miserably at creating a Vault");
                System.out.println("Bye this all wrong");
                return;
            }
            Service.m_userVault = userVault;
            TimeUnit.SECONDS.sleep(1);
            Service.Menu();

        }
        else
        {
            System.out.println("Enter your password:");
            String passwordAttempt = Service.getLine();
            if(userVault.CheckPassword(passwordAttempt))
            {
                Audit.AddToAudit("Entered Correct Password");
                Vault decryptedVault = VaultImportExport.decryptVault(userVault, passwordAttempt);
                System.out.println("Logged in successfully!");
                TimeUnit.SECONDS.sleep(1);
                Service.m_userVault = decryptedVault;
                Service.Menu();

                Vault encryptedVault = VaultImportExport.encryptVault(decryptedVault, passwordAttempt, decryptedVault.getSalt());
                VaultImportExport.exportToDB(encryptedVault);
                Audit.AddToAudit("Closed The Password Manager");
            }
            else 
                Audit.AddToAudit("Entered Wrong Password");
            
        }
    } 
}