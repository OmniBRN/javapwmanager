package com.tudor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    
    static Vault userVault;
    static Scanner input = new Scanner(System.in);

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

    public static void ManageEntries()
    {
            
    }

    public static void newCategory()
    {

        System.out.println("New Category");
        System.out.println("Enter Category name:");
        String categoryName = getLine();
        Category newCategory = new Category(categoryName);
        userVault.addCategory(newCategory);
    }

    public static void ManageCategories()
    {
        System.out.println("Manage Categories");
        List<Category> tempCategories = userVault.getCategories();
        int stop2 = 0;
        while(stop2 == 0)
        {

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
            for(int i=0; i<noCategories; i++)
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
                    userVault.removeCategory(idsList.get(categoryIndex));
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


    public static void Menu()
    {
        int stop = 0;
        while(stop==0)
        {
            System.out.println("Options:");  
            System.out.println("[1] Manage Entries");
            System.out.println("[2] Manage Categories");
            System.out.println("[3] Exit");
            int Option = getInt();
            switch (Option) {
                case 1:
                {
                       
                }
                case 2:
                {
                    ManageCategories();
                    break;
                }
                case 3:
                    stop = 1;
                    break;
                default:
                    continue;
            }
        }
        System.out.println("Goodbye!");
    }
    
    public static void main(String[] args) {
        // Momentan nu o sa memorez nimic ca pana nu vad ca merge totul ;-;
        System.out.println("Welcome to <name>\n"); 
        System.out.println("Enter a vault name:");
        String vaultName = getLine();
        System.out.println("Enter a master password (Don't forget it, you won't be able to access the passwords)");
        String password = getLine();
        try
        {
            userVault = new Vault(vaultName, password);
            System.out.println("Vault Created!");
            Menu();

        }
        catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
        input.close();
    } 
}