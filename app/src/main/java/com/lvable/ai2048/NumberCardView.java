package com.lvable.ai2048;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;

import android.widget.TextView;

/**
 * Created by Jiaqi Ning on 6/5/2015.
 */
public class NumberCardView extends TextView {
    private int val;
    private int preVal;
    private int preColor;
    private int curColor;
    private int[] colorArray = {0xff00BCD4,0xff03A9F4,0xff4CAF50,0xff009688
            ,0xff3F51B5,0xff673AB7,0xffF44336,0xffE91E63,0xffFFA726,0xffFF9800,0xffF4511E,0xffBCAAA4};
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    public NumberCardView(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        preVal = 0;
        val = 0;
        preColor = curColor = colorArray[11];
    }

    public void setNum(int val){
        this.val = val;
        if (val == 0){
            setText("");
        }else {
            setText(val + "");
        }

        switch (val){
            case 2:
                //setBackgroundColor(colorArray[0]);
                curColor = colorArray[0];
                break;
            case 4:
                //setBackgroundColor(colorArray[1]);
                curColor = colorArray[1];
                break;
            case 8:
                //setBackgroundColor(colorArray[2]);
                curColor = colorArray[2];
                break;
            case 16:
                //setBackgroundColor(colorArray[3]);
                curColor = colorArray[3];
                break;
            case 32:
               // setBackgroundColor(colorArray[4]);
                curColor = colorArray[4];
                break;
            case 64:
                //setBackgroundColor(colorArray[5]);
                curColor = colorArray[5];
                break;
            case 128:
               // setBackgroundColor(colorArray[6]);
                curColor = colorArray[6];
                break;
            case 256:
                //setBackgroundColor(colorArray[7]);
                curColor = colorArray[7];
                break;
            case 512:
              //  setBackgroundColor(colorArray[8]);
                curColor = colorArray[8];
                break;
            case 1024:
               // setBackgroundColor(colorArray[9]);
                curColor = colorArray[9];
                break;
            case 2048:
               // setBackgroundColor(colorArray[10]);
                curColor = colorArray[10];
                break;
            default:
               // setBackgroundColor(colorArray[11]);
                curColor = colorArray[11];
                break;

        }


        if (preVal == 0 && val != 0){
            // pop up animation
            ObjectAnimator.ofObject(this, "backgroundColor",mArgbEvaluator , colorArray[11], curColor)
                    .setDuration(250)
                    .start();

        }else if (preVal != 0 && val == 0){
            // disappear animation
            ObjectAnimator.ofObject(this, "backgroundColor",mArgbEvaluator , preColor, colorArray[11])
                    .setDuration(250)
                    .start();

        }else {
            // combine animation
            ObjectAnimator.ofObject(this, "backgroundColor",mArgbEvaluator , preColor, curColor)
                    .setDuration(250)
                    .start();
        }
        preColor = curColor;
        preVal = val;
    }
}
