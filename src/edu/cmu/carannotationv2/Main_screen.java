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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.ExifInterface;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import org.apache.commons.io.IOUtils;
import edu.cmu.carannotationv2.R.string;

public class Main_screen extends Activity {
	private static final int CAMERA_REQUEST = 1888;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";// save in jpg format
															// to save space
	// ********flags for sample code***************//
	// ********Used when orientation changes*******//
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	// ********************************************//

	private boolean first_login;
	private Button retakeButton;
	private Button sendButton;
	// three flags indicating if sendButton works
	// 只有在 1. makespinner 被选中， && modelspinner被选中 && drawView被画
	// sendbutton 才会真正起作用
	// 剩下的时候都是给提示 TOAST
	private boolean make_done = false;
	private boolean model_done = false;
	private boolean draw_done = false;

	/* button for clearing rectangle */
	private Button btn_confirm;

	// Temporily using ImageView instead of DrawImageView
	// private DrawImageView mImageView;
	private DrawImageView mImageView;
	private Bitmap mImageBitmap; // stores the bitmap
	private String mCurrentPhotoPath;
	private String imageFileName=null;
	private boolean prevent_reDraw = false;
	/** draw rectangles on ***/
	private Canvas canvas;

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	// 2.1 Add spinners!
	private Spinner makeSpinner;
	private Spinner modelSpinner;
	private ArrayAdapter<String> makeAdapter;
	private ArrayAdapter<String> modelAdapter;
	// ***************also items that will be sent to parse**********//
	private String selectedMake;
	private String selectedModel;
	private Point rect_ul;// the uppler_left point of rectangle
	private Point rect_br;// the bottom_right point of rectangle

	// **************************************************************//

	private boolean modelEnabled = false; // 用以指示model 的spinner是不是被激活了
	// 2.3 Database related!
	private File makeModelDataFile;
	// 2.3.2 Database Queries!
	private static final String DATABASE_TABLE = "makemodel";
	private static final String DATABASE_NAME = "CAR.db";
	private static final String KEY_ID = "_id";
	private static final String CAR_MAKE = "make";
	private static final String CAR_MODEL = "model";
	private static final String INSERT_ITEM_PREFIX = "INSERT INTO "
			+ DATABASE_TABLE + "( " + CAR_MAKE + " , " + CAR_MODEL + " )"
			+ " VALUES (";
	private static final String INSERT_ITEM_SUFFIX = " );";
	private static final String CREATE_TABLE1 = "DROP TABLE IF EXISTS "
			+ DATABASE_TABLE;
	private static final String CREATE_TABLE2 = "CREATE TABLE "
			+ DATABASE_TABLE + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + CAR_MAKE
			+ " TEXT NOT NULL , " + CAR_MODEL + " TEXT NOT NULL )";
	private static final String SELECT_MAKE = "SELECT DISTINCT " + CAR_MAKE
			+ " from " + DATABASE_TABLE;
	private static final String SELECT_MODEL_ALL = "SELECT DISTINCT "
			+ CAR_MODEL + " from " + DATABASE_TABLE;
	private static final String SELECT_ONE_MODEL_PREFIX = SELECT_MODEL_ALL
			+ " where " + CAR_MAKE + " = '";
	private static final String SELECT_ONE_MODEL_SUFFIX = "';";
	private static final String NONE_EXISTING = "NONE ABOVE";
	private static final String BLANK_FISRT_ITEM = "";

	private SQLiteDatabase carDatabase;

	private int scaleFactor;

	// Parse Related...
	private ParseObject pb_send;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		// initialize PARSE

		Parse.initialize(this, "hR5F7PLUvr2vkKTo8gfEQRKXgOqdvc6kehlYJREq",
				"b0Fks95H8U5pE62QPWTUipzZaiRyp8iZxqCSdey0");
		// To track statistics around application opens, add the following to
		// the onCreate method of your main Activity:
		ParseAnalytics.trackAppOpened(getIntent());

		retakeButton = (Button) findViewById(R.id.button_take_new);
		sendButton = (Button) findViewById(R.id.button_send_server);

		// button for clearing rectangles on DrawImageView
		btn_confirm = (Button) findViewById(R.id.btn_confirmRect);

		sendButton.setEnabled(false);
		btn_confirm.setEnabled(false);

