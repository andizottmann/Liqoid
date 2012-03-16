/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

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
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

/**
 *
 * @author andi
 */
public class CachedAPI1Queries {

    File cacheFolder;
    public String url, api;
    public boolean wasReadFromNetwork = false;
    public long dataage = 0;

    public CachedAPI1Queries(File cacheFolder) {
        this.cacheFolder = cacheFolder;
    }

    private String convertStreamToString(InputStream is) throws IOException {
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
        if (wasReadFromNetwork) {
            File myfile = new File(cacheFolder, url.hashCode() + ".xml");
            try {
                String endswith = ">";
                if (api.equals("area")) {
                    endswith = "</area_list>";
                }
                if (api.equals("initiative")) {
                    endswith = "</initiative_list>";
                }
                String string = convertStreamToString(is);
                string = string.replaceAll("<current_draft_content>(.*?)</current_draft_content>", "");
                if (string.contains(endswith)) {

                    myfile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(myfile);
                    fos.write(string.getBytes("UTF-8"));
                    fos.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public boolean cacheExists(String purl) {
        File myfile = new File(cacheFolder, purl.hashCode() + ".xml");
        return myfile.exists();
    }

    public String getApiURL(String api, String parameters, String apiUrl, String developerkey) {
        return apiUrl + api + ".html?key=" + developerkey + parameters;
    }

    public boolean willDownload(String apiUrl, boolean forceNetwork) throws FileNotFoundException {
        if (forceNetwork) {
            return true;
        }

        return !cacheExists(apiUrl);
    }
    public static long MIN_CACHE_AGE = 1 * 1000 * 60, MAX_CACHE_AGE = 1000 * 60 * 60 * 24 * 3;

    public boolean needsDownload(String apiUrl,LQFBInstance instance, Area area,String state) {
        long now = System.currentTimeMillis();
        File cachefile = new File(cacheFolder, apiUrl.hashCode() + ".xml");
        if (!cachefile.exists()) {
            return true;
        }
        if (now - cachefile.lastModified() < MIN_CACHE_AGE) {
            return false;
        }
        if (now - cachefile.lastModified() > MAX_CACHE_AGE) {
            return true;
        }

        if (area != null) {
            for (Initiative i : area.getInitiativen()) {
                if ((now - i.getDateForNextEvent().getTime()) > 0) {
                    return true;
                }
            }
        }

        if (state.equals("new")) {
            if (hasNewerInis(instance.getMaxIni(), instance, apiUrl)) {
                return true;
            }
        }


        return false;
    }

    public boolean hasNewerInis(int oldmax, LQFBInstance instance, String api) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;
        Area tempArea = new Area(instance.instancePrefs);
        InitiativenFromAPIParser iniParser = new InitiativenFromAPIParser(tempArea, instance);


        try {
            saxparser = factory.newSAXParser();
            saxparser.parse(networkInputStream(api + "&min_id=" + oldmax), iniParser);
        } catch (Exception e) {
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

    public InputStream queryInputStream(LQFBInstance instance, Area area, String api, String parameters, String apiUrl, String developerkey, String state, boolean forceNetwork, boolean noDownload) throws IOException, FileNotFoundException {
        url = apiUrl + api + ".html?key=" + developerkey + parameters;
        this.api = api;
        if ((forceNetwork) && (needsDownload(url, instance, area, state))) {
            return networkInputStream(url);
        }
        if (cacheExists(url)) {
            return cacheInputStream(url);
        }
        if (noDownload) {
            return null;
        }
        return networkInputStream(url);
    }

    private InputStream cacheInputStream(String purl) throws FileNotFoundException {
        File myfile = new File(cacheFolder, purl.hashCode() + ".xml");
        dataage = myfile.lastModified();
        FileInputStream fis = new FileInputStream(myfile);

        return fis;
    }

    private InputStream networkInputStream(String url) throws IOException {
        DefaultHttpClient httpClient;

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
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = (HttpResponse) httpClient.execute(httpPost);
        wasReadFromNetwork = true;
        dataage = System.currentTimeMillis();

        storeInCache(response.getEntity().getContent(), url);
        return cacheInputStream(url);
    }
}
