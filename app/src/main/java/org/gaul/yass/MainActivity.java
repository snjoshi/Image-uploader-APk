

package org.gaul.yass;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MainActivity extends AppCompatActivity {
    private static final String TAG = "yass";

    private AmazonS3 client;
    private YassPreferences preferences;
    private ListView mListView;
    private final List<String> listItems = new ArrayList<String>();
//    private ArrayAdapter<String> adapter;
    private ListViewAdapter adapter;
    private String prefix = "";
    private ObjectListing listing;
    public String projectName="";
   // public String SelectedImage="";
    public String selectedfolder="";
    SharedPreferences sharedpreferences;
    public String Email="";
    public String Name="";
    public String Phone="";
    private final SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    MainActivity.this.preferences = new YassPreferences(getApplicationContext());
                    MainActivity.this.client = getS3Client(MainActivity.this.preferences);
                }
            };

    //String [] SelectedImageList;
    private static final List<String> SelectedImageList = new ArrayList<String>();
    private static final List<Uri> selectedImageUri = new ArrayList<>();

    private final int PICK_IMAGE_MULTIPLE =20;


    private static final int PICK_IMAGE = 100;

    private ArrayList<String> imagesPathList;

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static long progress = 0;


    private ProgressDialog dialog;
    private View view1;
    public MainActivity() {


    }