		mImageView = (DrawImageView) findViewById(R.id.imageView1);/* DrawImageView */
		mImageView.setVisibility(DrawImageView.VISIBLE);
		// Don't know what exactly !
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		retakeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dispathTakePictureIntent();
			}
		});

		// dispathTakePictureIntent();
		// 2.2 Spinner initialization
		makeSpinner = (Spinner) findViewById(R.id.carmakespinner);
		modelSpinner = (Spinner) findViewById(R.id.carmodelspinner);

		// modelSpinner.setEnabled(false);
		makeSpinner.setEnabled((mCurrentPhotoPath != null));// 要仔细看看这里:
															// 想做的：
															// 当第一次登入界面的时候是关闭的
															// 后来登入的过程中是被激活的

		// 2.3 get database data from raw
		// 2.3.1 read from raw file to local file
		try {
			ReadDataFromRaw(R.raw.car_make_model_revised);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("2.3.1", "Success!");
		// 2.3.2 read from local file to

		try {
			FormDatabase();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 2.3.3 Insert Data into Spinners
		// 注释： 为了使第一次的两个spinner 都变成 disabled， 添加了第一行为空白行
		generateAdapter(makeSpinner, SELECT_MAKE, makeAdapter);
		makeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectedMake = arg0.getItemAtPosition(arg2).toString();
				if (selectedMake.equals(NONE_EXISTING)) {
					selectedModel = NONE_EXISTING;

					sendButton.setEnabled(true);
				} else if (selectedMake.equals(BLANK_FISRT_ITEM)) {
					modelSpinner.setEnabled(false);
				} else {
					modelSpinner.setEnabled(true);
					sendButton.setEnabled(false);
					setModelSpinnerContent(SELECT_ONE_MODEL_PREFIX
							+ selectedMake + SELECT_ONE_MODEL_SUFFIX);
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
				sendButton.setEnabled(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// 4.1 Draw Rectangles on DrawImageView

		// 4.1.1 TODO: load a bitmap indicating to start ...
		// 4.1.2 set on touchlisnter
		mImageView.setOnTouchListener(new OnTouchListener() {
			// float ul_x_original;
			// float ul_y_original;
			// float br_y_original;
			// float br_x_original;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// int actionNo=event.getAction();
				DrawImageView drawView = (DrawImageView) v;
				// private boolean prevent_reDraw=false;

				if (prevent_reDraw) {
					// do nothing! jump out.
					return true;

				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					drawView.left = event.getX();
					drawView.top = event.getY();
				} else {
					drawView.right = event.getX();
					drawView.bottom = event.getY();
				}
				drawView.invalidate();
				drawView.drawRect = true;
				// prevent_reDraw=true;
				btn_confirm.setEnabled(true);

				return true;
			}
		});

		// 4.1.3 clearRect
		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// FIXME Transform local rect parameter to the image!
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
				double w_scaleFactor = 1;
				double h_scaleFactor = 1;
				if (targetH > 0 && targetW > 0) {
					w_scaleFactor = (double) photoW / targetW;
					h_scaleFactor = (double) photoH / targetH;

				}
				Log.d("scalefactor_w", "" + w_scaleFactor);
				Log.d("scalefactor_h", "" + h_scaleFactor);
				rect_ul.x = (int) (mImageView.left * w_scaleFactor);
				rect_ul.y = (int) (mImageView.top * h_scaleFactor);
				rect_br.x = (int) (mImageView.right * w_scaleFactor);
				rect_br.y = (int) (mImageView.bottom * h_scaleFactor);
				Log.d("Test", "left:  " + rect_ul.x);
				Log.d("Test", "right: " + rect_br.x);
				Log.d("Test", "top:  " + rect_ul.y);
				Log.d("Test", "bottom: " + rect_br.y);
				prevent_reDraw = true;
			}
		});
		// 3.1 Parse information and so on...

		// ********send file & point***************//
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// boolean flag_send = true;

				// Save rescaled image:
