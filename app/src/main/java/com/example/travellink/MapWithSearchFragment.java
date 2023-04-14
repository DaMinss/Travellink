

package com.example.travellink;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.protobuf.StringValue;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapWithSearchFragment extends DialogFragment implements OnMapReadyCallback {
    public interface MapWithSearchFragmentInterface {
        public void getLocationFromMap(String address);
    }

    private GoogleMap Map;
    SupportMapFragment supportMapFragment;
    androidx.appcompat.widget.SearchView searchView;
    Address currentAddress;
    AppCompatButton confirm;

    private int ACCESS_LOCATION_REQUEST_CODE = 1001;

    public MapWithSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_map_with_search, container, false);
        searchView = root.findViewById(R.id.searchLocation);
        confirm = root.findViewById(R.id.add_location);
        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.googleMap);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                if (location == null) {
                    Toast.makeText(getContext(), "Please enter your location name", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(location, 1);
                        if (addressList.size() > 0) {
                            LatLng search_location = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            currentAddress = addressList.get(0);
                            String currentQueryText = searchView.getQuery().toString();
                            // Set updated query text back to SearchView
                            String address = currentAddress.getThoroughfare();
                            String updatedQueryText =  currentAddress.getFeatureName() + " " ;
                            getLocation(search_location);
                            Map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    searchView.setQuery(updatedQueryText, false);
//                                    btn_add_location.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            });
                            confirm.setVisibility(View.VISIBLE);
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    MapWithSearchFragmentInterface itf = (MapWithSearchFragmentInterface) getActivity();
                                    itf.getLocationFromMap(updatedQueryText);
                                    dismiss();
                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "No location found! Please re-check and retry", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        supportMapFragment.getMapAsync(this);
        return root;
    }

    private void getLocation(LatLng latLng) {
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(currentAddress.getAddressLine(0).toString());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        Map.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Map = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == getActivity().getPackageManager().PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }

    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Map.setMyLocationEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == getContext().getPackageManager().PERMISSION_GRANTED) {
                enableUserLocation();
            } else {

                Toast.makeText(getContext(), "GPS Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