//    public void getAccountDetails()
//    {
//        final int PERMS_REQUEST_CODE = 1;
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.GET_ACCOUNTS)) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMS_REQUEST_CODE);
//            } else {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMS_REQUEST_CODE);
//            }
//        }
////        else {
//            ArrayList<String> emails = new ArrayList<>();
//            Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
//            Account[] accounts = AccountManager.get(this).getAccounts();
//            Log.i(null, "account size " + accounts.length);
//            for (Account account : accounts) {
//                if (gmailPattern.matcher(account.name).matches()) {
//                    emails.add(account.name);
//                }
//            }
//            Log.i(null,"emails size "+emails.size());
////        }
//
////        for (Account account : accounts) {
////            Log.d(TAG, "account: " + account.name + " : " + account.type);
////        }
//
//    }


    private class UploadFilesTask extends AsyncTask<String, Integer, Long> {
        SharedPreferences sharedPreferences;
        String username;
        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            username = sharedPreferences.getString("username", "");
        }

        protected Long doInBackground(String... x) {
            long totalSize = 0;
            String SelectedImage=x[1];
            Log.i(null,SelectedImage);
            String [] imglist= SelectedImage.split("/");
            String filename=imglist[imglist.length-1];
            filename=username+"@"+filename;
            System.out.println("Working "+filename);
            //getAssets().open("test2.txt");
            File file= new File(SelectedImage);
            System.out.println("File opened "+filename);
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String email = sharedPreferences.getString("email","");
            String fullname = sharedPreferences.getString("fullname","");
            String username = sharedPreferences.getString("username", "");
            Log.i(null,username);
            Log.i(null,email);
            Log.i(null,fullname);
            PutObjectRequest request = new PutObjectRequest("androidtesting", x[0].replace((char)1,'/')+filename, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/*");
            metadata.addUserMetadata("email", email);
            metadata.addUserMetadata("username", username);
            metadata.addUserMetadata("fullname",fullname);
            request.setMetadata(metadata);
            client.putObject(request);
//            .withMetadata(new ObjectMetadata()));
//
//            Log.i(null,"metadata "+metadata.getContentType());
//            for (String key : metadata.getUserMetadata().keySet()) {
//                 Log.i(null,"key "+key);
//                 Log.i(null,"metadata "+metadata.getUserMetaDataOf(key));
//            }
//            Log.i(null,"metadata "+metadata.getUserMetaDataOf("username"));
            System.out.println("Okay "+filename);

            file = new File(SelectedImage);
            Log.i(null, "image " + SelectedImage + " deleting");
            if (file.exists()) {
                file.delete();
                ContentResolver contentResolver = getContentResolver();
                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.ImageColumns.DATA + "=?", new String[]{SelectedImage});
//                                callBroadCast();
                if (!file.exists()) {
                    Log.i(null, "file deletion success");
                } else {
                    Log.i(null, "file could not be deleted");
                }
            } else {
                Log.i(null, "file does not exists");
            }
            progress += 1;
            publishProgress((int) progress);
            return progress;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        protected void onPostExecute(Long result) {

            Log.v("Re","___________________________"+result.toString());
            if (result == SelectedImageList.size()) {

                Log.v("Re","///////////////////////////////"+result.toString());
                dialog.dismiss();
            }
//

        }
    }

    private class createProjectTask extends AsyncTask<String , Integer, Long> {
        protected Long doInBackground(String... dirName) {
            String foldName=dirName[0]+"/";
            long totalSize = 0;
            Pattern space = Pattern.compile("^[\\s]");
            Matcher matcherSpace = space.matcher(dirName[0]);
          //  boolean containsSpace = matcherSpace.find();

            if( matcherSpace.find() || dirName[0].isEmpty() || dirName[0].contains("/")){
                //string contains space
                System.out.println("space present");
                Snackbar.make(view1, "Enter valid project name", 3000)
                        .setAction("Action", null).show();
            }
            else{
                System.out.println("ASCII VALUE:"+dirName[0]+"#");
                InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(0);


                try {

                    System.out.println("File opened");
                    client.putObject(new PutObjectRequest("androidtesting", foldName, emptyContent,metadata));
                    System.out.println("Okay");
                    new BlobListTask().execute("");
                    Snackbar.make(view1, "Project Created", 3000)
                            .setAction("Action", null).show();
                }
                catch (Exception ex)
                {}

                return totalSize;
                //string does not contain any space
               // System.out.println("no space");
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }



    public void createProject(View view) {
//        projectName="Suraj12";
//        new createProjectTask().execute(projectName);
        view1=view;
        AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
        mydialog.setTitle("Enter Project Name");

        final EditText dirname= new EditText(MainActivity.this);
        dirname.setInputType(InputType.TYPE_CLASS_TEXT);
        mydialog.setView(dirname);
        mydialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mydir=dirname.getText().toString();
                new createProjectTask().execute(mydir);
             //   Toast.makeText(MainActivity.this,mydir+" created",Toast.LENGTH_LONG).show();
            }
        });
        mydialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        mydialog.show();

    }


    public void uploadimage(View view) {
        // projectName="Suraj12";
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
        view1=view;
        SelectedImageList.clear();
        selectedImageUri.clear();

//
         // ACTION_PICK: pick an item from data returning what was selected
        //MediaStore.Video.Media.INTERNAL_CONTENT_URI: uri fro internal storage;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        //setType() parameter tells the type of data we want to force user to  select from intent;
        gallery.setType("*/*");
//        gallery.setType("video/*");
        //EXTRA_ALLOW_MULTIPLE: allows to select multiple images;
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //start the activity with request code pick images;
        startActivityForResult(gallery, PICK_IMAGE);
//        Context context = this;

//        Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
////comma-separated MIME types
//        mediaChooser.setType("video/*, image/*");
//        mediaChooser.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(mediaChooser, PICK_IMAGE);

//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);

        // new UploadFilesTask().execute(1);

//        final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        galleryIntent.setType("video/*");
//        startActivityForResult(galleryIntent, PICK_IMAGE);

    }


    // Get Original image path
//    public static String getRealPathFromUri(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            cursor = context.getContentResolver().query(contentUri, proj, null,
//                    null, null);
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

    //    private String getRealPathFromURI(Uri contentURI) {
//        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
//    }
//    public String renameFile(String filePath) {
//        File file = new File(filePath);
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String username = sharedPreferences.getString("username", "");
//        System.out.println("File opened " + filePath);
//
////            Log.i(null,"gmail account "+Email);
////        Log.i(null, "new file name " + uploadedFileName);
//        File oldFile = new File(filePath);
//        String oldFileName = filePath;
////            Toast.makeText(this, SelectedImage, Toast.LENGTH_LONG).show();
//        String segments[] = oldFileName.split("/");
//        String fileName = segments[segments.length - 1];
//        String uploadedFileName = username + "@" + fileName;
//        String newFileName = oldFileName.replace(fileName, uploadedFileName);
//        File newFile = new File(newFileName);
//
//
//        if (!oldFile.isDirectory()) {
//
//            System.out.println("File Name is:" + fileName);
//
//            try {
//                if (oldFile.renameTo(newFile)) {
//                    System.out.println("File renamed successfull !");
//                    return newFileName;
//                } else {
//                    System.out.println("File renamed operation failed");
//                    return filePath;
//                }
//
//
//            } catch (Exception ex) {
//                System.out.println("Exception :" + ex.getMessage());
//            }
//
//        }
//        callBroadCast(file);
//        callBroadCast(newFile);
//        return filePath;
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        add selected files to SelectedImageList
//        if(resultCode==RESULT_OK)
//        {
//
//        }

//
//        if (resultCode == Activity.RESULT_OK)
//        {
//            //Bitmap photo = (Bitmap) data.getExtras().get("data");
//            //imageView.setImageBitmap(photo);
//            if(data!=null) {
//                Uri uri = data.getData();
////                String path= uri.toString();
////                Log.i("Test", "RESULT_OK");
////                Log.i("Test", path);
////                //Log.i("Path", path);
//
//                String[] projection = { MediaStore.Images.Media.DATA };
//                Cursor cursor = managedQuery(uri, projection, null, null, null);
//                startManagingCursor(cursor);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                //  cursor.getString(column_index);
//                Log.i("Path", cursor.getString(column_index));
//            }
//
//
//        }

        if (resultCode == RESULT_OK) {
            Log.i("Test", "RESULT_OK");


//            Uri myuri=data.getData();
//          //  Uri uri = data.getData();
//            Log.i("uri",myuri.toString());

            /////////////////////////////////
            ClipData mClipData = data.getClipData();
            if (mClipData==null)
            {
                Log.i("Info", "Single Image Selected");
                Uri uri = data.getData();
//                selectedImageUri.add(uri);
//                String[] projection = { MediaStore.Video.Media.DATA };
//                Cursor cursor = managedQuery(uri, projection, null, null, null);
//                startManagingCursor(cursor);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//                cursor.moveToFirst();
                String y= getRealPathFromURI(uri);
//                Log.i("Path", cursor.getString(column_index));
                String newFileName=y;//renameFile(y);
                SelectedImageList.add(newFileName);

//                    final String y = uri.getPath().substring(5, uri.getPath().length());
//                    Log.i("Path", y);


//                }
            }
            else {
                Log.i("Info", "Mult Imag Selected");

                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();



//                String[] projection = { MediaStore.Images.Media.DATA };
//                Cursor cursor = managedQuery(uri, projection, null, null, null);
//                startManagingCursor(cursor);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
                    String y=getRealPathFromURI(uri);
//                Log.i("Path", cursor.getString(column_index));



//                    final String y = uri.getPath().substring(5, uri.getPath().length());
//                    Log.i("Path", y);
                    Log.i(null,"imagePath "+y);
                    String newFileName=y;//renameFile(y);
                    SelectedImageList.add(newFileName);

//
//                new Thread(new Runnable() {
//                    public void run(){
//                        new UploadFilesTask().execute(y);
//                    }
//                }).start();

                }

            }
//            callBroadCast();
            }


                Snackbar.make(view1, "Select project", 3000)
                        .setAction("Action", null).show();