/*				Bitmap temp = BitmapFactory.decodeFile(mCurrentPhotoPath);
				File public_dir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);*/
                ExifInterface exif;
				try {
					exif = new ExifInterface(mCurrentPhotoPath);
				
                String longti=exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String lati=exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longti_ref=exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                String lati_ref=exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                //String imgName=exif.getAttribute()
				WifiManager wifi_connected = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				
//				if (wifi_connected.isWifiEnabled()) {
					// Send everything except image !
					pb_send=new ParseObject("annotation_info");
					pb_send.put("Rect_Top",rect_ul.y );
					pb_send.put("Rect_Left", rect_ul.x);
					pb_send.put("Rect_Botton", rect_br.y);
					pb_send.put("Rect_Rigth", rect_br.x);
					pb_send.put("Location_Lati", lati+" "+lati_ref);
					pb_send.put("Location_Longti", longti+" "+longti_ref);
					pb_send.put("imgName", imageFileName);
                    // Send image --file
					//pb_send.saveInBackground();
					//FileInputStream imageStream=null;
					try {
					//	imageStream=new FileInputStream(mCurrentPhotoPath);
						//final byte[] data = IOUtils.toByteArray(imageStream);
						//final byte[] data=imageStream.
						Bitmap bm=BitmapFactory.decodeFile(mCurrentPhotoPath);
						ByteArrayOutputStream baos=new ByteArrayOutputStream();
						bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
						byte[] data=baos.toByteArray();
						ParseFile imgFile=new ParseFile(imageFileName,data);
						
						
						pb_send.put("imagefile",imgFile);
						
						
						if (wifi_connected.isWifiEnabled()) {
							 pb_send.saveInBackground(new SaveCallback() {
									
									@Override
									public void done(ParseException arg0) {
										// TODO Auto-generated method stub
									

											String send_success="Send to server successfully!";
											showToast(send_success, R.drawable.success);

									}
								});
						}else {
							pb_send.saveEventually(new SaveCallback() {
								
								@Override
								public void done(ParseException arg0) {
									// TODO Auto-generated method stub
									String send_success="Last unsent items send successfully";
									showToast(send_success, R.drawable.success);
								}
							});
						}
 

					} catch (Exception e) {
						// TODO: handle exception
					}finally{
						//IOUtils.closeQuietly(imageStream);
					}
//				} else {
//					// Save data
//					String wifi_not_available_string = "Cannot connect to Wifi, will save image on your local momory ";
//					showToast(wifi_not_available_string, R.drawable.caution);
//					
//					
//				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		// *****************************************//

		// XXX Parse data

	}// end of OnCreate

	private void showToast(String show_String, int icon) {
		// TODO Auto-generated method stub
		Toast showToast = Toast.makeText(getApplicationContext(), show_String,
				Toast.LENGTH_SHORT);
		showToast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) showToast.getView();
		ImageView imgToast = new ImageView(getApplicationContext());
		imgToast.setImageResource(icon);
		toastView.addView(imgToast, 0);
		showToast.show();
	}

	private void setModelSpinnerContent(String string) {
		// TODO Auto-generated method stub
		// modelSpinner.setEnabled(true==(modelEnabled=true));
		// modelSpinner.setEnabled(true);
		generateAdapter(modelSpinner, string, modelAdapter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */

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
		labelsList.add(BLANK_FISRT_ITEM);
		Cursor cursor = carDatabase.rawQuery(select_query, null);
		if (cursor.moveToFirst()) {
			labelsList.add(cursor.getString(0));

			while (cursor.moveToNext()) {

				labelsList.add(cursor.getString(0));
			}
		}
		cursor.close();
		labelsList.add(new String(NONE_EXISTING));
		return labelsList;

	}

	private void FormDatabase() throws SQLException, IOException {
		// FIXME Auto-generated method stub

		// 2.3.2.1 Create database
		carDatabase = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		carDatabase.execSQL(CREATE_TABLE1);
		carDatabase.execSQL(CREATE_TABLE2);
		// 2.3.2.2 insert data
		FileReader databaseFileReader = new FileReader(
				makeModelDataFile.getAbsolutePath());
		BufferedReader databaseBufferedReader = new BufferedReader(
				databaseFileReader);
		String line = "";

		carDatabase.beginTransaction();

		while ((line = databaseBufferedReader.readLine()) != null) {
			String[] all_elements = line.split(",");
			/*
			 * Log.d("Generate DATABASE",
			 * ""+all_elements.length+"   "+all_elements[1]); String[]
			 * test_elements=all_elements[0].split(":");
			 * Log.d(""+test_elements[0], test_elements[1]);
			 */
			String[] make_model_String = new String[2];
			for (int i = 0; i < all_elements.length; i++) {
				StringBuilder sb_insert = new StringBuilder(INSERT_ITEM_PREFIX);
				make_model_String = all_elements[i].split(":");
				if (2 == make_model_String.length) {
					// Log.d(""+i, "  "+make_model_String.length);
					sb_insert.append("'" + make_model_String[0] + "',");
					sb_insert.append(" '" + make_model_String[1] + "'");
					sb_insert.append(INSERT_ITEM_SUFFIX);
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
		// Size of Imageview
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
		scaleFactor = 1;
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

			galleryAddPic(); // mCurrentPhotoPath = null;
			// */// needed?

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
			Log.d("OnActivityResult", "Called!");
			handleBigCameraPhoto();
			makeSpinner.setEnabled(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		carDatabase.close();
	}

	// 2.3

	// Create OnResume: when comes back to
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// FIXME Not Verified
		modelEnabled = false;
		modelSpinner.setEnabled(modelEnabled);

		prevent_reDraw = false;
		invalidateSendBtn();
		rect_br = new Point();
		rect_ul = new Point();
		super.onResume();
	}

	private void invalidateSendBtn() {
		// TODO Auto-generated method stub
		make_done = false;
		model_done = false;
		draw_done = false;
		sendButton.setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (null != mImageView) {
			mImageView.clearrect();// 去除留下的rect
		}
		prevent_reDraw = false;
		super.onPause();
	}

	// 4.? getSizeof imageview

	// Toast

}// Ending of whole class!
