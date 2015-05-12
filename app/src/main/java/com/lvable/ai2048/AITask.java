package com.lvable.ai2048;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jiaqi Ning on 10/5/2015.
 */
public class AITask extends AsyncTask<int[][], Void, Boolean> {
    private GameView mGame;
    private int[][] mState;
    private Context mContext;
    private StringBuffer mStringBuffer = new StringBuffer();
    private List<Point> emptyCellList = new ArrayList<>();
    ProgressDialog progressDialog;
    private Random mRandom = new Random();
    public AITask(GameView game,int[][] state,Context context){
        mGame = game;
        mState = AI.arrayCopy(state);
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(int[][]... val) {
        AI ai = new AI();
        boolean isWin = false;
        boolean isLose = false;
        while (true){
            if (isCancelled())
                return false;
            isWin = AI.isGoalState(mState);
            isLose = AI.isGameOver(mState);
            if (isWin)
                return true;
            if (isLose)
                return false;
            AI.MOVE_DIRECTION nxtMove = ai.dumySolution(mState);
            switch (nxtMove){
                case LEFT:
                    AI.swipeLeft(mState);
                    break;
                case UP:
                    AI.swipeUp(mState);
                    break;
                case RIGHT:
                    AI.swipeRight(mState);
                    break;
                case DOWN:
                    AI.swipeDown(mState);
                    break;
                default:
                    Toast.makeText(mContext,"No move",Toast.LENGTH_SHORT).show();
                    break;
            }
            addRandomNumber();
            //Log.d("wtf",getArrayString(mState));
        }

    }


    @Override
    protected void onPostExecute(Boolean isWin) {
        progressDialog.dismiss();
        mGame.setCurState(mState);
        mGame.updateGameBoard();
        if (isWin)
            Toast.makeText(mContext,"Win",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mContext,"Lose",Toast.LENGTH_LONG).show();
    }

    private String getArrayString(int[][] state){
        mStringBuffer.setLength(0);
        for (int i = 0; i < 4;i++){
            for (int j =0;j < 4;j++){
                mStringBuffer.append(state[i][j]+"");
            }
            mStringBuffer.append("\n");
        }
        return mStringBuffer.toString();
    }

    private void addRandomNumber(){
        emptyCellList.clear();
        emptyCellList = AI.getEmptyCellList(mState);
        int emptyCellCount = emptyCellList.size();
        if (emptyCellCount > 0) {
            Point randomPick = emptyCellList.get(mRandom.nextInt(emptyCellCount));
            mState[randomPick.x][randomPick.y] = Math.random() > 0.1 ? 2 : 4;
        }
    }
}
