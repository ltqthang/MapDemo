package alphadev.demo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import alphadev.demo.R;
import alphadev.demo.component.DaggerHomeMapComponent;
import alphadev.demo.controller.IMapMarkerController;
import alphadev.demo.controller.IRouteController;
import alphadev.demo.databinding.FragmentHomeMapBinding;
import alphadev.demo.module.HomeMapModule;
import alphadev.demo.util.ErrorHandler;
import alphadev.demo.viewmodel.IRouteViewModel;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMapLongClickListener {
    private static final String TAG = "HomeMapFragment";
    private static final int PICK_DEPARTURE_LOCATION_REQUEST_CODE = 1;
    private static final int PICK_ARRIVAL_LOCATION_REQUEST_CODE = 2;
    private static final float DEFAULT_ZOOM = 15;

    @Bind(R.id.tabLayout) TabLayout tabLayout;
    @Inject BehaviorSubject<Location> locationBehaviorSubject;
    @Inject BehaviorSubject<HomeMapFragment> onDestroys;
    @Inject IRouteViewModel viewModel;
    @Inject IRouteController routeController;
    @Inject IMapMarkerController mapMarkerController;

    GoogleApiClient googleApiClient;
    BaseMapFragment mapFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerHomeMapComponent.builder()
                .homeMapModule(new HomeMapModule(getContext()))
                .build()
                .inject(this);

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(HomeMapFragment.this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_map, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        FragmentHomeMapBinding binding = FragmentHomeMapBinding.bind(view);
        binding.setViewModel(viewModel);

        final TabLayout.Tab startTab = tabLayout.newTab().setText(R.string.start);
        TabLayout.Tab endTab = tabLayout.newTab().setText(R.string.end);
        tabLayout.addTab(startTab);
        tabLayout.addTab(endTab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == startTab) {
                    viewModel.showStart();
                } else {
                    viewModel.showEnd();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mapFragment = (BaseMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.onMapReadies()
                .takeUntil(onDestroys)
                .subscribe(new Action1<GoogleMap>() {
                    @Override
                    public void call(GoogleMap googleMap) {
                        mapMarkerController.setGoogleMap(googleMap);
                        setupMap(googleMap);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

        Observable
                .combineLatest(mapFragment.onMapReadies(), locationBehaviorSubject, new Func2<GoogleMap, Location, Void>() {
                    @Override
                    public Void call(GoogleMap googleMap, Location location) {
                        LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target(target)
                                .zoom(DEFAULT_ZOOM)
                                .build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        return null;
                    }
                })
                .subscribe();

    }

    private void setupMap(GoogleMap googleMap) {
        int paddingTop = getContext().getResources().getDimensionPixelOffset(R.dimen.route_view_height);
        int paddingBottom = getContext().getResources().getDimensionPixelOffset(R.dimen.go_button_height);
        googleMap.setPadding(0, paddingTop, 0, paddingBottom);
        googleMap.setOnMapLongClickListener(HomeMapFragment.this);
    }

    @OnClick(R.id.searchView)
    public void onClickDepartureView() {
        if (viewModel.isShowingStart()) {
            pickLocation(PICK_ARRIVAL_LOCATION_REQUEST_CODE);
        } else {
            pickLocation(PICK_DEPARTURE_LOCATION_REQUEST_CODE);
        }
    }

    @OnClick(R.id.goButton)
    public void getRoute() {
        routeController.showRoute(mapFragment.getMap(), viewModel);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        locationBehaviorSubject.onNext(lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (viewModel.isShowingStart()) {
            viewModel.setStart(latLng)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Actions.empty(), ErrorHandler.logException());
            mapMarkerController.showStartMarker(viewModel.getStartMarkerOptions());
        } else {
            viewModel.setEnd(latLng)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Actions.empty(), ErrorHandler.logException());
            mapMarkerController.showEndMarker(viewModel.getEndMarkerOptions());
        }
        routeController.clearRoute();
    }

    private void pickLocation(int pickLocationRequestCode) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent intent = builder.build(getActivity());
            startActivityForResult(intent, pickLocationRequestCode);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(getContext(), data);

            animateCamera(place.getLatLng());

            switch (requestCode) {
                case PICK_ARRIVAL_LOCATION_REQUEST_CODE:
                    viewModel.setStart(place.getLatLng())
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Actions.empty(), ErrorHandler.logException());
                    mapMarkerController.showStartMarker(viewModel.getStartMarkerOptions());
                    break;
                case PICK_DEPARTURE_LOCATION_REQUEST_CODE:
                    viewModel.setEnd(place.getLatLng())
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())cd
                            .subscribe(Actions.empty(), ErrorHandler.logException());
                    mapMarkerController.showStartMarker(viewModel.getEndMarkerOptions());
                    break;
            }
            routeController.clearRoute();

        }
    }

    private void animateCamera(LatLng latLng) {
        GoogleMap map = mapFragment.getMap();
        if (map != null) {
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(latLng)
                    .zoom(DEFAULT_ZOOM)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onDestroy() {
        onDestroys.onNext(this);
        super.onDestroy();
    }
}
