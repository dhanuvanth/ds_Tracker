package ds.tracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;
    private Button b_login;
    private TextView tv_signUp;
    private ProgressBar progressBar;

    private String email, password;
    private boolean connected;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private long currentTime, blockTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.email_et);
        et_password = findViewById(R.id.password_et);
        b_login = findViewById(R.id.btn_login);
        tv_signUp = findViewById(R.id.tv_sign_up);
        progressBar = findViewById(R.id.progress_login);

        //blockPeriod();

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString().trim() + "@gmail.com";
                password = et_password.getText().toString();
                //progressBar
                progressBar.setVisibility(View.VISIBLE);
                //check if email exist
                //check not email field is empty
                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;

                    if (!email.equals("")) {
                        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(MainActivity.this, new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                try {
                                    boolean check = !task.getResult().getProviders().isEmpty();

                                    if (!check) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(MainActivity.this, "Email does not Exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //sign in
                                        //check not email and password is empty
                                        if (!email.equals("") && !password.equals("")) {
                                            mAuth.signInWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            if (task.isSuccessful()) {
                                                                Intent i = new Intent(MainActivity.this, FusedLocationActivity.class);
                                                                i.putExtra("emails", email);
                                                                startActivity(i);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(MainActivity.this, "Incorrect", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(MainActivity.this, "Enter All Fields", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "Check you Email Address!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Enter the Email Address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(v, "No Internet Connection...", Snackbar.LENGTH_LONG).show();
                    connected = false;
                }
            }
        });

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                email = et_email.getText().toString().trim() + "@gmail.com";
                password = et_password.getText().toString();
                //check if email exist
                //check not email field is empty
                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    mAuth.fetchProvidersForEmail(email).addOnCompleteListener(MainActivity.this, new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            try {
                                boolean check = !task.getResult().getProviders().isEmpty();
                                if (!check) {
                                    mAuth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Intent i = new Intent(MainActivity.this, FusedLocationActivity.class);
                                                        i.putExtra("email", "" + email);
                                                        startActivity(i);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Check your Email Address", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(v, "No Internet Connection...", Snackbar.LENGTH_LONG).show();
                    connected = false;
                }
            }
        });

    }

    /*private void blockPeriod() {
        currentTime = System.currentTimeMillis();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        blockTime = cal.getTimeInMillis();

        if (currentTime >= blockTime) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert")
                    .setMessage("Contact the Administrator for more info")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setCancelable(false).create().show();
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (currentTime >= blockTime) {
               // blockPeriod();
            } else {
                // User is signed in
                Intent i = new Intent(MainActivity.this, FusedLocationActivity.class);
                i.putExtra("email", "" + email);
                startActivity(i);
                finish();
            }
        } else {
            // No user is signed in
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.track:
                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    @SuppressLint("InflateParams") final View inflater = getLayoutInflater().inflate(R.layout.login_dialog, null);
                    builder.setView(inflater).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText et_password = inflater.findViewById(R.id.password);
                            String e_password = et_password.getText().toString();
                            if (e_password.equals("142536")) {
                                Intent i = new Intent(getApplicationContext(), TrackerListActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setCancelable(true).create().show();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
                    connected = false;
                }
                break;
            case R.id.about:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Developed By")
                        .setMessage("S.Dhanuvanth\ndhanuvanth@gmail.com\n+91 7010 384 896")
                        .create().show();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit")
                .setMessage("Do you want to Exit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
