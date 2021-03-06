package cloud.thecode.finalproject;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button get_location;
    private ListView places_list_view;
    private String url =  "http://thecode.cloud/maps/get.php";
    private String longitude = "32.4657788", latitude = "12.8479087";
    private ArrayAdapter<Place> arrayAdapter;
    private DatabaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        get_location = (Button) findViewById(R.id.get_location);
        places_list_view = (ListView) findViewById(R.id.places_list_view);
        dbh = new DatabaseHandler(getApplicationContext());

        arrayAdapter =
                new ArrayAdapter<Place>(getApplicationContext(), android.R.layout.simple_list_item_1, dbh.getVisitedPlaces());

        places_list_view.setAdapter(arrayAdapter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

                JsonArrayRequest jr = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for(int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject jsonObject = response.getJSONObject(i);
                                longitude = jsonObject.getString("longitude");
                                latitude  = jsonObject.getString("latitude");

                                mMap.clear();

                                LatLng somwhere = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                mMap.addMarker(new MarkerOptions().position(somwhere).title("Marker Somewhere"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(somwhere));

                                dbh.insertPlace(new Place(longitude, latitude));

                                /*
                                arrayAdapter =
                                        new ArrayAdapter<Place>(getApplicationContext(), android.R.layout.simple_list_item_1, dbh.getVisitedPlaces());
                                arrayAdapter.notifyDataSetChanged();

                                */
                                Toast.makeText(MapsActivity.this, "You are at " + longitude + ", " + latitude, Toast.LENGTH_SHORT).show();


                            } catch (Exception ex) {
                                Toast.makeText(MapsActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }

                        //someArrayAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                rq.getCache().remove(url);
                rq.getCache().clear();
                rq.add(jr);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
