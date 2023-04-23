

package com.example.travellink;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
    public interface OnLocationSelectedListener {
        void onLocationSelected(String data);
    }

    private OnLocationSelectedListener mListener;
    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        mListener = listener;
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
        Bundle bundle = getArguments();
        int status = bundle.getInt("status");
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
                            String addressString = currentAddress.getAddressLine(0).toString();
                            String[] addressArray = addressString.split(",");
                            String streetAddress = addressArray[0].trim();
                            getLocation(search_location);
                            Map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    searchView.setQuery(streetAddress, false);
                                    confirm.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            });
                            confirm.setVisibility(View.VISIBLE);
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(status == 1) {
                                        mListener.onLocationSelected(streetAddress);
                                    }else {
                                        MapWithSearchFragment.MapWithSearchFragmentInterface itf = (MapWithSearchFragment.MapWithSearchFragmentInterface) getActivity();
                                        itf.getLocationFromMap(streetAddress);
                                    }
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
        getLocationPermission();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener( getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
                        currentAddress = addresses.get(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses != null && addresses.size() > 0) {
                        String addressString = currentAddress.getAddressLine(0).toString();
                        String[] addressArray = addressString.split(",");
                        String streetAddress = addressArray[0].trim();
                        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(currentLocation);
                        markerOptions.title(currentAddress.getAddressLine(0).toString());
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        Map.addMarker(markerOptions);
                        Map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                searchView.setQuery(streetAddress, false);
//                                    btn_add_location.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });
                    }

                }
            }
        });

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationPermission();
            }
        }
    }
}
