package com.tudor;

import com.sshtools.common.publickey.SshKeyPairGenerator;
import com.sshtools.common.ssh.components.SshKeyPair;

public class EntrySSH extends Entry{
    private String m_privateKey;
    private String m_publicKey;
    SSHType m_sshType;

    public EntrySSH(String entryName, String additionalNote, SSHType sshType) throws Exception
    {
        super(entryName, additionalNote);
        switch(sshType)
        {
            case RSA:
            {
                SshKeyPair pair = SshKeyPairGenerator.generateKeyPair(
                    SshKeyPairGenerator.SSH2_RSA, 3072
                );
                m_privateKey = pair.getPrivateKey().toString();
                m_publicKey = pair.getPublicKey().toString();
                break;
            }

            case ECDSA:
            {
                SshKeyPair pair = SshKeyPairGenerator.generateKeyPair(
                    SshKeyPairGenerator.ECDSA,521 
                );
                m_privateKey = pair.getPrivateKey().toString();
                m_publicKey = pair.getPublicKey().toString();
                break;
            }

            case Ed25519:
            {
                SshKeyPair pair = SshKeyPairGenerator.generateKeyPair(
                    SshKeyPairGenerator.ED25519
                );
                m_privateKey = pair.getPrivateKey().toString();
                m_publicKey = pair.getPublicKey().toString();
                break;
            }
        }

    }

    public String getPrivateKey(){ return m_privateKey;};
    public String getPublicKey(){ return m_publicKey;};




}
