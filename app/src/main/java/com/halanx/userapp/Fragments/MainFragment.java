package com.halanx.userapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Adapters.ProductAdapter;
import com.halanx.userapp.Adapters.ProductSearchAdapter;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.ProductInfo;
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
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    ProgressBar pbProducts;
    Spinner storeSpinner, categorySpinner;
    LinearLayout main;
    RelativeLayout stores;
    TextView brandName;
    ImageView brandLogo;
    DataInterface client;
    Retrofit retrofit;
    Retrofit.Builder builder;
    CollapsingToolbarLayout collapsingToolbarLayout;
    List<StoreInfo> storesList = null;
    List<StoreInfo> grocery, food;
    String mob;
    JSONArray array;
    JSONObject json;
    ListView list;
    ListViewAdapter searchadapter;
    ProductSearchAdapter sadapter;
    RelativeLayout brand_name;



    SearchView svProducts;
    List<String> suggestions = new ArrayList<>();

    TextView itemCount;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mob = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null);

        pbProducts = (ProgressBar) view.findViewById(R.id.pb_products);
        storeSpinner = (Spinner) view.findViewById(R.id.store_spinner);
        categorySpinner = (Spinner) view.findViewById(R.id.for_spinner);
        brand_name = (RelativeLayout) view.findViewById(R.id.brand_name);
        list = (ListView) view.findViewById(R.id.listview);


        svProducts = (SearchView) view.findViewById(R.id.sv_products);

        svProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                svProducts.setIconified(false);
            }
        });
//        final EditText searchPlate = (EditText) svProducts.findViewById(android.support.v7.appcompat.R.id.search_src_text);
//        searchPlate.setHint("Search Products");
//        View searchPlateView = svProducts.findViewById(android.support.v7.appcompat.R.id.search_plate);

