package com.halanx.userapp.Adapters;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Fragments.MainFragment;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nishant on 16/08/17.
 */

public class StoreSearchAdapter extends RecyclerView.Adapter<StoreSearchAdapter.StoresViewHolder> {

    JSONObject storeList;
    Context context;
    JSONObject data;
    TextView itemCount;
    String Image;


    public StoreSearchAdapter(JSONObject json, Context c) {
        storeList = json;
        Log.d("valueofjson", String.valueOf(storeList));
        context = c;


    }

    @Override
    public StoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_store_recycler, parent, false);
        return new StoresViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StoresViewHolder holder, int position) {


        String url = null;
        try {
            url = "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com/stores/" + storeList.getString("Id");

        JSONObject obj = new JSONObject();

        Volley.newRequestQueue(context).add(new JsonObjectRequest(Request.Method.GET, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                data = response;
                Log.d("storeapi_call", String.valueOf(data));

                try {
                    Image = response.getString("StoreLogo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Picasso.with(context).load(Image).into(holder.ivLogo);
        try {
            Log.d("productsname",storeList.getString("StoreName"));
            holder.tvName.setText(storeList.getString("StoreName"));
           holder.tvAddress.setText(storeList.getString("StoreAddress"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class StoresViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivLogo;
        TextView tvName, tvAddress;

        public StoresViewHolder(View itemView) {
            super(itemView);
            ivLogo = (ImageView) itemView.findViewById(R.id.iv_store_logo);
            tvName = (TextView) itemView.findViewById(R.id.tv_store_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_store_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int pos = getAdapterPosition();
            try {
                HomeActivity.storeID = Integer.parseInt(storeList.getString("Id"));
                HomeActivity.storeLogo = Image;
                HomeActivity.storeName = storeList.getString("StoreName");
                HomeActivity.storePosition = pos;
                HomeActivity.storeCat = storeList.getString("StoreCategory");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            MainFragment fragment = new MainFragment();
            fragment.passdata(itemCount);
            FragmentTransaction fragmentTransaction =
                    ((HomeActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frag_container, fragment);
            fragmentTransaction.commit();


            //  getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).edit().putBoolean("isMap", false).apply();
//                Log.d("position", String.valueOf(pos));


        }
    }
}