//            this.mListView = (ListView) findViewById(R.id.blob_list_view);
//            this.adapter = new ListViewAdapter(this,listItems);
//            mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//                blinkTextView();
                final String path = prefix + listItems.get(position);
//                if (listing.getCommonPrefixes().contains(path)) {
//                    MainActivity.this.prefix = path;
//                    new BlobListTask().execute(path);
//                } else {
//                    new SelectBlobTask().execute(path);
//                }
//                Snackbar.make(view1, "Selected folder "+path, 1500)
//                        .setAction("Action", null).show();
                Log.i("SELECTED FOLDER",path);
//                Toast.makeText(getApplicationContext(), "project selected: "+path, Toast.LENGTH_SHORT).show();
//                Log.i("temp","hi");
                progress=0;
                for(final String y:SelectedImageList)
                {

                    // new Thread(new Runnable() {
                    //  public void run(){

                    Log.i(null,"imagepath "+y+" uploading");
                    new UploadFilesTask().execute(path,y);
                    Log.i(null,"progress value "+progress);
                    //delete all the selected files

                    // }
                    // }).start();

                }
                mListView.setOnItemClickListener(null);
                dialog = new ProgressDialog(MainActivity.this);

                long contentLength = SelectedImageList.size();
                dialog.setMax((int) contentLength);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMessage("Uploading...");

                // TODO: use human-friendly units via setProgressNumberFormat
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                       // dialog.cancel();
                    }
                });
                dialog.show();


                Log.i(null,"selectedImageListSizeupdation loop "+SelectedImageList.size());

