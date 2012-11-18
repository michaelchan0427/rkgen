package com.rkgen.jsontorestkitgenerator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.rkgen.jsontorestkitgenerator.exception.RKCodeGenException;


/**
 * A simple utility class uses {@link DefaultHttpClient} to fetch JSON from the URL specified.
 * 
 * @author mhc
 *
 */
public class JsonRequestHelper {
    
    private static Logger logger = Logger.getLogger("RKCodeGen");
    
    private static final String ACCEPT_HEADER_VALUE = "application/json";
    
    private static final String ACCEPT_HEADER = "accept";
    
    /**
     * Fetches JSON from the URL 
     * @param URL
     *      The URL to fetch the JSON from, must not be null / empty
     * @return
     *      InputStream of the JSON response
     * @throws RKCodeGenException
     *      if response status code is not 200
     * @throws IOException
     *      Error reading response
     */
    public static InputStream getJsonInputStream(String URL) throws RKCodeGenException, IOException{
        
        DefaultHttpClient client = new DefaultHttpClient();
        
        HttpGet getRequest = new HttpGet(URL);
        
        getRequest.addHeader(ACCEPT_HEADER, ACCEPT_HEADER_VALUE);
        
        logger.info("Fetching JSON from URL: " + URL);
        
        HttpResponse response = client.execute(getRequest);
        if (response.getStatusLine().getStatusCode() != 200){
            throw new RKCodeGenException("Cannot fetch JSON from URL: " + URL);
        }
        
        
        return response.getEntity().getContent();
    }

}
