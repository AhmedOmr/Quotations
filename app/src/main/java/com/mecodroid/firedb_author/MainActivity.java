package com.mecodroid.firedb_author;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String token = "615186953472-fd3qf63pim192gi27e4cr0l0o6ltp37j.apps.googleusercontent.com";
    int Rec_Sign_In = 0;
    GoogleSignInOptions moInOptions;
    GoogleSignInClient signInClient;
    FirebaseAuth mAuth;
    List<String> au;
    EditText email, password, confirmpass, username;
    FirebaseUser user;
    Button signem, cancelSi;
    private CheckInternet checkInternet;
    private Dialog nbuilder;
    private EditText texteml, textpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.emailsign);
        password = findViewById(R.id.passwordsign);
        confirmpass = findViewById(R.id.conpasswordsign);
        username = findViewById(R.id.userNa);
        checkInternet = new CheckInternet(this);

        mAuth = FirebaseAuth.getInstance();
        moInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail().build();
        signInClient = GoogleSignIn.getClient(getApplicationContext(), moInOptions);

        au = new ArrayList<>();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent n = new Intent(MainActivity.this, Home.class);
            n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(n);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Rec_Sign_In) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                signwithfirbase(account);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    private void signwithfirbase(final GoogleSignInAccount account) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String nameRef = currentUser.getUid();
                            SharedPreferences sh = getSharedPreferences("Referncename", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sh.edit();
                            editor1.putString("nam", nameRef);
                            editor1.commit();
                            updateUser(currentUser);
                            Intent n = new Intent(MainActivity.this, Home.class);
                            n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(n);

                            Toast.makeText(MainActivity.this, getResources().getString(R.string.wel) + " " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.loginfail), Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed To login" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(FirebaseUser User) {
        if (User != null) {
            mAuth.updateCurrentUser(User);
        } else {
            mAuth.signOut();
            signInClient.signOut();
        }
    }


    public void log_in(View view) {
        setDailogSign();
        nbuilder.show();
        signem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Remail = texteml.getText().toString().trim();
                String Rpass = textpass.getText().toString().trim();
                if (!TextUtils.isEmpty(Remail) && !TextUtils.isEmpty(Rpass)) {
                    mAuth.signInWithEmailAndPassword(Remail, Rpass)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user1 = mAuth.getCurrentUser();
                                    mAuth.updateCurrentUser(user1);
                                    String nameRef = user1.getUid();
                                    SharedPreferences sh = getSharedPreferences("Referncename", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sh.edit();
                                    editor1.putString("nam", nameRef);
                                    editor1.commit();
                                    updateUser(user1);

                                    Intent n = new Intent(MainActivity.this, Home.class);
                                    n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(n);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            email.setText("");
                            password.setText("");
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.wel) + " " + task.getResult().getUser().getDisplayName(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(MainActivity.this, "فشل تسجيل الدخول", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                nbuilder.dismiss();
            }
        });

        cancelSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nbuilder.dismiss();
            }
        });

    }

    public void sign_up(View view) {
        String Remail = email.getText().toString().trim();
        String Rpass = password.getText().toString().trim();
        String Rconpass = confirmpass.getText().toString().trim();
        final String usern = username.getText().toString().trim();
        if (!TextUtils.isEmpty(Remail) && !TextUtils.isEmpty(Rpass)
                && !TextUtils.isEmpty(Rconpass) && TextUtils.equals(Rpass, Rconpass)) {
            mAuth.createUserWithEmailAndPassword(Remail, Rpass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String nameRef = currentUser.getUid();
                            SharedPreferences sh = getSharedPreferences("Referncename", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sh.edit();
                            editor1.putString("nam", nameRef);
                            editor1.commit();

                            Intent n = new Intent(MainActivity.this, Home.class);
                            n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(n);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    email.setText("");
                                                    password.setText("");
                                                    confirmpass.setText("");
                                                }
                                            }
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user2 = task.getResult().getUser();
                        UserProfileChangeRequest userProfileChangeRequest =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(usern)
                                        .build();
                        user2.updateProfile(userProfileChangeRequest);
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.wel) + " " + usern, Toast.LENGTH_SHORT).show();

                    } else {
                        // if email already registerd
                        if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.email), Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(MainActivity.this, "فشل التسجيل ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.data), Toast.LENGTH_SHORT).show();
        }
    }

    public void setDailogSign() {
        nbuilder = new Dialog(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_sign, (ViewGroup) findViewById(R.id.cardviewzkr));
        texteml = v.findViewById(R.id.textemail);
        textpass = v.findViewById(R.id.textpassword);
        signem = v.findViewById(R.id.signInem);
        cancelSi = v.findViewById(R.id.cancelsign);
        nbuilder.setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nbuilder.setCancelable(false);
        nbuilder.show();
    }

    public void sign_WithGoogle(View view) {
        if (mAuth.getCurrentUser() == null) {
            Intent sign = signInClient.getSignInIntent();
            startActivityForResult(sign, Rec_Sign_In);
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.allogin) + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        }
    }
}
