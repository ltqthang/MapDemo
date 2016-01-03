package alphadev.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class BaseMapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private final PublishSubject<GoogleMap> onMapReadies = PublishSubject.create();
    private final PublishSubject<Fragment> onDestroyViews = PublishSubject.create();
    private final PublishSubject<Fragment> onDestroys = PublishSubject.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMapAsync(this);
    }

    @Override
    public void onDestroyView() {
        onDestroyViews.onNext(this);
        super.onDestroyView();
        onMapReadies.onNext(null);
    }

    @Override
    public void onDestroy() {
        onDestroys.onNext(this);
        super.onDestroy();
    }

    public Observable<GoogleMap> onMapReadies() {
        return onMapReadies
                .filter(new Func1<GoogleMap, Boolean>() {
                    @Override
                    public Boolean call(GoogleMap googleMap) {
                        return googleMap != null;
                    }
                });
    }

    public final Observable<Fragment> onDestroyViews() {
        return onDestroyViews;
    }

    public final Observable<Fragment> onDestroys() {
        return onDestroys;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        onMapReadies.onNext(googleMap);
    }
}