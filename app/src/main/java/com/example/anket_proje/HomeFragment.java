package com.example.anket_proje;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class HomeFragment extends Fragment {

    private String userID;

    private TextView tvName,tvCounter;

    private FirebaseFirestore db;

    private DocumentReference dRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvCounter = (TextView) view.findViewById(R.id.tvSayac);
        tvName =(TextView) view.findViewById(R.id.tvIsim);

        Bundle arg=getActivity().getIntent().getExtras();
        userID=arg.getString("userID");

        db=FirebaseFirestore.getInstance();
        dRef=db.collection("users").document(userID);
        dRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tvCounter.setText(documentSnapshot.getString("counter"));
                        tvName.setText(documentSnapshot.getString("name"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Hata!", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }
}