//                Log.i(null,"in deleting loop "+SelectedImageList.size());
//                while(progress<=SelectedImageList.size())
//                {
//                    if(progress<SelectedImageList.size())
//                    {
//                        continue;
//                    }
//                    else if(progress==SelectedImageList.size()) {
//
//                        Toast.makeText(MainActivity.this, "Upload successful to folder: "+path, Toast.LENGTH_SHORT).show();
//                        for (String y : SelectedImageList) {
//                            File file = new File(y);
//                            Log.i(null, "image " + y + " deleting");
//                            if (file.exists()) {
//                                file.delete();
//                                ContentResolver contentResolver = getContentResolver();
//                                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                        MediaStore.Images.ImageColumns.DATA + "=?", new String[]{y});
////                                callBroadCast();
//                                if (!file.exists()) {
//                                    Log.i(null, "file deletion success");
//                                } else {
//                                    Log.i(null, "file could not be deleted");
//                                }
//                            } else {
//                                Log.i(null, "file does not exists");
//                            }
//                        }
//                        break;
//                    }
//
//                }
//                Log.i(null,"size of uris is "+selectedImageUri.size());
//                System.out.println("hello world");
//                for(Uri uri:selectedImageUri)
//                {
//
//                    Log.i(null,uri.getPath()+" hello there");
////                  file.delete();
//                    String y=getRealPathFromURI(uri);
//
//                }
            }
        });

//        System.out.println(selectedImageUri.size());

/////////////////////////////////////



            //if (requestCode == PICK_IMAGE_MULTIPLE) {

//            ClipData mClipData = data.getClipData();
//            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//            for (int i = 0; i < mClipData.getItemCount(); i++) {
//
//                ClipData.Item item = mClipData.getItemAt(i);
//                Uri uri = item.getUri();
//                Log.i("Path",uri.getPath());
//                //openPath(uri);
//                // Log.i("Path",uri.toString());
//                final String y=uri.getPath().substring(5,uri.getPath().length());
//                Log.i("Path",y);
//
//
//                new Thread(new Runnable() {
//                    public void run(){
//                        new UploadFilesTask().execute(y);
//                    }
//                }).start();
//
//            }



            //openPath(data.getData());
//            Uri myuri=data.getData();
//          //  Uri uri = data.getData();
//            Log.i("uri",myuri.toString());
//            File file = new File(myuri.getPath());//create path from uri
//            //String real_Path = getRealPathFromUri(MainActivity.this,data.getData());
//            Log.i("Path",myuri.getPath());
            //  Log.i("Path",File.getpath(openPath(myuri)));
            //openPath(myuri);


//            Log.i("Path",Paths.get(data.getData().toString()).toString());
//
//                    SelectedImage=data.getData().toString();
//                    new UploadFilesTask().execute(1);

//                data.getStringExtra().split()

//                if (data.hasExtra("uri")) {
//                   Uri uri = data.getParcelableExtra("uri");
//                    Log.i("Path","OKAY");
//                }
//                else
//                {
//                    Log.i("Path","NOT OKAY");
//                }

//                imagesPathList = new ArrayList<String>();
//                String[] imagesPath = data.getStringExtra("uri").split("\\|");
//                for (String path : imagesPath)
//                {
//                    Log.i("Path",path);
//                }
//            Log.i("Result", "MULTIPLE IMAGES SELECRED");
//                Log.i("Result", x);
//        }

        }
//    }

