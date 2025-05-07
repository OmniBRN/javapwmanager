package com.tudor;

import java.util.Scanner;
// Ce vreau eu:
// Daca folosesc scanc.read() -> citeste ca un string
// scanc.readNextInt() -> citeste urmatorul int 
public class Scanc {

    static Scanner m_fileInput = null;
    static boolean m_fileIsOpened = false;
    static String read()
    {
        Scanner input = new Scanner(System.in);
        String line = input.nextLine();
        input.close();
        return line;
    }
    static String readf(String filename)
    {
        if(m_fileInput == null)
        {
            m_fileInput = new Scanner(filename);
            m_fileIsOpened = true;
            return m_fileInput.nextLine();
        }
        if(!m_fileInput.hasNextLine())
        {
            m_fileIsOpened = false;
            m_fileInput.close();
            m_fileInput = null;
            return "";
        }
        return m_fileInput.nextLine();
    }

    static String readf()
    {

        if(m_fileInput == null)
        {
            return "";
        }
        if(!m_fileInput.hasNextLine())
        {
            m_fileIsOpened = false;
            m_fileInput.close();
            m_fileInput = null;
            return "";
        }
        return m_fileInput.nextLine();
    }
    static void readfClose()
    {
        m_fileInput.close();
        m_fileInput = null;
        m_fileIsOpened = false;
    }
    
}
