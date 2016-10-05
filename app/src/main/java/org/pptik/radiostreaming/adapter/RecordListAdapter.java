package org.pptik.radiostreaming.adapter;

import java.util.ArrayList;
import org.pptik.radiostreaming.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordListAdapter extends BaseAdapter
{
    private ArrayList<String> RecordFile = null;
    
    private Context mContext = null;
    
    private LayoutInflater mInflater = null;
    
    public RecordListAdapter(Context context, ArrayList<String> RecordFile)
    {
        this.mContext = context;
        this.RecordFile = RecordFile;
        this.mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount()
    {
        
        if (RecordFile != null)
        {
            return RecordFile.size();
        }
        else
        {
            return 0;
        }
    }
    
    @Override
    public Object getItem(int position)
    {
        
        if (RecordFile != null)
        {
            return RecordFile.get(position);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public long getItemId(int position)
    {
        
        return position;
    }
    
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.record_list_adapter, null);
        TextView Record_List_TextView = (TextView)convertView.findViewById(R.id.Record_List_TextView);
        Record_List_TextView.setText(RecordFile.get(position));
        return convertView;
    }
    
}
