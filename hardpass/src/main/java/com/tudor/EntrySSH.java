package com.tudor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bouncycastle.util.encoders.UTF8;

import com.sshtools.common.publickey.SshKeyPairGenerator;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.components.SshKeyPair;

public class EntrySSH extends Entry{
    SshKeyPair m_keyPair;
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

    }

    public String getPrivateKey() throws Exception
    {

        File tempFile = File.createTempFile("ssh-private-key-", ".tmp");
        tempFile.deleteOnExit();
        String nullString = null;
        SshKeyUtils.createPrivateKeyFile(m_keyPair, nullString, tempFile);
        return Files.readString(tempFile.toPath());
    }
    public String getPublicKey() throws IOException
    {
        File tempFile = File.createTempFile("ssh-public-key-", ".tmp");
        tempFile.deleteOnExit();
        String nullString = null;
        SshKeyUtils.createPublicKeyFile(m_keyPair.getPublicKey(), nullString, tempFile);
        return Files.readString(tempFile.toPath());

    }



}
