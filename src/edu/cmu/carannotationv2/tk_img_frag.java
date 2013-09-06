package edu.cmu.carannotationv2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
//http://bradipao.blogspot.com/2012/03/fragment-tutorial-part-2.html
public class tk_img_frag extends Fragment {
	private Button tkImgButton;
    private OnTkImgListener listener;
	
	@Override
	public void onAttach(Activity activity) {
	
		super.onAttach(activity);
		try {
			listener=(OnTkImgListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement XXX");
		}
	}

	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}




	


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tkimg_frag, container, false);
		tkImgButton = (Button) view.findViewById(R.id.btn_tkimg_frag);
		tkImgButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				communicateMainScreen();

			}

		});

		return view;
	}


	private void communicateMainScreen() {
          listener.onTkImg(true);
	}

	public interface OnTkImgListener{
		public void onTkImg(Boolean	 s);
	}
}
