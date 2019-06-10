package com.example.mrv.huarongdao;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class begin extends AppCompatActivity {

    public static final Integer TEXT_REQUEST = 1;
    public static final String EXTRA_MESSAGE =
            "com.example.mrv.huarongdao.extra.MESSAGE";

    private final int maxLevel=9;

    int level= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
    }
    public void beginGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, level);
        startActivityForResult(intent, TEXT_REQUEST);

    }

    public void exit(View view) {
        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage("确定要退出程序吗？");
        alertdialogbuilder.setPositiveButton("确定",click1);
        alertdialogbuilder.setNegativeButton("取消",click2);
        AlertDialog alertdialog1=alertdialogbuilder.create();
        alertdialog1.show();
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

    public void helper(View view) {
        Intent intent = new Intent(this, helper.class);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    public void choose(View view) {
        level = level % maxLevel;
        level += 1;
        Button b = (Button)findViewById(R.id.choose);
        String name[] = {"小试牛刀","横刀立马", "横刀立马2", "齐头并进", "兵分三路", "屯兵东路","四面楚歌","阿谀奉承","暗度陈仓","别无选择"};
        b.setText(name[level-1]);
    }
}