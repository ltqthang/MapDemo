package alphadev.demo.viewmodel;

import android.databinding.ObservableField;
import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import alphadev.demo.R;
import alphadev.demo.util.ILatLngDecoder;
import rx.Observable;
import rx.Subscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class RouteViewModelTest extends AndroidTestCase {
    @Mock ILatLngDecoder latLngDecoder;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    public void testGetDisplayName_0() {
        RouteViewModel routeViewModel = new RouteViewModel(getContext(), latLngDecoder);

        ObservableField<String> actual = routeViewModel.getDisplayName();

        assertThat(actual.get()).isEqualTo(getContext().getString(R.string.pick_start));
    }

    public void testGetDisplayName_1() {
        RouteViewModel routeViewModel = new RouteViewModel(getContext(), latLngDecoder);

        routeViewModel.showEnd();
        ObservableField<String> actual = routeViewModel.getDisplayName();

        assertThat(actual.get()).isEqualTo(getContext().getString(R.string.pick_end));
    }

    public void testGetDisplayName_2() {
        Observable<String> value = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Ho Chi Minh, Viet Nam");
            }
        });
        when(latLngDecoder.decode(any(LatLng.class))).thenReturn(value);
        RouteViewModel routeViewModel = new RouteViewModel(getContext(), latLngDecoder);

        routeViewModel.setStart(new LatLng(123.45, 56.89)).toBlocking().first();
        ObservableField<String> actual = routeViewModel.getDisplayName();

        assertThat(actual.get()).isEqualTo("Ho Chi Minh, Viet Nam");
    }

    public void testGetDisplayName_3() {
        Observable<String> startObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Ho Chi Minh, Viet Nam");
            }
        });

        Observable<String> endObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Ha Noi, Viet Nam");
            }
        });

        LatLng start = new LatLng(123.45, 56.89);
        LatLng end = new LatLng(123.45, 134.1234);
        when(latLngDecoder.decode(start)).thenReturn(startObservable);
        when(latLngDecoder.decode(end)).thenReturn(endObservable);
        RouteViewModel routeViewModel = new RouteViewModel(getContext(), latLngDecoder);

        routeViewModel.setStart(start).toBlocking().first();
        ObservableField<String> actual = routeViewModel.getDisplayName();

        assertThat(actual.get()).isEqualTo("Ho Chi Minh, Viet Nam");

        routeViewModel.setEnd(end).toBlocking().first();

        assertThat(actual.get()).isEqualTo("Ha Noi, Viet Nam");
    }
}