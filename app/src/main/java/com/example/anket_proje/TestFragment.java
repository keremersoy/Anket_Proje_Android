package com.example.anket_proje;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends Fragment {

    private String key;

    private int num;

    private TextView tvTitle, tvQuestion;

    private Button btnPrev, btnNext;

    private RadioGroup rgAnswer;

    private FirebaseFirestore db;

    private List<String> questions;

    private List<Integer> answer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        questions = new ArrayList<>();
        answer = new ArrayList<>();

        num = 0;

        Bundle arg = getArguments();
        key = arg.getString("key");

        tvTitle = (TextView) view.findViewById(R.id.tvBaslik);
        tvQuestion = (TextView) view.findViewById(R.id.tvSoru);

        btnPrev = (Button) view.findViewById(R.id.btnGeri);
        btnNext = (Button) view.findViewById(R.id.btnIleri);

        rgAnswer = (RadioGroup) view.findViewById(R.id.radioGroup);
        rgAnswer.clearCheck();

        tvTitle.setText("Ne Kadar " + key + "sin?");
        btnPrev.setVisibility(View.INVISIBLE);

        db = FirebaseFirestore.getInstance();

        db.collection(key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot data : task.getResult()) {
                        questions.add(data.getString("question"));
                    }
                    tvQuestion.setText(questions.get(num));
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (rgAnswer.getCheckedRadioButtonId()) {
                    case R.id.radioButton:
                        addAnswer(5);
                        break;
                    case R.id.radioButton2:
                        addAnswer(0);
                        break;
                    case R.id.radioButton3:
                        addAnswer(2);
                        break;
                    default:
                        Toast.makeText(getActivity(),
                                "Lütfen bir şık seçiniz!",
                                Toast.LENGTH_SHORT).show();
                        return;
                }

                if (num == 0)
                    btnPrev.setVisibility(View.VISIBLE);

                ++num;

                if (num == questions.size() - 1) {
                    btnNext.setText("Testi Bitir");
                }

                if (questions.size() == num) {
                    testFinish();
                } else {
                    tvQuestion.setText(questions.get(num));

                    if (answer.size() > num)
                        getAnswer();
                    else
                        rgAnswer.clearCheck();
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num + 1 == questions.size())
                    btnNext.setText("İleri");

                --num;
                if (num == 0) {
                    btnPrev.setVisibility(View.INVISIBLE);
                }
                tvQuestion.setText(questions.get(num));
                getAnswer();
            }
        });
        return view;
    }

    private void getAnswer() {
        switch (answer.get(num)) {
            case 5:
                rgAnswer.check(R.id.radioButton);
                break;
            case 0:
                rgAnswer.check(R.id.radioButton2);
                break;
            case 2:
                rgAnswer.check(R.id.radioButton3);
                break;
        }
    }

    private void addAnswer(int x) {
        if (answer.size() == num)
            answer.add(num, x);
        else
            answer.set(num, x);
    }

    private void testFinish() {
        ReturnFragment fragment=new ReturnFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bnd = new Bundle();
        bnd.putString("return", getReturn());
        bnd.putString("key", key);
        fragment.setArguments(bnd);
        transaction.replace(R.id.home_frameLayout, fragment);
        transaction.commit();
    }

    private String getReturn() {
        int total=0,x;

        for (int i:answer) {
            total+=i;
        }
        x=((100*total)/(answer.size()*5));
        return String.valueOf(x)+"%";
    }
}