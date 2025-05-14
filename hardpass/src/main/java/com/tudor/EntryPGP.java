package com.tudor;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.pgpainless.PGPainless;
import org.pgpainless.key.generation.type.rsa.RsaLength;

public class EntryPGP extends Entry{

    private PGPSecretKeyRing keyRing;
    private String m_privateKey;
    private String m_publicKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
    } 

    //format of userId: user <email> 
    public EntryPGP(String EntryName, String additionalNote, String userId) throws Exception
    {
        super(EntryName, additionalNote);

        keyRing = PGPainless.generateKeyRing()
            .simpleRsaKeyRing(userId, RsaLength._4096);

        m_publicKey =  keyRing.getPublicKey().toString();
        m_privateKey =  keyRing.getSecretKey().toString();
    }

    public String getPublicKey()
    {
        return m_publicKey;
    }
    public String getPrivateKey()
    {
        return m_privateKey;
    }

    // public String EncryptMessage(String message)
    // {
    // }

    // public String DecryptMessage(String encryptedMessage)
    // {
        
    // }

}
