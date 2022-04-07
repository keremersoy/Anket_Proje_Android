package com.example.anket_proje;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private String userName;

    private TextView tvGiris, tvHata, tvUyari;

    private EditText txtK_Adi, txtAdSoyad, txtParola, txtParola2;

    private CheckBox cbGoster, cbGoster2;

    private Button btnKayit;

    private FirebaseFirestore db;

    private Map<String, Object> user;

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!controlPass()) {
                tvHata.setText("Parolalar Uyuşmuyor!");
            } else
                tvHata.setText("");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //parola görünürlüğü değiştir
    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            EditText editText = txtParola;
            if (compoundButton.getId() == R.id.cbGoster2)
                editText = txtParola2;
            if (b == true)
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tvGiris = (TextView) this.findViewById(R.id.tvGiris);
        tvHata = (TextView) this.findViewById(R.id.tvHata);
        tvUyari = (TextView) this.findViewById(R.id.tvUyariKayit);
        tvUyari.setText("");

        btnKayit = (Button) this.findViewById(R.id.btnKayit);

        txtK_Adi = (EditText) this.findViewById(R.id.txtKullaniciAdi);
        txtAdSoyad = (EditText) this.findViewById(R.id.txtAdSoyad);
        txtParola = (EditText) this.findViewById(R.id.txtParola);
        txtParola2 = (EditText) this.findViewById(R.id.txtParolaTekrar);

        cbGoster = (CheckBox) this.findViewById(R.id.cbGoster);
        cbGoster2 = (CheckBox) this.findViewById(R.id.cbGoster2);

        txtParola.addTextChangedListener(tw);
        txtParola2.addTextChangedListener(tw);

        cbGoster.setOnCheckedChangeListener(checkedChangeListener);
        cbGoster2.setOnCheckedChangeListener(checkedChangeListener);

        tvGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent girisIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(girisIntent);
                finish();
            }
        });

        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = txtK_Adi.getText().toString().trim().toLowerCase(Locale.ROOT);
                tvUyari.setText("");

                if (!TextUtils.isEmpty(txtK_Adi.getText())) {
                    if (!TextUtils.isEmpty(txtAdSoyad.getText())) {
                        if (!TextUtils.isEmpty(txtParola.getText())) {
                            if (controlPass()) {
                                db = FirebaseFirestore.getInstance();
                                user = new HashMap<>();
                                user.put("name", txtAdSoyad.getText().toString().trim());
                                user.put("userName", userName);
                                user.put("pass", txtParola.getText().toString());
                                user.put("counter", "0");
                                db.collection("users")
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        boolean c = false;
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot data : task.getResult()) {
                                                if (data.getString("userName").equals(userName)) {
                                                    c = true;
                                                    break;
                                                }
                                            }
                                            if (c) {
                                                tvUyari.setText("Bu kullanıcı adı daha önce alınmış.");
                                            } else {
                                                db.collection("users")
                                                        .add(user)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Intent myIntent = new Intent(SignupActivity.this, LoginActivity.class);
                                                                Toast.makeText(SignupActivity.this, "Kayıt işlemi başarılı.", Toast.LENGTH_SHORT).show();
                                                                startActivity(myIntent);
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        new AlertDialog.Builder(SignupActivity.this)
                                                                .setTitle("Hata!")
                                                                .setMessage("Kullanıcı oluşturulamadı!")
                                                                .setPositiveButton("Tamam", null)
                                                                .show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });

                            } else {
                                tvUyari.setText("Parolalar eşleşmiyor!");
                            }
                        } else {
                            tvUyari.setText("Lütfen bir parola giriniz!");
                        }
                    } else {
                        tvUyari.setText("İsim adı alanı boş!");
                    }
                } else {
                    tvUyari.setText("Kullanıcı adı alanı boş!");
                }
            }
        });
    }

    private boolean controlPass() {
        if (txtParola.getText().toString().equals(txtParola2.getText().toString())) {
            return true;
        }
        return false;
    }
}