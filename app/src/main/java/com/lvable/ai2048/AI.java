package com.lvable.ai2048;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jiaqi Ning on 8/5/2015.
 */
public class AI {
    public enum GAMESTURN {MAX_STATE,EXP_STATE};
    public static enum MOVE_DIRECTION{LEFT,UP,RIGHT,DOWN,NOMOVE};
    public static boolean isGoalState(int[][] gameBoard){
        for (int i = 0;i < gameBoard.length;i++){
            for (int j =0;j < gameBoard.length;j++){
                if (gameBoard[i][j] == 2048){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isGameOver(int[][] state){
        int count = GameView.getEmptyCellCount(state);
        if (count == 0){
            int[][] ls = AI.arrayCopy(state);
            int[][] us = AI.arrayCopy(state);
            int[][] rs = AI.arrayCopy(state);
            int[][] ds = AI.arrayCopy(state);
            if (!swipeLeft(ls) && !swipeUp(us)
                    && !swipeRight(rs) && !swipeDown(ds)){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    private int value(int[][] state,GAMESTURN gameTurn,int depth){
        depth--;
        if (isGoalState(state) || depth < 0) {
            return getH(state);
        }
        if (gameTurn == GAMESTURN.MAX_STATE){
            return maxTurnValue(state,depth);
        }else {
            return expTurnValue(state,depth);
        }
    }

    private int getH(int[][] state) {
        int emptyCellCount = getEmptyCellCount(state);
        int mono = getMonotonicity(state);
        int smooth = getSmooth(state);
        return emptyCellCount+mono+smooth;

    }


    private int getCornerMaxH(int[][] state){
        int emptyCellCount = getEmptyCellCount(state);
        return 0;
    }

    // range : 0~14
    public static int getEmptyCellCount(int[][] state) {
        int emptyCellCount = 0;
        for (int i = 0; i < 4;i++){
            for (int j = 0; j <4;j++){
                if (state[i][j] == 0)
                    emptyCellCount++;
            }
        }
        return emptyCellCount;
    }

    // return max value is 24
    public static int getMonotonicity(int[][] state){
        int m1 = 0,m2 = 0 ,m3 = 0,m4 = 0;
        for (int i=0; i<4;i++){
            for (int j=0;j<3;j++){
                if (state[i][j] > state[i][j+1])
                    m1++;
                else
                    m2++;
            }
        }

        // vertical
        for (int i=0;i<4;i++){
            for (int j=0;j<3;j++){
               if (state[j][i] > state[j+1][i])
                   m3++;
                else
                   m4++;
            }
        }
        int c1 = m1+m3;
        int c2 = m2+m3;
        int c3 = m1 + m4;
        int c4 = m2 + m4;
        int max1 = Math.max(c1,c2);
        int max2 = Math.max(c3,c4);
        return Math.max(max1,max2);

    }

    public static int getSmooth(int[][] state){
        int s1 = 0,s2 = 0,s3 = 0,s4=0;
        for (int i=0; i<4;i++){
            for (int j=0;j<3;j++){
                if (state[i][j] == 0 || state[i][j+1] == 0)
                    continue;
                if (state[i][j] > state[i][j+1]){
                    if ((state[i][j]/state[i][j+1]) <=2 )
                        s1++;
                }else {
                    if ((state[i][j+1]/state[i][j]) <=2 )
                        s2++;
                }

            }
        }
        for (int i=0;i<4;i++){
            for (int j=0;j<3;j++){
                if (state[j][i] == 0 || state[j+1][i] == 0)
                    continue;
                if (state[j][i] > state[j+1][i]){
                    if ((state[j][i]/state[j+1][i]) <= 4)
                        s3++;
                }else {
                    if ((state[j+1][i]/state[j][i]) <= 4)
                        s4++;
                }
            }
        }
        int c1 = s1+s3;
        int c2 = s2+s3;
        int c3 = s1 + s4;
        int c4 = s2 + s4;
        int max1 = Math.max(c1,c2);
        int max2 = Math.max(c3,c4);
        return Math.max(max1,max2);

    }

    private int maxTurnValue(int[][] state,int depth) {
        int v = Integer.MIN_VALUE;
        HashMap<MOVE_DIRECTION,int[][]> successor= getMaxStateSuccessor(state);
        int[][] leftState = successor.get(MOVE_DIRECTION.LEFT);
        int[][] upState = successor.get(MOVE_DIRECTION.UP);
        int[][] rightState = successor.get(MOVE_DIRECTION.RIGHT);
        int[][] downState = successor.get(MOVE_DIRECTION.DOWN);
        if (leftState != null){
            v = Math.max(v, value(leftState, GAMESTURN.EXP_STATE, depth));
        }
        if (upState != null){
            v = Math.max(v, value(upState, GAMESTURN.EXP_STATE, depth));
        }
        if (rightState != null){
            v = Math.max(v, value(rightState, GAMESTURN.EXP_STATE, depth));
        }
        if (downState != null){
            v = Math.max(v, value(downState, GAMESTURN.EXP_STATE, depth));
        }
        return v;

    }

    private int expTurnValue(int[][] state,int depth){
        int v = 0;
        List<int[][]> successorList= getExpStateSuccessor(state);
        if (successorList == null)
            return 1;
        int i = 0;
        float p;
        for (int[][] successor : successorList){
           if (i % 2 == 0){
               p = 0.9f;
           }else {
               p = 0.1f;
           }
            v += p*value(successor,GAMESTURN.MAX_STATE,depth);
        }
        return v;
    }

    private List<int[][]> getExpStateSuccessor(int[][] state) {
        List<int[][]> successorList = new ArrayList<>();
        List<Point> emptyList = getEmptyCellList(state);
        for (Point point : emptyList){
            successorList.add(insertOneCard(state,2,point));
            successorList.add(insertOneCard(state,4,point));
        }
        return successorList;
    }

    private int[][] insertOneCard(int[][] preState,int val,Point point){
        int[][] result = arrayCopy(preState);
        result[point.x][point.y] = val;
        return result;
    }

    private HashMap<MOVE_DIRECTION,int[][]> getMaxStateSuccessor(int[][] currentState){
        int[][] leftState = arrayCopy(currentState);
        int[][] upState = arrayCopy(currentState);
        int[][] rightState = arrayCopy(currentState);
        int[][] downState = arrayCopy(currentState);

        boolean validMoveL = swipeLeft(leftState);
        boolean validMoveU = swipeUp(upState);
        boolean validMoveR = swipeRight(rightState);
        boolean validMoveD = swipeDown(downState);

        HashMap<MOVE_DIRECTION,int[][]> map = new HashMap<>();
        if (validMoveL)
            map.put(MOVE_DIRECTION.LEFT,leftState);
        if (validMoveU)
            map.put(MOVE_DIRECTION.UP,upState);
        if (validMoveR)
            map.put(MOVE_DIRECTION.RIGHT,rightState);
        if (validMoveD)
            map.put(MOVE_DIRECTION.DOWN,downState);
        return map;
    }

    public static List<Point> getEmptyCellList(int[][] cards) {
        List<Point> emptyCellList = new ArrayList<>();
        for (int i = 0;i < cards.length;i++){
            for (int j =0;j < cards.length;j++){
                if (cards[i][j] == 0){
                    emptyCellList.add(new Point(i,j));
                }
            }
        }
        return emptyCellList;
    }

    public MOVE_DIRECTION dumySolution(int[][] s){
        int[][] state = arrayCopy(s);
        HashMap<MOVE_DIRECTION,int[][]> successor = getMaxStateSuccessor(state);
        MOVE_DIRECTION nxtMove = MOVE_DIRECTION.NOMOVE;
        int v = Integer.MIN_VALUE;
        int temp;
        int[][] leftState = successor.get(MOVE_DIRECTION.LEFT);
        int[][] upState = successor.get(MOVE_DIRECTION.UP);
        int[][] rightState = successor.get(MOVE_DIRECTION.RIGHT);
        int[][] downState = successor.get(MOVE_DIRECTION.DOWN);
        if (leftState != null){
            temp = value(leftState, GAMESTURN.EXP_STATE, 4);
            if (temp > v)
                nxtMove = MOVE_DIRECTION.LEFT;
        }
        if (upState != null){
            temp = value(upState, GAMESTURN.EXP_STATE, 4);
            if (temp > v)
                nxtMove = MOVE_DIRECTION.UP;
        }
        if (rightState != null){
            temp = value(rightState, GAMESTURN.EXP_STATE, 4);
            if (temp > v)
                nxtMove = MOVE_DIRECTION.RIGHT;
        }
        if (downState != null){
            temp = value(downState, GAMESTURN.EXP_STATE, 4);
            if (temp > v)
                nxtMove = MOVE_DIRECTION.DOWN;
        }
           return nxtMove;
    }

    // TODO: add to util class
    public static int[][] arrayCopy(int[][] src){
        int[][] result = new int[4][4];
        for (int i = 0;i < 4;i++){
            for (int j = 0;j < 4;j++){
                result[i][j] = src[i][j];
            }
        }
        return result;
    }

    public static boolean swipeUp(int[][] cards){
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
    public static boolean swipeRight(int[][] cards){
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
                            cards[x][y1] =0;

                        }

                        break;
                    }
                }
            }
        }
        return hasChanged;

    }
    public static boolean swipeLeft(int[][] state){
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
                            state[x][y1] = 0;
                        }

                        break;

                    }
                }
            }
        }
        return hasChanged;

    }
    public static boolean swipeDown(int[][] cards){
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
                            cards[x1][y] =0;

                        }

                        break;
                    }
                }
            }
        }
        return hasChanged;

    }


}

