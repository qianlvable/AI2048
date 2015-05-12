package com.lvable.ai2048;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    AITask mAITask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GameView gameBorad = (GameView)findViewById(R.id.game_board);
        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAITask = new AITask(gameBorad, gameBorad.getCurState(), MainActivity.this);
                mAITask.execute();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAITask != null)
            mAITask.cancel(true);
    }
}
