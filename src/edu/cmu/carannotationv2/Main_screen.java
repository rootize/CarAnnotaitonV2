package edu.cmu.carannotationv2;

// also you can define alubmn yourself
//������1���
//������ ������nullpointer ���warning���imageview ��������������� ���������������onCreate���������������������������������������
//���������������
//TODO list:
// Making uploading things  to be services



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;

import com.parse.ParseException;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main_screen extends Activity implements
tk_img_frag.OnTkImgListener {
	/********************added 20131201*************/

	// files
	public static final String SENTFILEDIR_STRING="/CarAnnotationSentFiles";
	private MMdata mMdata;
	ParseCommunication pCommunication;
	/* ********************************************/


	// Used for Debugging
	private static final String MAINSTRING = "MainScreen";
	private static final String SAVELOGGININ_STRING = "loggedin";
	private static final String SAVEWIFI_STRING = "wifi";



	private int scaleFactor = 0;
	private Boolean screenRotationLocked;
	private static final int CAMERA_REQUEST = 1888;
	private static final int LOAD_IMG_FROM_G = 1666;


	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final String IMG_PATH_KEY = "imgpath";
	private static final String infoDir="InfoDir";

	private Bitmap mImageBitmap;
	private String mCurrentPhotoPath;
	private File imgFile;
	private String imageFileName = null;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	private boolean take_valide_img = true;

	// data encryption
	private EncryptedData ed;

	private TextView welcomeText;

	private TextView GuideText;
	private Button btn_takeimg;
	private Button btn_selectmm;
	private Button btn_send;
	private Button btn_save;
	private Button btn_upload;
	private DrawImageView mImageView;
	private TextView makemodelshowTextView;


	private ExpandableListView make_model_listView;
	private Dialog make_model_Dialog;
	private View viewlist;
	// private View viewListLastSelected;
	private int lastGroupPosition = -1;
	List<String> makeGroup = new ArrayList<String>();
	List<List<String>> makemodelGroup = new ArrayList<List<String>>();

	private boolean global_prevent_reDraw = false;
	private String selectedMake;
	private String selectedModel;
	private ProgressBar progressBar;
	private int gRectCount = 0;
	// **************************************************************//
	private File makeModelDataFile;
	private SQLiteDatabase carDatabase;

	private String usr_name;
	private boolean flag_isWifiConnected;
	private static boolean flag_isLoggedin = false;
	private AnnotatorInput annotatorInput;

	private JSONdata jsonData;
	private boolean isFirstTimeLogin = true;

	private LocationInfo locationInfo;

	// Popupwindow
	private PopupWindow mPopupWindow;

	//	private static final String offline_filename_bk = "offline_bk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.main_screen);




		// Change Screen Rotation to Enabled
		screenRotationLocked=Utils.isScreenLocked(getApplicationContext());
		// Location information 		
		locationInfo = new LocationInfo(getApplicationContext());


		mAlbumStorageDirFactory = GlobalFuns
				.setmAlbumStorageDirFactory();

		ed = new EncryptedData(getApplicationContext(), R.raw.key,
				R.raw.plaintext);

		Parse.initialize(this, ed.getCipherTextApplicationId(),
				ed.getCipherTextClientKey());
		ParseAnalytics.trackAppOpened(getIntent());
		pCommunication=new ParseCommunication(getApplicationContext(), ed);
		GuideText = (TextView) findViewById(R.id.mainscreen_textview_textguidance);
		flag_isWifiConnected = Utils.isWifi(getApplicationContext());
		if (flag_isWifiConnected) {
			pCommunication.loggingIn();
		}
		usr_name = getUsrFromIntent();

		//setting up core interfaces
		pre_initialize_mm_selection_dialog();
		setWelcomeword(usr_name, flag_isWifiConnected);
		initialize_mm_textView();
		initialize_btn_takeimg();
		initialize_mm_selection_dialog();
		initialize_btn_selectmm();
		initialize_btn_send();
		initialize_btn_save();
		initialize_drawImageView();
		initialize_progbar();
		initialize_btn_upload();


	}

	// Leave it alone
	private void showReminder() {

		File mydir = this.getDir(infoDir, Context.MODE_PRIVATE); //Creating an internal dir;

		if (mydir.listFiles().length>0&& flag_isWifiConnected && pCommunication.isLoggedin() ) {

			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle("Caution")
			.setMessage(
					"There are unuploaded images on your phone, please upload them when wifi are available.")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							new FileUploadThread(
									getApplicationContext(), ed).run();

						}
					}).setNegativeButton("Not now", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {


						}
					}).show();
		}

	}




	private void initialize_btn_upload() {
		boolean flag_uploadPre=false;
		btn_upload = (Button) findViewById(R.id.btn_upload);
		File saveFolder=new File(Environment.getExternalStorageDirectory().toString(), SENTFILEDIR_STRING);
		if (flag_isLoggedin && flag_isWifiConnected) {
			flag_uploadPre=true;
		}

		if (saveFolder.exists() &&  saveFolder.listFiles().length>0 && flag_uploadPre) {
			btn_upload.setVisibility(View.VISIBLE);
		}else {
			btn_upload.setVisibility(View.INVISIBLE);
		}

		btn_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Set image view to something like uploading
				mImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.uploading));
				btn_upload.setEnabled(false);
				check_upload_localData();


			}
		});

	}

	@Override
	protected void onStart() {

		super.onStart();
		Log.d(MAINSTRING, "onStartCalled");
		showReminder();
	}




	private void pre_initialize_mm_selection_dialog() {
		try {
			mMdata=new MMdata(getApplicationContext(), R.raw.makejson, R.raw.modeljson);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//		makeHashMap=mMdata.getMakeHashMap();
		//		modelHashMap=mMdata.getModelHashMap();
		//		modelDisplayHashMap=mMdata.getModelDisplayHashmap();
		makeGroup = mMdata.getMakeGroup();
		List<String> individual_model = new ArrayList<String>();
		for (int i = 0; i < makeGroup.size(); i++) {
			String temp_make = makeGroup.get(i);
			individual_model=mMdata.getSingleChildList(temp_make);
			makemodelGroup.add(individual_model);
			individual_model.clear();
		}

	}



	private void initialize_mm_selection_dialog() {
		viewlist = this.getLayoutInflater().inflate(R.layout.expandablelist,
				null);
		make_model_Dialog = new Dialog(Main_screen.this);
		make_model_Dialog.setContentView(viewlist);
		make_model_Dialog.setTitle("Select make and model");
		make_model_listView = (ExpandableListView) viewlist
				.findViewById(R.id.elvForDialog);
		make_model_listView
		.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

		make_model_listView.setFastScrollEnabled(true);
		MyExpandableListAdapter mAdapter = new MyExpandableListAdapter(this,
				makeGroup, makemodelGroup, make_model_listView);
		make_model_listView.setAdapter(mAdapter);
		make_model_listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) throws RuntimeException {


				if (makeGroup.get(groupPosition).toString()
						.equals("Unknown")) {

					selectedMake = mMdata.getMakeId("Unknown");
					selectedModel = mMdata.getModelId(selectedMake+"Unknown");
					make_model_Dialog.dismiss();
					makemodelshowTextView.setText("Unknown make"
							+ "  " + "Unknown model");
					return true;
				} else {

					if (parent.isGroupExpanded(groupPosition)) {
						parent.collapseGroup(groupPosition);
					} else {
						parent.expandGroup(groupPosition);
					}

					return true;
				}
			}
		});

		make_model_listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id)
							throws RuntimeException {
				if (lastGroupPosition != -1
						&& lastGroupPosition != groupPosition) {
					// make_model_listView.collapseGroup(lastGroupPosition) ;
					make_model_listView.collapseGroup(lastGroupPosition);
				}

				lastGroupPosition = groupPosition;

				selectedMake = mMdata.getMakeId(makeGroup.get(groupPosition).toString()) ;
				selectedModel = mMdata.getModelId(selectedMake+makemodelGroup.get(groupPosition)
						.get(childPosition).toString()); 
				makemodelshowTextView.setText(makeGroup.get(groupPosition)
						.toString()
						+ "  "
						+ makemodelGroup.get(groupPosition).get(childPosition)
						.toString());

				if (selectedMake.equals(database.NONE_EXISTING)) {
					selectedMake = "unknown";
					makemodelshowTextView.setText("I don't know the make"
							+ "  "
							+ makemodelGroup.get(groupPosition)
							.get(childPosition).toString());
				}
				if (selectedModel.equals(database.NONE_EXISTING_MODEL)) {
					selectedModel = "unknown";
					makemodelshowTextView.setText(makeGroup.get(groupPosition)
							.toString() + "  " + "I don't know the model");
				}

				// Close the dialog
				make_model_Dialog.dismiss();
				return false;
			}
		});

	}

	private void initialize_mm_textView() {
		makemodelshowTextView = (TextView) findViewById(R.id.mainscreen_makemodel_tv);
	}

	private void initialize_btn_selectmm() {
		// TODO Auto-generated method stub
		btn_selectmm = (Button) findViewById(R.id.btn_select_mm);
		btn_selectmm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (lastGroupPosition != -1) {
					make_model_listView.collapseGroup(lastGroupPosition);
				}
				make_model_Dialog.show();
				btn_save.setEnabled(true);
			}
		});
	}

	private String getUsrFromIntent() {
		return getIntent().getStringExtra(Login.USR_TOMAIN_INTENT);
	}

	private void setWelcomeword(String usrname, boolean isWifi) {
		String welcome_words_string;
		if (GlobalFuns.isEmailValid(usr_name)) {
			welcome_words_string = usr_name;
		} else {
			welcome_words_string = "Dear guest";
		}
		if (isWifi) {
			welcome_words_string += " (Online)";
		} else {
			welcome_words_string += " (Offline)";
		}

		welcomeText = (TextView) findViewById(R.id.mainscreen_welcome_text);
		welcomeText.setText(welcome_words_string);

	}

	private void initialize_progbar() {
		progressBar = (ProgressBar) findViewById(R.id.single_upload_progressbar);
		progressBar.setVisibility(View.INVISIBLE);
		setProgressBarIndeterminate(true);
		setProgressBarVisibility(false);
		setProgress(3500);
	}

	private void initialize_drawImageView() {

		mImageView = (DrawImageView) findViewById(R.id.imageView1);/* DrawImageView */
		mImageView.setVisibility(DrawImageView.VISIBLE);
		mImageView.setImageDrawable(getResources().getDrawable(
				R.drawable.firstlogin));
		global_prevent_reDraw = true; // indicator if one could draw on iamge
		mImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (!global_prevent_reDraw) {
					makemodelshowTextView.setText(" ");
					DrawImageView drawView = (DrawImageView) v;
					if (event.getAction() == MotionEvent.ACTION_DOWN) {

						drawView.setFix_x(event.getX());
						drawView.setFix_y(event.getY());

					} else {
						drawView.setSliding_x(event.getX());
						drawView.setSliding_y(event.getY());
						drawView.adjustCortex();
						btn_selectmm.setEnabled(true);
						btn_save.setEnabled(true);
						GuideText.setText("Press \" Make&Model\" to annotate");
					}
					drawView.invalidate();

				}

				return true;
			}
		});

	}

	private void initialize_btn_save() {
		btn_save = (Button) findViewById(R.id.btn_confirm);
		btn_save.setEnabled(false);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mImageView.addRect();
				annotatorInput.updateAnnotation(mImageView.getLastRect(), selectedMake,
						selectedModel);
				//				gRectCount = gRectCount + 1;
				global_prevent_reDraw = false;

				btn_save.setEnabled(false);
				btn_send.setEnabled(true);
				mImageView.invalidate();
				if (global_prevent_reDraw == true) {
					GuideText.setText("Press \"Done!\" to finish");
				} else {
					GuideText
					.setText("Press \"Done!\" if no other cars in the image");

				}
				// After each selection, make them null
				selectedMake = null;
				selectedModel = null;

			}
		});
	}

	private void initialize_btn_send() {
		btn_send = (Button) findViewById(R.id.button_send_server);
		// btn_send.setEnabled(false);
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (flag_isWifiConnected && flag_isLoggedin) {
					Log.d("Main Activity", "Come to this step");
					ParseObject pb_send=annotatorInput.export2ParseObject(ed.getCipherTextClassName());
					pb_send.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException arg0) {
							if (arg0 == null) {
								String send_success = "Send to server successfully!";
								GlobalFuns.ShowToast_short(
										getApplicationContext(), send_success,
										R.drawable.success);

							} else {

								String send_failuer = "Problem occured while sending, will save and try again next time";
								GlobalFuns.ShowToast_short(
										getApplicationContext(), send_failuer,
										R.drawable.error);
								annotatorInput.savetoOfflineFile(getApplicationContext());
								String showMessage = "Saved in Local machine, image will be uploaded when wifi available";
								GlobalFuns.ShowToast_short(
										getApplicationContext(), showMessage,
										R.drawable.success);
								mImageView.setImageDrawable(getResources()
										.getDrawable(R.drawable.buttonfinish));
							}
						}
					}
							);

				} else {

					annotatorInput.savetoOfflineFile(getApplicationContext());



					// showReminder();

				}
				gRectCount = 0;
				btn_send.setEnabled(false);
				btn_save.setEnabled(false);
				btn_selectmm.setEnabled(false);
				if (!flag_isWifiConnected) {
					mImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.buttonfinish));
				} else {
					mImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.processing));
				}

				mImageView.clearRecords();
				mImageView.clearrect();
				mImageView.invalidate();
				global_prevent_reDraw = true;

				GuideText
				.setText("Thanks, please press \"Take a Photo\" to take a new photo ");

			}

		});
	}

	private void initialize_btn_takeimg() {

		btn_takeimg = (Button) findViewById(R.id.button_take_new);
		btn_takeimg.setVisibility(View.VISIBLE);
		btn_takeimg.setEnabled(true);
		
		btn_takeimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				View popupView = getLayoutInflater().inflate(R.layout.popup,
						null);
				
				mPopupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						true);
				mPopupWindow
				.setAnimationStyle(android.R.style.Animation_Dialog);
				Rect btn_takeimg_location = GlobalFuns
						.locateView(btn_takeimg);
				if (btn_takeimg_location == null) {
					mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM
							| Gravity.LEFT, 0, 0);
				} else {
					mPopupWindow.showAtLocation(popupView, Gravity.LEFT
							| Gravity.TOP, btn_takeimg_location.left,
							btn_takeimg_location.bottom);
				}
