package com.piglin.testing;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by swyna on 3/1/15.
 */
public class Crawler {


    public static String crawl(String parameter) {
        URL url = null;
        String str = null;
        try {
            url = new URL(parameter);

            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
            Matcher m = p.matcher(con.getContentType());
        /* If Content-Type doesn't match this pre-conception, choose default and
        * hope for the best. */
            String charset = m.matches() ? m.group(1) : "ISO-8859-1";
            Reader r = new InputStreamReader(con.getInputStream(), "ISO-8859-1");//charset);
            StringBuilder buf = new StringBuilder();
            while (true) {
                int ch = r.read();
                if (ch < 0)
                    break;
                buf.append((char) ch);
            }
            str = buf.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //System.out.println("Error: Parameter: "+parameter);
        }
        return str;
    }

}
