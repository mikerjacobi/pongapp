package com.jacobra.pongapp.app;

import android.os.AsyncTask;
import com.squareup.okhttp.*;
import java.util.Map;

/**
 * Created by mjacobi on 5/23/2014.
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
            OkHttpClient client = new OkHttpClient();
            Response response;
            if (method == "GET") {
                Request request = new Request.Builder().url(url).build();
                response = client.newCall(request).execute();
            } else if (method == "POST")  {
                RequestBody body = RequestBody.create(null, "");
                Request request = new Request.Builder().url(url).post(body).build();
                response = client.newCall(request).execute();
            } else {
                data[0].put("response", "Missing method");
                data[0].put("statusCode", -9999);
                return data[0];
            }
            data[0].put("response", response.body().string());
            data[0].put("statusCode", response.code());

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


