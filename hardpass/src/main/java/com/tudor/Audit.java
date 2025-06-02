package com.tudor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Audit {
    private static final String filePath = "./audit.csv";
    
    public static void AddToAudit(String nume_actiune) throws IOException
    {
        LocalDateTime timestamp = LocalDateTime.now();
        Path path = Paths.get(filePath);
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND))
        {
            timestamp.atZone(ZoneId.of("Europe/Bucharest"));
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(nume_actiune);
            sb.append("\",\"");
            sb.append(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            sb.append("\"\n");
            writer.write(sb.toString());
        }

        
    }

}
