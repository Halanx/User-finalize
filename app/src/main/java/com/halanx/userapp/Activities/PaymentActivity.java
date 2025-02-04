package com.halanx.userapp.Activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.OrderInfo;
import com.halanx.userapp.POJO.UserInfo;
import com.halanx.userapp.R;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;


public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {


    LinearLayout ll1, ll2, ll3;

    String addressDetails, date, timings, starttime, endtime;

    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;
    OrderInfo order;
    ProgressDialog pd;
    int cartId;

    String total;
    private String merchantKey, userCredentials;

    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;

    // This sets the configuration
    private PayuConfig payuConfig;
    Boolean isOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

//        Log.i("Subs",getIntent().getStringExtra("Subscription"));
//        SubscriptionInfo info = new GsonBuilder().create().fromJson(getIntent().getStringExtra("Subscription"), SubscriptionInfo.class);
//        Log.i("Subs",info.getDeliveriesLeft()+"");


        isOrder = getIntent().getBooleanExtra("isOrder", false);
        Log.i("IF",isOrder+" ");
        //If its an order post order, otherwise its a subscription. DON'T POST ORDER.

        Payu.setInstance(this);

        total = getIntent().getStringExtra("total_cost");
        Log.d("total",total);

        Log.d("current_time", new SimpleDateFormat("HH:mm:ss").format(new Date().getTime() + (01 * 60 * 60 * 1000)));
        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);

        addressDetails = getIntent().getStringExtra("AddressDetails");
        Boolean isDelScheduled = getIntent().getBooleanExtra("deliveryScheduled", false);
        Log.d("date_change", String.valueOf(isDelScheduled));

        if (isDelScheduled) {
            date = String.valueOf(getIntent().getStringExtra("Date"));
            timings = String.valueOf(getIntent().getStringExtra("Timings"));

            Log.d("date_change", date);
            Log.d("date_change", timings);
            starttime = timings.substring(0, 5);
            endtime = timings.substring(6, 11);
        }

        ll1 = (LinearLayout) findViewById(R.id.ll_PayU);
        ll3 = (LinearLayout) findViewById(R.id.ll_Cash);
        if(!isOrder){
            ll3.setVisibility(View.GONE);
            View v =findViewById(R.id.view);
            v.setVisibility(View.GONE);
        }


        ll1.setOnClickListener(this);
        ll3.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_PayU: {


                navigateToBaseActivity();
                break;


            }


            case R.id.ll_Cash: {

                //If it's an order for products
                if (isOrder) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    long userMobile = Long.parseLong(sharedPreferences.getString("MobileNumber", null));

                    SharedPreferences sharedPref = getSharedPreferences("location", Context.MODE_PRIVATE);
                    float latitude = sharedPref.getFloat("latitudeDelivery", 0);// LATITUDE
                    float longitude = sharedPref.getFloat("longitudeDelivery", 0);// LONGITUDE
                    String trans_id = null;

//                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, "https://api.halanx.com/carts/detail/", null, new com.android.volley.Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                cartId = response.getInt("id");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new com.android.volley.Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                        }
//                    }){
//                        @Override
//                        public Map<String, String> getHeaders() throws AuthFailureError {
//                            Map<String, String> params = new HashMap<String, String>();
//                            params.put("Content-Type", "application/json");
//                            params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
//                            return params;
//                        }
//
//                    });

                    if ((getIntent().getBooleanExtra("deliveryScheduled", false))) {
                        order = new OrderInfo(userMobile, addressDetails, date, starttime, endtime, false, null, latitude, longitude, trans_id,Double.parseDouble(total),true);
                        Log.d("done", "done");
                    } else if (!(getIntent().getBooleanExtra("deliveryScheduled", true))) {
                        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        String current_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                        Log.d("time", current_time);

                        order = new OrderInfo(userMobile, addressDetails, date, current_time, null, true, null, latitude, longitude, trans_id,Double.parseDouble(total),true);
                    }


                    Log.i("ORDER", order.getDeliveryAddress() + order.getLatitude() + order.getLongitude());


                    pd = new ProgressDialog(PaymentActivity.this);
                    pd.setTitle("Please wait");
                    pd.setMessage("Posting your order");
                    pd.setCancelable(false);
                    pd.show();

                    Call<OrderInfo> callOrder = client.postUserOrder(getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null),order);
                    callOrder.enqueue(new Callback<OrderInfo>() {
                        @Override
                        public void onResponse(Call<OrderInfo> call, Response<OrderInfo> response) {
//                        Toast.makeText(PaymentActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();

                            pd.setTitle("Order placed!");
                            pd.setMessage("You can review your order in orders.");
                            pd.dismiss();

                            startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                            finish();

                        }

                        @Override
                        public void onFailure(Call<OrderInfo> call, Throwable t) {

                            Toast.makeText(PaymentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        }
                    });

                }
                else
                {
                    String uri = "https://api.halanx.com/users/addbalance/";
                    JSONObject jsonObject = new JSONObject();
                    Log.d("ammount added",uri);
                    try {
                        jsonObject.put("Value",Double.parseDouble(total));
                        Log.d("ammount added",total);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, uri, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ammount added",total);
                            startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                            finish();


                        }
                    },new com.android.volley.Response.ErrorListener(){

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ammount added", String.valueOf(error));
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                            return params;
                        }

                    });
                }


                break;

            }

        }
    }

    public void navigateToBaseActivity() {

        String userInfo = getSharedPreferences("Login", Context.MODE_PRIVATE).getString("UserInfo", null);
        UserInfo user = new GsonBuilder().create().fromJson(userInfo, UserInfo.class);


        merchantKey = "f1tDUh";
        int environment = PayuConstants.PRODUCTION_ENV;
        String email = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("email",null);
        String amount = total;

//        merchantKey = ((EditText) findViewById(R.id.editTextMerchantKey)).getText().toString();
//        String amount = ((EditText) findViewById(R.id.editTextAmount)).getText().toString();
//        String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
//
//        String value = environmentSpinner.getSelectedItem().toString();
//        int environment;
//        String TEST_ENVIRONMENT = getResources().getString(R.string.test);
//        if (value.equals(TEST_ENVIRONMENT))
//            environment = PayuConstants.STAGING_ENV;
//        else
//            environment = PayuConstants.PRODUCTION_ENV;
//
        userCredentials = merchantKey + ":" + email;

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        /**
         * For Test Environment, merchantKey = "gtKFFx"
         * For Production Environment, merchantKey should be your live key or for testing in live you can use "0MQaQP"
         */
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo("product_info");
        mPaymentParams.setFirstName(getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("firstname","sidhhant"));
        mPaymentParams.setEmail(email);

        /*
        * Transaction Id should be kept unique for each transaction.
        * */
        mPaymentParams.setTxnId("" + System.currentTimeMillis());

        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");

        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        /**
         * These are used for store card feature. If you are not using it then user_credentials = "default"
         * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
         * here merchant_key = your merchant key,
         * user_id = unique id related to user like, email, phone number, etc.
         * */
        mPaymentParams.setUserCredentials(userCredentials);

        //TODO Pass this param only if using offer key
        //mPaymentParams.setOfferKey("cardnumber@8370");

        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
        generateHashFromServer(mPaymentParams);

        /**
         * Below approach for generating hash is not recommended. However, this approach can be used to test in PRODUCTION_ENV
         * if your server side hash generation code is not completely setup. While going live this approach for hash generation
         * should not be used.
         * */
        //String salt = "13p0PXZk";
        //generateHashFromSDK(mPaymentParams, salt);

    }

    /**
     * This method generates hash from server.
     *
     * @param mPaymentParams payments params used for hash generation
     */
    public void generateHashFromServer(PaymentParams mPaymentParams) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PaymentActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected PayuHashes doInBackground(String... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {

                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("http://ec2-35-154-159-227.ap-south-1.compute.amazonaws.com/payment/payment.php");       //replace this

                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        //TODO Below three hashes are mandatory for payment flow and needs to be generated at merchant server
                        /**
                         * Payment hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_hash -
                         *
                         * sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT)
                         *
                         */
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));
                            break;
                        /**
                         * vas_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating vas_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be "default"
                         *
                         */
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            break;
                        /**
                         * payment_related_details_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_related_details_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                            break;

                        //TODO Below hashes only needs to be generated if you are using Store card feature
                        /**
                         * delete_user_card_hash is used while deleting a stored card.
                         * Below is formula for generating delete_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        /**
                         * get_user_cards_hash is used while fetching all the cards corresponding to a user.
                         * Below is formula for generating get_user_cards_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        /**
                         * edit_user_card_hash is used while editing details of existing stored card.
                         * Below is formula for generating edit_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        /**
                         * save_user_card_hash is used while saving card to the vault
                         * Below is formula for generating save_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;

                        //TODO This hash needs to be generated if you are using any offer key
                        /**
                         * check_offer_status_hash is used while using check_offer_status api
                         * Below is formula for generating check_offer_status_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be Offer Key.
                         *
                         */
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);

            progressDialog.dismiss();
            launchSdkUI(payuHashes);
        }
    }

    /**
     * This method adds the Payuhashes and other required params to intent and launches the PayuBaseActivity.java
     *
     * @param payuHashes it contains all the hashes generated from merchant server
     */
    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);

        //Lets fetch all the one click card tokens first
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                //If its an order for items
                if (isOrder) {

                    /**
                     * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                     * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                     *
                     * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                     * for identifying status of transaction. There are two possible status like, success or failure
                     * */
//                new AlertDialog.Builder(this)
//                        .setCancelable(false)
//                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
//                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                dialog.dismiss();
//                            }
//                        }).show();

                    String id = data.getStringExtra("payu_response");

                    JsonObject obj = new JsonParser().parse(id).getAsJsonObject();
                    Log.d("json_data", String.valueOf(obj));

                    String trans_id = String.valueOf(obj.get("txnid"));

                    //id -
                    //txnid -


                    SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    long userMobile = Long.parseLong(sharedPreferences.getString("MobileNumber", null));

                    SharedPreferences sharedPref = getSharedPreferences("location", Context.MODE_PRIVATE);
                    float latitude = sharedPref.getFloat("latitudeDelivery", 0);// LATITUDE
                    float longitude = sharedPref.getFloat("longitudeDelivery", 0);// LONGITUDE
                    Log.d("latitudea", "" + latitude);
                    Log.d("longitude", "" + longitude);

                    if ((getIntent().getBooleanExtra("deliveryScheduled", false))) {

                        order = new OrderInfo(userMobile, addressDetails, date, starttime, endtime, false, null, latitude, longitude, trans_id,Double.parseDouble(total),false);
                        Log.d("done", "done");
                    } else if (!(getIntent().getBooleanExtra("deliveryScheduled", true))) {
                        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

                        order = new OrderInfo(userMobile, addressDetails, date, time, null, true, null, latitude, longitude, trans_id,Double.parseDouble(total),false);
                    }


                    Log.i("ORDER", order.getDeliveryAddress() + order.getLatitude() + order.getLongitude());


                    pd = new ProgressDialog(PaymentActivity.this);
                    pd.setTitle("Please wait");
                    pd.setMessage("Posting your order");
                    pd.setCancelable(false);
                    pd.show();

                    Call<OrderInfo> callOrder = client.postUserOrder(getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null),order);
                    callOrder.enqueue(new Callback<OrderInfo>() {
                        @Override
                        public void onResponse(Call<OrderInfo> call, Response<OrderInfo> response) {
//                        Toast.makeText(PaymentActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();


                            Intent resultIntenta = new Intent(PaymentActivity.this, HomeActivity.class);

                            resultIntenta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent piResulta = PendingIntent.getActivity(getApplicationContext(),
                                    (int) Calendar.getInstance().getTimeInMillis(), resultIntenta, 0);


// Assign big picture notification

                            NotificationCompat.Builder builder =
                                    (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.logochange)
                                            .setContentTitle("Halanx")
                                            .setContentText("Thank You. You successfully paid Rs. " + total + " for your order to Halanx")
                                            .setSound(RingtoneManager.getValidRingtoneUri(getApplicationContext()))
                                            .setContentIntent(piResulta)
                                            .setAutoCancel(true);
//set intents and pending intents to call activity on click of "show activity" action button of notification

// Gets an instance of the NotificationManager service
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


//to post your notification to the notification bar
                            notificationManager.notify(0, builder.build());


//                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                resultIntent.putExtra("message", message);
//
//                // check for image attachment
//                if (TextUtils.isEmpty("data")) {
//                    showNotificationMessage(getApplicationContext(), title, message, resultIntent);
//                } else {
//                    // image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, resultIntent);
//                }


                            pd.setTitle("Order placed!");
                            pd.setMessage("You can review your order in orders.");
                            pd.dismiss();

                            startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                            finish();

                        }

                        @Override
                        public void onFailure(Call<OrderInfo> call, Throwable t) {

                            Toast.makeText(PaymentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        }
                    });

                }

                else{
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Value",Double.parseDouble(total));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/addbalance/", jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("ammount added",total);
                            startActivity(new Intent(PaymentActivity.this,HomeActivity.class));
                            finish();
                        }
                    },new com.android.volley.Response.ErrorListener(){

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                            return params;
                        }

                    });
                }
            } else {
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

}
