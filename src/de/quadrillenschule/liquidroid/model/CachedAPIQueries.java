/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.xml.sax.SAXException;

/**
 *
 * @author andi
 */
public class CachedAPIQueries {

    File cacheFolder;
    public String url, api;
    public long dataage = 0;
    SharedPreferences globalPrefs;

    public CachedAPIQueries(File cacheFolder, SharedPreferences globalPrefs) {
        this.cacheFolder = cacheFolder;
        this.globalPrefs = globalPrefs;
    }

    public String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public void storeInCache(InputStream is, String url) {

        File myfile = getCacheFile(url);

        String endswith = ">";
        if (api.equals("area")) {
            endswith = "</area_list>";
        }
        if (api.equals("initiative")) {
            endswith = "</initiative_list>";
        }

        try {
            String string = convertStreamToString(is);

            if (string.contains(endswith) || (string.contains("status") && (string.contains("ok")))) {
                myfile.delete();

                FileOutputStream fos = new FileOutputStream(myfile);
                fos.write(string.getBytes("UTF-8"));
                fos.close();

            }
        } catch (IOException e) {
        }


    }

    public boolean cacheExists(String purl) {

        return getCacheFile(purl).exists();
    }

    public File getCacheFile(String purl) {
        return new File(cacheFolder, Integer.toHexString(purl.hashCode()) + ".xml");
    }

    public boolean willDownload(String apiUrl, boolean forceNetwork) throws FileNotFoundException {
        if (forceNetwork) {
            return true;
        }

        return !cacheExists(apiUrl);
    }

    public boolean needsDownload(String apiUrl, LQFBInstance instance, Area area, String state) {
        long now = System.currentTimeMillis();
        File cachefile = getCacheFile(apiUrl);
        if (!cachefile.exists()) {
            return true;
        }
        if (now - cachefile.lastModified() > Long.parseLong(globalPrefs.getString("maxdataage", "432000000"))) {
            return true;
        }
        if ((now - cachefile.lastModified()) < Long.parseLong(globalPrefs.getString("mindataage", "180000"))) {
            return false;
        }

        if (area != null) {
            if ((instance.hasSelectedInititiativen(area.getId())) && ((now - cachefile.lastModified()) > Long.parseLong(globalPrefs.getString("favdataage", "3600000")))) {
                return true;
            }
            for (Initiative i : area.getInitiativen()) {
                if ((now - i.getDateForNextEvent().getTime()) > 0) {
                    return true;
                }
            }
        }
        if ((instance.getApiversion().equals(LQFBInstance.API1)) && (state.equals("new")) && hasNewerInis(instance.getMaxIni(), instance, api)) {
            return true;
        }
        return false;


    }

    public boolean hasNewerInis(int oldmax, LQFBInstance instance, String api) {
        if (!api.startsWith("http")) {
            return false;
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;
        Area tempArea = new Area(instance.instancePrefs);
        InitiativenFromAPI1Parser iniParser = new InitiativenFromAPI1Parser(tempArea, instance);
        try {
            saxparser = factory.newSAXParser();
            saxparser.parse(networkInputStream(api + "&min_id=" + oldmax), iniParser);

        } catch (ParserConfigurationException ex) {
            return false;
        } catch (SAXException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }

        if (iniParser.inis.size() > 1) {
            return true;
        } else {
            if ((oldmax == 0) && (iniParser.inis.size() > 0)) {
                return true;
            }
            return false;
        }

    }

    public String getApiURL(String api, String parameters, String apiUrl, String developerkey, String apiversion) {
        if (apiversion.equals(LQFBInstance.API1)) {
            return apiUrl + api + ".html?key=" + developerkey + parameters;
        } else {
            return apiUrl + api +parameters;
        }
    }

    public InputStream queryInputStream(LQFBInstance instance, Area area, String api, String parameters, String apiUrl, String developerkey, String state, boolean forceNetwork, boolean noDownload) throws IOException, FileNotFoundException {
        url = getApiURL(api, parameters, apiUrl, developerkey, instance.getApiversion());
        //  url = apiUrl + api + ".html?key=" + developerkey + parameters;
        this.api = api;
        if ((forceNetwork) && (needsDownload(url, instance, area, state))) {
            try {
                return networkInputStream(url);
            } catch (IOException e) {
                return cacheInputStream(url);
            }
        }
        if (cacheExists(url)) {
            return cacheInputStream(url);
        }
        if (noDownload) {
            return null;
        }
        // area.getInitiativen().clear();
        return networkInputStream(url);
    }

    private InputStream cacheInputStream(String purl) throws FileNotFoundException {
        File myfile = getCacheFile(purl);
        dataage = myfile.lastModified();
        FileInputStream fis = new FileInputStream(myfile);

        return fis;
    }

    private InputStream networkInputStream(String url) throws IOException {
        HttpClient httpClient;

        if (url.startsWith("https")) {
            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            httpClient = new DefaultHttpClient(mgr, client.getParams());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        } else {

            httpClient = new DefaultHttpClient();
        }

     //   HttpPost httpPost = new HttpPost(url);
        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = (HttpResponse) httpClient.execute(httpGet);
        dataage = System.currentTimeMillis();
        storeInCache(response.getEntity().getContent(), url);
        return cacheInputStream(url);
    }
}
