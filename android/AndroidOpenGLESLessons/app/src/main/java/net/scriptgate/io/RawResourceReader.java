package net.scriptgate.io;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RawResourceReader {

    public static String readTextFromResource(String resourceName) {
        return readText(RawResourceReader.class.getClassLoader().getResourceAsStream("res/raw/" + resourceName + ".glsl"));
    }

    public static String readTextFromResource(final Context context, final int resourceId) {
        return readText(context.getResources().openRawResource(resourceId));
    }

    private static String readText(InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String nextLine;
        try {
            StringBuilder text = new StringBuilder();
            while ((nextLine = bufferedReader.readLine()) != null) {
                text.append(nextLine).append('\n');
            }
            return text.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
