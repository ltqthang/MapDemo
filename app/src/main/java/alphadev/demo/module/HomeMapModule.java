package alphadev.demo.module;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;

import alphadev.demo.controller.IMapMarkerController;
import alphadev.demo.controller.IRouteController;
import alphadev.demo.controller.MapMarkerController;
import alphadev.demo.controller.RouteController;
import alphadev.demo.fragment.HomeMapFragment;
import alphadev.demo.util.ILatLngDecoder;
import alphadev.demo.util.LatLngDecoder;
import alphadev.demo.viewmodel.IRouteViewModel;
import alphadev.demo.viewmodel.RouteViewModel;
import dagger.Module;
import dagger.Provides;
import rx.subjects.BehaviorSubject;

@Module
public class HomeMapModule {

    private Context context;

    public HomeMapModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    public BehaviorSubject<Location> provideBehaviorSubject() {
        return BehaviorSubject.create();
    }

    @Provides
    public BehaviorSubject<HomeMapFragment> provideHomeMapFragmentBehaviorSubject() {
        return BehaviorSubject.create();
    }

    @Provides
    public IMapMarkerController provideIMapMarkerController() {
        return new MapMarkerController();
    }

    @Provides
    public ILatLngDecoder provideILatLngDecoder(Context context) {
        return new LatLngDecoder(new Geocoder(context));
    }

    @Provides
    public IRouteViewModel provideIRouteViewModel(Context context, ILatLngDecoder latLngDecoder) {
        return new RouteViewModel(context, latLngDecoder);
    }

    @Provides
    public IRouteController provideIRouteController(Context context) {
        return new RouteController(context);
    }
}