// Get image form camera:
				Button btnfromCamera = (Button) popupView
						.findViewById(R.id.btntakefromcamera);
				btnfromCamera.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mImageView.prepareCamera();
						mPopupWindow.dismiss();
						dispathTakePictureIntent();

					}
				});
// Get image from Gallery
				Button btnfromGallery = (Button) popupView
						.findViewById(R.id.btntakefromgallery);
				btnfromGallery.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mImageView.prepareCamera();
						mPopupWindow.dismiss();
						dispatchSelectFromGalleryIntent();
						

					}

				});
// Cancel
				Button btnforcancel = (Button) popupView
						.findViewById(R.id.btncancellfrompopup);
				btnforcancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mPopupWindow.dismiss();

					}
				});

			}
		});

	}

	private void dispatchSelectFromGalleryIntent() {
		Intent toGallery = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(toGallery, LOAD_IMG_FROM_G);

	}

	private void dispathTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			ImageStorage imageStorage=new ImageStorage(getApplicationContext());
			mCurrentPhotoPath=imageStorage.getImagePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(imageStorage.getImageFile()));
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		startActivityForResult(takePictureIntent, CAMERA_REQUEST);
	}
	
	private void check_upload_localData() {
		Log.d("upload data", "called");
		new FileUploadThread(getApplicationContext(), ed).run();

	}



	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
				(mImageBitmap != null));
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putString(IMG_PATH_KEY, mCurrentPhotoPath);
		outState.putBoolean(SAVELOGGININ_STRING, flag_isLoggedin);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		mImageView
		.setVisibility(savedInstanceState
				.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE
						: ImageView.INVISIBLE);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);

		mImageView.setImageBitmap(mImageBitmap);
		flag_isLoggedin = savedInstanceState.getBoolean(SAVELOGGININ_STRING);
		flag_isWifiConnected = savedInstanceState.getBoolean(SAVEWIFI_STRING);
		mCurrentPhotoPath = savedInstanceState.getString(BITMAP_STORAGE_KEY);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	/* moved from sample: Photo album for this application */

	/* moved from sample */
	
	

	private int setScaleFactor(DrawImageView mImageView2,
			String mCurrentPhotoPath2) {
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();
		Log.d("width_target", "" + targetW);
		Log.d("height_target", "" + targetH);

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		Log.d("width", "" + photoW);
		Log.d("height", "" + photoH);
		int sf = 1;
		if (targetH > 0 || targetW > 0) {
			sf = Math.min(photoW / targetW, photoH / targetH);

		}

		return sf;
	}

	private void setPic() {
		if (scaleFactor == 0) {
			scaleFactor = setScaleFactor(mImageView, mCurrentPhotoPath);
		}
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		} catch (Exception e) {
			System.gc();
			bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		}

		mImageBitmap = Bitmap.createScaledBitmap(bitmap, mImageView.getWidth(),
				mImageView.getHeight(), false);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(View.VISIBLE);
		bitmap.recycle();
	}

	// don't know if we need this!
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		// File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(imgFile);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}



	private void handleBigCameraPhoto() {
		if (mCurrentPhotoPath != null) {

			setPic();
			galleryAddPic();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("OnActivityResult", "2");
		boolean loadLocationInfo=false;
		if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
			if (mImageBitmap != null) {
				mImageBitmap.recycle();
				mImageBitmap = null;
				System.gc();
			}
			handleBigCameraPhoto();

			GuideText.setText("Swipe to draw image:");
			makemodelshowTextView.setText("");
			take_valide_img = true;
			loadLocationInfo=true;
		}
		if (resultCode == RESULT_CANCELED
				&& (requestCode == CAMERA_REQUEST || requestCode == LOAD_IMG_FROM_G)) {
			mImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.ready));
			take_valide_img = false;
		}
		if (resultCode == RESULT_OK && requestCode == LOAD_IMG_FROM_G) {
			if (mImageBitmap != null) {
				mImageBitmap.recycle();
				mImageBitmap = null;
				System.gc();
			}
			Uri selectedImgUri = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImgUri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			mCurrentPhotoPath = cursor.getString(columnIndex);
			cursor.close();
			setPic();
			take_valide_img = true;
			loadLocationInfo=true;
		}

		if (loadLocationInfo) {
			annotatorInput.updateLocationInfo(getApplicationContext());
			annotatorInput.setImageInfo(mCurrentPhotoPath);
			if (!annotatorInput.scaleRatioDefined()) {
				annotatorInput.addScaleRatio(new ScaleRatio(mImageView, mCurrentPhotoPath));
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		carDatabase.close();
		if (screenRotationLocked == false) {
			GlobalFuns.setAutoOrientationEnabled(
					getContentResolver(), false);
		}
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onResume() {
	
		btn_selectmm.setEnabled(false);
		btn_save.setEnabled(false);
		btn_send.setEnabled(false);
		btn_takeimg.setEnabled(true);
		if (isFirstTimeLogin) {
			global_prevent_reDraw = true;
			isFirstTimeLogin = false;

		} else {
			if (take_valide_img) {
				global_prevent_reDraw = false;
			} else {
				global_prevent_reDraw = true;
			}
		}

		annotatorInput = new AnnotatorInput(usr_name);	
		annotatorInput.setWifiStatus(flag_isWifiConnected);

		super.onResume();

		Log.d("OnResume", "1");
	}

	@Override
	protected void onPause() {
		if (null != mImageView) {
			mImageView.clearrect();// 
			mImageView.clearRecords();
			mImageView.setImageResource(0);
		}
		take_valide_img = false;
		super.onPause();
	}

	class FileSavingThread extends Thread {
		private String content;
		private String fileName;

		public FileSavingThread(String fileName, String content) {
			this.fileName = fileName;
			this.content = content;
		}

		@Override
		public void run() {
			FileOperation.save(getApplicationContext(), fileName, content);
		}
	}

	public void showDialog(String showString) {

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Warning");
		alertBuilder.setMessage(showString);
		alertBuilder.setIcon(R.drawable.caution);

		alertBuilder.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dispathTakePictureIntent();

			}
		});

		alertBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertBuilder.setCancelable(true);
		AlertDialog warningDialog = alertBuilder.create();
		warningDialog.show();

	}

	@Override
	public void onTkImg(Boolean s) {

		if (s) {
			dispathTakePictureIntent();
		}
	}



	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

}
