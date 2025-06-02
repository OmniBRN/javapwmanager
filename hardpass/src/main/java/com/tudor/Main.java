package com.tudor;

import java.util.concurrent.TimeUnit;


public class Main {
    
    public static void main(String[] args) throws Exception {
        // Momentan nu o sa memorez nimic pana nu vad ca merge totul ;-;
        Vault userVault = VaultImportExport.importUserVault();
        if(userVault == null) 
        {
            
            userVault = Vault.CreateVault();
            if(userVault == null)
            {
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
                Vault decryptedVault = VaultImportExport.decryptVault(userVault, passwordAttempt);
                System.out.println("Logged in successfully!");
                TimeUnit.SECONDS.sleep(1);
                Service.m_userVault = decryptedVault;
                Service.Menu();

                Vault encryptedVault = VaultImportExport.encryptVault(decryptedVault, passwordAttempt, decryptedVault.getSalt());
                VaultImportExport.exportToDB(encryptedVault);
            }
            
        }
    } 
}