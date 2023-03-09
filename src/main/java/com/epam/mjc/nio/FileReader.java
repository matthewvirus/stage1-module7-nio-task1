package com.epam.mjc.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReader {

    private static final Logger log = Logger.getLogger(FileReader.class.getName());

    public Profile getDataFromFile(File file) {
        HashMap<String, String> profileMap = extractMapFromFile(file);
        return new Profile(
                profileMap.get("Name"),
                Integer.parseInt(profileMap.get("Age")),
                profileMap.get("Email"),
                Long.parseLong(profileMap.get("Phone"))
        );
    }

    public HashMap<String, String> extractMapFromFile(File file) {
        HashMap<String, String> map = new HashMap<>();
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "r");
             FileChannel inChannel = accessFile.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder line = new StringBuilder();
            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                for (int i = 0; i < buffer.limit(); i++) {
                    line.append((char) buffer.get());
                    if((char) buffer.get(i) == '\n') {
                        String[] keyValue = line.toString().trim().split(":");
                        map.put(keyValue[0].trim(), keyValue[1].trim());
                        line = new StringBuilder();
                    }
                }
                buffer.clear();
            }
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
        }
        return map;
    }
}