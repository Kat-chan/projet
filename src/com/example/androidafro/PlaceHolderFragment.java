package com.example.androidafro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaceHolderFragment extends Fragment {
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	public static PlaceHolderFragment newInstance(int sectionNumber) 
	{
		PlaceHolderFragment fragment = new PlaceHolderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

		return fragment;
	}
	
	public PlaceHolderFragment() { 	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.viewpager, container, false);

		// TODO: normal onCreat code here

		return rootView;
	}
}