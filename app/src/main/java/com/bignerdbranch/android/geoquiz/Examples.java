package com.bignerdbranch.android.geoquiz;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Examples {
    private Button falseButton;

    public void aMethod(){
        View.OnClickListener listener=new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             System.out.println("it is well");
            }
        };
        falseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("it is well");
            }
        });
    }
}
