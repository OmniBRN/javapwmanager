package com.tudor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


import com.sshtools.common.publickey.SshKeyPairGenerator;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.components.SshKeyPair;

public class EntrySSH extends Entry{
    private SshKeyPair m_keyPair;
    private String m_publicKey;
    private String m_privateKey;
    private SSHType m_sshType;

    public EntrySSH(String entryName, String additionalNote, SSHType sshType) throws Exception
    {
        super(entryName, additionalNote);
        m_sshType = sshType;
        switch(m_sshType)
        {
            case RSA:
            {
                SshKeyPair pair = SshKeyPairGenerator.generateKeyPair(
                    SshKeyPairGenerator.SSH2_RSA, 3072
                );
                m_keyPair = pair;
                break;
            }

            case ECDSA:
            {
                SshKeyPair pair = SshKeyPairGenerator.generateKeyPair(
                    SshKeyPairGenerator.ECDSA,521 
                );
                m_keyPair = pair;
                break;
            }

            case Ed25519:
            {
                SshKeyPair pair = SshKeyPairGenerator.generateKeyPair(
                    SshKeyPairGenerator.ED25519
                );
                m_keyPair = pair;
                break;
            }
        }

        String nullString = null, nullString2 = null;
        File tempFile = File.createTempFile("ssh-private-key-", ".tmp");
        tempFile.deleteOnExit();
        SshKeyUtils.createPrivateKeyFile(m_keyPair, nullString, tempFile);
        m_privateKey = Files.readString(tempFile.toPath());

        File tempFile2 = File.createTempFile("ssh-public-key-", ".tmp");
        tempFile2.deleteOnExit();
        SshKeyUtils.createPublicKeyFile(m_keyPair.getPublicKey(), nullString2, tempFile2);
        m_publicKey =  Files.readString(tempFile2.toPath());


    }

    public String getSSHType() {return m_sshType.toString();};

    public String getPrivateKey() throws Exception
    {
        return m_privateKey;
    }
    public String getPublicKey() throws IOException
    {
        return m_publicKey;
    }


}
