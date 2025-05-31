package com.tudor;

import java.io.ByteArrayOutputStream;
import java.security.Security;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.pgpainless.PGPainless;
import org.pgpainless.key.generation.type.rsa.RsaLength;

public class EntryPGP extends Entry{

    private String m_privateKey;
    private String m_publicKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
    } 

    //format of userId: user <email> 
    public EntryPGP(String EntryName, String additionalNote, String userId) throws Exception
    {
        super(EntryName, additionalNote);

        PGPSecretKeyRing keyRing = PGPainless.generateKeyRing()
            .simpleRsaKeyRing(userId, RsaLength._4096);

        // Extracting the private key
        PGPSecretKey secretKey = keyRing.getSecretKey();
        ByteArrayOutputStream outPrivate = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutPrivate = new ArmoredOutputStream(outPrivate);
        secretKey.encode(armoredOutPrivate);
        armoredOutPrivate.close();
        m_privateKey =  outPrivate.toString("UTF-8");

        // Extracting the public key
        PGPPublicKey publicKey = keyRing.getPublicKey();
        ByteArrayOutputStream outPublic = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutPublic = new ArmoredOutputStream(outPublic);
        publicKey.encode(armoredOutPublic);
        armoredOutPublic.close();
        m_publicKey = outPublic.toString("UTF-8");
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
