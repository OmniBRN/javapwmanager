package main.java.com.tudor;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPKeyPair;

public class EntryPGP extends Entry{

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public EntryPGP(String EntryName, String additionalNote, String publicKey)
    {
        super(EntryName, additionalNote);
    }

    // public String EncryptMessage(String message)
    // {

    // }

    // public String DecryptMessage(String encryptedMessage)
    // {
        
    // }

}
