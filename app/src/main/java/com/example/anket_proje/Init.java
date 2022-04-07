package com.example.anket_proje;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Init {
    private FirebaseFirestore db;

    private HashMap<String,Object> map;

    private String array[]=new String[]{"Örnek soru 1","Örnek soru 2","Örnek soru 3","Örnek soru 4","Örnek soru 5","Örnek soru 6"};

    private void start(String[] array,String key){
        db=FirebaseFirestore.getInstance();
        map=new HashMap<>();
        for (String str:array) {
            map.put("question",str);
            db.collection(key).add(map);
        }
    }

    public void init(){
        start(array,"Kerem");
        start(array,"Cem");
        start(array,"Bilal");
    }
}
