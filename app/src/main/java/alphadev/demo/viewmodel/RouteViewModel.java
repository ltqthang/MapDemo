package alphadev.demo.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import alphadev.demo.R;
import alphadev.demo.util.ILatLngDecoder;
import rx.Observable;
import rx.functions.Action1;

public class RouteViewModel implements IRouteViewModel {
    private final ILatLngDecoder latLngDecoder;
    private Context context;
    private LatLng start;
    private LatLng end;
    private ObservableField<String> displayName = new ObservableField<>();
    private String startAddress;
    private String endAddress;
    private boolean showingStart;

    public RouteViewModel(Context context, ILatLngDecoder latLngDecoder) {
        this.context = context;
        this.latLngDecoder = latLngDecoder;
        showStart();
    }

    @Override
    public Observable<String> setStart(LatLng start) {
        this.start = start;
        return latLngDecoder.decode(start)
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String address) {
                        startAddress = address;
                        displayName.set(address);
                    }
                });
    }

    @Override
    public Observable<String> setEnd(LatLng end) {
        this.end = end;
        return latLngDecoder.decode(end)
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String address) {
                        endAddress = address;
                        displayName.set(address);
                    }
                });
    }

    @Override
    public LatLng getStart() {
        return start;
    }

    @Override
    public LatLng getEnd() {
        return end;
    }

    public ObservableField<String> getDisplayName() {
        return displayName;
    }

    @Override
    public MarkerOptions getStartMarkerOptions() {
        return createMarkerOption(start, context.getString(R.string.start), BitmapDescriptorFactory.HUE_GREEN);
    }

    private MarkerOptions createMarkerOption(LatLng latLng, String title, float hue) {
        return new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(hue));
    }

    @Override
    public MarkerOptions getEndMarkerOptions() {
        return createMarkerOption(end, context.getString(R.string.end), BitmapDescriptorFactory.HUE_RED);
    }

    @Override
    public void showStart() {
        this.showingStart = true;
        if (start != null) {
            displayName.set(startAddress);
        } else {
            displayName.set(context.getString(R.string.pick_start));
        }
    }

    @Override
    public void showEnd() {
        this.showingStart = false;
        if (end != null) {
            displayName.set(endAddress);
        } else {
            displayName.set(context.getString(R.string.pick_end));
        }
    }

    @Override
    public boolean isShowingStart() {
        return showingStart;
    }
}
