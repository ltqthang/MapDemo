package alphadev.demo.controller;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

public interface IMapMarkerController {
    void showStartMarker(MarkerOptions markerOptions);
    void showEndMarker(MarkerOptions markerOptions);
    void setGoogleMap(GoogleMap googleMap);
}