//        searchPlateView.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        svProducts.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    if (list.getVisibility() == View.GONE) {
                        list.setVisibility(View.VISIBLE);
                    }

                } else {
                    list.setVisibility(View.GONE);
                }
            }
        });

        svProducts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suggestions.clear();

                list.setVisibility(View.VISIBLE);
                String url = "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com:9200/product/_search?q=ProductName:" + newText + "*";
                Log.i("Search", url);
                Volley.newRequestQueue(getActivity()).add(new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Search", s);
                        json = null;
                        try {
                            json = new JSONObject(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                            array = json.getJSONObject("hits").getJSONArray("hits");
                            for (int i = 0; i < array.length(); i++) {
                                String proName = array.getJSONObject(i).getJSONObject("_source").getString("ProductName");
                                suggestions.add(proName);
                            }

                            Log.d("suggestions", String.valueOf(suggestions));

                            //  ListAdapter
                            searchadapter = new ListViewAdapter(getActivity().getApplicationContext(), suggestions);
                            // Binds the Adapter to the ListView
                            list.setAdapter(searchadapter);

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Log.d("selected_position", suggestions.get(i));
                                    list.setVisibility(View.GONE);
                                    try {
                                        array = json.getJSONObject("hits").getJSONArray("hits");
                                        JSONObject jsonObject = array.getJSONObject(i).getJSONObject("_source");
                                        Log.d("category", String.valueOf(jsonObject));
                                        sadapter = new ProductSearchAdapter(jsonObject, getActivity(), HomeActivity.storeCat, mob, HomeActivity.itemCount);


                                            if (jsonObject.getString("StoreId").equals("62")) {
                                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                                recyclerView.setLayoutManager(layoutManager);
                                            } else {
                                                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                                                recyclerView.setLayoutManager(layoutManager);
                                            }

                                            recyclerView.setAdapter(sadapter);
                                            recyclerView.setHasFixedSize(true);




//
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


//


                                }
                            });




                            // Recycler Adapter
//                            adapterTemp = new SuggestionAdapter(suggestions, getActivity().getApplicationContext(), svProducts,json);
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

        brandName = (TextView) view.findViewById(R.id.brandName);
        brandLogo = (ImageView) view.findViewById(R.id.logo);
        List<String> suggestions = new ArrayList<>();
        main = (LinearLayout) view.findViewById(R.id.main);
        HomeActivity.backPress = 0;

//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE);
//        Picasso.with(getActivity()).load(sharedPreferences.getString("storeLogo", null)).into(brandLogo);
//        brandName.setText(sharedPreferences.getString("storeName", null));
//        storeID = sharedPreferences.getInt("storeID", 0);
//        storePosition = sharedPreferences.getInt("storePosition", 0);

        Picasso.with(getActivity()).load(HomeActivity.storeLogo).into(brandLogo);
        brandName.setText(HomeActivity.storeName);


        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

//        if (getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).getBoolean("isMap", false)) {
//            main.setVisibility(View.GONE);
//            stores.setVisibility(View.VISIBLE);
//
//        } else {
//            main.setVisibility(View.VISIBLE);
//            stores.setVisibility(View.GONE);
//        }

        client = retrofit.create(DataInterface.class);
        storeSpinner.setOnItemSelectedListener(this);

        Call<List<StoreInfo>> callStores = client.getStoreInfo();
        callStores.enqueue(new Callback<List<StoreInfo>>() {
            @Override
            public void onResponse(Call<List<StoreInfo>> call, Response<List<StoreInfo>> response) {


                storesList = response.body();

                food = new ArrayList<StoreInfo>();
                grocery = new ArrayList<StoreInfo>();
                //Separate stores according to category
                if (storesList.size() != 0) {
                    for (int i = 0; i < storesList.size(); i++) {
                        if (storesList.get(i).getStoreCategory().equals("Food")) {
                            food.add(storesList.get(i));
                        } else if (storesList.get(i).getStoreCategory().equals("Grocery")) {
                            grocery.add(storesList.get(i));
                        }
                    }
                }


                pbProducts.setVisibility(View.VISIBLE);
                //Save names of a particular category in spinner
                if (HomeActivity.storeCat.equals("Food")) {

                    List<String> names = new ArrayList<String>();
                    for (int i = 0; i < food.size(); i++) {
                        if (food.get(i) != null) {
                            if (food.get(i).getStoreName() != null && !food.get(i).getStoreName().isEmpty()) {
                                names.add(food.get(i).getStoreName());
                            }
                        }
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, names);
                    spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    storeSpinner.setAdapter(spinnerAdapter);
                    storeSpinner.setSelection(HomeActivity.storePosition);
                    getProductsFromStore(HomeActivity.storeID, HomeActivity.storeCat);

                } else if (HomeActivity.storeCat.equals("Grocery")) {
                    List<String> names = new ArrayList<String>();
                    for (int i = 0; i < grocery.size(); i++) {
                        if (grocery.get(i) != null) {
                            if (grocery.get(i).getStoreName() != null && !grocery.get(i).getStoreName().isEmpty()) {
                                names.add(grocery.get(i).getStoreName());
                            }
                        }
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, names);
                    spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    storeSpinner.setAdapter(spinnerAdapter);
                    storeSpinner.setSelection(HomeActivity.storePosition);
                    getProductsFromStore(HomeActivity.storeID, HomeActivity.storeCat);

                }
            }

            @Override
            public void onFailure(Call<List<StoreInfo>> call, Throwable t) {

                Log.d("servererror", String.valueOf(true));
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("scroll_state", String.valueOf((newState)));
            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    Log.d("scroll_value", String.valueOf(dy));
                    RelativeLayout.LayoutParams lp =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    recyclerView.setLayoutParams(lp);
//                    brand_name.setVisibility(View.GONE);
                    TranslateAnimation collapseAnim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -brand_name.getHeight());
                    collapseAnim.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            brand_name.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    collapseAnim.setDuration(20);
                    //Starts Animation
                    brand_name.startAnimation(collapseAnim);
                    // Scrolling up
                } else {

                    Log.d("scroll_value", String.valueOf(dy));

                    // Scrolling down
                }
            }

        });


        return view;

    }

    private void getProductsFromStore(int storeID, final String storeCategory) {

        Call<List<ProductInfo>> callProductsStore = client.getProductsFromStore(Integer.toString(storeID));
        callProductsStore.enqueue(new Callback<List<ProductInfo>>() {
            @Override
            public void onResponse(Call<List<ProductInfo>> call, Response<List<ProductInfo>> response) {
                pbProducts.setVisibility(View.GONE);
                if (response.body() != null) {
                    List<ProductInfo> products = response.body();
                    adapter = new ProductAdapter(products, getActivity(), HomeActivity.storeCat, mob, HomeActivity.itemCount);

                    if (storeCategory.equals("Food")) {
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                    } else if (storeCategory.equals("Grocery")) {
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                    }

                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                }

            }

            @Override
            public void onFailure(Call<List<ProductInfo>> call, Throwable t) {
                Log.i("TAG", "R" + t.toString());
                pbProducts.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (HomeActivity.storeCat.equals("Food")) {
            Picasso.with(getActivity()).load(food.get(i).getStoreLogo()).into(brandLogo);
            brandName.setText(food.get(i).getStoreName());
            getProductsFromStore(food.get(i).getId(), food.get(i).getStoreCategory());
            storeSpinner.setSelection(i);
        } else if (HomeActivity.storeCat.equals("Grocery")) {
            Picasso.with(getActivity()).load(grocery.get(i).getStoreLogo()).into(brandLogo);
            brandName.setText(grocery.get(i).getStoreName());
            getProductsFromStore(grocery.get(i).getId(), grocery.get(i).getStoreCategory());
            storeSpinner.setSelection(i);
        }


//        getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).edit().
//                putInt("storeID", storesList.get(i).getId()).
//                putString("storeLogo", storesList.get(i).getStoreLogo()).
//                putString("storeName", storesList.get(i).getStoreName()).
//                putInt("storePosition", i).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void passdata(TextView itemCount) {
        this.itemCount = itemCount;
    }

//    class SuggestionAdapter extends RecyclerView.Adapter<MainFragment.SuggestionAdapter.TempViewHolder> {
//
//        List<String> suggestion;
//        Context context;
//        SearchView searchView;
//        JSONObject json;
//        TextView data;
//
//        SuggestionAdapter(List<String> s, Context applicationContext, SearchView searchView, JSONObject json) {
//            suggestion = s;
//            context = applicationContext;
//            this.searchView = searchView;
//            this.json = json;
//        }
//
//        @Override
//        public MainFragment.SuggestionAdapter.TempViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
//            return new MainFragment.SuggestionAdapter.TempViewHolder(view, context, suggestion,json);
//        }
//
//        @Override
//        public void onBindViewHolder(MainFragment.SuggestionAdapter.TempViewHolder holder, int position) {
//            holder.number.setText(String.valueOf(position + 1));
//            holder.data.setText(suggestion.get(position));
//        }
//
//
//        public class TempViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
//            List<String> suggestion;
//            TextView data, number;
//            Context c;
//            JSONObject json;
//            TempViewHolder(View view, Context context, List<String> suggestion, JSONObject json) {
//                super(view);
//                this.suggestion = suggestion;
//                c = context;
//                this.json = json;
//                Log.d("json", String.valueOf(json));
//                data = (TextView) view.findViewById(R.id.search_item);
//                number = (TextView) view.findViewById(R.id.number);
//                data.setOnClickListener(this);
//                number.setOnClickListener(this);
//            }
//
//
//            @Override
//            public void onClick(View view) {
//                int pos = getAdapterPosition();
//                Log.d("position", String.valueOf(suggestion));
//                String proName = null;
//
//
//                JSONArray array = null;
//                try {
//                    array = json.getJSONObject("hits").getJSONArray("hits");
//                    proName = array.getJSONObject(pos).getJSONObject("_source").toString();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.d("productname123",(proName));
//
//
//
////                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
////                recyclerView.setLayoutManager(layoutManager);
////                recyclerView.setAdapter(adapter);
////                recyclerView.setHasFixedSize(true);
//
//
//            }
//        }
//
//
//        @Override
//        public int getItemCount() {
//            return suggestion.size();
//        }
//   }


    public static class ListViewAdapter extends BaseAdapter {

        // Declare Variables

        Context mContext;
        LayoutInflater inflater;
        List<String> suggestions;

        public ListViewAdapter(Context context, List<String> suggestions) {
            mContext = context;

            inflater = LayoutInflater.from(mContext);
            this.suggestions = suggestions;
            }

        public class ViewHolder {
            TextView name;
            TextView number;
        }

        @Override
        public int getCount() {
            return suggestions.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.search_list_item, null);
                // Locate the TextViews in listview_item.xml
                holder.name = (TextView) view.findViewById(R.id.search_item);
                holder.number = (TextView) view.findViewById(R.id.number);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.name.setText(suggestions.get(position));
            holder.number.setText(String.valueOf(position+1));
            return view;

        }
    }


}