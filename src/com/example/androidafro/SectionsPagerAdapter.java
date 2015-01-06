package com.example.androidafro;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	Context context;

	public SectionsPagerAdapter(FragmentManager fragmentManager, Context _context) 
	{
		super(fragmentManager);
        context = _context;
	}

	@Override
	public Fragment getItem(int position) 
	{
		// getItem is called to instantiate the fragment for the given page.
		// Return a PlaceholderFragment (defined as a static inner class below).
		return PlaceHolderFragment.newInstance(position + 1);
	}

	@Override
	public int getCount() 
	{
		// Show 3 total pages.
		return 3;
	}
	
	/**
	 * Les correspondants
	 */
	@Override
	public CharSequence getPageTitle(int position) 
	{
		Locale l = Locale.getDefault();
		switch (position) {
			case 0: return context.getString(R.string.title_section1).toUpperCase(l);
			case 1: return context.getString(R.string.title_section2).toUpperCase(l);
			case 2: return context.getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
}

