package org.pptik.radiostreaming.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.pptik.radiostreaming.R;

public class ExitDialog extends Dialog implements OnClickListener
{
    private Context mContext;
    
    private Button cancel;
    
    private Button confirm;
    
    private Button mini;
    
    public ExitDialog(Context context)
    {
        super(context);
        this.mContext = context;
    }
    
    public ExitDialog(Context context, int theme)
    {
        super(context, theme);
        this.mContext = context;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_dialog_layout);
        cancel = (Button)findViewById(R.id.cancel);
        confirm = (Button)findViewById(R.id.confirm);
        mini = (Button)findViewById(R.id.mini);
        cancel.setTextColor(0xff1E90FF);
        confirm.setTextColor(0xff1E90FF);
        mini.setTextColor(0xff1E90FF);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        mini.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.cancel:
                this.dismiss();
                break;
            case R.id.mini:
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.MAIN");
                intent1.addCategory("android.intent.category.HOME");
                mContext.startActivity(intent1);
                this.dismiss();
                break;
            case R.id.confirm:
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
        }
    }
    
}
