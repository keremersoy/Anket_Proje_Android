package com.example.anket_proje;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ReturnFragment extends Fragment {

    private TextView tvReturn, tvKey;

    private Button btnClose;

    private String userID, counter;

    private FirebaseFirestore db;

    private DocumentReference dRef;

    private HashMap<String, Object> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_return, container, false);

        tvReturn = (TextView) view.findViewById(R.id.tvSonuc);
        tvKey = (TextView) view.findViewById(R.id.tvNe);
        btnClose = (Button) view.findViewById(R.id.btnKapat);

        Bundle arg = getArguments();
        tvReturn.setText(arg.getString("return"));
        tvKey.setText(arg.getString("key"));

        Bundle user = getActivity().getIntent().getExtras();
        userID = user.getString("userID");

        db = FirebaseFirestore.getInstance();
        dRef = db.collection("users").document(userID);

        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists())
                    counter = documentSnapshot.getString("counter");
                data = new HashMap<>();
                data.put("counter", setCounter());
                dRef.update(data);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Hata!", Toast.LENGTH_SHORT).show();
                    }
                });



        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment fragment = new HomeFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.home_frameLayout, fragment);
                transaction.commit();
                Toast.makeText(getActivity(), "Test tamamlandı.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private String setCounter() {
        int x = Integer.valueOf(counter);
        return String.valueOf(x + 1);
    }
}