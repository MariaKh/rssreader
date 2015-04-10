package com.android.rssreader.tools;

/**
 * Created by mariam on 09.04.2015.
 */

        import java.io.File;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.URI;
        import java.text.SimpleDateFormat;
        import java.util.Date;

        import android.content.Context;
        import android.content.CursorLoader;
        import android.content.pm.PackageManager;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.util.LruCache;
        import android.widget.ImageView;
        import android.widget.Toast;


public abstract class ToolsUtils {

//    public static class DisplayToast implements Runnable {
//        private final Context mContext;
//        String mText;
//
//        public DisplayToast(Context mContext, String text){
//            this.mContext = mContext;
//            mText = text;
//        }
//
//        public void run(){
//            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
//        }
//    }
//    public static class Constants {
//        public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
//        public static final int MEDIA_TYPE_IMAGE = 1;
//        public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
//        public static final int FILE_SELECT_CODE = 2;
//        public static final String IMAGE_FILE_EXT = ".jpg";
//        public static final Uri SCAN_URI=Uri.parse("content://com.moocology.descryptor.data.ScanProvider/table_of_scans");
//
//
//
//        // Defines a custom Intent action
//        public static final String BROADCAST_ACTION =
//                "com.moocology.descryptor.BROADCAST";
//
//        // Defines the key for the status "extra" in an Intent
//        public static final String EXTENDED_DATA =
//                "com.moocology.descryptor..DATA";
//    }
//
//    public static Uri samsungHackUri = null;
//
//    public static void setContext(Context context) {
//        mContext = context;
//    }
//
//    private static Context mContext;
//    private static final String LOGTAG = "ToolsUtils.class";
//
//
//    /**
//     * Check if this device has a camera
//     */
//    public static boolean checkCameraHardware(Context context) {
//        Log.d(LOGTAG, "checkCameraHardware()");
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }
//
//    /**
//     * Create a file Uri for saving an image
//     */
//
//    public static Uri getOutputMediaFileUri(int type) {
//        Log.d(LOGTAG, "getOutputMediaFileUri");
//        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
//            Log.d("file",Environment.getExternalStorageState().toString());
//            return null;
//        }
//        Log.d("file","bb");
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), mContext.getResources().getString(R.string.pics_folder_path));
//        Log.d("file","cc");
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("CameraSample", "failed to create directory");
//                return null;
//            }
//        }
//        // Create a media file name
////        String timeStamp = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
////        File mediaFile;
////        if (type == Constants.MEDIA_TYPE_IMAGE) {
////            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
////                    "IMG_" + timeStamp + Constants.IMAGE_FILE_EXT);
////        } else {
////            Log.e(LOGTAG, "not a " + Constants.MEDIA_TYPE_IMAGE);
////            return null;
////        }
////
////        return Uri.fromFile(mediaFile);
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//
//        if (type == Constants.MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//            Log.d("file",mediaFile.toString());
//        }  else {
//            return null;
//        }
//
//        return Uri.fromFile(mediaFile);
//    }
//
//    /**
//     * Gets the real path from media Uri
//     * @param contentUri Uri for source
//     * @return String real path
//     */
//
//    public static String getRealPathFromURI(Uri contentUri) {
//        Log.d(LOGTAG,"getRealPathFromURI for: "+contentUri.toString());
//        String[] proj = { MediaStore.Images.Media.DATA };
//        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
//        Cursor cursor = loader.loadInBackground();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//    /**
//     * Checks if file exists
//     * @param uri Uri for file
//     * @return Boolean
//     */
//    public static boolean checkIsFileExists(Uri uri) {
//        Log.d(LOGTAG,"checkIsFileExists for: "+uri.toString());
//        return  (new File(uri.getPath())).exists();
//    }
//
//    public void loadBitmap(ImageView imageView, Uri path, int imgWidth, int imgHeight) {
//        Bitmap bitmap = LruCacheManager.getInstance().getBitmapFromMemCache(path.getPath());
//
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//        } else {
//            BitmapWorkerTask bitmapWorkerTask =
//                    new BitmapWorkerTask(imageView, path, imgWidth, imgHeight);
//
//            bitmapWorkerTask.execute();
//        }
//    }





    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

//    public static long setDate(){
//        Date c = new Date(System.currentTimeMillis());
//        return c.getTime();
//    }
//    public static String getDate(long milliseconds){
//        return  new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date(milliseconds));
//    }
}

