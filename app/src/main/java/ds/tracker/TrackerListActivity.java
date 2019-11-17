package ds.tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class TrackerListActivity extends AppCompatActivity {

    ListView lv_loc;
    List<String> lst_loc = new ArrayList<>();
    ArrayAdapter<String> lst_adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_list);

        lv_loc = findViewById(R.id.list_loc);
        //view Document name
        db.collection("Location").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                lst_loc.clear();
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String data = snapshot.getReference().getId().replace("@gmail.com", "").toUpperCase();
                        lst_loc.add(data);
                    }
                }
                lst_adapter = new ArrayAdapter<>(TrackerListActivity.this, R.layout.txt_listview, R.id.txt_loc, lst_loc);
                lv_loc.setAdapter(lst_adapter);
                lst_adapter.notifyDataSetChanged();
            }
        });

        //onItem click
        lv_loc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                db.collection("Location").document(lv_loc.getItemAtPosition(position).toString()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Intent i = new Intent(TrackerListActivity.this, MapsActivity.class);
                                i.putExtra("loc", lv_loc.getItemAtPosition(position).toString());
                                startActivity(i);
                                finish();
                                Toast.makeText(TrackerListActivity.this, "" + lv_loc.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        lv_loc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TrackerListActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Do you want to delete?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Location")
                                        .document(lv_loc.getItemAtPosition(position).toString().toLowerCase() + "@gmail.com").delete();
                                lst_adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();

                return true;
            }
        });
    }
}
