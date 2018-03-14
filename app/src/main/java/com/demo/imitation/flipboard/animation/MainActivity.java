package com.demo.imitation.flipboard.animation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.demo.imitation.flipboard.animation.view.FlipBoardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FlipBoardView view = (FlipBoardView) findViewById(R.id.view_flipboard);
    }
}
