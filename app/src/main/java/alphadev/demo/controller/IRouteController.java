package alphadev.demo.controller;

import com.google.android.gms.maps.GoogleMap;

import alphadev.demo.viewmodel.IRouteViewModel;

public interface IRouteController {
    void showRoute(GoogleMap map, IRouteViewModel viewModel);
    void clearRoute();
}
