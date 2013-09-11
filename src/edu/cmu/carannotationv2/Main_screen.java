package edu.cmu.carannotationv2;

// also you can define alubmn yourself
//注释1：
//原先 出现nullpointer 的warning让imageview 无法实现， 调换了一下onCreate中的各个函数的顺序，就好了
//不知道原因
import java.io.BufferedReader;

import java.io.File;

import java.io.FileReader;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
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

	private int scaleFactor=0;
	private Boolean isScreenRotationLocked;
	private static final int CAMERA_REQUEST = 1888;
	private static final String offline_filename = "offline";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final String IMG_PATH_KEY = "imgpath";
	private Bitmap mImageBitmap;
	private String mCurrentPhotoPath;
	private File imgFile;
	private String imageFileName = null;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	// data encryption
	private EncryptedData ed;

	private TextView welcomeText;
	// private String welcome_name = "";
	private TextView GuideText;
	private Button btn_takeimg;
	private Button btn_selectmm;
	private Button btn_send;
	private Button btn_save;
	private DrawImageView mImageView;
	private TextView makemodelshowTextView;
	// 为了弹出的make model

	private ExpandableListView make_model_listView;
	private Dialog make_model_Dialog;
	private View viewlist;
	private View viewListLastSelected;
	private int lastGroupPosition=-1;
	List<String> makeGroup = new ArrayList<String>();
	List<List<String>> makemodelGroup = new ArrayList<List<String>>();

	/*
	 * private Spinner makeSpinner; private Spinner modelSpinner;
	 */
