package edu.cmu.carannotationv2;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ParceableList implements Parcelable{
	
	public   List<String> makeGroup = new ArrayList<String>();
	public   List<List<String>> makemodelGroup = new ArrayList<List<String>>();
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
