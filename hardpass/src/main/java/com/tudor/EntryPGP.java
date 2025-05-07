package com.tudor;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.pgpainless.PGPainless;
import org.pgpainless.key.generation.type.rsa.RsaLength;

public class EntryPGP extends Entry{

    private PGPSecretKeyRing keyRing;

    static {
        Security.addProvider(new BouncyCastleProvider());
    } 

    //format of userId: user <email> 
    public EntryPGP(String EntryName, String additionalNote, String userId) throws Exception
    {
        super(EntryName, additionalNote);

        keyRing = PGPainless.generateKeyRing()
            .simpleRsaKeyRing(userId, RsaLength._4096);

    }

    public String getPublicKey()
    {
        return keyRing.getPublicKey().toString();
    }
    public String getPrivateKey()
    {
        return keyRing.getSecretKey().toString();
    }

    // public String EncryptMessage(String message)
    // {
    // }

    // public String DecryptMessage(String encryptedMessage)
    // {
        
    // }

}
