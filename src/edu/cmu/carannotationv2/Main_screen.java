package edu.cmu.carannotationv2;

// also you can define alubmn yourself
//注释1：
//原先 出现nullpointer 的warning让imageview 无法实现， 调换了一下onCreate中的各个函数的顺序，就好了
//不知道原因
//TODO list:
// Making uploading things  to be services

import java.io.BufferedReader;

import java.io.File;

import java.io.FileReader;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import java.util.List;

import android.R.integer;
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
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
 private static final String SENTFILEDIR_STRING="/CarAnnotationSentFiles";
/* ********************************************/
	public String locationinfo_string;
	private LocationManager locationMangaer = null;
	private LocationListener locationListener = null;
	private final static String INFOFOLDER_STRING = "data_info";
	// Used for Debugging
	private static final String MAINSTRING = "MainScreen";

	private static final String SAVELOGGININ_STRING = "loggedin";
	private static final String SAVEWIFI_STRING = "wifi";

	
	
	private int scaleFactor = 0;
	private Boolean isScreenRotationLocked;
	private static final int CAMERA_REQUEST = 1888;
	private static final int LOAD_IMG_FROM_G = 1666;

	private static final String offline_filename = "offline";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final String IMG_PATH_KEY = "imgpath";
	private static final String infoDir="InfoDir";
	private int file_NO;
	private Editor file_no_recoderEditor;
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
	// 为了弹出的make model

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
	private ProgressDialog pd_uploadingDialog;
	private int gRectCount = 0;
	// **************************************************************//
	private File makeModelDataFile;
	private SQLiteDatabase carDatabase;

	private String usr_name;
	private JSONArray offline_JsonArray;
	private boolean wifi_connected;
	private boolean isLoggedin = false;
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

	/*	if (fileExistance(offline_filename_bk)) {
			File oldFile = this.getFileStreamPath(offline_filename_bk);
			File newFile = this.getFileStreamPath(offline_filename);
			oldFile.renameTo(newFile);
		}*/

		
		
		Log.d(MAINSTRING, "onCreateCalled");
		// Change Screen Rotation to Enabled
		if (android.provider.Settings.System.getInt(getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
			isScreenRotationLocked = false;
		} else {
			static_global_functions.setAutoOrientationEnabled(
					getContentResolver(), true);
			isScreenRotationLocked = true;
		}

		locationInfo = new LocationInfo(getApplicationContext());
		locationinfo_string = locationInfo.getLoation();

		mAlbumStorageDirFactory = static_global_functions
				.setmAlbumStorageDirFactory();

		ed = new EncryptedData(getApplicationContext(), R.raw.key,
				R.raw.plaintext);
		Parse.initialize(this, ed.getCipherTextApplicationId(),
				ed.getCipherTextClientKey());
		ParseAnalytics.trackAppOpened(getIntent());

		GuideText = (TextView) findViewById(R.id.mainscreen_textview_textguidance);
		wifi_connected = static_global_functions
				.wifi_connection(getApplicationContext());
		if (wifi_connected) {
			login_global_usr();
		}
		pre_initialize_mm_selection_dialog();
		usr_name = getUsrFromIntent();
		setWelcomeword(usr_name, wifi_connected);
		initialize_mm_textView();
		initialize_btn_takeimg();
		initialize_mm_selection_dialog();
		initialize_btn_selectmm();
		initialize_btn_send();
		initialize_btn_save();
		initialize_drawImageView();
		initialize_progbar();
		initialize_btn_upload();
		//later set move them to file operation system
		File mydir = this.getDir(infoDir, Context.MODE_PRIVATE); //Creating an internal dir;
		if(!mydir.exists())
		{
			file_NO=0;
			/*file_no_recoderEditor=PreferenceManager.getDefaultSharedPreferences(this).edit();
			file_no_recoderEditor.putInt("file_number", file_NO);
			file_no_recoderEditor.commit();*/
		     mydir.mkdirs();
		}else {
			file_NO = mydir.listFiles().length;
//		file_NO=PreferenceManager.getDefaultSharedPreferences(this).getInt("file_number", 0);
		}
		
		
		
	}

	private int getFilecounts(File mydir){
//		File mydir=this.getDir(infoDir, Context.MODE_PRIVATE);
		if (mydir.exists()) {
			return mydir.listFiles().length;
		}else {
			return -1;
		}
	}
	
	public boolean fileExistance(String fname) {
		File file = getBaseContext().getFileStreamPath(fname);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private void showReminder() {
          
		File mydir = this.getDir(infoDir, Context.MODE_PRIVATE); //Creating an internal dir;
		
		if (mydir.listFiles().length>0&& wifi_connected ) {
			login_global_usr();
			Log.d("Main", "satisfies");
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
//	total_number = mydir.listFiles().length;
	private void initialize_btn_upload() {
		btn_upload = (Button) findViewById(R.id.btn_upload);
		File saveFolder=new File(Environment.getExternalStorageDirectory().toString(), SENTFILEDIR_STRING);
		if (saveFolder.exists() &&  saveFolder.listFiles().length>0 ) {
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
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(MAINSTRING, "onStartCalled");
		showReminder();
	}

	private void SetBtnUploading() {

		if (fileExistance(offline_filename) && wifi_connected) {
			login_global_usr();
			btn_upload.setVisibility(View.VISIBLE);
			btn_upload.setEnabled(true);
		} else {
			btn_upload.setVisibility(View.INVISIBLE);
		}
	}

	private void pre_initialize_mm_selection_dialog() {

		try {
			makeModelDataFile = FileOperation.transferRawtoFile(
					getApplicationContext(), R.raw.car_make_model_revised,
					"makemodel", "car.csv");
			FormDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		makeGroup = getLabels(database.SELECT_MAKE, true);
		for (int i = 0; i < makeGroup.size(); i++) {
			String temp_make = makeGroup.get(i);
			List<String> individual_model = new ArrayList<String>();
			individual_model = getLabels(database.SELECT_ONE_MODEL_PREFIX
					+ temp_make + database.SELECT_ONE_MODEL_SUFFIX, false);
			removeMake(individual_model, temp_make);
			makemodelGroup.add(individual_model);
		}

	}

	private void removeMake(List<String> individual_model, String temp_make) {
		String temp_model;
		String new_model = "";
		for (int i = 0; i < individual_model.size(); i++) {
			temp_model = individual_model.get(i);

			String[] splited_temp = temp_model.split("\\s+");

			if (splited_temp.length > 2
					&& splited_temp[1].equalsIgnoreCase(temp_make)) {
				for (int j = 2; j < splited_temp.length; j++) {
					new_model = new_model + splited_temp[j];
				}

			} else {
				new_model = temp_model;
			}
			individual_model.set(i, new_model);
			new_model = "";
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

				// try {
				// /*if (lastGroupPosition!=-1 &&
				// lastGroupPosition!=groupPosition) {
				// make_model_listView.collapseGroup(lastGroupPosition);
				// }
				//
				// lastGroupPosition=groupPosition;*/
				// } catch (Exception e) {
				// Log.v("LH", "ERROR@onCreate: " + e.toString());
				// }
				// return false;

				if (makeGroup.get(groupPosition).toString()
						.equals(database.NONE_EXISTING)) {

					selectedMake = "unknown";
					selectedModel = "unknown";
					make_model_Dialog.dismiss();
					makemodelshowTextView.setText("I don't know the make"
							+ "  " + "I don't know the model");
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

				selectedMake = makeGroup.get(groupPosition).toString();
				selectedModel = makemodelGroup.get(groupPosition)
						.get(childPosition).toString();
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
		if (static_global_functions.isEmailValid(usr_name)) {
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
				annotatorInput.update(mImageView.getLastRect(), selectedMake,
						selectedModel);
				gRectCount = gRectCount + 1;
				global_prevent_reDraw = false;

				if (gRectCount == 5) {
					global_prevent_reDraw = true;
					String showMessage = "You have annotated 5 cars,click Done! ";
					static_global_functions.ShowToast_short(
							getApplicationContext(), showMessage,
							R.drawable.caution);
				}

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

				// setup a uploading dialog
				annotatorInput.addPath(mCurrentPhotoPath);
				annotatorInput.addImgName(imageFileName + JPEG_FILE_SUFFIX);
				annotatorInput.addScaleRatio(new ScaleRatio(mImageView,
						mCurrentPhotoPath));
				wifi_connected = static_global_functions
						.wifi_connection(getApplicationContext());

				annotatorInput.addWifiStatus(wifi_connected);
				jsonData = new JSONdata(annotatorInput, getApplicationContext());

				if (wifi_connected && isLoggedin) {
					Log.d("Main Activity", "Come to this step");
					ParseObject pb_send = jsonData.formatParseObject(ed
							.getCipherTextClassName());
					pb_send.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException arg0) {
							if (arg0 == null) {
								String send_success = "Send to server successfully!";
								static_global_functions.ShowToast_short(
										getApplicationContext(), send_success,
										R.drawable.success);

							} else {

								String send_failuer = "Problem occured while sending, will save and try again next time";
								static_global_functions.ShowToast_short(
										getApplicationContext(), send_failuer,
										R.drawable.error);
								try {
									JSONArray old_offlineJsonArray;
									String temp = FileOperation.read(
											getApplicationContext(),
											offline_filename);
									if (temp == null) {
										old_offlineJsonArray = new JSONArray();
									} else {

										old_offlineJsonArray = new JSONArray(
												temp);

									}

									JSONObject toSend_item = jsonData
											.getJsonObject();
									old_offlineJsonArray.put(toSend_item);
									// Using Thread?
									FileOperation.save(getApplicationContext(),
											offline_filename,
											old_offlineJsonArray.toString());
									String showMessage = "Saved in Local machine, image will be uploaded when wifi available";
									static_global_functions.ShowToast_short(
											getApplicationContext(),
											showMessage, R.drawable.success);
								} catch (JSONException e) {

									e.printStackTrace();
								}
							}
							mImageView.setImageDrawable(getResources()
									.getDrawable(R.drawable.buttonfinish));
						}
					});

				} else {
			
					
					
					
					JSONObject toSend_item = jsonData.getJsonObject();
					
					File saveFolder=new File(Environment.getExternalStorageDirectory().toString(), SENTFILEDIR_STRING);
					if (!saveFolder.exists()) {
						if (!saveFolder.mkdirs()) {
							Log.e("MainScreen", "Cannot create folder saving sent files");
				
						}else {
							Log.d("MainScreen", saveFolder.toString());
                          sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(saveFolder)));
						}
					}
					
					String tempFile=util.setFileNamebyDate();
					Log.d("Filename", tempFile);
//					
//					String tempFile=String.format("%05d", getFilecounts(saveFolder));
//					tempFile=tempFile+calendar.get(Calendar.DAY_OF_MONTH)+calendar.get(Calendar.HOUR_OF_DAY)+calendar.get(Calendar.MINUTE)+calendar.get(Calendar.SECOND);
//					File fileDir=new File(getFilesDir(),infoDir);
					
					FileOperation.saveCostomizedDir(getApplicationContext(),
							tempFile,saveFolder,
							toSend_item.toString());
					 sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse(saveFolder.toString()+tempFile)));
					String showMessage = "Saved in Local machine, image will be uploaded when wifi available";
					static_global_functions.ShowToast_short(
							getApplicationContext(), showMessage,
							R.drawable.success);
					// showReminder();

				}
				gRectCount = 0;
				btn_send.setEnabled(false);
				btn_save.setEnabled(false);
				btn_selectmm.setEnabled(false);
				if (!wifi_connected) {
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
		btn_takeimg.setEnabled(true);

		btn_takeimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				View popupView = getLayoutInflater().inflate(R.layout.popup,
						null);
				/* mPopupWindow=new PopupWindow(popupView, , 100, , true); */
				mPopupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						true);
				mPopupWindow
						.setAnimationStyle(android.R.style.Animation_Dialog);
				Rect btn_takeimg_location = static_global_functions
						.locateView(btn_takeimg);
				if (btn_takeimg_location == null) {
					mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM
							| Gravity.LEFT, 0, 0);
				} else {
					mPopupWindow.showAtLocation(popupView, Gravity.LEFT
							| Gravity.TOP, btn_takeimg_location.left,
							btn_takeimg_location.bottom);
				}

				Button btnfromCamera = (Button) popupView
						.findViewById(R.id.btntakefromcamera);
				if (btnfromCamera == null) {
					Log.d(MAINSTRING, "btn is null");
				}
				btnfromCamera.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mImageView.clearrect();
						mImageView.setImageDrawable(getResources().getDrawable(
								R.drawable.processing));
						mImageView.invalidate();

						if (gRectCount != 0) {
							String showMessage = "Abondon all the rectangles and start a new one?";
							showDialog(showMessage);
						} else {
							dispathTakePictureIntent();
							mPopupWindow.dismiss();
						}

					}
				});

				Button btnfromGallery = (Button) popupView
						.findViewById(R.id.btntakefromgallery);
				btnfromGallery.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mImageView.clearrect();
						mImageView.setImageDrawable(getResources().getDrawable(
								R.drawable.processing));
						mImageView.invalidate();

						dispatchSelectFromGalleryIntent();
						mPopupWindow.dismiss();

					}

				});

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

	private void check_upload_localData() {
		Log.d("upload data", "called");
		new FileUploadThread(getApplicationContext(), ed).run();

	}

	private List<String> getLabels(String select_query, boolean isMake) {
		// TODO Auto-generated method stub
		// String DataBaseQuery=
		List<String> labelsList = new ArrayList<String>();
		Cursor cursor = carDatabase.rawQuery(select_query, null);
		if (cursor.moveToFirst()) {
			labelsList.add(cursor.getString(0));

			while (cursor.moveToNext()) {

				labelsList.add(cursor.getString(0));
			}
		}
		cursor.close();
		if (isMake) {
			labelsList.add(new String(database.NONE_EXISTING));
		} else {
			labelsList.add(new String(database.NONE_EXISTING_MODEL));
		}

		return labelsList;

	}

	private void FormDatabase() throws SQLException, IOException {
		// FIXME Auto-generated method stub

		// 2.3.2.1 Create database
		carDatabase = openOrCreateDatabase(database.DATABASE_NAME,
				Context.MODE_PRIVATE, null);
		carDatabase.execSQL(database.CREATE_TABLE1);
		carDatabase.execSQL(database.CREATE_TABLE2);
		// 2.3.2.2 insert data
		FileReader databaseFileReader = new FileReader(
				makeModelDataFile.getAbsolutePath());
		BufferedReader databaseBufferedReader = new BufferedReader(
				databaseFileReader);
		String line = "";

		carDatabase.beginTransaction();

		while ((line = databaseBufferedReader.readLine()) != null) {
			String[] all_elements = line.split(",");

			String[] make_model_String = new String[2];
			for (int i = 0; i < all_elements.length; i++) {
				StringBuilder sb_insert = new StringBuilder(
						database.INSERT_ITEM_PREFIX);
				make_model_String = all_elements[i].split(":");
				if (2 == make_model_String.length) {
					// Log.d(""+i, "  "+make_model_String.length);
					sb_insert.append("'" + make_model_String[0] + "',");
					sb_insert.append(" '" + make_model_String[1] + "'");
					sb_insert.append(database.INSERT_ITEM_SUFFIX);
					carDatabase.execSQL(sb_insert.toString());
				} else {
					Log.d("" + i, "" + make_model_String[0]);
				}

			}

		}
		databaseBufferedReader.close();
		carDatabase.setTransactionSuccessful();
		carDatabase.endTransaction();

		Log.d("Done DATABASE ", "Initialization");
	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
				(mImageBitmap != null));
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putString(IMG_PATH_KEY, mCurrentPhotoPath);
		outState.putBoolean(SAVELOGGININ_STRING, isLoggedin);
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
		isLoggedin = savedInstanceState.getBoolean(SAVELOGGININ_STRING);
		wifi_connected = savedInstanceState.getBoolean(SAVEWIFI_STRING);
		mCurrentPhotoPath = savedInstanceState.getString(BITMAP_STORAGE_KEY);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	/* moved from sample: Photo album for this application */
	private String getAlbumName() {

		// TODO 稍后再改动名称
		return getString(R.string.album_name);

	}

	/* moved from sample */
	private File getAlbumDir() {
		File storageDir = null;
		Log.d("GetAlbumdir", "Called!");
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("StorageProblem", "Failed to create Dir");
						return null;
					}
				}
			}
		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE");
			Log.d("Mount", "Problem");
		}
		return storageDir;
	}

	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		imageFileName = JPEG_FILE_PREFIX + timeStamp;
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName + "_",
				JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}

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

	private void dispathTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imgFile = null;
		mCurrentPhotoPath = null;
		try {
			imgFile = setUpPhotoFile();
			mCurrentPhotoPath = imgFile.getAbsolutePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(imgFile));
		} catch (Exception e) {
			e.printStackTrace();
			imgFile = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, CAMERA_REQUEST);
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
		if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
			Log.d("OnActivityResult", "Called!");
			if (mImageBitmap != null) {
				mImageBitmap.recycle();
				mImageBitmap = null;
				System.gc();
			}
			handleBigCameraPhoto();

			GuideText.setText("Swipe to draw image:");
			makemodelshowTextView.setText("");
			take_valide_img = true;
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
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		carDatabase.close();
		if (isScreenRotationLocked == false) {
			static_global_functions.setAutoOrientationEnabled(
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
		if (static_global_functions.wifi_connection(getApplicationContext())) {
			login_global_usr();
			Log.d("MainScreen", "connect and login");
		}

		gRectCount = 0;
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

		annotatorInput = new AnnotatorInput();
		annotatorInput.addUsr(usr_name);
		annotatorInput.setLocationinfo(locationinfo_string);
		super.onResume();
		// if (isLoggedin&&) {
		// btn_upload.setEnabled(true);
		// }
		Log.d("OnResume", "1");
	}

	@Override
	protected void onPause() {
		if (null != mImageView) {
			mImageView.clearrect();// 去除留下的rect
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

	private void login_global_usr() {
		ParseUser.logInInBackground(ed.getCipherTextUserName(),
				ed.getCipherTextUserPassword(), new LogInCallback() {

					@Override
					public void done(ParseUser usr, ParseException e) {
						if (usr != null) {
							Log.d("Login_before", "Successfully");
							isLoggedin = true;
						} else {
							Log.d("error logging in ", e.toString());
							isLoggedin = false;
							static_global_functions
									.ShowToast_short(
											getApplicationContext(),
											"Remote server not responding, save to local memory",
											R.drawable.caution);
						}

					}
				});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
	}

}
