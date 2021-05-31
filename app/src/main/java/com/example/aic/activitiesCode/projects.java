package com.example.aic.activitiesCode;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.aic.R;

public class projects extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
    }

    public void AR(View view) {
        Intent in = new Intent(this, came2.class);
        startActivity(in);

    }

    public void _3d(View view) {
        Intent in = new Intent(this, _3D_objectViewer.class);
        startActivity(in);

    }
}
