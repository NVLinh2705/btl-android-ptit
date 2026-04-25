package com.btl_ptit.hotelbooking.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.remote.MockApiClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;
import com.btl_ptit.hotelbooking.databinding.ActivitySearchBinding;
import com.btl_ptit.hotelbooking.databinding.HotelItemBinding;
import com.btl_ptit.hotelbooking.databinding.PopularDestinationItemBinding;
import com.btl_ptit.hotelbooking.listener.OnHotelClickListener;
import com.btl_ptit.hotelbooking.utils.Constants;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.btl_ptit.hotelbooking.view.adapter.HotelAdapter;
import com.btl_ptit.hotelbooking.view.fragment.FilterBottomSheet;
import com.btl_ptit.hotelbooking.view_model.OccupancyViewModel;
import com.btl_ptit.hotelbooking.view_model.factory.OccupancyViewModelFactory;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModelFactory;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivityTAG";
    private ActivitySearchBinding mActivitySearchBinding;
    private Context mContext;
    private MyHotelRepository mMyHotelRepository;
    private HotelRestService mHotelRestService;
    private HotelViewModel mHotelViewModel;
    private OccupancyViewModel mOccupancyViewModel;
    private HotelAdapter mHotelAdapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String checkin;
    private String checkout;
    private String province;
    private String district;
    private int numRoom;
    private int numAdult;
    private int numChildren;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySearchBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mActivitySearchBinding.getRoot());

        mContext = this;
        initListeners();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            province = extras.getString(Constants.KEY_PROVINCE);
            district = extras.getString(Constants.KEY_DISTRICT);
            Log.d(TAG, "province: " + province);
            Log.d(TAG, "district: " + district);
            checkin = extras.getString(Constants.KEY_CHECKIN);
            checkout = extras.getString(Constants.KEY_CHECKOUT);
            Log.d(TAG, "checkin: " + checkin);
            Log.d(TAG, "checkout: " + checkout);
            numRoom = extras.getInt(Constants.KEY_NUM_ROOM, 1); // mặc định 1
            Log.d(TAG, "numRoom: " + numRoom);
            numAdult = extras.getInt(Constants.KEY_NUM_ADULT, 1);
            Log.d(TAG, "numAdult: " + numAdult);
            numChildren = extras.getInt(Constants.KEY_NUM_CHILDREN, 0);
            Log.d(TAG, "numRoom: " + numRoom);


            // Sau đó truyền các biến này vào ViewModel để gọi Paging
            // searchViewModel.initData(province, district, checkin, checkout, numRoom, numAdult, numChildren);
            initSearchByNumPersonHotels();
        } else {
            initRecommendedHotels();
        }
    }

    private void initListeners() {
        mActivitySearchBinding.btnBack.setOnClickListener(view -> {
            finish();
        });
        mActivitySearchBinding.btnInnerFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterBottomSheet filterSheet = new FilterBottomSheet();
                filterSheet.show(getSupportFragmentManager(), "Filter");
            }
        });
    }

    private void initRecommendedHotels() {
        initHotelAdapter();
        if (mHotelAdapter != null) {
            mHotelRestService = MockApiClient.createService(HotelRestService.class);
            mMyHotelRepository = new MyHotelRepository(mHotelRestService);
            mHotelViewModel = new ViewModelProvider(this, new HotelViewModelFactory(mMyHotelRepository, false)).get(HotelViewModel.class);

            compositeDisposable.add(
                    mHotelViewModel.pagingDataFlow
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(pagingData -> mHotelAdapter.submitData(getLifecycle(), pagingData))
            );
        }
    }

    private void initSearchByNumPersonHotels() {
        initHotelAdapter();
        if (mHotelAdapter != null) {
            mHotelRestService = MockApiClient.createService(HotelRestService.class);
            mMyHotelRepository = new MyHotelRepository(mHotelRestService);
            mOccupancyViewModel = new ViewModelProvider(this, new OccupancyViewModelFactory(mMyHotelRepository)).get(OccupancyViewModel.class);
            mOccupancyViewModel.setCheckInDate(checkin);
            mOccupancyViewModel.setCheckOutDate(checkout);
            mOccupancyViewModel.setDoubleBed(numAdult);
            mOccupancyViewModel.setSingleBed(numChildren);
            mOccupancyViewModel.setRooms(numRoom);
            mOccupancyViewModel.setProvinceName(province);
            mOccupancyViewModel.setDistrictName(district);
            mOccupancyViewModel.initPaging();

            compositeDisposable.add(
                mOccupancyViewModel.pagingDataFlow
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pagingData -> mHotelAdapter.submitData(getLifecycle(), pagingData))
            );
        }
    }

    private void initHotelAdapter() {
        if (mHotelAdapter == null) {
            mHotelAdapter = new HotelAdapter(new MyComparator<MyHotel>(), mContext, new OnHotelClickListener() {
                @Override
                public void onHotelClick(MyHotel myHotel) {
                    Intent intent = new Intent(mContext, HotelInfoActivity.class);
                    int hotelId = myHotel.getId();
                    intent.putExtra(Constants.HOTEL_ID, hotelId);
                    Log.d(TAG, "hotel ID: " + hotelId);
                    startActivity(intent);
                }
            });
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mContext,
                LinearLayoutManager.VERTICAL,
                false
        );

        if (mActivitySearchBinding.rcvHotels.getLayoutManager() == null) {
            mActivitySearchBinding.rcvHotels.setLayoutManager(layoutManager);
            mActivitySearchBinding.rcvHotels.setHasFixedSize(false);
            mActivitySearchBinding.rcvHotels.setAdapter(mHotelAdapter);
            mActivitySearchBinding.rcvHotels.setNestedScrollingEnabled(false);
        }

        if (mHotelAdapter.getItemCount() == 0 && mActivitySearchBinding.shimmerContainer.getChildCount() == 0) {
            setupShimmerPlaceholder();
        }

        mHotelAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading || loadState.getRefresh() instanceof LoadState.Error) {
                mActivitySearchBinding.shimmerContainer.setVisibility(View.VISIBLE);
                mActivitySearchBinding.rcvHotels.setVisibility(View.GONE);
                Log.d(TAG, "mHotelAdapter loading!");
            } else {
//                Log.d(TAG, mHotelAdapter.getItemCount() + " mHotelAdapter ");
                mActivitySearchBinding.rcvHotels.setVisibility(View.VISIBLE);
                mActivitySearchBinding.shimmerContainer.setVisibility(View.GONE);
                Log.d(TAG, "mHotelAdapter not load!");
            }
            return null;
        });
    }

    private void setupShimmerPlaceholder() { // set shimmer place holder với số lượng cố định
        int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;

        float density = mContext.getResources().getDisplayMetrics().density;
        int itemHeightPx = (int) (120 * density);

        int count = (int) Math.ceil((float) screenHeight / itemHeightPx) + 1;

        // Giới hạn tối thiểu 1 item
        count = Math.max(1, count);

        mActivitySearchBinding.shimmerContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            // Inflate item
            HotelItemBinding shimmerBinding = HotelItemBinding.inflate(getLayoutInflater(), mActivitySearchBinding.shimmerContainer, false);

            if (shimmerBinding.shimmerLayout != null) {
                shimmerBinding.shimmerLayout.startShimmer();
            }

            mActivitySearchBinding.shimmerContainer.addView(shimmerBinding.getRoot());
        }
        Log.d(TAG, "setupShimmerPlaceholder! " + count);
    }
}