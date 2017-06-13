/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import uk.ac.leeds.ccg.andyt.web.WebScraper;

/**
 *
 * @author geoagdt
 */
public class MetOfficeScraper extends WebScraper {
    
    // Rather than store this directly, read it from a config file that is not to be shared online.
    String API_KEY;
            
    public static void main (String[] args) {
        new MetOfficeScraper().run();
    }
    
    public void run() {
        API_KEY = getAPI_KEY();
    }
    
    public String getAPI_KEY(){
        String userDir;
        userDir = System.getProperty("user.dir");
        File f;
        f = new File(
                userDir,
                "MetOfficeAPIKey.txt");
        
        return "382c1804-3077-48cf-a301-f6f95e39679";
    }
    
    public String getXML(
            String aURLString) {
        String result = "";
        HttpURLConnection connection;
        BufferedReader br;
        String line;
        try {
            connection = getOpenHttpURLConnection(aURLString);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                if (responseCode == 404) {
                    return result;
                }
                String message = aURLString + " connection.getResponseCode() "
                        + responseCode
                        + "see http://en.wikipedia.org/wiki/List_of_HTTP_status_codes";
                if (responseCode == 301 || responseCode == 302 || responseCode == 303
                        || responseCode == 403) {
                    message += "and http://en.wikipedia.org/wiki/HTTP_";
                    message += Integer.toString(responseCode);
                }
                throw new Error(message);
            }
            br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            try {
                while ((line = br.readLine()) != null) {
                    if (line.contains("attributes-update")) {
                        line = br.readLine();
                        while (!line.contains("<")) {
                            result += line.trim();
                            line = br.readLine();
                        }
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace(System.err);
            }
            br.close();
            //}
        } catch (Exception e) {
            //e.printStackTrace(System.err);
        }
        return result;
    }
}
