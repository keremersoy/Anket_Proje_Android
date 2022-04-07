package com.example.anket_proje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private String kAdi, parola;

    private TextView tvKayıt, tvUyari;

    private EditText txtK_Adi, txtParola;

    private Button btnGiris;

    private CheckBox cbGoster;

    private FirebaseFirestore db;

    @Override
    protected void onStop() {
        super.onStop();
        txtParola.setText("");
        txtK_Adi.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvKayıt = (TextView) this.findViewById(R.id.tvKayıt);
        tvUyari = (TextView) this.findViewById(R.id.tvUyariGiris);

        txtK_Adi = (EditText) this.findViewById(R.id.txtKullaniciAdi2);
        txtParola = (EditText) this.findViewById(R.id.txtParola2);

        btnGiris = (Button) this.findViewById(R.id.btnGiris);

        cbGoster = (CheckBox) this.findViewById(R.id.cbGGoster);

        cbGoster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                    txtParola.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    txtParola.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        tvKayıt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kayıt = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(kayıt);
            }
        });

        btnGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUyari.setText("");
                kAdi = txtK_Adi.getText().toString().trim().toLowerCase(Locale.ROOT);
                parola = txtParola.getText().toString().trim();

                if (!TextUtils.isEmpty(kAdi)) {
                    if (!TextUtils.isEmpty(parola)) {
                        db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        boolean c = true;
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot data : task.getResult()) {
                                                if (data.get("userName").toString().equals(kAdi)) {
                                                    c = false;
                                                    if (data.get("pass").toString().equals(parola)) {
                                                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                                                        home.putExtra("userID", data.getId());
                                                        startActivity(home);
                                                        finish();
                                                        break;
                                                    } else {
                                                        tvUyari.setText("Hatalı parola!");
                                                    }
                                                }
                                            }
                                            if (c) {
                                                tvUyari.setText("Böyle bir kullanıcı bulunamadı!");
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Hata!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        tvUyari.setText("Parola alanı boş!");
                    }
                } else {
                    tvUyari.setText("Kullanıcı adı alanı boş!");
                }
            }
        });
    }

}