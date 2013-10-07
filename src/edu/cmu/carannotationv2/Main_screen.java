package edu.cmu.carannotationv2;

// also you can define alubmn yourself
//������1���
//������ ������nullpointer ���warning���imageview ��������������� ���������������onCreate���������������������������������������
//���������������
import java.io.BufferedReader;

import java.io.File;

import java.io.FileReader;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import java.util.List;

import android.app.ActionBar.LayoutParams;
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
import android.graphics.Rect;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
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

	private static final String MAINSTRING = "MainScreen";

	public String locationinfo_string;
	// private LocationManager locationMangaer=null;
	// private LocationListener locationListener=null;

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
	private PopupWindow mPopupWindow;

	private ScreenOrientationDetector soDetector;

	private GPSTracker gpsLocation;

	// newly added on 20131006:

	private parseSendData pSendData = new parseSendData();
	private AnnotationInfo singleAnnotationInfo;
	private ScaleRatio mScaleRatio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.main_screen);

		Log.d(MAINSTRING, "onCreateCalled");
		soDetector = new ScreenOrientationDetector(this);
		isScreenRotationLocked = soDetector.isLocked();
		if (isScreenRotationLocked) {
			soDetector.unLockScreen();
		}

		gpsLocation = new GPSTracker(this);

		mAlbumStorageDirFactory = StaticGlobalFunctions
				.setmAlbumStorageDirFactory();
		wifi_connected = StaticGlobalFunctions
				.wifi_connection(getApplicationContext());

		ed = new EncryptedData(getApplicationContext(), R.raw.key,
				R.raw.plaintext);
		Parse.initialize(this, ed.getCipherTextApplicationId(),
				ed.getCipherTextClientKey());
		ParseAnalytics.trackAppOpened(getIntent());

		GuideText = (TextView) findViewById(R.id.mainscreen_textview_textguidance);
		pre_initialize_mm_selection_dialog();
		usr_name = getUsrFromIntent();
		// add user to parse data
		pSendData.addUsr(usr_name);

		setWelcomeword(usr_name, StaticGlobalFunctions.wifi_connection(this));
		initialize_mm_textView();
		initialize_btn_takeimg();
		initialize_mm_selection_dialog();
		initialize_btn_selectmm();
		initialize_btn_send();
		initialize_btn_save();
		initialize_drawImageView();
		// initialize_progbar();
		initialize_btn_upload();
		showReminder();
	}

	private void showReminder() {
		String fileContent = FileOperation.read(getApplicationContext(),
				offline_filename);
		if (fileContent == null) {

		} else {
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
									// Do nothing;

								}
							}).show();
		}

	}

	private void initialize_btn_upload() {
		btn_upload = (Button) findViewById(R.id.btn_upload);
		btn_upload.setVisibility(View.INVISIBLE);
		btn_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Set image view to something like uploading
				mImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.uploading));
			 if (loginGlobalUser()) {
				 check_upload_localData();
			}
				

			}
		});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(MAINSTRING, "onStartCalled");
		SetBtnUploading();
	}

	private void SetBtnUploading() {

		String fileContent = FileOperation.read(getApplicationContext(),
				offline_filename);
		if (fileContent == null) {

		} else {
			if (StaticGlobalFunctions.wifi_connection(getApplicationContext())) {
				btn_upload.setVisibility(View.VISIBLE);
				// btn_upload.setEnabled(true);
			} else {
				btn_upload.setVisibility(View.VISIBLE);
				btn_upload.setEnabled(false);
			}
		}
	}

	private void pre_initialize_mm_selection_dialog() {

		try {
			makeModelDataFile = FileOperation.transferRawtoFile(this,
					R.raw.car_make_model_revised, "makemodel", "car.csv");
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
							+ " & " + "I don't know the model");
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
						+ " & "
						+ makemodelGroup.get(groupPosition).get(childPosition)
								.toString());

				if (selectedMake.equals(database.NONE_EXISTING)) {
					selectedMake = "unknown";
					makemodelshowTextView.setText("I don't know the make"
							+ " & "
							+ makemodelGroup.get(groupPosition)
									.get(childPosition).toString());
				}
				if (selectedModel.equals(database.NONE_EXISTING_MODEL)) {
					selectedModel = "unknown";
					makemodelshowTextView.setText(makeGroup.get(groupPosition)
							.toString() + " & " + "I don't know the model");
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
		if (StaticGlobalFunctions.isEmailValid(usr_name)) {
			welcome_words_string = usr_name;
		} else {
			welcome_words_string = "Dear guest";
		}
		if (isWifi) {
			welcome_words_string += " (Online)";
		} else {
			welcome_words_string += " (Offline)";
		}
		// Activity activity
		welcomeText = (TextView) findViewById(R.id.mainscreen_welcome_text);
		welcomeText.setText(welcome_words_string);

	}

	// private void initialize_progbar() {
	// progressBar = (ProgressBar) findViewById(R.id.single_upload_progressbar);
	// progressBar.setVisibility(View.INVISIBLE);
	// setProgressBarIndeterminate(true);
	// setProgressBarVisibility(false);
	// setProgress(3500);
	// }

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
				singleAnnotationInfo = new AnnotationInfo();
				mImageView.addRect();
				// annotatorInput.update(mImageView.getLastRect(), selectedMake,
				// selectedModel);
				
				singleAnnotationInfo.setRect(mImageView.getLastRect(),mScaleRatio);
				//singleAnnotationInfo.setRectRatio(mImageView.getRectSize());
				singleAnnotationInfo.setMake(selectedMake);
				singleAnnotationInfo.setMake(selectedModel);
				pSendData.addAnnoatation(singleAnnotationInfo);

				// gRectCount = gRectCount + 1;
				global_prevent_reDraw = false;

				if (gRectCount == 5) {
					global_prevent_reDraw = true;
					String showMessage = "You have annotated 5 cars,click Done! ";
					StaticGlobalFunctions.ShowToast_short(
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
				boolean isSent = false;
				
				if (loginGlobalUser()) {
						isSent = pSendData.sendOnLine();
				}
				if (isSent == false) {
					pSendData.saveOffLine();
				}

				if (gpsLocation.canGetLocation()) {
					locationinfo_string = gpsLocation.getLatitude() + " "
							+ gpsLocation.getLongitude();

				} else {
					gpsLocation.showSettingsAlert();
					if (gpsLocation.canGetLocation()) {
						locationinfo_string = gpsLocation.getLatitude() + " "
								+ gpsLocation.getLongitude();
					} else {
						locationinfo_string = "Not Available" + " "
								+ "Not Available";
					}
					Log.d("GPS LOCATION:", locationinfo_string);

				}
				annotatorInput.setLocationinfo(locationinfo_string);
				annotatorInput.addPath(mCurrentPhotoPath);
				annotatorInput.addImgName(imageFileName + JPEG_FILE_SUFFIX);
				annotatorInput.addScaleRatio(new ScaleRatio(mImageView,
						mCurrentPhotoPath));
				wifi_connected = StaticGlobalFunctions
						.wifi_connection(getApplicationContext());
//				if (wifi_connected) {
//					login_global_usr();
//				}

				annotatorInput.addWifiStatus(wifi_connected);
				jsonData = new JSONdata(annotatorInput, getApplicationContext());

				if (wifi_connected && isLoggedin) {

					ParseObject pb_send = jsonData.formatParseObject(ed
							.getCipherTextClassName());
					pb_send.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException arg0) {
							if (arg0 == null) {
								String send_success = "Send to server successfully!";
								StaticGlobalFunctions.ShowToast_short(
										getApplicationContext(), send_success,
										R.drawable.success);

							} else {

								String send_failuer = "Problem occured while sending, will save and try again next time";
								StaticGlobalFunctions.ShowToast_short(
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
									StaticGlobalFunctions.ShowToast_short(
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
						StaticGlobalFunctions.ShowToast_short(
								getApplicationContext(), showMessage,
								R.drawable.success);
						// showReminder();
					} catch (JSONException e) {

						e.printStackTrace();
					}

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
				Rect btn_takeimg_location = StaticGlobalFunctions
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
		// if (isLoggedin) {
		new UploadFileThread().start();
		// }

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

		// TODO ���������������������
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

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
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

			// 20131007 add image data information to parseSendData
			pSendData.setImageData(mCurrentPhotoPath);
            mScaleRatio=new ScaleRatio(mImageView, mCurrentPhotoPath);
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
		if (isScreenRotationLocked == true) {
			soDetector.LockScreen();
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
				/*
				 * int item_num = offline_JsonArray.length();
				 * 
				 * if (item_num > 0) {
				 * 
				 * JSONObject tem_item; try { tem_item = (JSONObject)
				 * offline_JsonArray.get(0); ParseObject pobject = new
				 * JSONdata(tem_item)
				 * .formatParseObject(ed.getCipherTextClassName());
				 * pobject.saveInBackground(new SaveCallback() {
				 * 
				 * @Override public void done(ParseException arg0) { if
				 * (arg0==null) { offline_JsonArray = static_global_functions
				 * .remove(0, offline_JsonArray);
				 */
				recursive_upload();
				/*
				 * } else {
				 * static_global_functions.ShowToast_short(getApplicationContext
				 * (), "Unable to connect to server, will try later",
				 * R.drawable.caution); } }
				 * 
				 * });
				 */
				/*
				 * } catch (JSONException e) { e.printStackTrace(); }
				 */

			}

		}

	}

	// }

	private void recursive_upload() {
		if (offline_JsonArray.length() > 0) {

			JSONObject tem_item;
			try {
				tem_item = (JSONObject) offline_JsonArray.get(0);

				ParseObject pobject = new JSONdata(tem_item)
						.formatParseObject(ed.getCipherTextClassName());
				pobject.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {
						if (arg0 == null) {
							offline_JsonArray = StaticGlobalFunctions.remove(0,
									offline_JsonArray);
							recursive_upload();
						} else {
							StaticGlobalFunctions
									.ShowToast_short(
											getApplicationContext(),
											"Error occured during uploading, please try later",
											R.drawable.caution);
							mImageView.setImageDrawable(getResources()
									.getDrawable(R.drawable.ready));
							btn_upload.setText("Retry");
						}

					}

				});
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("Main_Screen", "JSON array null");
			}

		} else {
			StaticGlobalFunctions.ShowToast_short(getApplicationContext(),
					"Previous data uploaded!", R.drawable.success);
			Log.d("All files", "Send successfully");

			FileOperation.delete(getApplicationContext(), offline_filename);
			btn_upload.setVisibility(View.INVISIBLE);
			mImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.ready));

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

	private boolean loginGlobalUser() {
// Doubt if it is correct!
		if (StaticGlobalFunctions.wifi_connection(this)) {

			ParseUser.logInInBackground(ed.getCipherTextUserName(),
					ed.getCipherTextUserPassword(), new LogInCallback() {

						@Override
						public void done(ParseUser usr, ParseException e) {
							if (usr != null) {
								// Log.d("Login_before", "Successfully");
								isLoggedin = true;
							} else {
								// Log.d("error logging in ", e.toString());
								isLoggedin = false;
								StaticGlobalFunctions
										.ShowToast_short(
												getApplicationContext(),
												"Remote server not responding, save to local memory",
												R.drawable.caution);
							}

						}
					});
			return isLoggedin;
		} else {
			return false;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// this.finish();
	}

}
