package com.tudor;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.pgpainless.PGPainless;
import org.pgpainless.algorithm.KeyFlag;
import org.pgpainless.key.generation.KeySpec;
import org.pgpainless.key.generation.type.KeyType;
import org.pgpainless.key.generation.type.ecc.EllipticCurve;
import org.pgpainless.key.generation.type.ecc.ecdh.ECDH;
import org.pgpainless.key.generation.type.rsa.RsaLength;
import org.pgpainless.util.Passphrase;

public class EntryPGP extends Entry{

    PGPSecretKeyRing keyRing;

    static {
        Security.addProvider(new BouncyCastleProvider());
    } 

    //format of userId: user <email> 
    public EntryPGP(String EntryName, String additionalNote, String userId, String password) throws Exception
    {
        super(EntryName, additionalNote);

        keyRing = PGPainless.buildKeyRing()
                .setPrimaryKey(KeySpec.getBuilder(
                        KeyType.RSA(RsaLength._3072),
                        KeyFlag.SIGN_DATA, KeyFlag.CERTIFY_OTHER))
                .addUserId(userId)
                .addSubkey(KeySpec.getBuilder(
                        ECDH.fromCurve(EllipticCurve._P256),
                        KeyFlag.ENCRYPT_COMMS))
                .setPassphrase(Passphrase.fromPassword(password))
                .build();
    }

    // public String EncryptMessage(String message)
    // {
    // }

    // public String DecryptMessage(String encryptedMessage)
    // {
        
    // }

}
