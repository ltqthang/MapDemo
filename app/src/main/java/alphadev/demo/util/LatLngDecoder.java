package alphadev.demo.util;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class LatLngDecoder implements ILatLngDecoder {

    private Geocoder geocoder;

    public LatLngDecoder(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    @Override
    public Observable<String> decode(final LatLng latLng) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StringBuilder addressLine = new StringBuilder();
                int index = addresses.get(0).getMaxAddressLineIndex();
                for (int i = 0; i <= index; i++) {
                    if (i != 0) addressLine.append(", ");
                    addressLine.append(addresses.get(0).getAddressLine(i));
                }
                subscriber.onNext(new String(addressLine));
            }
        });
    }
}
