package alphadev.demo.controller;

import android.content.Context;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import alphadev.demo.R;
import alphadev.demo.viewmodel.IRouteViewModel;

public class RouteController implements IRouteController {
    private Context context;
    private List<Polyline> polylines = new ArrayList<>();
    private int[] colors = new int[] {R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent, R.color.primary_dark_material_light, R.color.green};

    public RouteController(Context context) {
        this.context = context;
    }

    @Override
    public void showRoute(final GoogleMap map, final IRouteViewModel viewModel) {
        if (viewModel.getStart() == null || viewModel.getEnd() == null) {
            Toast.makeText(context, R.string.please_select_location, Toast.LENGTH_SHORT).show();
            return;
        }
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure() {
                        Toast.makeText(context, R.string.routing_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                        adjustMap(map, viewModel.getStart(), viewModel.getEnd());
                        showResult(map, arrayList);
                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                })
                .waypoints(viewModel.getStart(), viewModel.getEnd())
                .build();
        routing.execute();
    }

    @Override
    public void clearRoute() {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
    }

    private void adjustMap(GoogleMap map, LatLng arrival, LatLng departure) {
        LatLngBounds.Builder builder = LatLngBounds.builder().include(arrival).include(departure);
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.spacing_huge);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
    }

    private void showResult(GoogleMap map, ArrayList<Route> routes) {
        clearRoute();

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < routes.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % colors.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(context.getResources().getColor(colors[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(routes.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }
}
