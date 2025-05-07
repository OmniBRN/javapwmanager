package com.tudor;

import java.util.Scanner;

public class Main {
    
    public static void Menu(Scanner input)
    {
        int stop = 0;
        while(stop==0)
        {
            System.out.println("Options:");  
            System.out.println("[1] Manage Entries");
            System.out.println("[2] Manage Categories");
            System.out.println("[3] Exit");
            int Option = Integer.parseInt(input.nextLine().replace("\\s+", ""));
            if(Option > 3 || Option < 1) continue;
            if(Option == 3) stop = 1;
        }
        System.out.println("Goodbye!");
    }
    
    public static void main(String[] args) {
        // Momentan nu o sa memorez nimic ca pana nu vad ca merge totul ;-;
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to <name>\n"); 
        System.out.println("Enter a vault name:");
        String vaultName = input.nextLine().replace("\\s+", "");
        System.out.println("Enter a master password (Don't forget it, you won't be able to access the passwords)");
        String password = input.nextLine().replace("\\s+", "");
        try
        {
            Vault userVault = new Vault(vaultName, password);
            System.out.println("Vault Created!");
            Menu(input);

        }
        catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        input.close();
    } 
}