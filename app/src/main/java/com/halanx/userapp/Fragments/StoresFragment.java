package com.halanx.userapp.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.StoreInfo;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoresFragment extends Fragment {

    ProgressBar pbFood,pbGrocery;
    TextView itemCount,grocery_text;
    Context c;
    CardView food_layout,grocery_layout;
    SearchView svstore;

    TextView searchtext;
    List<String> product_category;

    ListView list;
    MainFragment.ListViewAdapter searchadapter;
    StoreSearchAdapter sadapter;
    List<String> suggestions = new ArrayList<>();
    JSONObject json;
    JSONArray array;
    String mob;
    JSONArray storesearchdata;
    View v_top;

    public StoresFragment() {
        // Required empty public constructor
    }

    RecyclerView[] rvList = new RecyclerView[2];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stores, container, false);

        rvList[0] = (RecyclerView) v.findViewById(R.id.rv_food);
        rvList[1] = (RecyclerView) v.findViewById(R.id.rv_grocery);
        pbFood = (ProgressBar) v.findViewById(R.id.pb_food);
        pbGrocery = (ProgressBar) v.findViewById(R.id.pb_grocery);
        food_layout = (CardView) v.findViewById(R.id.food_layout);

        svstore = (SearchView) v.findViewById(R.id.storesearch);

        searchtext = (TextView) v.findViewById(R.id.searchtext);
        grocery_text = (TextView) v.findViewById(R.id.grocery_text);
        list = (ListView) v.findViewById(R.id.listview);

        v_top =(View) v.findViewById(R.id.v_top);
        svstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                svstore.setIconifiedByDefault(false);
                searchtext.setVisibility(View.GONE);

            }
        });
//        final EditText searchPlate = (EditText) svstore.findViewById(android.support.v7.appcompat.R.id.search_src_text);
//        searchPlate.setHint("Search Products");
//        View searchPlateView = svstore.findViewById(android.support.v7.appcompat.R.id.search_plate);

//        searchPlateView.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        svstore.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    if (list.getVisibility() == View.GONE) {
                        list.setVisibility(View.VISIBLE);
                        searchtext.setVisibility(View.GONE);
                    }

                } else {
                    list.setVisibility(View.GONE);
                    searchtext.setVisibility(View.GONE);
                }
            }
        });

        mob = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null);
        svstore.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suggestions.clear();

                String url = "https://api.halanx.com/stores/search/" + newText + "/";
                Log.i("Search", url);
                Volley.newRequestQueue(getActivity()).add(new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        list.setVisibility(View.VISIBLE);
                        Log.i("Search", s);
                        json = null;
                        try {
                            json = new JSONObject(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            suggestions.clear();
                            array = json.getJSONObject("hits").getJSONArray("hits");
                            for (int i = 0; i < array.length(); i++) {
                                String proName = array.getJSONObject(i).getJSONObject("_source").getString("StoreName");
                                suggestions.add(proName);
                            }

                            Log.d("storenamedsta", String.valueOf(suggestions));

                            grocery_text.setVisibility(View.GONE);
                            food_layout.setVisibility(View.GONE);
                            v_top.setVisibility(View.GONE);
                            //  ListAdapter
                            searchadapter = new MainFragment.ListViewAdapter(getActivity().getApplicationContext(), suggestions);
                            // Binds the Adapter to the ListView
                            list.setAdapter(searchadapter);

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Log.d("selected_position", String.valueOf(i));
                                    svstore.setQuery(suggestions.get(i),true);

                                    list.setVisibility(View.GONE);
                                    try {
                                        array = json.getJSONObject("hits").getJSONArray("hits");
                                        JSONObject jsonObject = array.getJSONObject(i).getJSONObject("_source");
                                        sadapter = new StoreSearchAdapter(jsonObject,getActivity());


                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                        rvList[1].setLayoutManager(layoutManager);

                                        rvList[1].setAdapter(sadapter);
                                        rvList[1].setHasFixedSize(true);




//
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


//


                                }
                            });




                            // Recycler Adapter
//                            adapterTemp = new SuggestionAdapter(suggestions, getActivity().getApplicationContext(), svstore,json);
//                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//                            suggestion_data.setAdapter(adapterTemp);
//                            suggestion_data.setLayoutManager(layoutManager);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i("Search", volleyError.toString());
                    }
                })).setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 0;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 0;
                    }

                    @Override
                    public void retry(VolleyError volleyError) throws VolleyError {

                    }
                });
                Log.i("Search", suggestions.toString());


                return false;
            }
        });





        HomeActivity.backPress = 1;

        pbFood.setVisibility(View.VISIBLE);
        pbGrocery.setVisibility(View.VISIBLE);

        Retrofit.Builder builder = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl);
        DataInterface client = builder.build().create(DataInterface.class);
        Call<List<StoreInfo>> callStores = client.getStoreInfo();
        callStores.enqueue(new Callback<List<StoreInfo>>() {
            @Override
            public void onResponse(Call<List<StoreInfo>> call, Response<List<StoreInfo>> response) {


                List<StoreInfo> stores = response.body();
                Log.d("store_info", String.valueOf(stores.get(0).getAvailable_categories()));
                List<StoreInfo> grocery = new ArrayList<>();
                List<StoreInfo> food = new ArrayList<>();
                for (int i = 0; i < stores.size(); i++) {
                    if (stores.get(i).getStoreCategory().equals("Food")) {
                        food_layout.setVisibility(View.VISIBLE);
                        food.add(stores.get(i));
                    } else if (stores.get(i).getStoreCategory().equals("Grocery")) {
                        grocery.add(stores.get(i));
                    }
                }

                pbFood.setVisibility(View.GONE);
                pbGrocery.setVisibility(View.GONE);
                rvList[0].setAdapter(new StoresAdapter(food));
                rvList[1].setAdapter(new StoresAdapter(grocery));
                rvList[0].setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rvList[1].setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


            }

            @Override
            public void onFailure(Call<List<StoreInfo>> call, Throwable t) {

                Log.d("done ", "done");
            }
        });

        return v;
    }

    public void passData(Context context,TextView itemCount) {
        c = context;
        this.itemCount = itemCount;


    }

    public class StoresAdapter extends RecyclerView.Adapter<StoresFragment.StoresAdapter.StoresViewHolder> {

        List<StoreInfo> storeList;

        TextView itemCount;
        public StoresAdapter(List<StoreInfo> storeList) {
            this.storeList = storeList;
            Log.d("Storedata", String.valueOf(storeList));
        }

        @Override
        public StoresFragment.StoresAdapter.StoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_store_recycler, parent, false);
            return new StoresFragment.StoresAdapter.StoresViewHolder(v);
        }

        @Override
        public void onBindViewHolder(StoresFragment.StoresAdapter.StoresViewHolder holder, int position) {
            Picasso.with(getActivity().getApplicationContext()).load(storeList.get(position).getStoreLogo()).into(holder.ivLogo);
            holder.tvName.setText(storeList.get(position).getStoreName());
            holder.tvAddress.setText(storeList.get(position).getStoreAddress());
        }

        @Override
        public int getItemCount() {
            return storeList.size();
        }

        public class StoresViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView ivLogo;
            public TextView tvName;
            public TextView tvAddress;

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

