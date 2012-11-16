package com.rkgen.jsontorestkitgenerator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.rkgen.jsontorestkitgenerator.exception.RKCodeGenException;

public class JsonRequestHelper {
    private static Logger logger = Logger.getLogger("RKCodeGen");
    
    public static InputStream getJsonInputStream(String URL) throws RKCodeGenException, IOException{
        
        DefaultHttpClient client = new DefaultHttpClient();
        
        HttpGet getRequest = new HttpGet(URL);
        
        getRequest.addHeader("accept", "application/json");
        
        HttpResponse response = client.execute(getRequest);
        if (response.getStatusLine().getStatusCode() != 200){
            throw new RKCodeGenException("Cannot fetch JSON from URL: " + URL);
        }
        
        return response.getEntity().getContent();
    }

}
