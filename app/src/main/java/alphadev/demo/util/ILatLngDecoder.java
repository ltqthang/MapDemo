package alphadev.demo.util;

import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public interface ILatLngDecoder {
    Observable<String> decode(LatLng latLng);
}
