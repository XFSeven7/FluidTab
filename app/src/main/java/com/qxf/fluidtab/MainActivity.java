package com.qxf.fluidtab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qxf.library.FluidTab;
import com.qxf.library.Tab;

public class MainActivity extends AppCompatActivity {

	FluidTab fluidTab;

	Tab[] tabs = new Tab[4];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

		tabs[0] = new Tab(R.drawable.ic_home, "主页", OneFragment.newInstance("fragment1"));
		tabs[1] = new Tab(R.drawable.ic_record, "记录", OneFragment.newInstance("fragment2"));
		tabs[2] = new Tab(R.drawable.ic_shop, "购物", OneFragment.newInstance("fragment3"));
		tabs[3] = new Tab(R.drawable.ic_mine, "我的", OneFragment.newInstance("fragment4"));

		fluidTab.involve(getSupportFragmentManager(), R.id.content);
		fluidTab.setTabs(tabs);

	}

	private void initView() {
		fluidTab = findViewById(R.id.fluidTab);
	}

}
