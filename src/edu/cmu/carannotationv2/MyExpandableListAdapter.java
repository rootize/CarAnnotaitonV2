package edu.cmu.carannotationv2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.R.integer;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter implements SectionIndexer{

	Activity activity;
	List<String> listGroup=new ArrayList<String>();
	List<List<String>>listChild=new ArrayList<List<String>>();
	private int _posGroup=0;
	private int _posChild=0;
	private HashMap<String, Integer>alphaIndexer;
	private String[]sections;
	
	public MyExpandableListAdapter(Activity a, List<String> group, List<List<String>> children ){
		super();
		activity=a;
		listGroup=group;
		listChild=children;
		
		 alphaIndexer = new HashMap<String, Integer>();
	        for (int i = 0; i < group.size(); i++)
	        {
	            String s = group.get(i).substring(0, 1).toUpperCase(Locale.getDefault());
	            if (!alphaIndexer.containsKey(s))
	                alphaIndexer.put(s, i);
	        }

	        Set<String> sectionLetters = alphaIndexer.keySet();
	        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
	        Collections.sort(sectionList);
	        sections = new String[sectionList.size()];
	        for (int i = 0; i < sectionList.size(); i++)
	            sections[i] = sectionList.get(i);   
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return listChild.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		_posChild=childPosition;
		_posGroup=groupPosition;
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	    String string = listChild.get(groupPosition).get(childPosition); 
	       
        //Log.v("LH","@getChildView: " + string);       
        View view = getGenericView(string); 
               
        TextView text = (TextView) view; 
       
        if(this._posGroup==groupPosition && 
                   this._posChild==childPosition)          
                { 
                    text.setTextColor(Color.WHITE); 
                    text.setBackgroundResource(R.drawable.bg_toolbar); 
                } 
       
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return listChild.get(groupPosition).size();
	}

	
	//gourp stubs
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return listGroup.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return listGroup.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 String string = listGroup.get(groupPosition);       
        //Log.v("LH","@getGroupView: " + string); 
        return getGenericView(string); 
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	 public TextView getGenericView(String s) { 
	        // Layout parameters for the ExpandableListView 
	        AbsListView.LayoutParams lp = new AbsListView.LayoutParams( 
	                ViewGroup.LayoutParams.MATCH_PARENT, 64); 

	        lp.height=50; 
	        TextView text = new TextView(activity); 
	        text.setLayoutParams(lp); 
	        // Center the text vertically 
	        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT); 
	        // Set the text starting position 
	        text.setPadding(80, 0, 0, 0); 
	        text.setTextColor(Color.BLACK);       
	        text.setText(s); 
	        text.setTextSize(20);       
	        return text; 
	    }
	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		return alphaIndexer.get(sections[section]);
	}
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return "A B C D F H I J K L M N O P R S T V Z".split(" ");
		
	} 
	
	
}
