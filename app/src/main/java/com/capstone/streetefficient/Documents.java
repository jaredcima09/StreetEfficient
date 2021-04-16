package com.capstone.streetefficient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.os.Bundle;

import com.capstone.streetefficient.adapters.DocumentsAdapter;
import com.google.android.material.tabs.TabLayout;

public class Documents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        TabLayout MTabLayout = findViewById(R.id.document_tabs);
        ViewPager MViewPager = findViewById(R.id.document_pager);


        DocumentsAdapter documentsAdapter = new DocumentsAdapter(getIntent().getStringArrayListExtra("documents"), this);
        MViewPager.setAdapter(documentsAdapter);
        MTabLayout.setupWithViewPager(MViewPager, true);
    }
}