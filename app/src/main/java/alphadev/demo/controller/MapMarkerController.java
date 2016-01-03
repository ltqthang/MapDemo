package alphadev.demo.controller;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarkerController implements IMapMarkerController {
    private GoogleMap googleMap;
    private Marker oldStartMarker;
    private Marker oldEndMarker;

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void showStartMarker(MarkerOptions markerOptions) {
        if (googleMap == null) return;
        if (oldStartMarker != null) {
            oldStartMarker.remove();
        }
        oldStartMarker = googleMap.addMarker(markerOptions);
    }

    @Override
    public void showEndMarker(MarkerOptions markerOptions) {
        if (googleMap == null) return;
        if (oldEndMarker != null) {
            oldEndMarker.remove();
        }
        oldEndMarker = googleMap.addMarker(markerOptions);
    }
}
