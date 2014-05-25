package com.jacobra.pongapp.app;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * Created by mjacobi on 5/23/2014.
 *
 * RestCaller needs a hashmap input with:
 * url
 * method
 * callback
 */

interface Callback{
    void invoke(Map<Object, Object> data);
}

public class RestCaller extends AsyncTask<Map<Object,Object>, Void, Map<Object,Object>> {
    protected Map<Object, Object> doInBackground(Map<Object, Object>... data) {
        try {
            String url = (String) data[0].get("url");
            String method = (String) data[0].get("method");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            if (method == "GET") {
                HttpGet request = new HttpGet(url);
                response = client.execute(request);

            } else if (method == "POST")  {
                HttpPost request = new HttpPost(url);
                response = client.execute(request);
            } else {
                data[0].put("response", "Missing method");
                data[0].put("statusCode", -9999);
                return data[0];
            }

            int statusCode = response.getStatusLine().getStatusCode();
            data[0].put("response", EntityUtils.toString(response.getEntity(), "UTF-8"));
            data[0].put("statusCode", statusCode);

        } catch (Exception e) {
            data[0].put("response", e.toString());
            data[0].put("statusCode", -9999);
        }
        return data[0];
    }

    protected void onPostExecute(Map<Object, Object> data) {
        Callback cb = ((Callback)data.get("callback"));
        cb.invoke(data);
    }
}


