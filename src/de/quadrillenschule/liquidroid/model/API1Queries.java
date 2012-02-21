/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.quadrillenschule.liquidroid.model;

import java.io.IOException;
import java.io.InputStream;
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
public class API1Queries {

 public static InputStream queryOutputStream(String api, String parameters, String apiUrl, String developerkey) throws IOException {
        String url = apiUrl + api + ".html?key=" + developerkey + parameters;
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
        String responseString = "";
        HttpResponse response = (HttpResponse) httpClient.execute(httpPost);
        return response.getEntity().getContent();
    }

}
