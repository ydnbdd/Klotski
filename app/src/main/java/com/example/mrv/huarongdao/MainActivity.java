package com.example.mrv.huarongdao;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    GestureDetector newData;
    final int Directiony = 5;
    final int Directionx = 4;
    final int N = 10;
    final int widthDp = 80;
    final int size[]={1,2, 2,2, 1,2, 1,2, 2,1, 1,2, 1,1, 1,1, 1,1, 1,1};
    //关卡
    final int position[][]={
            {0,0, 0,3, 1,0, 2,0, 1,2, 3,0, 0,2, 3,2, 3,3, 3,4},
            {0,0, 1,0, 3,0, 0,2, 1,2, 3,2, 1,3, 2,3, 0,4, 3,4},
            {0,0, 1,0, 3,0, 0,3, 1,2, 3,3, 1,3, 2,3, 0,2, 3,2},
            {0,0, 1,0, 3,0, 0,3, 1,3, 3,3, 0,2, 1,2, 2,2, 3,2},
            {0,1, 1,0, 3,1, 0,3, 1,2, 3,3, 0,0, 3,0, 1,3, 2,3},
            {2,0, 0,0, 3,0, 0,3, 0,2, 1,3, 2,2, 3,2, 2,3, 3,3},
            {3,0, 0,0, 2,1, 1,3, 2,3, 0,2, 2,0, 1,2, 3,2, 2,4},
            {2,0, 0,0, 1,2, 0,3, 2,2, 3,3, 0,2, 1,4, 2,3, 2,4},
            {2,0, 0,2, 3,0, 2,2, 0,1, 3,2, 0,4, 1,4, 2,4, 3,4},
            {2,1, 0,1, 3,1, 2,3, 0,3, 3,3, 0,0, 1,0, 2,0, 3,0}
    };
    int phoneW;
    int phoneH;
    Stack actons_of_game;
    int level = 0;
    final int maxLevel = 10;
    int borderWidthPx;


    TextView show[];

    Handler handler = new Handler();

    public static final String EXTRA_REPLY =
            "com.example.mrv.lab22.extra.REPLY";
    private Intent replyIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        level = intent.getIntExtra(begin.EXTRA_MESSAGE, -1)-1;
        if(level == -1)level = 0;

        newData = new GestureDetector(this, new MyGestureListener());
        borderWidthPx = DpToPx(this, widthDp);
        show = new TextView[N];
        Resources res=getResources();
        for(int i=0;i<N;i++) {
            show[i] = findViewById(res.getIdentifier("textView" + (i + 1), "id", getPackageName()));
        }
        actons_of_game=new Stack();

        initLayout();

    }

    public void initLayout(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        phoneH = dm.heightPixels;
        phoneW = dm.widthPixels;

        borderWidthPx = phoneW/Directionx;

        for(int i = 0; i < N; ++i){
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) show[i].getLayoutParams();
            layoutParams.width = size[2*i]*borderWidthPx;
            layoutParams.height = size[2*i+1]*borderWidthPx;

            layoutParams.x = position[level][i*2]*borderWidthPx;
            layoutParams.y = position[level][i*2+1]*borderWidthPx;
            show[i].setLayoutParams(layoutParams);
        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return newData.onTouchEvent(event);
    }


    private DialogInterface.OnClickListener click1=new DialogInterface.OnClickListener()
    {
        //使用该标记是为了增强程序在编译时候的检查，如果该方法并不是一个覆盖父类的方法，在编译时编译器就会报告错误。
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            //当按钮click1被按下时执行结束进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };
    private DialogInterface.OnClickListener click2=new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            //当按钮click2被按下时则取消操作
            arg0.cancel();
        }
    };

    public void back(View view) {
        if(actons_of_game.empty()){
            Toast toast = Toast.makeText(this, "已经是第一步，无法回退",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        GameAction g = (GameAction)actons_of_game.pop();
        switch(g.move){
            case "up": moveDown(g.view);break;
            case "down": moveUp(g.view);break;
            case "left": moveRight(g.view);break;
            case "right": moveLeft(g.view);break;
        }
    }

    public void newGame(View view) {
        initLayout();
        actons_of_game.clear();
    }

    public void toHome(View view) {
        replyIntent.putExtra(EXTRA_REPLY, "toHome");
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void next(View view) {
        level += 1;
        level %= maxLevel;
        newGame(view);
    }

    public void showDialog(){
        final EditText inputServer = new EditText(this);
        inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("恭喜过关！请点击下一关继续游戏！").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("确定", null);
        builder.show();
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String TAG = "MyGestureListener";

        public MyGestureListener() {

        }
        /**
         * 双击的第二下Touch down时触发
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap : " + e.getAction());
            return super.onDoubleTap(e);
        }

        /**
         * 双击的第二下 down和up都会触发，可用e.getAction()区分。
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i(TAG, "onDoubleTapEvent : " + e.getAction());
            return super.onDoubleTapEvent(e);
        }

        /**
         * down时触发
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        /**
         * Touch了滑动一点距离后，up时触发。
         *
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            TextView t = findViewByPosition((int)e1.getX(), (int)e1.getY()-borderWidthPx);

            double down = e2.getY() - e1.getY();
            double right = e2.getX() - e1.getX();
            if(t != null){
                boolean ok;
                if(Math.abs(down)>Math.abs(right)){
                    if(down > 0) {
                        ok = moveDown(t);
                        if(ok)
                            actons_of_game.push(new GameAction(t, "down"));
                    }
                    else {
                        ok=moveUp(t);
                        if(ok)
                            actons_of_game.push(new GameAction(t, "up"));
                    }
                }else{
                    if(right > 0) {
                        ok=moveRight(t);
                        if(ok)
                            actons_of_game.push(new GameAction(t, "right"));
                    }
                    else {
                        ok=moveLeft(t);
                        if(ok)actons_of_game.push(new GameAction(t, "left"));
                    }
                }
            }
            if(win()) {
                showDialog();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        /**
         * Touch了不移动一直 down时触发
         *
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        /**
         * Touch了滑动时触发。
         *
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * @return
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            Log.i(TAG, "onScroll e1 : " + e1.getAction() + ", e2 : " + e2.getAction() + ", distanceX : " + distanceX + ", distanceY : " + distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * Touch了还没有滑动时触发
         *
         * @param e
         */
        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
    }

    TextView findViewByPosition(int xPx, int yPx){
        for(int i = 0; i < N; ++i){
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) show[i].getLayoutParams();

            int findViewByPositionxPx = layoutParams.x;
            int findViewByPositionyPx = layoutParams.y;
            int widthPx =  layoutParams.width;
            int heightPx = layoutParams.height;

            if(xPx >= findViewByPositionxPx && xPx <= findViewByPositionxPx + widthPx && yPx >= findViewByPositionyPx && yPx <= findViewByPositionyPx + heightPx) {
                return show[i];
            }
        }
        return null;
    }
    public boolean moveUp(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();
        int moveUpxPx = layoutParams.x;
        int moveUpyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int x = moveUpxPx + borderWidthPx/2; x < moveUpxPx + widthPx; x += borderWidthPx){
            if(findViewByPosition(x, moveUpyPx - borderWidthPx/2) != null)
                return false;
        }
        if(moveUpyPx - borderWidthPx/2 < 0)
            return false;
        layoutParams.y = t.getTop() - borderWidthPx;
        t.setLayoutParams(layoutParams);
        return true;
    }
    public boolean moveDown(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();

        int moveDownxPx = layoutParams.x;
        int moveDownyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int x = moveDownxPx + borderWidthPx/2; x < moveDownxPx + widthPx; x += borderWidthPx){
            if( findViewByPosition(x, moveDownyPx + borderWidthPx/2 + heightPx)!= null){
                return false;
            }
        }
        if(moveDownyPx + heightPx +borderWidthPx/2 > Directiony * borderWidthPx)
            return false;
        layoutParams.y = t.getTop() + borderWidthPx;
        t.setLayoutParams(layoutParams);

        return true;

    }
    public boolean moveLeft(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();

        int moveLeftxPx = layoutParams.x;
        int moveLeftyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int y = moveLeftyPx + borderWidthPx/2; y < moveLeftyPx + heightPx; y += borderWidthPx){
            if( findViewByPosition(moveLeftxPx - borderWidthPx/2, y)!= null){
                return false;
            }
        }
        if(moveLeftxPx - borderWidthPx/2 < 0)
            return false;
        layoutParams.x = t.getLeft() - borderWidthPx;
        t.setLayoutParams(layoutParams);

        return true;

    }
    public boolean moveRight(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();

        int moveRightxPx = layoutParams.x;
        int moveRightyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int y = moveRightyPx + borderWidthPx/2; y < moveRightyPx + heightPx; y += borderWidthPx){
            if( findViewByPosition(moveRightxPx + widthPx + borderWidthPx/2, y)!= null){
                return false;
            }
        }
        if(moveRightxPx + borderWidthPx/2 + widthPx > borderWidthPx * Directionx)
            return false;
        layoutParams.x = t.getLeft() + borderWidthPx;
        t.setLayoutParams(layoutParams);

        return true;
    }
    public int DpToPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public int PxToDp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public boolean win(){
        TextView caocao = show[1];
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) caocao.getLayoutParams();

        int winxPx = layoutParams.x;
        int winyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        if(winxPx == borderWidthPx && winyPx+heightPx == borderWidthPx*Directiony) {
            return true;
        }
        return false;
    }
    public void add() {
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("winHistory",MODE_APPEND);
            writer=new BufferedWriter(new OutputStreamWriter(out));
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
class GameAction{
    TextView view;
    String move;
    GameAction(TextView v, String m){view=v; move=m;}
}