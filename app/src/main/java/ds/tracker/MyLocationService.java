package ds.tracker;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "ds.tracker.UPDATE_LOCATION";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (mAuth.getCurrentUser() != null) {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_PROCESS_UPDATE.equals(action)) {
                    LocationResult result = LocationResult.extractResult(intent);
                    if (result != null) {
                        Location location = result.getLastLocation();
                        Map<String, Object> LatLog = new HashMap<>();
                        LatLog.put("Lat", location.getLatitude());
                        LatLog.put("Log", location.getLongitude());
                        try {
                            db.collection("Location").document(mAuth.getCurrentUser().getEmail()).set(LatLog);
                        } catch (Exception e) {
                            location.reset();
                        }
                    }
                }
            }
        }
    }
}
