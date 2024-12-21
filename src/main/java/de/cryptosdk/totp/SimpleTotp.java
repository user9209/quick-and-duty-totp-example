package de.cryptosdk.totp;

import de.cryptosdk.totp.gui.BufferedImageDisplay;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SimpleTotp {
    public static void main(String[] args) {

        File secretFile = new File("mySecret.txt");
        String secret;
        if(secretFile.exists()) {
            try {
                secret = Files.readString(secretFile.toPath());
            } catch (IOException e) {
                System.err.println("Secret file can not be read! " + secretFile.getAbsolutePath());
                return;
            }
        }
        else {
            secret = JOptionPane.showInputDialog(null,
                    "Please enter your TOTP secret:",
                    "Input TOTP Secret",
                    JOptionPane.QUESTION_MESSAGE);

            if(secret == null) {
                System.err.println("Canceled by user!");
                return;
            }

            try {
                Files.writeString(secretFile.toPath(), secret);
            } catch (IOException e) {
                System.err.println("Secret file can not be written! " + secretFile.getAbsolutePath());
                return;
            }
        }

        QrData data = new QrData.Builder()
                .label("SimpleTOTP")
                .secret(secret)
                .issuer("AppName")
                .algorithm(HashingAlgorithm.SHA1) // More on this below
                .digits(6)
                .period(30)
                .build();
        QrGenerator generator = new ZxingPngQrGenerator();
        try {
            byte[] imageData = generator.generate(data);
            String mimeType = generator.getImageMimeType();

            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImageDisplay.show(bis);
            bis.close();

        } catch (QrGenerationException|IOException e) {
            System.err.println(e.getMessage());
        }

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();

        try {
            String pin = codeGenerator.generate(secret, timeProvider.getTime() / 30);

            JOptionPane.showInputDialog(null,
                "TOTP Pin",
                pin);
            System.exit(0);
        } catch (CodeGenerationException e) {
            throw new RuntimeException(e);
        }
    }
}
