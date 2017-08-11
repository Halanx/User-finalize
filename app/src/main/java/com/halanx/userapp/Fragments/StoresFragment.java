package com.halanx.userapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.StoreInfo;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

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

                List<StoreInfo> grocery = new ArrayList<>();
                List<StoreInfo> food = new ArrayList<>();
                for (int i = 0; i < stores.size(); i++) {
                    if (stores.get(i).getStoreCategory().equals("Food")) {
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

            }
        });

        return v;
    }

    public class StoresAdapter extends RecyclerView.Adapter<StoresFragment.StoresAdapter.StoresViewHolder> {

        List<StoreInfo> storeList;

        public StoresAdapter(List<StoreInfo> storeList) {
            this.storeList = storeList;
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

//                getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).edit().
//                        putInt("storeID", storeList.get(pos).getId()).
//                        putString("storeLogo", storeList.get(pos).getStoreLogo()).
//                        putString("storeName", storeList.get(pos).getStoreName()).
//                        putInt("storePosition", pos).apply();

                HomeActivity.storeID = storeList.get(pos).getId();
                HomeActivity.storeLogo = storeList.get(pos).getStoreLogo();
                HomeActivity.storeName = storeList.get(pos).getStoreName();
                HomeActivity.storePosition = pos;
                HomeActivity.storeCat = storeList.get(pos).getStoreCategory();

                MainFragment fragment = new MainFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.commit();


                //  getActivity().getSharedPreferences("Store", Context.MODE_PRIVATE).edit().putBoolean("isMap", false).apply();
//                Log.d("position", String.valueOf(pos));


            }
        }
    }

}