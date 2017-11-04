package com.halanx.userapp.Fragments;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Adapters.ProductAdapter;
import com.halanx.userapp.Adapters.ProductSearchAdapter;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
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
    RecyclerView categories_Recycler;
    CategoryAdapter categoryAdapter;
    Boolean checked[];
    int selectedPosition=-1;
    int selected=-1;
    TextView searchtext;
    TextView noresult;

    SearchView svProducts;
    List<String> suggestions = new ArrayList<>();

    List<String> categories = new ArrayList<>();
    TextView itemCount;
    String product_category;

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
        brandName = (TextView) view.findViewById(R.id.brandName);
        brandLogo = (ImageView) view.findViewById(R.id.logo);

        searchtext = (TextView) view.findViewById(R.id.searchtext);

        categories_Recycler = (RecyclerView) view.findViewById(R.id.categories_recycler);
        categoryAdapter = new CategoryAdapter(getActivity(),categories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        categories_Recycler.setLayoutManager(layoutManager);
        categories_Recycler.setAdapter(categoryAdapter);
        categories_Recycler.setHasFixedSize(true);

        noresult = (TextView) view.findViewById(R.id.noresult);

        HomeActivity.position=1;
        svProducts = (SearchView) view.findViewById(R.id.sv_products);

        svProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                svProducts.setIconified(false);
                svProducts.setFocusable(true);
                svProducts.setIconifiedByDefault(false);
                searchtext.setVisibility(View.GONE);

            }
        });
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

                searchtext.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.d("newtext",String.valueOf(newText.length()));
                if (newText.length() != 0) {
                    suggestions.clear();
                    searchtext.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    list.setAdapter(null);
                    suggestions.clear();

                    String url = djangoBaseUrl + "products/search/" + newText + "/";
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

                                suggestions.clear();
                                array = json.getJSONObject("hits").getJSONArray("hits");
                                for (int i = 0; i < array.length(); i++) {
                                    String proName = array.getJSONObject(i).getJSONObject("_source").getString("ProductName");
                                    suggestions.add(proName);
                                }

                                Log.d("suggestions", String.valueOf(suggestions));
                                list.setAdapter(null);
                                list.clearChoices();
                                //  ListAdapter
                                list.clearTextFilter();
                                searchadapter = new ListViewAdapter(getActivity().getApplicationContext(), suggestions);
                                // Binds the Adapter to the ListView

                                if (suggestions.size() == 0) {
                                    noresult.setVisibility(View.VISIBLE);
                                    brandLogo.setVisibility(View.GONE);
                                    brandName.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    categories_Recycler.setVisibility(View.GONE);

                                } else {
                                    noresult.setVisibility(View.GONE);
                                    brandLogo.setVisibility(View.VISIBLE);
                                    brandName.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    categories_Recycler.setVisibility(View.VISIBLE);
                                }

                                list.setAdapter(searchadapter);
                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Log.d("selected_position", suggestions.get(i));
                                        svProducts.setQuery(suggestions.get(i), true);
                                        list.setVisibility(View.GONE);

                                        noresult.setVisibility(View.GONE);
                                        brandLogo.setVisibility(View.VISIBLE);
                                        brandName.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        categories_Recycler.setVisibility(View.VISIBLE);
                                        try {
                                            array = json.getJSONObject("hits").getJSONArray("hits");
                                            final JSONObject jsonObject = array.getJSONObject(i).getJSONObject("_source");
                                            Volley.newRequestQueue(getActivity()).add(new JsonObjectRequest(Request.Method.GET, djangoBaseUrl + "stores/" + jsonObject.getString("StoreId"), null, new com.android.volley.Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d("responsedata", String.valueOf(response));
                                                    try {
                                                        categories.clear();

                                                        JSONArray array = new JSONArray(response.getString("CategoriesAvailable"));
                                                        for (int i = 0; i < array.length(); i++) {
                                                            categories.add(String.valueOf(array.get(i)));
                                                        }
                                                        Log.d("catdata", String.valueOf(categories));
                                                        categoryAdapter = new CategoryAdapter(getActivity(), categories);
                                                        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                                        categories_Recycler.setLayoutManager(layoutManager1);
                                                        categories_Recycler.setAdapter(categoryAdapter);
                                                        categories_Recycler.setHasFixedSize(true);


                                                        Picasso.with(getActivity()).load(response.getString("StoreLogo")).into(brandLogo);
                                                        brandName.setText(response.getString("StoreName"));

                                                        sadapter = new ProductSearchAdapter(jsonObject, getActivity(), HomeActivity.storeCat, mob, HomeActivity.itemCount);
                                                        if (response.getString("StoreCategory").equals("Grocery")) {
                                                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                                                            recyclerView.setLayoutManager(layoutManager);

                                                        } else {
                                                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                                            recyclerView.setLayoutManager(layoutManager);
                                                        }

                                                        recyclerView.setAdapter(sadapter);
                                                        recyclerView.setHasFixedSize(true);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            }, new com.android.volley.Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                }
                                            }));
                                            Log.d("category", String.valueOf(jsonObject));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

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
                            return 100000;
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


                }else {
                    noresult.setVisibility(View.GONE);
                    brandLogo.setVisibility(View.VISIBLE);
                    brandName.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    categories_Recycler.setVisibility(View.VISIBLE);
                }
                return false;

            }
        });



        List<String> suggestions = new ArrayList<>();
        main = (LinearLayout) view.findViewById(R.id.main);
        HomeActivity.backPress = 0;

        Picasso.with(getActivity()).load(HomeActivity.storeLogo).into(brandLogo);
        brandName.setText(HomeActivity.storeName);

        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
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
                    adapter = new ProductAdapter(products, getActivity(), HomeActivity.storeCat, mob, HomeActivity.itemCount, null);
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

 }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void passdata(TextView itemCount, String product_category) {
        this.itemCount = itemCount;
        this.product_category = product_category;

        try{
            JSONArray obj = new JSONArray(String.valueOf(this.product_category));
            //   Log.d("product_data", String.valueOf(product_category.get(2)));
            categories= new ArrayList<>();
            for(int i =0;i<obj.length();i++){
                categories.add(String.valueOf(obj.get(i)));
            }

        }catch (Throwable t){
            Log.d("productdata", String.valueOf(t));

        }

    }

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


    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
        Context context;
        List<String> categories;

        public CategoryAdapter(Context c, List<String> categories) {
            context = c;
            this.categories = categories;

        }


        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
            CategoryViewHolder holder = new CategoryAdapter.CategoryViewHolder(view, context);
            return holder;
        }

        @Override
        public void onBindViewHolder(final CategoryViewHolder holder, final int position) {

            Boolean flag = false;
            if(position==0){
                selected++;
            }
            else{
                flag = false;
            }

            //holder.setIsRecyclable(false);
            if(selectedPosition==position) {
                holder.category_name.setBackgroundColor(Color.parseColor("#f00004"));

                holder.category_name.setTextColor(Color.parseColor("#ffffff"));
            }
            else{
                holder.category_name.setBackgroundColor(Color.parseColor("#ffffff"));

                holder.category_name.setTextColor(Color.parseColor("#000000"));
            }
            holder.category_name.setText(categories.get(position));

            if (selected==0)
            {
                holder.category_name.setBackgroundColor(Color.parseColor("#f00004"));
                holder.category_name.setTextColor(Color.parseColor("#ffffff"));
            }
            selected++;
            holder.setIsRecyclable(false);
            holder.category_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    recyclerView.setVisibility(View.INVISIBLE);
                    pbProducts.setVisibility(View.VISIBLE);
                    selected++;
                    holder.category_name.getText();
                    selectedPosition = position;
                    notifyDataSetChanged();

                    Call<List<ProductInfo>> callProductsStore = client.getProductsFromStore(Integer.toString(HomeActivity.storeID));
                    callProductsStore.enqueue(new Callback<List<ProductInfo>>() {

                        @Override
                        public void onResponse(Call<List<ProductInfo>> call, Response<List<ProductInfo>> response) {
                            pbProducts.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            pbProducts.setVisibility(View.GONE);
                            if (response.body() != null) {
                                List<ProductInfo> products = response.body();
                                List<ProductInfo> product_with_specific_category = new ArrayList<ProductInfo>();
                                Log.d("category_name", String.valueOf(holder.category_name.getText()));
                                if (response.body() != null) {
                                    if (String.valueOf(holder.category_name.getText()).equals("All")) {
                                        Log.d("category_name", "done");
                                        List<ProductInfo> productsa = response.body();
                                        adapter = new ProductAdapter(productsa, getActivity(), HomeActivity.storeCat, mob, HomeActivity.itemCount, null);
                                        if (HomeActivity.storeCat.equals("Food")) {
                                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                            recyclerView.setLayoutManager(layoutManager);
                                        } else if (HomeActivity.storeCat.equals("Grocery")) {
                                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                                            recyclerView.setLayoutManager(layoutManager);
                                        }
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setHasFixedSize(true);
                                    }
                                    else {
                                        Log.d("category_name", String.valueOf(holder.category_name.getText()));
                                        Log.d("productsize", String.valueOf(products.size()));

                                        for (int i = 0; i < products.size(); i++) {
                                            if (products.get(i).getCategory().equals(String.valueOf(holder.category_name.getText())))
                                                product_with_specific_category.add(products.get(i));

                                        }
                                        adapter = new ProductAdapter(product_with_specific_category, getActivity(), HomeActivity.storeCat, mob, HomeActivity.itemCount, String.valueOf(holder.category_name.getText()));

                                        if (HomeActivity.storeCat.equals("Food")) {
                                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                            recyclerView.setLayoutManager(layoutManager);
                                        } else if (HomeActivity.storeCat.equals("Grocery")) {
                                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                                            recyclerView.setLayoutManager(layoutManager);
                                        }

                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setHasFixedSize(true);

                                    }
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<List<ProductInfo>> call, Throwable t) {
                            Log.i("TAG", "R" + t.toString());
                            pbProducts.setVisibility(View.GONE);
                        }
                    });
                }
            });

        }


        @Override
        public int getItemCount() {
            return categories.size();
        }


        class CategoryViewHolder extends RecyclerView.ViewHolder {

            Context context;
            Button category_name;

            public CategoryViewHolder(View itemView, Context context) {
                super(itemView);
                this.context = context;
                category_name = (Button) itemView.findViewById(R.id.categories_name);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final String token = getActivity().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");
        Log.d("token",token);

        Call<List<CartItem>> callItems = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class)
                .getUserCartItems(token);
        callItems.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                List<CartItem> items = response.body();


                Log.d("items", String.valueOf(items));

                if (items != null && items.size() > 0) {
                    //Accesss views?
                    Log.d("itemcount", String.valueOf(items.size()));
                    HomeActivity.cartItems.setVisibility(View.VISIBLE);
                    HomeActivity.itemCount.setText(String.valueOf(items.size()));

                }
                else
                {
                }
            }
            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {

            }
        });
    }


}