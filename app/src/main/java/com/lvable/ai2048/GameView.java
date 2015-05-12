package com.lvable.ai2048;

import android.content.Context;

import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Jiaqi Ning on 5/5/2015.
 */
public class GameView extends GridLayout {
    public int[][] getCurState() {
        return curState;
    }
    private  boolean isWin = false;

    public  boolean isLose() {
        return isLose;
    }

    private static boolean isLose = false;

    public void setCurState(int[][] curState) {
        this.curState = curState;
    }
    private int[][] curState = new int[4][4];
    private int cardMargin = 3;
    private ArrayList<NumberCardView> cardViews = new ArrayList<>();
    private List<Point> emptyCellList = new ArrayList<>();
    private Random mRandom = new Random();
    private Context mContext;
    public GameView(Context context) {
        super(context);
        setupGestureListener();
        mContext = context;
    }


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupGestureListener();
        mContext = context;
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupGestureListener();
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int cardWidth = (w-6*cardMargin)/4;


        addCards(cardWidth);

/*
        curState[0][0]=4;
        curState[1][0]=4;
        curState[2][0]=4;
        curState[3][0]=2;


        curState[0][1]=16;
        curState[1][1]=2;
        curState[2][1]=64;
        curState[3][1]=4;

        curState[0][2]=2;
        curState[1][2]=8;
        curState[2][2]=128;
        curState[3][2]=256;

        curState[0][3]=16;
        curState[1][3]=64;
        curState[2][3]=256;
        curState[3][3]=4;*/
        updateGameBoard();
    }

    private void addCards(int cardWidth) {
        for (int i = 0;i<4;i++){
            for (int j =0;j<4;j++){
                NumberCardView textView = new NumberCardView(getContext());
                textView.setHeight(cardWidth);
                textView.setWidth(cardWidth);
                textView.setNum(0);
                textView.setTextSize(25);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(cardMargin, cardMargin, cardMargin, cardMargin);
                textView.setLayoutParams(params);
                addView(textView);
                cardViews.add(textView);
            }
        }
    }

    public void updateGameBoard(){
        addRandomNumber();
        for (int i = 0;i < curState.length;i++){
            for (int j = 0;j< curState.length;j++){
                cardViews.get(i*4 + j).setNum(curState[i][j]);
            }
        }
        if (isWin){
            Toast.makeText(mContext,"You win!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (isGameOver(curState)){
            Toast.makeText(mContext,"GameOver!",Toast.LENGTH_SHORT).show();
        }
        Log.d("heu","empty: "+AI.getEmptyCellCount(curState) +
                " smooth: "+AI.getSmooth(curState) + " mono: "+AI.getMonotonicity(curState));

    }

    private void setupGestureListener(){
        setOnTouchListener(new OnTouchListener() {
            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                Log.d("swipe", "left");

                                if (swipeLeft(curState)) {
                                    updateGameBoard();
                                }
                            } else if (offsetX > 5) {
                                Log.d("swipe", "right");

                                if (swipeRight(curState)) {
                                    updateGameBoard();
                                }
                            }
                        } else {
                            if (offsetY < -5) {
                                Log.d("swipe", "up");

                                if (swipeUp(curState)) {
                                    updateGameBoard();
                                }
                            } else if (offsetY > 5) {
                                Log.d("swipe", "down");
                                if (swipeDown(curState)) {
                                    updateGameBoard();
                                }
                            }
                        }
                        break;
                }
                return true;
            }

        });
    }

    public boolean swipeUp(int[][] cards){
        boolean hasChanged = false;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                for (int x1 = x+1; x1 < 4; x1++) {
                    if (cards[x1][y]>0) {
                        if (cards[x][y]<=0) {
                            cards[x][y]= cards[x1][y];
                            cards[x1][y]=0;
                            hasChanged =true;
                            x--;
                        }else if (cards[x][y]==cards[x1][y]) {
                            cards[x][y]=cards[x][y]*2;
                            if (cards[x][y] == 2048)
                                isWin = true;
                            cards[x1][y]=(0);
                            hasChanged =true;
                        }
                        break;
                    }
                }
            }
        }
        return hasChanged;
    }
    public boolean swipeRight(int[][] cards){
        boolean hasChanged = false;
        for (int x = 0; x < 4; x++) {
            for (int y = 4-1; y >=0; y--) {

                for (int y1 = y-1; y1 >=0; y1--) {
                    if (cards[x][y1] >0) {

                        if (cards[x][y] <=0) {

                            cards[x][y] =cards[x][y1] ;
                            cards[x][y1] =0;
                            hasChanged = true;
                            y++;

                        }else if (cards[x][y]==(cards[x][y1])) {
                            hasChanged = true;
                            cards[x][y] *=2;
                            if (cards[x][y] == 2048)
                                isWin = true;
                            cards[x][y1] =0;

                        }

                        break;
                    }
                }
            }
        }
        return hasChanged;

    }
    public boolean swipeLeft(int[][] state){
        boolean hasChanged = false;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {

                for (int y1 = y+1; y1 < 4; y1++) {
                    if (state[x][y1]>0) {

                        if (state[x][y]<=0) {
                            state[x][y]=(state[x][y1]);
                            state[x][y1]=0;
                            hasChanged = true;
                            y--;

                        }else if (state[x][y]==state[x][y1]) {
                            hasChanged = true;
                            state[x][y] *= 2;
                            if (state[x][y] == 2048)
                                isWin = true;
                            state[x][y1] = 0;
                        }

                        break;

                    }
                }
            }
        }
        return hasChanged;

    }
    public boolean swipeDown(int[][] cards){
        boolean hasChanged = false;
        for (int y = 0; y <  4; y++) {
            for (int x =  4-1; x >=0; x--) {

                for (int x1 = x-1; x1 >=0; x1--) {
                    if (cards[x1][y] >0) {

                        if (cards[x][y] <=0) {

                            cards[x][y] = cards[x1][y] ;
                            cards[x1][y] =0;
                            hasChanged = true;
                            x++;
                        }else if (cards[x][y]==(cards[x1][y])) {
                            hasChanged = true;
                            cards[x][y] =cards[x][y] *2;
                            if (cards[x][y] == 2048)
                                isWin = true;
                            cards[x1][y] =0;

                        }

                        break;
                    }
                }
            }
        }
        return hasChanged;

    }

    private void addRandomNumber(){
        emptyCellList.clear();
        emptyCellList = getEmptyCellList(curState);
        int emptyCellCount = emptyCellList.size();
        if (emptyCellCount > 0) {
            Point randomPick = emptyCellList.get(mRandom.nextInt(emptyCellCount));
            curState[randomPick.x][randomPick.y] = Math.random() > 0.1 ? 2 : 4;
        }
    }

   List<Point> getEmptyCellList(int[][] cards) {
       emptyCellList.clear();
        for (int i = 0;i < cards.length;i++){
            for (int j =0;j < cards.length;j++){
                if (cards[i][j] == 0){
                    emptyCellList.add(new Point(i,j));
                }
            }
        }
        return emptyCellList;
    }

   public boolean isGameOver(int[][] state){
       int count = getEmptyCellCount(state);
       if (count == 0){
           int[][] ls = AI.arrayCopy(state);
           int[][] us = AI.arrayCopy(state);
           int[][] rs = AI.arrayCopy(state);
           int[][] ds = AI.arrayCopy(state);
           if (!swipeLeft(ls) && !swipeUp(us)
                   && !swipeRight(rs) && !swipeDown(ds)){
               isLose = true;
               return true;
           }else {
               return false;
           }
       }else {
           return false;
       }
   }

    public static int getEmptyCellCount(int[][] state) {
        int count = 0;
        for (int i = 0;i < 4;i++){
            for (int j = 0;j < 4;j++){
                if (state[i][j] == 0)
                    count++;
            }
        }
        return count;
    }

    public  boolean isWin(){
        return isWin;
    }


}
