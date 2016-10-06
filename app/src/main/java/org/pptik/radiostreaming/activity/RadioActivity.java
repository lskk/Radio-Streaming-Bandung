package org.pptik.radiostreaming.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.pptik.radiostreaming.R;
import org.pptik.radiostreaming.adapter.MainListAdapter;
import org.pptik.radiostreaming.util.PictureFormatTransform;
import org.pptik.radiostreaming.view.DragLayout;

import java.util.ArrayList;

public class RadioActivity extends AppCompatActivity {

    private DragLayout mDragLayout;
    private ListView MainMenuList;
    private ListView MainActivityList;
    private CollapsingToolbarLayout collapseToolbar;
    private AppBarLayout appBarLayout;
    private MainListAdapter mMainListAdapter;
    private ArrayList<String> mMainRadioName = new ArrayList<String>();
    private ArrayList<String> mMainRadioPath = new ArrayList<String>();
    private Toolbar toolbar;
    private boolean isExpand = false;
    private FloatingActionButton fShow, fHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_view_stream)
                .color(ContextCompat.getColor(this, R.color.colorLight))
                .sizeDp(24));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        bindingXml();
        setListView();
        initDragLayout();
        setShowHide();
    }

    private void setListView() {
        initMainListInfo();
        mMainListAdapter = new MainListAdapter(this, mMainRadioName, mMainRadioPath);
        MainActivityList.setAdapter(mMainListAdapter);
        MainActivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void bindingXml() {
        collapseToolbar = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapseToolbar.setExpandedTitleTextAppearance(R.style.TransparentText);
        collapseToolbar.setTitle("");
        mDragLayout = (DragLayout)findViewById(R.id.MainDragLayout);
        MainMenuList = (ListView)findViewById(R.id.MainMenuList);
        MainActivityList = (ListView)findViewById(R.id.MainActivityList);
        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        fHide = (FloatingActionButton)findViewById(R.id.hide);
        fShow = (FloatingActionButton)findViewById(R.id.show);
        fHide.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_keyboard_arrow_up)
                .color(Color.WHITE)
                .sizeDp(20)));
        fShow.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_keyboard_arrow_down)
                .color(Color.WHITE)
                .sizeDp(20)));
    }

    private void setShowHide(){
        fHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setExpanded(false);
            }
        });
        fShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setExpanded(true);
            }
        });
    }

    private void initDragLayout() {

        MainMenuList.setAdapter(new ArrayAdapter<String>(RadioActivity.this, R.layout.menu_list_adapter, new String[] {
                "My recording"}));
        MainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        mDragLayout.setDragListener(new DragLayout.DragListener()
        {
            @Override
            public void onOpen()
            {
                MainMenuList.smoothScrollToPosition(0);
                appBarLayout.setExpanded(false);
            }

            @Override
            public void onClose()
            {
              //  shake();
                appBarLayout.setExpanded(true);
            }

            @Override
            public void onDrag(float percent)
            {
              //  ViewHelper.setAlpha(mImage, 1 - percent);
            }
        });
    }

    private void initMainListInfo() {
        if (mMainRadioName != null) {
            mMainRadioName.removeAll(mMainRadioName);
        }
        if (mMainRadioPath != null) {
            mMainRadioPath.removeAll(mMainRadioPath);
        }
        String[] infos = getResources().getStringArray(R.array.radio_info);
        for (int i = 0; i < infos.length; i++) {
            if (i % 2 == 0) {
                mMainRadioName.add(infos[i]);
            }
            else {
                mMainRadioPath.add(infos[i]);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if(isExpand == false) {
                mDragLayout.open();
                isExpand = true;
            }else {
                mDragLayout.close();
                isExpand = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
