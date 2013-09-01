package edu.cmu.carannotationv2;

//注释1：
//原先 出现nullpointer 的warning让imageview 无法实现， 调换了一下onCreate中的各个函数的顺序，就好了
//不知道原因
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import edu.cmu.carannotationv2.MyLocation.LocationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main_screen extends Activity {
	private String lati=null;
	private String longti=null;
	private static final int CAMERA_REQUEST = 1888;
	private static final String offline_filename = "offline";
	private static final int NUM = 20;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private Bitmap mImageBitmap; 
	private String mCurrentPhotoPath;
	private String imageFileName = null;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private static final int total_rects = 100;
	private TextView welcomeText;
	String welcome_name = "";
	private TextView GuideText;
	private Button btn_takeimg;
	private Button btn_send;
	private Button btn_save;
	private DrawImageView mImageView;
	private Spinner makeSpinner;
	private Spinner modelSpinner;
	private ArrayAdapter<String> makeAdapter;
	private ArrayAdapter<String> modelAdapter;
	private boolean global_prevent_reDraw = false;// 等到最后再来看看怎么样
	// ***************also items that will be sent to parse**********//
	private String selectedMake;
	private String selectedModel;
	private Point rect_ul;// the uppler_left point of rectangle
	private Point rect_br;// the bottom_right point of rectangle
	private String[] makes = new String[NUM];
	private String[] models = new String[NUM];
	private int[][] rects = new int[NUM][4]; // records the number of rects
	private int global_info_count = 0;
	// **************************************************************//
	private File makeModelDataFile;
	private SQLiteDatabase carDatabase;
	private ParseObject pb_send;
	private String usr_name; // data received from intent
	private JSONArray offline_JsonArray;
	private SharedPreferences sp_location;
	private boolean wifi_connected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_screen);
	
		//shared preference
		sp_location=this.getSharedPreferences("location_info", Context.MODE_PRIVATE);
		// initialize parse
		Parse.initialize(this, "hR5F7PLUvr2vkKTo8gfEQRKXgOqdvc6kehlYJREq",
				"b0Fks95H8U5pE62QPWTUipzZaiRyp8iZxqCSdey0");
		ParseAnalytics.trackAppOpened(getIntent());
		// get intent
		Intent receiver = getIntent();
		usr_name = receiver.getStringExtra("usr");

		
		wifi_connected=static_global_functions.wifi_connection(getApplicationContext());
		
		if (static_global_functions.isEmailValid(usr_name)) {
			welcome_name = usr_name;
		} else {
			welcome_name = "Dear guest";
		}
		
		//*********************************************************
		if (wifi_connected) {
			welcome_name = welcome_name + " (online)";
			check_upload_localData();
			
		} else {
			welcome_name = welcome_name + " (offline)";
		}
		//*************************************************************
		welcomeText = (TextView) findViewById(R.id.mainscreen_welcome_text);
		welcomeText.setText("Welcome! " + welcome_name);

		// initialization on counting images

		global_info_count = 0;
		rects = new int[total_rects][4];

		initialize_btn_takeimg();
		initialize_btn_send();
		initialize_btn_save();
		initialize_drawImageView();
	

		makeSpinner = (Spinner) findViewById(R.id.carmakespinner);
		modelSpinner = (Spinner) findViewById(R.id.carmodelspinner);

	
		GuideText = (TextView) findViewById(R.id.mainscreen_textview_textguidance);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
		
		//take a new image, if the current number of annotated rects is not 0, reminds user if they want to give up
		

		// 注释： 为了使第一次的两个spinner 都变成 disabled， 添加了第一行为空白行
		generateAdapter(makeSpinner, database.SELECT_MAKE, makeAdapter);
		makeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				global_prevent_reDraw = true;
				selectedMake = arg0.getItemAtPosition(arg2).toString();
				if (selectedMake.equals(database.NONE_EXISTING)) {
					selectedModel = database.NONE_EXISTING;
					btn_save.setEnabled(true);
					btn_send.setEnabled(false);
					modelSpinner.setEnabled(false);
				} else if (selectedMake.equals(database.BLANK_FISRT_ITEM)) {
					modelSpinner.setEnabled(false);
					btn_save.setEnabled(false);
					btn_send.setEnabled(false);
				} else {
					btn_save.setEnabled(false);
					modelSpinner.setEnabled(true);
					btn_send.setEnabled(false);
					setModelSpinnerContent(database.SELECT_ONE_MODEL_PREFIX
							+ selectedMake + database.SELECT_ONE_MODEL_SUFFIX);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
			}
		});

		modelSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				selectedModel = arg0.getItemAtPosition(arg2).toString();
				btn_save.setEnabled(true);
				GuideText.setText("If everything is correct, press \"Save Information\"  button to save");

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		mImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				DrawImageView drawView = (DrawImageView) v;
				// private boolean prevent_reDraw=false;

				if (!global_prevent_reDraw) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {

						drawView.setRect_left(event.getX());
						drawView.setRect_top(event.getY());
                        Log.d("imageview left", drawView.getRect_left()+"");
                        Log.d("imageview top", drawView.getRect_top()+"");
					} else {

						drawView.setRect_right(event.getX());
						drawView.setRect_bottom(event.getY());
						Log.d("imageview right", drawView.getRect_right()+"");
						Log.d("imageview bottom", drawView.getRect_bottom()+"");
						
					}
					drawView.invalidate();
					// drawView.drawRect = true;

					makeSpinner.setEnabled(true);
					btn_save.setEnabled(false);
					btn_send.setEnabled(false);
					GuideText.setText("Please set make/model of the car");
				}

				return true;
			}
		});

		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int targetW = mImageView.getWidth();
				int targetH = mImageView.getHeight();
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;
				Log.d("Image view width", "" + targetW);
				Log.d("Image view height", "" + targetH);
				Log.d("stored image width", "" + photoW);
				Log.d("stored image height", "" + photoH);
				double w_scaleFactor = 1;
				double h_scaleFactor = 1;
				if (targetH > 0 && targetW > 0) {
					w_scaleFactor = (double) photoW / targetW;
					h_scaleFactor = (double) photoH / targetH;

				}
				Log.d("scalefactor_w", "" + w_scaleFactor);
				Log.d("scalefactor_h", "" + h_scaleFactor);

				mImageView.rectArray[global_info_count][0] = (int) mImageView
						.getRect_top();
				mImageView.rectArray[global_info_count][1] = (int) mImageView
						.getRect_left();
				mImageView.rectArray[global_info_count][2] = (int) mImageView
						.getRect_bottom();
				mImageView.rectArray[global_info_count][3] = (int) mImageView
						.getRect_right();

				// by sequence: upper left bottom right

				rects[global_info_count][0] = (int) (mImageView.rectArray[global_info_count][0]*h_scaleFactor);
				rects[global_info_count][1] = (int) (mImageView.rectArray[global_info_count][1]*w_scaleFactor);
				rects[global_info_count][2] = (int) (mImageView.rectArray[global_info_count][2]*h_scaleFactor);
				rects[global_info_count][3] = (int) (mImageView.rectArray[global_info_count][3]*w_scaleFactor);

				makes[global_info_count] = new String(selectedMake);
				models[global_info_count] = new String(selectedModel);

				global_prevent_reDraw = false;
				global_info_count = global_info_count + 1; // only at this place
															// +1;
				mImageView.setRect_count(global_info_count) ;
				// after imaged saved make all other things gray
				btn_save.setEnabled(false);
				global_prevent_reDraw = false;
				makeSpinner.setEnabled(false);
				modelSpinner.setEnabled(false);

				Log.d("OnRectCount", "" + global_info_count);
				if (global_info_count == 5) {
					btn_save.setEnabled(false);
					global_prevent_reDraw = true;
					//btn_send.setEnabled(true);
					String showMessage = "The number annotations has exceeded 5";
					//showToast(showMessage, R.drawable.caution);
					static_global_functions.ShowToast_short(getApplicationContext(), showMessage, R.drawable.caution);
				}
				btn_send.setEnabled(true);
				GuideText.setText("Press \"Done!\" to finish or start annotating another image by drawing on the image");
				
			}
		});


		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				ExifInterface exif;
				try {
					exif = new ExifInterface(mCurrentPhotoPath);

					String longti = exif
							.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
					String lati = exif
							.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
					String longti_ref = exif
							.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
					String lati_ref = exif
							.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);

					pb_send = new ParseObject("annotation_info");
					pb_send.put("usr", usr_name);

					if (global_info_count != 0) {

						for (int i = 0; i < global_info_count; i++) {
							pb_send.put("make_" + i, makes[i]);
							pb_send.put("model_" + i, models[i]);

							pb_send.put("Rect_Top_" + i, rects[i][0]);
							pb_send.put("Rect_Left_" + i, rects[i][1]);
							pb_send.put("Rect_Bottom_" + i, rects[i][2]);
							pb_send.put("Rect_Rigth_" + i, rects[i][3]);

						}

					}

					pb_send.put("Location_Lati", lati + " " + lati_ref);
					pb_send.put("Location_Longti", longti + " " + longti_ref);
					
					
					//if (!(sp_location.getString("lati", "").equals(lati + " " + lati_ref)&&sp_location.getString("longti", "").equals(longti + " " + longti_ref))) {
					if (wifi_connected) {
						Editor editor=sp_location.edit();
						editor.putString("lati", lati + " " + lati_ref);
						editor.putString("longti", longti + " " + longti_ref);
						editor.commit();
					}
					
					//}
						Log.d("lati", sp_location.getString("lati", ""));
						Log.d("longti", sp_location.getString("longti", ""));
					pb_send.put("focalLength",
							exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
					pb_send.put("flash",
							exif.getAttribute(ExifInterface.TAG_FLASH));
					pb_send.put("exposuretime",
							exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME));
					pb_send.put("image_make",
							exif.getAttribute(ExifInterface.TAG_MAKE));
					pb_send.put("imagemodel",
							exif.getAttribute(ExifInterface.TAG_MODEL));
					pb_send.put("whitebalance",
							exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE));

					pb_send.put("imgName", imageFileName + JPEG_FILE_SUFFIX);

					try {
						// imageStream=new FileInputStream(mCurrentPhotoPath);
						// final byte[] data = IOUtils.toByteArray(imageStream);
						// final byte[] data=imageStream.
						Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
						byte[] data = baos.toByteArray();
						ParseFile imgFile = new ParseFile(imageFileName
								+ JPEG_FILE_SUFFIX, data);

						pb_send.put("imagefile", imgFile);

						if (wifi_connected) {
							pb_send.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException arg0) {
									// TODO Auto-generated method stub

									String send_success = "Send to server successfully!";
									//static_global_functions.showToast(send_success, R.drawable.success);
                                     static_global_functions.ShowToast_short(getApplicationContext(), send_success, R.drawable.success);
									global_info_count = 0;

								}
							});
							// FIXME
							// 1. delete all files
							// 2. draw a new image---please take a new one!

						} else {
						
							JSONArray old_offlineJsonArray;
							String temp = filesaveread.read(
									getApplicationContext(), offline_filename);
							if (temp == null) {
								old_offlineJsonArray = new JSONArray();
							} else {
								old_offlineJsonArray = new JSONArray(temp);
							}

							JSONObject toSend_item = new JSONObject();

							
							toSend_item.put("usr", usr_name);
							toSend_item.put("globalcount", global_info_count);
							if (global_info_count != 0) {

								for (int i = 0; i < global_info_count; i++) {
									toSend_item.put("make_" + i, makes[i]);
									toSend_item.put("model_" + i, models[i]);

									toSend_item.put("Rect_Top_" + i,
											rects[i][0]);
									toSend_item.put("Rect_Left_" + i,
											rects[i][1]);
									toSend_item.put("Rect_Bottom_" + i,
											rects[i][2]);
									toSend_item.put("Rect_Rigth_" + i,
											rects[i][3]);

								}

							}

							toSend_item.put("Location_Lati", sp_location.getString("lati", "")+ " ap");
							toSend_item.put("Location_Longti", sp_location.getString("longti", "")+" ap");
									Log.d("lati", sp_location.getString("lati", ""));
									Log.d("longti", sp_location.getString("longti", ""));
							toSend_item.put(
									"focalLength",
									exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
							toSend_item.put("flash",
									exif.getAttribute(ExifInterface.TAG_FLASH));
							toSend_item.put(
									"exposuretime",
									exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME));
							toSend_item.put("image_make",
									exif.getAttribute(ExifInterface.TAG_MAKE));
							toSend_item.put("imagemodel",
									exif.getAttribute(ExifInterface.TAG_MODEL));
							toSend_item.put(
									"whitebalance",
									exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE));

							toSend_item.put("imgName", imageFileName
									+ JPEG_FILE_SUFFIX);
							toSend_item.put("savepath", mCurrentPhotoPath);

							old_offlineJsonArray.put(toSend_item);

							filesaveread.save(getApplicationContext(),
									offline_filename,
									old_offlineJsonArray.toString());
							global_info_count = 0;
							String showMessage = "Saved in Local machine, image will be uploaded when wifi available";
							static_global_functions.ShowToast_short(getApplicationContext(), showMessage, R.drawable.success);
						}

					} catch (Exception e) {
						// TODO: handle exception
					} finally {
						// IOUtils.closeQuietly(imageStream);
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				btn_send.setEnabled(false);
				makeSpinner.setEnabled(false);
				modelSpinner.setEnabled(false);
				mImageView.clearRecords();
				mImageView.clearrect();
				mImageView.invalidate();
				global_prevent_reDraw=true;
				mImageView.setImageDrawable(getResources().getDrawable(R.drawable.buttonfinish));
				
				GuideText.setText("Thanks, please press \"Take a Photo\" to take a new photo ");
				
			}

			
			
			
		});

		GuideText.setText("Please press \" Take a Photo! \"  to take a picture");
		// *****************************************//
	}// end of OnCreate

	


	private void initialize_drawImageView() {
		// TODO Auto-generated method stub

		mImageView = (DrawImageView) findViewById(R.id.imageView1);/* DrawImageView */
		mImageView.setVisibility(DrawImageView.VISIBLE);
		mImageView.setImageDrawable(getResources().getDrawable(
				R.drawable.firstlogin));
		global_prevent_reDraw = true; //indicator if one could draw on iamge

	}




	private void initialize_btn_save() {
		// TODO Auto-generated method stub
		btn_save = (Button) findViewById(R.id.btn_confirm);
		btn_save.setEnabled(false);
	}


	private void initialize_btn_send() {
		// TODO Auto-generated method stub
		btn_send = (Button) findViewById(R.id.button_send_server);
		btn_send.setEnabled(false);
	}

	private void initialize_btn_takeimg() {
		// TODO Auto-generated method stub
		btn_takeimg = (Button) findViewById(R.id.button_take_new);

		btn_takeimg.setEnabled(true);
		btn_takeimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (global_info_count != 0) {
					String showMessage = "Abondon all the rectangles and start a new one?";
					showDialog(showMessage);
				} else {
					dispathTakePictureIntent();
				}
			}
		});

		try {
			ReadDataFromRaw(R.raw.car_make_model_revised);
			FormDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	private void check_upload_localData() {
		lati=sp_location.getString("lati", "");
		longti=sp_location.getString("longti", "");
		new UploadFileThread().start();
	}




	private void setModelSpinnerContent(String string) {
		
		generateAdapter(modelSpinner, string, modelAdapter);

	}

	

	private void generateAdapter(Spinner spinner, String select_query,
			ArrayAdapter<String> adapter) {
		// TODO Auto-generated method stub
		List<String> labels = getLabels(select_query);
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.simple_spinner_item, labels);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinner.setAdapter(adapter);

	}

	private List<String> getLabels(String select_query) {
		// TODO Auto-generated method stub
		List<String> labelsList = new ArrayList<String>();
		labelsList.add(database.BLANK_FISRT_ITEM);
		Cursor cursor = carDatabase.rawQuery(select_query, null);
		if (cursor.moveToFirst()) {
			labelsList.add(cursor.getString(0));

			while (cursor.moveToNext()) {

				labelsList.add(cursor.getString(0));
			}
		}
		cursor.close();
		labelsList.add(new String(database.NONE_EXISTING));
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

	private void ReadDataFromRaw(int csvID) throws IOException {
		// TODO Auto-generated method stub
		InputStream isInputStream = getResources().openRawResource(csvID);
		File make_model_file_dir = getDir("make_model", Context.MODE_PRIVATE);
		makeModelDataFile = new File(make_model_file_dir, "car.csv");
		FileOutputStream osFileOutputStream = new FileOutputStream(
				makeModelDataFile);
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = isInputStream.read(buffer)) != -1) {
			osFileOutputStream.write(buffer, 0, bytesRead);

		}
		osFileOutputStream.close();
		isInputStream.close();

	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		// outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
				(mImageBitmap != null));
		// outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri !=
		// null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		// mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView
				.setVisibility(savedInstanceState
						.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE
						: ImageView.INVISIBLE);

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
		imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}

	private void setPic() {
	
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();
		Log.d("width_target", "" + targetW);
		Log.d("height_target", "" + targetH);
		// Size of iamge stored in temp
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		Log.d("width", "" + photoW);
		Log.d("height", "" + photoH);
		 int scaleFactor = 1;
		if (targetH > 0 || targetW > 0) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		}
		Log.d("ScaleFactor:  ", "" + scaleFactor);
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		mImageView.setImageBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);

	}

	// don't know if we need this!
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private void dispathTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (Exception e) {
			e.printStackTrace();
			f = null;
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
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
			Log.d("OnActivityResult", "Called!");
			handleBigCameraPhoto();
			//step2:
			GuideText.setText("Please draw a rectangle that covers the (first)car in the photo");
			//makeSpinner.setEnabled(true);
		}
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		carDatabase.close();
	}


	@Override
	protected void onResume() {
		cleanVectors();
		makeSpinner.setEnabled(false);
		modelSpinner.setEnabled(false);
		btn_save.setEnabled(false);
		btn_send.setEnabled(false);
		btn_takeimg.setEnabled(true);
		global_prevent_reDraw = false;
		global_info_count = 0; // 每次重新一张图片的时候清零
		super.onResume();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (null != mImageView) {
			mImageView.clearrect();// 去除留下的rect
			mImageView.clearRecords();
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
			filesaveread.save(getApplicationContext(), fileName, content);
		}
	}

	class UploadFileThread extends Thread {
		public UploadFileThread() {
		}
		@Override
		public void run() {		

			String offline_string = filesaveread.read(getApplicationContext(),
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

						ParseObject pobject = new ParseObject("annotation_info");
								
						static_global_functions.transfer_Json_Pobject(pobject, tem_item);

						pobject.saveInBackground(new SaveCallback() {

							@Override
							public void done(ParseException arg0) {
								// TODO Auto-generated method stub

								offline_JsonArray =static_global_functions. remove(0, offline_JsonArray);
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

	public void showDialog(String showString) {

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				this);
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
	
	private void cleanVectors() {
		for (int i = 0; i < NUM; i++) {
			makes[i] = null;
			models[i] = null;
			for (int j = 0; j < 4; j++) {
				rects[i][j] = 0;
			}

		}

	}

	private void recursive_upload() {
		if (offline_JsonArray.length() > 0) {

			JSONObject tem_item;
			try {
				tem_item = (JSONObject) offline_JsonArray.get(0);

				ParseObject pobject = new ParseObject("annotation_info");
				pobject.put("usr", tem_item.getString("usr"));
				static_global_functions.transfer_Json_Pobject(pobject, tem_item/*,lati,longti*/);
				pobject.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {
						
						offline_JsonArray = static_global_functions.remove(0,
								offline_JsonArray);
						recursive_upload();
					}

				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("Main_Screen", "JSON array null");
			}

		} else {
			filesaveread.delete(getApplicationContext(), offline_filename);
		}
	}
}// Ending of whole class!