//    private void blinkTextView() {
//        final Handler handler = new Handler();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int timeToBlink = 1000;
//                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView txt = findViewById(R.id.text);
//                        if(txt.getVisibility() == View.VISIBLE) {
//                            txt.setVisibility(View.INVISIBLE);
//                        } else {
//                            txt.setVisibility(View.VISIBLE);
//                        }
//                        blinkTextView();
//                    }
//                });
//            }
//        }).start();
//    }
//public void callBroadCast(File out) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri contentUri = Uri.fromFile(out); // out is your output file
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    } else {
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//    }
//
//}

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void getPermissions()
    {

        String [] Permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
        };

        for(String per:Permissions)
        {
            // Checking if permission is not granted
            if (ContextCompat.checkSelfPermission(
                    MainActivity.this,
                    per)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat
                        .requestPermissions(
                                MainActivity.this,
                                new String[] { per },
                                1);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getPermissions();
//        Log.i(null,"hello there are you");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener
                (listener);
        preferences = new YassPreferences(getApplicationContext());
        // TODO: if prefs not set, show settings

        client = getS3Client(preferences);
        TextView profileTextView = (TextView)findViewById(R.id.profileTextView);
        if(profileTextView!=null) {
            profileTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), updateProfile.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
//        this.mListView = (ListView) findViewById(R.id.blob_list_view);
//        this.adapter = new ListViewAdapter(this,listItems);

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//                Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
//                String path = prefix + listItems.get(position);
//                if (listing.getCommonPrefixes().contains(path)) {
//                    MainActivity.this.prefix = path;
//                    new BlobListTask().execute(path);
//                } else {
//                    new SelectBlobTask().execute(path);
//                }
//            }
//        });
//        mListView.setAdapter(adapter);
        System.out.println("hello");
        // TODO: long press
        new BlobListTask().execute("");
//        this.mListView = (ListView) findViewById(R.id.blob_list_view);
//        this.adapter = new ListViewAdapter(this,this.listItems);
//        mListView.setAdapter(adapter);
        Log.i(null,"size "+listItems.size());
    }

    @Override
    public void onBackPressed() {
        if (prefix.equals("")) {
            super.onBackPressed();
        }

        int index = prefix.lastIndexOf('/', prefix.length() - 2);
        prefix = prefix.substring(0, index + 1);
        Log.i(TAG, "Changing prefix to: " + prefix);
        new BlobListTask().execute(prefix);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                Log.i(TAG, "reload");
                new BlobListTask().execute(this.prefix);
                return true;

//            case R.id.action_settings:
//                Log.i(TAG, "settings");
//                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(myIntent);
//                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    private class BlobListTask extends AsyncTask<String, Void, Collection<String>> {
        List<String> listItems = new ArrayList<>();
        @Override
        public Collection<String> doInBackground(String... path) {

            String prefix = path[0];
            try {
                MainActivity.this.listing = client.listObjects(new ListObjectsRequest()
                        .withBucketName(preferences.bucketName)
                        .withDelimiter("/")
                        .withPrefix(prefix));
            } catch (AmazonClientException ace) {
                Log.e(TAG, "Error listing with prefix: " + prefix + " " + ace.getMessage());
                return null;
            }
            for (S3ObjectSummary summary : MainActivity.this.listing.getObjectSummaries()) {
                String key = summary.getKey();

                //selectedfolder= key.;
                Log.d(TAG, "listing key: " + key);
                if (key.equals(prefix)) {
                    continue;
                }
                listItems.add(key.substring(prefix.length()));
            }
            for (String commonPrefix : MainActivity.this.listing.getCommonPrefixes()) {
                Log.d(TAG, "listing common prefix: " + commonPrefix);
                commonPrefix=commonPrefix.replace('/',(char)1);
                Log.d(TAG, "listing common prefix: " + commonPrefix);
                listItems.add(commonPrefix.substring(prefix.length()));
            }
            Collections.sort(listItems);
            return listItems;
        }

        @Override
        protected void onPostExecute(Collection<String> listItems) {
            if (listItems == null) {
                Toast.makeText(MainActivity.this, "Could not list keys", Toast.LENGTH_LONG).show();
                return;
            }
            MainActivity.this.listItems.clear();
            Log.i("inpostExecute ","size "+listItems.size());
            MainActivity.this.listItems.addAll(listItems);
            MainActivity.this.mListView = (ListView) findViewById(R.id.blob_list_view);
            MainActivity.this.adapter=new ListViewAdapter(MainActivity.this, MainActivity.this.listItems);
            MainActivity.this.mListView.setAdapter(MainActivity.this.adapter);
            MainActivity.this.adapter.notifyDataSetChanged();
        }
    }

    private class SelectBlobTask extends AsyncTask<String, Integer, File> {
        private ProgressDialog dialog;
        private S3Object object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Downloading...");
            // TODO: use human-friendly units via setProgressNumberFormat
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(false);
                }
            });
            dialog.show();
        }

        @Override
        public File doInBackground(String... path) {
            String key = path[0];
            Log.d(TAG, "downloading: " + key);

            S3Object object;
            try {
                object = client.getObject(preferences.bucketName, path[0]);
            } catch (AmazonClientException ace) {
                Log.e(TAG, "Error getting blob: " + key + " " + ace.getMessage());
                return null;
            }
            long contentLength = object.getObjectMetadata().getContentLength();
            dialog.setMax((int) contentLength);
            File file;
            try {
                String eTag = object.getObjectMetadata().getETag();
                if (eTag == null) {
                    // Some object stores do not return a sensible ETag, e.g., S3Proxy with
                    // filesystem backend.
                    eTag = UUID.randomUUID().toString();
                }
                file = File.createTempFile(eTag, null, MainActivity.this.getCacheDir());
                byte[] buffer = new byte[4096];
                try (InputStream is = object.getObjectContent();
                     OutputStream os = new FileOutputStream(file)) {
                    long progress = 0;
                    while (true) {
                        int count = is.read(buffer);
                        if (count == -1) {
                            break;
                        }
                        os.write(buffer, 0, count);
                        progress += count;
                        publishProgress((int) progress);
                        if (isCancelled()) {
                            Log.i(TAG, "Cancelling: " + key);
                            return null;
                        }
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error downloading blob: " + key + " " + ioe.getMessage());
                return null;
            }

            this.object = object;
            return file;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (dialog != null) {
                dialog.dismiss();
            }

            if (file == null) {
                Toast.makeText(MainActivity.this, "Could not download file", Toast.LENGTH_LONG).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(MainActivity.this, "org.gaul.yass", file);
            String mime = object.getObjectMetadata().getContentType();

            if (mime == null || mime.equals("binary/octet-stream")) {
                int index = object.getKey().lastIndexOf('.');
                if (index != -1) {
                    mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            object.getKey().substring(index + 1));
                    Log.d(TAG, "guessed mime type: " + mime);
                }
            }

            // TODO: does not work for HTML
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                // TODO: convert to text/plain?
                Log.e(TAG, "No intent for " + object.getKey() + " with mime " + mime + " " + anfe);
                Toast.makeText(MainActivity.this, "No registered intent", Toast.LENGTH_LONG).show();
                return;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    static final class YassPreferences {
        final String accessKey;
        final String secretKey;
        final String bucketName;
        final String endpoint;
        final  String region;
        final boolean cameraUpload;
        final boolean cameraUploadOnlyOnWifi;

        YassPreferences(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            // TODO: should default values be null?
            this.accessKey = "Q9VTZDEKKACQ4S2SZF9U";//prefs.getString("access_key", "access_key");
            this.secretKey = "eLh5LfQCLqYPmFN4uhIOZAakSOTSeu8rOSdNZ75C";//prefs.getString("secret_key", "secret_key");
            this.bucketName = "androidtesting";//prefs.getString("bucket_name", "bucket_name");
            this.region="us-east-1";
            this.endpoint = "s3.wasabisys.com";//prefs.getString("endpoint", null);
            this.cameraUpload = prefs.getBoolean("camera_upload", false);
            this.cameraUploadOnlyOnWifi = prefs.getBoolean("camera_upload_only_on_wifi", false);
        }
    }

    static AmazonS3Client getS3Client(YassPreferences preferences) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(preferences.accessKey,
                preferences.secretKey);
        AmazonS3Client client = new AmazonS3Client(awsCreds, new ClientConfiguration());
        if (preferences.endpoint != null && !preferences.endpoint.isEmpty()) {
            client.setEndpoint(preferences.endpoint);
        }
        return client;
    }
}
