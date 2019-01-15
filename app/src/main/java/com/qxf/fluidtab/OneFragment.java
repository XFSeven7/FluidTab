package com.qxf.fluidtab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OneFragment extends Fragment {

	public static String MSG;

	private ListView listView;

	public static OneFragment newInstance(String msg) {
		OneFragment fragment = new OneFragment();
		Bundle args = new Bundle();
		args.putString(OneFragment.MSG, msg);
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = view.findViewById(R.id.listview);

		String msg = getArguments().getString(MSG);

		ArrayList<String> strings = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			strings.add("我叫" + msg + "，我是消息" + i);
		}
		listView.setAdapter(new ArrayAdapter<String>(view.getContext(), R.layout.item, strings));

	}
}
