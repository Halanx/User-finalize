package com.halanx.userapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url, JSONObject message,String token) {

        URL obj = null;
        StringBuffer response = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setReadTimeout(15000);
            con.setConnectTimeout(20000);
            //add reuqest header
            //add reuqest header
            con.setRequestMethod("PATCH");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", token);
            con.connect();
            //con.setFixedLengthStreamingMode(message.getBytes().length);
            //  String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

            // Send post request

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            //    wr.writeBytes(urlParameters);
            wr.write(message.toString().getBytes());
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            Log.d("Post parameters : ", String.valueOf(responseCode));
            if(responseCode == 301) {
                Log.d("responseheader",con.getHeaderField("location"));
                con.getHeaderField("location");
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("response", String.valueOf(response));

            jObj = new JSONObject(String.valueOf(response));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jObj;
    }
    public JSONObject getJSONFromArray (String url, JSONArray message) {

        URL obj = null;
        StringBuffer response = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setReadTimeout(15000);
            con.setConnectTimeout(20000);
            //add reuqest header
            //add reuqest header
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            con.connect();
            //con.setFixedLengthStreamingMode(message.getBytes().length);
            //  String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

            // Send post request

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            //    wr.writeBytes(urlParameters);
            wr.write(message.toString().getBytes());
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            Log.d(" URL :", url);
            // Log.d("Post parameters : ",urlParameters);


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            jObj = new JSONObject(String.valueOf(response));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jObj;
    }

    public JSONObject getJSONFromUrl(String url) {

        StringBuffer response = new StringBuffer();
        JSONObject jsonObject = null;
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setReadTimeout(15000);
            con.setConnectTimeout(20000);

            // optional default is GET
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //add request header
            int responseCode = con.getResponseCode();
            Log.d("Sending 'GET'", url);
            Log.d("Response Code : ", String.valueOf(responseCode));

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Log.d("response", String.valueOf(response));
            jsonObject = new JSONObject(String.valueOf(response));
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //print result
       return jsonObject;

    }

    public String jsonpostrequest(String targetURL, String urlParameters)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;payload=" + urlParameters);


            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

}