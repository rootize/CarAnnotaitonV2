package edu.cmu.carannotationv2;
//注释1：
//原先 出现nullpointer 的warning让imageview 无法实现， 调换了一下onCreate中的各个函数的顺序，就好了
//不知道原因
import java.io.BufferedReader;
import java.io.File;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import com.parse.Parse;
import com.parse.ParseAnalytics;

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
	//只有在 1. makespinner 被选中， && modelspinner被选中 && drawView被画
	// sendbutton 才会真正起作用
	// 剩下的时候都是给提示 TOAST
	private boolean make_done=false;
	private boolean model_done=false;
	private boolean draw_done=false;
	
	/* button for clearing rectangle*/
	private Button clearRButton;
	
	//Temporily using ImageView instead of DrawImageView
	//private DrawImageView mImageView;
	private DrawImageView mImageView;
	private Bitmap mImageBitmap; // stores the bitmap
	private String mCurrentPhotoPath;

	
	
	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	// 2.1 Add spinners!
	private Spinner makeSpinner;
	private Spinner modelSpinner;
	private ArrayAdapter<String> makeAdapter;
	private ArrayAdapter<String> modelAdapter;
	
	private String selectedMake;
	private String selectedModel;
	
	
	
	private boolean modelEnabled=false; //用以指示model 的spinner是不是被激活了
	//2.3 Database related!
	private File makeModelDataFile;
	//2.3.2 Database Queries!
	private static final String DATABASE_TABLE="makemodel";
	private static final String DATABASE_NAME="CAR.db";
	private static final String KEY_ID="_id";
	private static final String CAR_MAKE="make";
	private static final String CAR_MODEL="model";
	private static final String INSERT_ITEM_PREFIX="INSERT INTO " + DATABASE_TABLE + "( "
			+ CAR_MAKE + " , " + CAR_MODEL + " )" + " VALUES (";
	private static final String INSERT_ITEM_SUFFIX=" );";
	private static final String CREATE_TABLE1="DROP TABLE IF EXISTS " + DATABASE_TABLE;
	private static final String CREATE_TABLE2="CREATE TABLE " + DATABASE_TABLE + "("
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CAR_MAKE
			+ " TEXT NOT NULL , " + CAR_MODEL + " TEXT NOT NULL )";
	private static final String SELECT_MAKE="SELECT DISTINCT " + CAR_MAKE + " from "
			+ DATABASE_TABLE;
	private static final String SELECT_MODEL_ALL="SELECT DISTINCT " + CAR_MODEL + " from "
				+ DATABASE_TABLE;
	private static final String SELECT_ONE_MODEL_PREFIX=SELECT_MODEL_ALL+ " where "+CAR_MAKE+" = '";
	private static final String SELECT_ONE_MODEL_SUFFIX="';";
	private static final String NONE_EXISTING="NONE ABOVE";
	private static final String BLANK_FISRT_ITEM="";
	
	
	private SQLiteDatabase carDatabase;
	
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

		retakeButton = (Button) findViewById(R.id.button_take_new);
		sendButton = (Button) findViewById(R.id.button_send_server);
		
		//button for clearing rectangles on DrawImageView
		clearRButton=(Button)findViewById(R.id.btn_clearRect);
		
		
		sendButton.setEnabled(false);
		clearRButton.setEnabled(false);
		
		
		
		
		mImageView = (DrawImageView) findViewById(R.id.imageView1);/*DrawImageView*/
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
		
	//	dispathTakePictureIntent();
		// 2.2 Spinner initialization
		makeSpinner=(Spinner)findViewById(R.id.carmakespinner);
		modelSpinner=(Spinner)findViewById(R.id.carmodelspinner);
		
	//	modelSpinner.setEnabled(false);
		makeSpinner.setEnabled((mCurrentPhotoPath!=null));//要仔细看看这里:
		                                                 // 想做的： 当第一次登入界面的时候是关闭的
		                                                 // 后来登入的过程中是被激活的
		
		
		
		
		
		
		//2.3 get database data from raw
		//2.3.1 read from raw file to local file
		try {
			ReadDataFromRaw(R.raw.car_make_model_revised);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("2.3.1", "Success!");
		//2.3.2 read from local file to 
		
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
		
		//2.3.3 Insert Data into Spinners
		//注释： 为了使第一次的两个spinner 都变成 disabled， 添加了第一行为空白行
		generateAdapter(makeSpinner,SELECT_MAKE,makeAdapter);
		makeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectedMake=arg0.getItemAtPosition(arg2).toString();
				if(selectedMake.equals(NONE_EXISTING))
				{   
					selectedModel=NONE_EXISTING;
					
					sendButton.setEnabled(true);
				}else if (selectedMake.equals(BLANK_FISRT_ITEM)) {
					modelSpinner.setEnabled(false);
				}else{
					modelSpinner.setEnabled(true);
					sendButton.setEnabled(false);
				setModelSpinnerContent(SELECT_ONE_MODEL_PREFIX+selectedMake+SELECT_ONE_MODEL_SUFFIX);
				}
				
			}

			

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				//Do nothing
			}
		});
		
		
		modelSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				selectedModel=arg0.getItemAtPosition(arg2).toString();
				sendButton.setEnabled(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		
		
		//4.1 Draw Rectangles on DrawImageView
		
		
  // 3.1 Parse information and so on...
		

	}// end of OnCreate

	
	private void setModelSpinnerContent(String string) {
		// TODO Auto-generated method stub
		//modelSpinner.setEnabled(true==(modelEnabled=true));
		//modelSpinner.setEnabled(true);
		generateAdapter(modelSpinner, string, modelAdapter);
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */

	private void generateAdapter(Spinner spinner, String select_query, ArrayAdapter<String> adapter) {
		// TODO Auto-generated method stub
		List<String> labels=getLabels(select_query);
		adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,labels);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner.setAdapter(adapter);
		
	}

	private List<String> getLabels(String select_query) {
		// TODO Auto-generated method stub
		List<String> labelsList=new ArrayList<String>();
		labelsList.add(BLANK_FISRT_ITEM);
		Cursor cursor=carDatabase.rawQuery(select_query, null);
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
		carDatabase=openOrCreateDatabase(DATABASE_NAME,Context.MODE_PRIVATE, null);
		carDatabase.execSQL(CREATE_TABLE1);
		carDatabase.execSQL(CREATE_TABLE2);
		//2.3.2.2 insert data
		FileReader databaseFileReader=new FileReader(makeModelDataFile.getAbsolutePath());
		BufferedReader databaseBufferedReader=new BufferedReader(databaseFileReader);
		String line="";
		
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
		carDatabase.setTransactionSuccessful();
		carDatabase.endTransaction();
		
		Log.d("Done DATABASE ", "Initialization");
	}

	private void ReadDataFromRaw(int csvID) throws IOException {
		// TODO Auto-generated method stub
		InputStream isInputStream=getResources().openRawResource(csvID);
		File make_model_file_dir=getDir("make_model", Context.MODE_PRIVATE);
		makeModelDataFile=new File(make_model_file_dir,"car.csv");
		FileOutputStream osFileOutputStream=new FileOutputStream(makeModelDataFile);
		byte[] buffer=new byte[4096];
		int bytesRead;
		while ((bytesRead=isInputStream.read(buffer))!=-1) {
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
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
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
		// Size of iamge stored in temp
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		if (targetH > 0 || targetW > 0) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		}

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
			
			galleryAddPic(); //mCurrentPhotoPath = null;
			// */// needed?

		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode==RESULT_OK&& requestCode==CAMERA_REQUEST) {
		 Log.d("OnActivityResult", "Called!");
			handleBigCameraPhoto();
			makeSpinner.setEnabled(true);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		carDatabase.close();
	}

	

	//2.3
	
	//Create OnResume: when comes back to
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// FIXME  Not Verified
		modelEnabled=false;
		modelSpinner.setEnabled(modelEnabled);
		
		
		invalidateSendBtn();
		
		super.onResume();
	}


	private void invalidateSendBtn() {
		// TODO Auto-generated method stub
		make_done=false;
		model_done=false;
		draw_done=false;
		sendButton.setEnabled(false);
	}
	
	
}// Ending of whole class!