//	private ArrayAdapter<String> makeAdapter;
//	private ArrayAdapter<String> modelAdapter;
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

	private AnnotatorInput annotatorInput;
	private JSONdata jsonData;
	private boolean isFirstTimeLogin = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main_screen);

		// Change Screen Rotation to Enabled
		if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION,0)==1) {
			isScreenRotationLocked=true;
		}else {
			static_global_functions.setAutoOrientationEnabled(getContentResolver(),
					true);
			isScreenRotationLocked=false;
		}
		
		mAlbumStorageDirFactory = static_global_functions
				.setmAlbumStorageDirFactory();
		wifi_connected = static_global_functions
				.wifi_connection(getApplicationContext());

		ed = new EncryptedData(getApplicationContext(), R.raw.key,
				R.raw.plaintext);
		Parse.initialize(this, ed.getCipherTextApplicationId(),
				ed.getCipherTextClientKey());
		ParseAnalytics.trackAppOpened(getIntent());

		
		
		GuideText = (TextView) findViewById(R.id.mainscreen_textview_textguidance);
		if (wifi_connected) {
			login_global_usr();
			check_upload_localData();
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

		makeGroup = getLabels(database.SELECT_MAKE);
		for (int i = 0; i < makeGroup.size(); i++) {
			String temp_make = makeGroup.get(i);
			List<String> individual_model = new ArrayList<String>();
			individual_model = getLabels(database.SELECT_ONE_MODEL_PREFIX
					+ temp_make + database.SELECT_ONE_MODEL_SUFFIX);
			removeMake(individual_model,temp_make);
			makemodelGroup.add(individual_model);
		}

	}

	private void removeMake(List<String> individual_model,String temp_make) {
		String temp_model;
		String new_model="";
		for (int i = 0; i < individual_model.size(); i++) {
			temp_model=individual_model.get(i);
			
			String[] splited_temp=temp_model.split("\\s+");
	
			
			if (splited_temp[1].equalsIgnoreCase(temp_make)&& splited_temp.length>2) {
				for (int j = 2; j < splited_temp.length; j++) {
					new_model=new_model +splited_temp[j];
				}
				
			}else {
				new_model=temp_model;
			}
			individual_model.set(i, new_model);
			new_model="";
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
        make_model_listView.setFastScrollEnabled(true);
		MyExpandableListAdapter mAdapter = new MyExpandableListAdapter(this,
				makeGroup, makemodelGroup);
		make_model_listView.setAdapter(mAdapter);
		make_model_listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) throws RuntimeException {

				try {
					// 将上一个选中项的背景清空
					// viewListLastSelected.setBackgroundDrawable(null);
					/*Log.v("LH", "@setOnGroupClickListener");
					Log.v("LH", "" + viewListLastSelected.toString());
					Log.v("LH",
							"" + ((TextView) viewListLastSelected).getText());*/
					// viewListLastSelected.setBackgroundResource(R.drawable.bg_toolbar);
					// ((TextView)viewListLastSelected).setTextColor(Color.BLUE);
					// Generic.selectedTextViewID =
					// ((TextView)viewListLastSelected).getId();

					lastGroupPosition=groupPosition;
				} catch (Exception e) {
					Log.v("LH", "ERROR@onCreate: " + e.toString());
				}
				return false;
			}
		});

		make_model_listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id)
					throws RuntimeException {
			//	make_model_listView.clearChildFocus(viewListLastSelected);

			/*	try {
					// 将上一个选中项的背景清空
					//Log.v("LH", "@setOnChildClickListener");
					// viewListLastSelected.setBackgroundDrawable(null);
					((TextView) viewListLastSelected).setTextColor(Color.BLACK);
					// elvForDialog.getSelectedView().setBackgroundDrawable(null);
				} catch (Exception e) {
				}*/

		/*		if (viewListLastSelected!=null) {
					v.setBackgroundResource(R.drawable.bg_toolbar);
					((TextView) viewListLastSelected).setTextColor(Color.WHITE);

					// 缓存当前选中项至上一个选中项
					viewListLastSelected = v;
				}
				
			
*/
				// Put value to main UI's control
				selectedMake = makeGroup.get(groupPosition).toString();
				selectedModel = makemodelGroup.get(groupPosition)
						.get(childPosition).toString();
				makemodelshowTextView.setText(makeGroup.get(groupPosition)
						.toString()
						+ " & "
						+ makemodelGroup.get(groupPosition).get(childPosition)
								.toString());

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
				if (lastGroupPosition!=-1) {
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
		welcomeText.setText( welcome_words_string);

	}

	private void initialize_progbar() {
		progressBar = (ProgressBar) findViewById(R.id.login_bkuploading_progressbar);

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

					DrawImageView drawView = (DrawImageView) v;
					if (event.getAction() == MotionEvent.ACTION_DOWN) {

						drawView.setFix_x(event.getX());

						drawView.setFix_y(event.getY());

					} else {
						drawView.setSliding_x(event.getX());
						drawView.setSliding_y(event.getY());
						drawView.adjustCortex();
                        btn_selectmm.setEnabled(true);
				        //btn_save.setEnabled(false);
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

				if (wifi_connected) {
					// check parse usr:
					// login_global_usr();
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
						}
					});
					// FIXME
					// 1. delete all files
					// 2. draw a new image---please take a new one!

				} else {
					try {
						JSONArray old_offlineJsonArray;
						String temp = FileOperation.read(
								getApplicationContext(), offline_filename);
						if (temp == null) {
							old_offlineJsonArray = new JSONArray();
						} else {

							old_offlineJsonArray = new JSONArray(temp);

						}

						JSONObject toSend_item = jsonData.getJsonObject();
						old_offlineJsonArray.put(toSend_item);
						// Using Thread?
						FileOperation.save(getApplicationContext(),
								offline_filename,
								old_offlineJsonArray.toString());
						String showMessage = "Saved in Local machine, image will be uploaded when wifi available";
						static_global_functions.ShowToast_short(
								getApplicationContext(), showMessage,
								R.drawable.success);
					} catch (JSONException e) {

						e.printStackTrace();
					}
				}
				gRectCount = 0;
				btn_send.setEnabled(false);
				btn_save.setEnabled(false);
				btn_selectmm.setEnabled(false);
				mImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.buttonfinish));
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

				mImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.buttonfinish));
				mImageView.invalidate();

				if (gRectCount != 0) {
					String showMessage = "Abondon all the rectangles and start a new one?";
					showDialog(showMessage);
				} else {
					dispathTakePictureIntent();
				}

			}
		});

	}

	private void check_upload_localData() {

		new UploadFileThread().start();
	}

	private List<String> getLabels(String select_query ) {
		// TODO Auto-generated method stub
		//String DataBaseQuery=
		List<String> labelsList = new ArrayList<String>();
		Cursor cursor = carDatabase.rawQuery(select_query, null);
		if (cursor.moveToFirst()) {
			labelsList.add(cursor.getString(0));

			while (cursor.moveToNext()) {

				labelsList.add(cursor.getString(0));
			}
		}
		cursor.close();
		labelsList.add(new String("Z"+database.NONE_EXISTING));
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
		// if (mImageView!=null) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		// }

		outState.putString(IMG_PATH_KEY, mCurrentPhotoPath);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		mImageView
				.setVisibility(savedInstanceState
						.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE
						: ImageView.INVISIBLE);
		// if (savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY))
		// {
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);

		mImageView.setImageBitmap(mImageBitmap);
		// }

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
	if (scaleFactor==0) {
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

		if (mImageBitmap != null) {
			mImageBitmap.recycle();
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

		if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
			Log.d("OnActivityResult", "Called!");
			handleBigCameraPhoto();
			Log.d("OnActivityResult", "2");
			GuideText
					.setText("Please draw a rectangle that covers the (first)car in the photo");

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		carDatabase.close();
		if (isScreenRotationLocked==false) {
			static_global_functions.setAutoOrientationEnabled(getContentResolver(), false);
		}
		super.onDestroy();
		
	}

	@Override
	protected void onResume() {

		gRectCount = 0;
		btn_selectmm.setEnabled(false);
		btn_save.setEnabled(false);
		btn_send.setEnabled(false);
		btn_takeimg.setEnabled(true);
		if (isFirstTimeLogin) {
			global_prevent_reDraw=true;
			isFirstTimeLogin=false;
		}else {
			global_prevent_reDraw = false;
		}
		
		annotatorInput = new AnnotatorInput();
		annotatorInput.addUsr(usr_name);
		super.onResume();
		Log.d("OnResume", "1");
	}

	@Override
	protected void onPause() {
		if (null != mImageView) {
			mImageView.clearrect();// 去除留下的rect
			mImageView.clearRecords();
			mImageView.setImageResource(0);
		}
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

	class UploadFileThread extends Thread {
		public UploadFileThread() {
		}

		@Override
		public void run() {

			String offline_string = FileOperation.read(getApplicationContext(),
					offline_filename);
			if (offline_string == null) {
				// do nothing
			} else {
				try {
					offline_JsonArray = new JSONArray(offline_string);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				int item_num = offline_JsonArray.length();

				if (item_num > 0) {

					JSONObject tem_item;
					try {
						tem_item = (JSONObject) offline_JsonArray.get(0);
						ParseObject pobject = new JSONdata(tem_item)
								.formatParseObject(ed.getCipherTextClassName());
						pobject.saveInBackground(new SaveCallback() {

							@Override
							public void done(ParseException arg0) {

								offline_JsonArray = static_global_functions
										.remove(0, offline_JsonArray);
								recursive_upload();
							}

						});
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			}

		}

	}

	private void recursive_upload() {
		if (offline_JsonArray.length() > 0) {

			JSONObject tem_item;
			try {
				tem_item = (JSONObject) offline_JsonArray.get(0);

				// ParseObject pobject = new ParseObject("annotation_info");
				// pobject.put("usr", tem_item.getString("usr"));
				// static_global_functions.transfer_Json_Pobject(pobject,
				// tem_item/*,lati,longti*/);
				ParseObject pobject = new JSONdata(tem_item)
						.formatParseObject(ed.getCipherTextClassName());
				pobject.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {

						offline_JsonArray = static_global_functions.remove(0,
								offline_JsonArray);
						recursive_upload();
					}

				});
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("Main_Screen", "JSON array null");
			}

		} else {
			FileOperation.delete(getApplicationContext(), offline_filename);
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
						// if (mImageBitmap!=null) {
						// mImageBitmap.recycle();
						// }

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
						} else {
							Log.d("error logging in ", e.toString());

						}

					}
				});

	}
	
	
	
}// Ending of whole class!

