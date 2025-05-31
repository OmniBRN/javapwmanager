package com.tudor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Service {
    public static Vault m_userVault = null;
    private static Scanner input = new Scanner(System.in);
    public static void clearScreen()
    {
        System.out.println("\033c");
    }
    public static int getInt() 
    {
        int Option = Integer.parseInt(input.nextLine().stripLeading().stripTrailing());
        return Option;
    }

    public static String getLine()
    {
        String Option = input.nextLine().stripLeading().stripTrailing();
        return Option;

    }

    public static char getChar()
    {
        char Option = input.nextLine().stripLeading().stripTrailing().charAt(0);
        return Option;
    }

    public static void checkEntry() throws Exception
    {

        List<Entry> tempEntries = m_userVault.getEntries();
        System.out.println("Enter the entries index:");
        int index = getInt();
        Entry tempEntry = tempEntries.get(index);

        int stop = 1;
        while(stop == 1)
        {
            clearScreen();
            if(tempEntry instanceof EntryLogin)    
            {

                EntryLogin temp = (EntryLogin) tempEntry;

                System.out.println("ID: " + temp.getId());

                System.out.println("Entry name: " + temp.getEntryName());

                if(!temp.getUsername().equals(""))
                    System.out.println("Username: " + temp.getUsername());

                if(!temp.getEmail().equals(""))
                    System.out.println("Email: " + temp.getEmail());

                System.out.println("Password: " + temp.getPassword());

                if(!temp.getAdditionalNote().equals(""))
                    System.out.println("Additional notes: " + temp.getAdditionalNote());
                
                if(temp.getCategoryId() != null)
                {
                    Category tempCategory = m_userVault.getCategory(temp.getCategoryId());
                    System.out.println("Category: " + tempCategory.getCategoryName());
                }
            } 
            if(tempEntry instanceof EntrySSH)    
            {
                EntrySSH temp = (EntrySSH) tempEntry;

                System.out.println("ID: " + temp.getId());

                System.out.println("Entry name: " + temp.getEntryName());

                System.out.println("Private key:\n" + temp.getPrivateKey());

                System.out.println("Public Key:\n" + temp.getPublicKey());

                if(temp.getCategoryId()!=null)
                {
                    Category tempCategory = m_userVault.getCategory(temp.getCategoryId());
                    System.out.println("Category: " + tempCategory.getCategoryName());
                }

                if(!temp.getAdditionalNote().equals(""))
                    System.out.println("Additional notes: " + temp.getAdditionalNote());

            }
            if(tempEntry instanceof EntryPGP)    
            {

                // System.out.println("Work in progress sorry :(");
                EntryPGP temp = (EntryPGP) tempEntry;

                System.out.println("ID: " + temp.getId());

                if(!temp.getEntryName().equals(""))
                    System.out.println("Entry name: " + temp.getEntryName());

                System.out.println("Public Key:\n" + temp.getPublicKey());

                System.out.println("Private Key:\n" + temp.getPrivateKey());

                if(!temp.getAdditionalNote().equals(""))
                    System.out.println("Additional notes: " + temp.getAdditionalNote());
                
                if(temp.getCategoryId()!=null)
                {
                    Category tempCategory = m_userVault.getCategory(temp.getCategoryId());
                    System.out.println("Category: " + tempCategory.getCategoryName());
                }

            }
            System.out.println("[D] Delete Entry\t[E] Edit Entry\t[C] Category\t[Q] Quit");
            char option = getChar();
            switch(option)
            {
                case 'D':
                {
                    System.out.println("Are you sure?(Y/N)");
                    char sure = getChar() ;
                    if(sure == 'y'|| sure == 'Y')
                    {
                        stop = 0;
                        m_userVault.removeEntry(tempEntry.getId());
                    }
                    break;
                }
                case 'E':
                {

                    break;
                }
                case 'C':
                {
                    HashMap<Integer, Category> tempCategories = m_userVault.getCategories();
                    if(tempCategories.size() == 0)
                    {
                        System.out.println("No Categories\n");
                        break;
                        
                    }
                    List<UUID> tempCategoriesIds = new ArrayList<UUID>();
                    if(tempEntry.getCategoryId() == null)
                    {
                         
                        System.out.println("Choose a category: ");
                        for(int i=0; i<tempCategories.size(); i++)
                        {
                            Category tempCategory = tempCategories.get(i);
                            tempCategoriesIds.add(tempCategory.getId());
                            System.out.println("[" + i  + "] " + tempCategory.getCategoryName());
                        }
                        int choice = getInt();
                        tempEntry.setCategoryId(tempCategoriesIds.get(choice));
                    }
                    else
                    {
                        System.out.println("[C] Change Category\t[R] Remove Category\t[E] Exit");
                        char choice = getChar();
                        switch(choice)
                        {
                            case 'C':
                            {
                                System.out.println("Choose a category: ");
                                for(int i=0; i<tempCategories.size(); i++)
                                {
                                    Category tempCategory = tempCategories.get(i);
                                    tempCategoriesIds.add(tempCategory.getId());
                                    System.out.println("[" + i  + "] " + tempCategory.getCategoryName());
                                }
                                int choice2 = getInt();
                                tempEntry.setCategoryId(tempCategoriesIds.get(choice2));
                                break;
                            }
                            case 'R':
                            {
                                tempEntry.setCategoryId(null);
                                break;
                            }
                            case 'E':
                            {
                                break;
                            }

                        }
                    }
                    
                    break;
                }
                case 'Q':
                {
                    stop = 0;
                    break;
                }
            }
        
        }
    }
    public static void newEntry() throws Exception
    {
        System.out.println("Which type of entry:");
        System.out.println("[0] Login Information\t[1] SSH Keys\t[2] PGP Keys\t[3] Exit");
        int option2 = getInt();
        switch(option2)
        {
            case 0:
            {
                System.out.println("Enter name of entry: ");
                String entryName = getLine();
                System.out.println("Enter any additional notes: ");
                String additionalNote = getLine();
                System.out.println("Enter Password: (Leave empty to automatically generate)");
                String password = getLine();
                System.out.println("Enter any username associated with the account:");
                String username = getLine();
                System.out.println("Enter any email associated with this account: ");
                String email = getLine();

                Entry newLoginEntry = new EntryLogin(entryName, additionalNote, password, username, email);
                m_userVault.addEntry(newLoginEntry);
                break;
            }
            case 1: 
            {
                System.out.println("Enter entry name: ");
                String entryName = getLine();
                System.out.println("Enter additional note: ");
                String additionalNote = getLine();
                System.out.println("Choose the type of SSH key type:");
                System.out.println("[0] RSA\t[1] ECDSA\t[2] Ed25519");
                int sshOption = getInt();
                EntrySSH newSSHEntry = null;
                int leave = 0;
                switch(sshOption)
                {
                    case 0:
                    {
                        newSSHEntry = new EntrySSH(entryName, additionalNote, SSHType.RSA);
                        break;
                    }
                    case 1:
                    {
                        newSSHEntry = new EntrySSH(entryName, additionalNote, SSHType.ECDSA);
                        break;
                    }
                    case 2:
                    {
                        newSSHEntry = new EntrySSH(entryName, additionalNote, SSHType.Ed25519);
                        break;
                    }
                    default:
                    {
                        System.out.println("Invalid SSH Type");
                        leave = 121;
                        
                    }
                }
                if(leave==121)
                    break;
                
                m_userVault.addEntry(newSSHEntry);

                break;
            }
            case 2:
            {
                EntryPGP newEntryPGP = null;
                System.out.println("Enter entry name: ");
                String entryName = getLine();
                System.out.println("Enter additional note: ");
                String additionalNote = getLine();
                System.out.println("Enter some user info: ");
                System.out.println("(Template: Jane Doe <Janedoe@email.com>)");
                String userEmail = getLine();
                if(userEmail.equals(""))
                {
                    userEmail = "janedoe@email.com";
                }
                newEntryPGP = new EntryPGP(entryName, additionalNote, userEmail);
                m_userVault.addEntry(newEntryPGP);
                break;
            }
        }

    }
    public static void ManageEntries() throws Exception
    {
        System.out.println("Manage Entries");
        List<Entry> tempEntries = m_userVault.getEntries();
        int stop = 0;
        while(stop==0)
        {
            clearScreen();
            int noEntries = tempEntries.size();
            if(noEntries == 0)
            {
                System.out.println("There are no entries");
                System.out.println("[N] New Entry\t[E] Exit");
                char option = getChar();
                switch (option) {
                    case 'N':
                    {
                        newEntry();
                        break;
                    }
                        
                
                    case 'E':
                    {
                        stop = 1;
                        break;
                    }
                    default:
                        break;
                }
            }
            else
                {
                for(int i=0; i<noEntries; i++)
                {
                    Entry tempEntry = tempEntries.get(i);
                    System.out.println("Entry [" + i + "]: " + tempEntry.getEntryName());
                    
                }
                System.out.println("[N] New Entry\t[S] Show Entry Information\t[E] Exit");
                char option = getChar();
                if(option == 'E')
                {
                    stop = 1;
                    continue;
                }
                if(option == 'N')
                {
                    newEntry();
                    continue;
                }
                if(option == 'S')
                {
                    checkEntry();
                }
                }
        }
    }
    
    public static void newCategory()
    {

        System.out.println("New Category");
        System.out.println("Enter Category name:");
        String categoryName = getLine();
        Category newCategory = new Category(categoryName);
        m_userVault.addCategory(newCategory);
    }

    public static void ManageCategories()
    {
        System.out.println("Manage Categories");
        HashMap<Integer, Category> tempCategories = m_userVault.getCategories();
        int stop2 = 0;
        while(stop2 == 0)
        {

        clearScreen();
        int noCategories = tempCategories.size();
        if(noCategories==0)
        {
            System.out.println("No Categories have been created!");
            System.out.println("[A] Add New Category\t[E] Exit");
            char option = getChar();
            switch (option) {
                case 'A':
                {
                    newCategory();
                    break;
                }
            
                case 'E':
                {
                    stop2 = 1;
                    break;
                }
                
                default:
                    break;
            }

        }
        
        else
        {

            List<UUID> idsList = new ArrayList<UUID>(); 
            for(Integer i=0; i<noCategories; i++)
            {
                Category tempCategory = tempCategories.get(i);
                idsList.add(tempCategory.getId());
                System.out.println("Categorie [" + i + "]:");
                System.out.println("ID: " + tempCategory.getId());
                System.out.println("Nume: " + tempCategory.getCategoryName());
                System.out.println();
            }

            System.out.println("[A] Add New Category\t[D] Delete Category\t[E] Exit");
            char Option = getChar();
            switch(Option)
            {
                case 'A':
                {
                    newCategory();
                    break;
                }

                case 'D':
                {
                    System.out.println("Delete Category");
                    System.out.println("Choose which category to delete");
                    int categoryIndex = getInt();
                    m_userVault.removeCategory(idsList.get(categoryIndex));
                    break;
                }

                case 'E':
                {
                    stop2 = 1;
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
        }

    }
    public static void Menu() throws Exception
    {

        int stop = 0;
        while(stop==0)
        {
            clearScreen();
            System.out.println("Options:");  
            System.out.println("[1] Manage Entries");
            System.out.println("[2] Manage Categories");
            System.out.println("[4] DEBUG");
            System.out.println("[3] Exit");
            int Option = getInt();
            switch (Option) {
                case 1:
                {
                    ManageEntries(); 
                    break;
                }
                case 2:
                {
                    ManageCategories();
                    break;
                }
                case 3:
                    stop = 1;
                    break;
                case 4:
                {
                    VaultImportExport.exportToFile(m_userVault);
                }
                default:
                    continue;
            }
        }
        
        input.close();
        System.out.println("Goodbye!");
    }
}
