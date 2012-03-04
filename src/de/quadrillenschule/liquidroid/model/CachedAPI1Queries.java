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

    public void storeInCache(InputStream is) {
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

    public boolean willDownloadQuery(String api, String parameters, String apiUrl, String developerkey, boolean forceNetwork) throws FileNotFoundException {
       if (forceNetwork){return true;}
        url = apiUrl + api + ".html?key=" + developerkey + parameters;
        return !cacheExists(url);
    }

    public InputStream queryInputStream(String api, String parameters, String apiUrl, String developerkey, boolean forceNetwork) throws IOException, FileNotFoundException {
        url = apiUrl + api + ".html?key=" + developerkey + parameters;
        this.api = api;
        if (forceNetwork) {
            return networkInputStream(url);
        }
        if (cacheExists(url)) {
            return cacheInputStream(url);
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

        storeInCache(response.getEntity().getContent());
        return cacheInputStream(url);
    }
}
