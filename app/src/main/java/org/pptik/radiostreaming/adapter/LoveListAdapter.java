package org.pptik.radiostreaming.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.pptik.radiostreaming.database.DBManager;
import org.pptik.radiostreaming.R;
import org.pptik.radiostreaming.util.RadioOperationInfo;

public class LoveListAdapter extends BaseAdapter
{
    private Context mContext = null;
    
    public ArrayList<String> mRadioName = null;
    
    private LayoutInflater mInflater = null;
    
    private DBManager mDBManager = null;
    
    public LoveListAdapter(Context context, ArrayList<String> mRadioName)
    {
        this.mContext = context;
        this.mRadioName = mRadioName;
        this.mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount()
    {
        
        if (mRadioName != null)
        {
            return mRadioName.size();
        }
        else
            return 0;
    }
    
    @Override
    public Object getItem(int position)
    {
        
        if (mRadioName != null)
        {
            return mRadioName.get(position);
        }
        else
            return null;
    }
    
    @Override
    public long getItemId(int position)
    {
        
        return position;
    }
    
    @SuppressLint({"InflateParams", "ViewHolder", "NewApi"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        final String ViewRadioName;
        final int pos = position;
        ViewRadioName = mRadioName.get(position);
        convertView = mInflater.inflate(R.layout.main_list_adapter, null);
        holder = new ViewHolder();
        holder.mLoveButton = (Button)convertView.findViewById(R.id.mainLoveButton);
        holder.mDisloveButton = (Button)convertView.findViewById(R.id.mainDisloveButton);
        holder.mTextView = (TextView)convertView.findViewById(R.id.mainTextView);
        
        holder.mDisloveButton.setVisibility(View.GONE);
        holder.mLoveButton.setVisibility(View.VISIBLE);
        
        holder.mTextView.setText(ViewRadioName);
        holder.mLoveButton.setOnClickListener(new View.OnClickListener()
        {
            
            public void onClick(View v)
            {
                
                holder.mLoveButton.setVisibility(View.GONE);
                removeRadio(ViewRadioName);
                mRadioName.remove(pos);
                notifyDataSetChanged();
                Intent intent = new Intent();
                intent.setAction(RadioOperationInfo.RADIO_LOVE_LIST_UPDATE);
                intent.putExtra(RadioOperationInfo.RADIO_INFO_NUM, pos);
                mContext.sendBroadcast(intent);
            }
        });
        
        return convertView;
    }
    
    private void removeRadio(String name)
    {
        mDBManager = new DBManager(mContext);
        mDBManager.remove(name);
        mDBManager.closeDB();
    }
    
    private class ViewHolder
    {
        private TextView mTextView;
        
        private Button mLoveButton;
        
        private Button mDisloveButton;
        
    }
    
}
