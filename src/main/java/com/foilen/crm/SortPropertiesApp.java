package com.foilen.crm;

import com.foilen.smalltools.tools.CloseableTools;
import com.foilen.smalltools.tuple.Tuple2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SortPropertiesApp {

    public static void main(String[] args) throws Exception {

        sort("src/main/resources/WEB-INF/crm/messages/messages_en.properties");
        sort("src/main/resources/WEB-INF/crm/messages/messages_fr.properties");

    }

    private static void sort(String filename) throws Exception {

        System.out.println(filename);

        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(filename);
        properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        CloseableTools.close(inputStream);

        PrintWriter printWriter = new PrintWriter(filename);
        properties.entrySet().stream()
                .map(e -> new Tuple2<>((String) e.getKey(), (String) e.getValue()))
                .sorted((a, b) -> a.getA().compareTo(b.getA()))
                .forEach(e -> printWriter.println(e.getA() + "=" + e.getB()));
        CloseableTools.close(printWriter);

    }

}
