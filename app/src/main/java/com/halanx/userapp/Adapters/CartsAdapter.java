package com.halanx.userapp.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.JSONParser;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by samarthgupta on 14/06/17.
 */

public class CartsAdapter extends RecyclerView.Adapter<CartsAdapter.TempViewHolder> {

    List<CartItem> listItems = new ArrayList<>();
    Context c;
    TextView totalitems, subtotal, delivery;
    int i;
    String val;

    double quantity;
    int quantityInt;


    public CartsAdapter(List<CartItem> listItems, Context cont) {
        this.listItems = listItems;
        this.c = cont;
    }

    @Override
    public TempViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_recycler, parent, false);
        return new TempViewHolder(view, c, listItems);
    }

    @Override
    public void onBindViewHolder(final TempViewHolder holder, final int position) {


        quantity = listItems.get(position).getQuantity();
        quantityInt = ((int) quantity) ;
        i = quantityInt;

        holder.etQuantity.setText(String.valueOf(quantityInt));

        String im = listItems.get(position).getItem().getProductImage();
        if(im!=null){
        Picasso.with(c).load(im).into(holder.cartImage);}
        else {
            Picasso.with(c).load(listItems.get(position).getItem().getRelatedStore().getStoreLogo()).into(holder.cartImage);
        }
        holder.cartName.setText(listItems.get(position).getItem().getProductName());

        String price = "₹ " + listItems.get(position).getItem().getPrice().toString();
        holder.cartPrice.setText(price);
        holder.cartNotes.setText(listItems.get(position).getNotes());


        holder. plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.halanx.com/carts/items/" + holder.holderCartItemList.get(position).getId()+"/";
                if (i < 10) {
                    String ur = "https://api.halanx.com/carts/items/" + holder.holderCartItemList.get(position).getId();
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("Quantity", i + 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(c,holderCartItemList.get(pos).getId()+ " Item "+i, Toast.LENGTH_SHORT).show();

                    Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.PATCH, ur, obj, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            i++;
                            val = Integer.toString(i);
                            holder.etQuantity.setText(val);
                            Log.i("Cart", "Quantity changed of item " + holder.holderCartItemList.get(position).getId());

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }));
                }
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.halanx.com/carts/items/" + holder.holderCartItemList.get(position).getId();
                if (i != 0) {
                    i--;
                    val = Integer.toString(i);
                    holder.etQuantity.setText(val);
                    notifyDataSetChanged();
                    String ur = "https://api.halanx.com/carts/items/" + holder.holderCartItemList.get(position).getId();
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("Quantity", i + 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(c,holderCartItemList.get(pos).getId()+ " Item "+i, Toast.LENGTH_SHORT).show();

                    Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.PATCH, ur, obj, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int position = holder.getAdapterPosition();
                            Log.i("Cart", "Quantity changed of item " + holder.holderCartItemList.get(position).getId());

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("Authorization", c.getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                            return params;
                        }

                    });
                }

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.halanx.com/carts/items/" + holder.holderCartItemList.get(position).getId()+"/";
                Log.d("urlfordelete",url);

                JSONObject obj = new JSONObject();

                try {
                    obj.put("RemovedFromCart", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    Toast.makeText(c,holderCartItemList.get(pos).getId()+ " Item "+i, Toast.LENGTH_SHORT).show();

                Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listItems.remove(position);
                        notifyDataSetChanged();

//                            Log.i("Cart", "Quantity changed of item " + holderCartItemList.get(pos).getId());

                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", c.getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                        return params;
                    }

                });

      //          new SubmitForm().execute(url);

            }
        });
        holder.btNotesProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.halanx.com/carts/items/" + holder.holderCartItemList.get(position).getId();
                holder.notes = holder.cartNotes.getText().toString();
                JSONObject objNotes = new JSONObject();
                try {
                    objNotes.put("Notes", holder.notes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.PATCH, url, objNotes, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        holder.holderCartItemList.get(position).setNotes(holder.notes);
                        notifyDataSetChanged();
                        Toast.makeText(c, "Note added", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c, "Network error", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", c.getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", null));
                        return params;
                    }

                });
            }
        });

        holder.spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = position;
                String url = "https://api.halanx.com/carts/items/" + holder.holderCartItemList.get(pos).getId();
                JSONObject obj = new JSONObject();

                try {
                    obj.put("Quantity", i + 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    Toast.makeText(c,holderCartItemList.get(pos).getId()+ " Item "+i, Toast.LENGTH_SHORT).show();

                Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

//                            Log.i("Cart", "Quantity changed of item " + holderCartItemList.get(pos).getId());

                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", c.getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                        return params;
                    }

                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    private class SubmitForm extends AsyncTask<String, Object, JSONObject> {

        HttpURLConnection urlConnection = null;
        JSONObject response = null;
//        String url = URLS.Post.SHORT_FORM;

        @Override
        protected JSONObject doInBackground(String... strings) {

            String url = strings[0];
            JSONParser jsonParser;
            JSONObject obj = new JSONObject();
          //  Log.d("url",url);
            try {
                obj.put("RemovedFromCart", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            jsonParser = new JSONParser();
            response = jsonParser.getJSONFromUrl(url,obj,c.getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", null));

            Log.d("response", String.valueOf(response));
            return response;
        }
        @Override
        protected void onPostExecute(JSONObject k) {

//            if (response!= null){
//                Intent intent = new Intent(mContext,ResultActivity.class);
//                startActivity(intent);
//            }

        }
    };


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class TempViewHolder extends RecyclerView.ViewHolder {

        ImageView cartImage;
        TextView cartPrice, cartName;
        Spinner spinnerQuantity;
        EditText cartNotes;
        EditText etQuantity;
        TextView plus, minus;

        ImageButton btnDelete, btNotesProceed;
        Context c;
        List<CartItem> holderCartItemList = new ArrayList<>();
        String notes;


        public TempViewHolder(View itemView, Context cont, List<CartItem> cartItems) {

            super(itemView);
            btnDelete = (ImageButton) itemView.findViewById(R.id.bt_product_delete);
            cartImage = (ImageView) itemView.findViewById(R.id.iv_product_image);
            cartName = (TextView) itemView.findViewById(R.id.tv_product_name);
            cartPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
            spinnerQuantity = (Spinner) itemView.findViewById(R.id.sp_product_quantity);
            cartNotes = (EditText) itemView.findViewById(R.id.et_product_notes);
            btNotesProceed = (ImageButton) itemView.findViewById(R.id.bt_product_notes_proceed);
            etQuantity = (EditText) itemView.findViewById(R.id.quantity);
            plus = (TextView) itemView.findViewById(R.id.increment);
            minus = (TextView) itemView.findViewById(R.id.decrement);




            c = cont;
            holderCartItemList = cartItems;




        }
}
}
