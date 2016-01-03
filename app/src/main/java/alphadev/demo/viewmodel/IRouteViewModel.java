package alphadev.demo.viewmodel;

import android.databinding.ObservableField;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import rx.Observable;

public interface IRouteViewModel {
    Observable<String> setStart(LatLng latLng);
    Observable<String> setEnd(LatLng latLng);
    LatLng getStart();
    LatLng getEnd();
    ObservableField<String> getDisplayName();
    MarkerOptions getStartMarkerOptions();
    MarkerOptions getEndMarkerOptions();
    void showStart();
    void showEnd();
    boolean isShowingStart();
}
