package com.qxf.library;

import android.support.v4.app.Fragment;

public class Tab {

	private int pic;
	private String text;
	private Fragment fragment;

	public Tab(int pic, String text) {
		this.pic = pic;
		this.text = text;
	}

	public Tab(int pic, String text, Fragment fragment) {
		this.pic = pic;
		this.text = text;
		this.fragment = fragment;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public int getPic() {
		return pic;
	}

	public void setPic(int pic) {
		this.pic = pic;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
