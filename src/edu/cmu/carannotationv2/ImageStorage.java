package edu.cmu.carannotationv2;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ImageStorage {
	Context context;
	private File imgFile;
	private String mCurrentPhotoPathString;
public   ImageStorage(Context context) {
		this.context=context;
		try {
			imgFile=setUpPhotoFile();
			mCurrentPhotoPathString=imgFile.getAbsolutePath();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
}
public String getImagePath() {
	return mCurrentPhotoPathString;
}
public File getImageFile() {
	return imgFile;
}
private File setUpPhotoFile() throws IOException {
	File f = createImageFile();
	mCurrentPhotoPathString = f.getAbsolutePath();
	return f;
}
private File createImageFile() throws IOException {
	 String imageFileName=Utils.createImageName();
	File albumF = getAlbumDir();
	File imageF = File.createTempFile(imageFileName, albumF.getAbsolutePath());
	return imageF;
}
private File getAlbumDir() {
	File storageDir = null;
	Log.d("GetAlbumdir", "Called!");
	if (Environment.MEDIA_MOUNTED.equals(Environment
			.getExternalStorageState())) {
		storageDir = GlobalFuns.setmAlbumStorageDirFactory()
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

	}
	return storageDir;
}

private String getAlbumName() {

	
	return context.getString(R.string.album_name);

}


}
