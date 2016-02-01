package com.example.rmk.mediaediting.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class APIManager {
    private static APIManager INSTANCE = null;

    public static APIManager getInstance() {

        if(INSTANCE == null) {
            INSTANCE = new APIManager();
        }

        return INSTANCE;
    }

    public JSONObject callPost(Context context, String method, List<NameValuePair> params, boolean bacount) {

        String api_url = method;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(api_url);
        InputStream is = null;

        try {
            if (params != null)
                httppost.setEntity(new UrlEncodedFormEntity(params));
            
            if (bacount) {

//            	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences prefs = Utilitie.getSharedPreferences(context);
//                prefs = context.getSharedPreferences(Constant.PREF_USER_DETAILS, Context.MODE_PRIVATE);
                String access_token = prefs.getString("access-token", null);
                String longitude = prefs.getString("longitude", null);
                String latitude = prefs.getString("latitude", null);
                
                httppost.addHeader("Access-Token", access_token);
                httppost.addHeader("Longitude", longitude);
                httppost.addHeader("Latitude", latitude);
            }
            
            try {
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    //convert response to string
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();

                    //try parse the string to a JSON object
                    return new JSONObject(new JSONTokener(sb.toString()));
                } else {
                    // close connection
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }

            } catch (ClientProtocolException cpe) {
                System.out.println("First Exception caz of HttpResponese :" + cpe);
                cpe.printStackTrace();
            } catch (IOException ioe) {
                System.out.println("Second Exception caz of HttpResponse :" + ioe);
                ioe.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public JSONObject callGet(Context context, String method, List<NameValuePair> params, boolean baccount) {

        String api_url = method;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = null;
        InputStream is = null;

        httpGet = new HttpGet(api_url);

        if (baccount) {
//        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences prefs = Utilitie.getSharedPreferences(context);
            String access_token = prefs.getString("access-token", null);
            String longitude = prefs.getString("longitude", null);
            String latitude = prefs.getString("latitude", null);

            httpGet.addHeader("Access-Token", access_token);
            httpGet.addHeader("Longitude", longitude);
            httpGet.addHeader("Latitude", latitude);
        }

        try {
            HttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                //convert response to string
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                //try parse the string to a JSON object
                return new JSONObject(new JSONTokener(sb.toString()));
            } else {
                // close connection
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (ClientProtocolException cpe) {
            System.out.println("First Exception caz of HttpResponese :" + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Second Exception caz of HttpResponse :" + ioe);
            ioe.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    public JSONObject postImage(Context context, String method, ArrayList<NameValuePair> params, String photo_url, boolean bacount) throws UnsupportedEncodingException {

        String api_url = method;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(api_url);
        InputStream is = null;

        //            if (params != null)
//                httppost.setEntity(new UrlEncodedFormEntity(params));

        if (bacount) {

            SharedPreferences prefs = Utilitie.getSharedPreferences(context);
            String access_token = prefs.getString("access-token", null);
            String longitude = prefs.getString("longitude", null);
            String latitude = prefs.getString("latitude", null);

            httppost.addHeader("Access-Token", access_token);
            httppost.addHeader("Longitude", longitude);
            httppost.addHeader("Latitude", latitude);
        }

        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntity.addPart(params.get(0).getName(), new StringBody(params.get(0).getValue()));
        multipartEntity.addPart(params.get(1).getName(), new StringBody(params.get(1).getValue()));
        multipartEntity.addPart(params.get(2).getName(), new StringBody(params.get(2).getValue()));
        multipartEntity.addPart(params.get(3).getName(), new StringBody(params.get(3).getValue()));
        if(params.get(1).getValue().equals("1")){
            multipartEntity.addPart("attach", new FileBody(new File(photo_url), "article.jpg", "image/jpeg"));
        }else {
            multipartEntity.addPart("attach", new FileBody(new File(photo_url), "article.mp4", "video/quicktime"));
        }

        httppost.setEntity(multipartEntity);


        try {
            HttpResponse response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                //convert response to string
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                //try parse the string to a JSON object
                return new JSONObject(new JSONTokener(sb.toString()));
            } else {
                // close connection
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (ClientProtocolException cpe) {
            System.out.println("First Exception caz of HttpResponese :" + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Second Exception caz of HttpResponse :" + ioe);
            ioe.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject uploadProfileImage(Context context, String method, ArrayList<NameValuePair> params, String photo_url, boolean bacount) throws UnsupportedEncodingException {

        String api_url = method;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(api_url);
        InputStream is = null;

        //            if (params != null)
//                httppost.setEntity(new UrlEncodedFormEntity(params));

        if (bacount) {

            SharedPreferences prefs = Utilitie.getSharedPreferences(context);
            String access_token = prefs.getString("access-token", null);
            String longitude = prefs.getString("longitude", null);
            String latitude = prefs.getString("latitude", null);

            httppost.addHeader("Access-Token", access_token);
            httppost.addHeader("Longitude", longitude);
            httppost.addHeader("Latitude", latitude);
        }

        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntity.addPart(params.get(0).getName(), new StringBody(params.get(0).getValue()));
        multipartEntity.addPart(params.get(1).getName(), new StringBody(params.get(1).getValue()));
        multipartEntity.addPart(params.get(2).getName(), new StringBody(params.get(2).getValue()));
        multipartEntity.addPart(params.get(3).getName(), new StringBody(params.get(3).getValue()));
        multipartEntity.addPart("photo", new FileBody(new File(photo_url), "avatar.jpg", "image/jpeg"));
        httppost.setEntity(multipartEntity);


        try {
            HttpResponse response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                //convert response to string
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                //try parse the string to a JSON object
                return new JSONObject(new JSONTokener(sb.toString()));
            } else {
                // close connection
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (ClientProtocolException cpe) {
            System.out.println("First Exception caz of HttpResponese :" + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Second Exception caz of HttpResponse :" + ioe);
            ioe.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getContentString(Context context, int string_id) {
		String mystring = context.getResources().getString(string_id);
		
		return mystring;
	}
}