//                getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).edit().
//                        putInt("storeID", storeList.get(pos).getId()).
//                        putString("storeLogo", storeList.get(pos).getStoreLogo()).
//                        putString("storeName", storeList.get(pos).getStoreName()).
//                        putInt("storePosition", pos).apply();


//              Log.d("product_categories", storeList.get(pos).getAvailable_categories());
                JsonArray product_category = storeList.get(pos).getAvailable_categories();
                Log.d("datatype", String.valueOf(product_category));
                List data = new ArrayList();
                for (int i=0;i<product_category.size();i++){
                    data.add(product_category.get(i));
                }
         //       Log.d("product_categories", String.valueOf(product_category.get(0).getAsString()));

                HomeActivity.storeID = storeList.get(pos).getId();
                HomeActivity.storeLogo = storeList.get(pos).getStoreLogo();
                HomeActivity.storeName = storeList.get(pos).getStoreName();
                HomeActivity.storePosition = pos;
                HomeActivity.storeCat = storeList.get(pos).getStoreCategory();

                MainFragment fragment = new MainFragment();
                fragment.passdata(itemCount, String.valueOf(data));
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.commit();


                //  getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).edit().putBoolean("isMap", false).apply();
//                Log.d("position", String.valueOf(pos));


            }
        }
    }

    public class StoreSearchAdapter extends RecyclerView.Adapter<StoreSearchAdapter.StoresViewHolder> {

        JSONObject storeList;
        Context context;
        JSONObject data;
        TextView itemCount;
        String Image;
        JSONArray product_category;
        ArrayList<String> storedata;

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
        public void onBindViewHolder(final StoresViewHolder holder, int position) {


            String url = null;
            try {
                url = "https://api.halanx.com/stores/" + storeList.getString("Id");

                JSONObject obj = new JSONObject();

                Volley.newRequestQueue(context).add(new JsonObjectRequest(Request.Method.GET, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        data = response;
                        try {
                            storesearchdata = data.getJSONArray("CategoriesAvailable");
                            Log.d("array", String.valueOf(storesearchdata));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("storeapi_call", String.valueOf(data));

                        try {
                            Image = response.getString("StoreLogo");
                            Log.d("logo",Image);
                            Picasso.with(context).load(Image).into(holder.ivLogo);

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
                    product_category = new JSONArray();
                    product_category = data.getJSONArray("CategoriesAvailable");
                    Log.d("product_categories", String.valueOf(product_category));

                    storedata = new ArrayList<>();
                    HomeActivity.storeID = Integer.parseInt(storeList.getString("Id"));
                    HomeActivity.storeLogo = Image;
                    HomeActivity.storeName = storeList.getString("StoreName");
                    HomeActivity.storePosition = pos;
                    HomeActivity.storeCat = storeList.getString("StoreCategory");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MainFragment fragment = new MainFragment();
                fragment.passdata(itemCount, String.valueOf(storesearchdata));
                FragmentTransaction fragmentTransaction =
                        ((HomeActivity)context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.commit();


                //  getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).edit().putBoolean("isMap", false).apply();
//                Log.d("position", String.valueOf(pos));


            }
        }
    }
}