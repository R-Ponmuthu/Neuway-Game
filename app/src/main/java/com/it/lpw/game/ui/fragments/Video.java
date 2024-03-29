package com.it.lpw.game.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.it.lpw.game.Responsemodel.VideoResponse;
import com.it.lpw.game.adapters.VideoAdapter;
import com.it.lpw.game.restApi.ApiClient;
import com.it.lpw.game.restApi.ApiInterface;
import com.it.lpw.game.ui.activity.MainActivity;
import com.it.lpw.game.util.Constant_Api;
import com.it.lpw.game.R;
import com.it.lpw.game.util.Method;
import com.it.lpw.game.databinding.FragmentVideoBinding;
import com.it.lpw.game.util.Session;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Video extends Fragment implements MaxAdViewAdListener, MaxAdRevenueListener {
    FragmentVideoBinding binding;
    private MaxAdView adView;
    Activity activity;
    Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(getLayoutInflater());
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        session=new Session(getActivity());
        activity=getActivity();
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        binding.toolbar.setText(Constant_Api.TITLE);
//        load_bannerads();

        binding.back.setOnClickListener(v -> {
            load_fragment(new FragmentMain());
        });

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                load_fragment(new FragmentMain());
                return true;
            }
            return false;
        });

        if (Method.isConnected(getActivity())){
            getdata();
        }else {
            Method.Error(getActivity(),getString(R.string.no_internet));
        }

        return binding.getRoot();
    }

    private void load_fragment(Fragment fragment){
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.commit();
    }

    private void getdata(){
        Call<VideoResponse>call= ApiClient.getClient(getActivity()).create(ApiInterface.class).getVideo();
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                if(response.isSuccessful() && response.body().getData()!=null){
                    VideoAdapter adapter = new VideoAdapter(getActivity(),response.body().getData());
                    binding.recyclerview.setAdapter(adapter);
                    binding.recyclerview.setVisibility(View.VISIBLE);
                }
                else {
                    binding.noResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getdata();
    }


    private void load_bannerads() {
        if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_STARTAPP)){
            Method.STARTAPP_Banner(getActivity(),binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_APPLOVIN)){
            adView = new MaxAdView(Constant_Api.BANNER_ID,getActivity());
            adView.setListener( this );
            adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.banner_height ) ) );
            binding.layoutBanner.BANNER.addView( adView );
            adView.loadAd();
        }
    }

    @Override
    public void onAdExpanded(MaxAd ad) {}
    @Override
    public void onAdCollapsed(MaxAd ad) {}
    @Override
    public void onAdLoaded(MaxAd ad) {}
    @Override
    public void onAdDisplayed(MaxAd ad) {}
    @Override
    public void onAdHidden(MaxAd ad) {}
    @Override
    public void onAdClicked(MaxAd ad) {}
    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {}
    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {}
    @Override
    public void onAdRevenuePaid(MaxAd ad) {}
}