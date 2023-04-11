package com.routeassociation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.DriverDetails;
import com.routeassociation.pojo.DriverTypeDetails;
import com.routeassociation.util.Util;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class AddDriverActivity extends AppCompatActivity {
    private Context context;
    private Button btnTakePicture, btnSave;
    private EditText edtDriverName, edtDriverContactNumber, edtDriverAddress;
    private ImageView imageView;
    private String imgBase64String, imgContentType, imgFileName;
    private Util util;
    private int orgId, depId;
    private String response;
    private Spinner driverTypeSpinner;
    private ProgressDialog progDailog;
    private String drvName, drvContactNumber, drvAddress, drvType;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private String userChoosenTask;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(AddDriverActivity.this,
                    MainActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Bus Driver/Attendant");
        toolbar.setTitleTextColor(Color.WHITE);

        //load login details
        try {
            SharedPreferences loginDetails = getSharedPreferences("user", 0);
            String params = loginDetails.getString("params", null);

            JSONArray jsonArray = new JSONArray(params);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String status = jsonObject.getString("status");
            if (status.equals("SUCCESS")) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");

                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);

                }
                //set white back icon to toolbar
                final Drawable upArrow = getResources().getDrawable(R.drawable.back);
                upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);

                context = this;
                util = new Util(context);

                //get reference
                imageView = (ImageView) findViewById(R.id.imageView);
                edtDriverName = (EditText) findViewById(R.id.edtDriverName);
                edtDriverContactNumber = (EditText) findViewById(R.id.edtDriverContactNumber);
                edtDriverAddress = (EditText) findViewById(R.id.edtDriverAddress);
                btnSave = (Button) findViewById(R.id.btnSave);
                btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
                driverTypeSpinner = findViewById(R.id.driverTypeSpinner);

                final ArrayList<DriverTypeDetails> driverTypeDetailsArrayList = new ArrayList<>();
                driverTypeDetailsArrayList.add(new DriverTypeDetails(1, "Driver", "D"));
                driverTypeDetailsArrayList.add(new DriverTypeDetails(2, "Maid", "M"));

                ArrayAdapter<DriverTypeDetails> spinnerData = new ArrayAdapter<DriverTypeDetails>(context, R.layout.support_simple_spinner_dropdown_item, driverTypeDetailsArrayList);
                driverTypeSpinner.setAdapter(spinnerData);

                drvType = driverTypeDetailsArrayList.get(0).getTypeCode();

                driverTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        drvType = driverTypeDetailsArrayList.get(position).getTypeCode();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        drvType = driverTypeDetailsArrayList.get(0).getTypeCode();
                    }
                });

                //check internet connection
                CheckInternet checkInternet = new CheckInternet();
                boolean checkConnection = checkInternet.checkConnection(context);
                if (checkConnection) {

                } else {
                    Toast.makeText(context,
                            "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
                }

                btnTakePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });


                //save driver details
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            drvName = edtDriverName.getText().toString().trim();
                            drvContactNumber = edtDriverContactNumber.getText().toString().trim();
                            drvAddress = edtDriverAddress.getText().toString().trim();

                            //name
                            if (drvName.equals("") || drvName == null) {
                                Toast.makeText(context, "Driver name should not be empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //contact number
                            if (drvContactNumber.equals("") || drvContactNumber == null) {
                                Toast.makeText(context, "Driver contact number should not be empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //contact number
                            if (drvContactNumber.length() < 10) {
                                Toast.makeText(context, "Please enter valid contact number!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //address
                            if (drvAddress.equals("") || drvAddress == null) {
                                Toast.makeText(context, "Driver address should not be empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //add driver
                            AddDriverActivity.AddNewDriver addNewDriver = new AddDriverActivity.AddNewDriver();
                            addNewDriver.execute(null, null);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        }catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);

        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
    }
    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try {
                byteBuffer.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
        return bytesResult;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDriverActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = AddDriverActivity.checkPermission(AddDriverActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    //gallary
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    //camera
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File downloadsDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        imgFileName = "DRV_IMG_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(downloadsDirectory, imgFileName);

        try {
            OutputStream out = new FileOutputStream(imageFile.getPath());
            out.write(bytes.toByteArray());
            out.close();

            Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFile);
            byte[] byteArray = getBytes(context, imageUri);
            byte[] encoded = Base64.encodeBase64(byteArray);
            imgBase64String = new String(encoded);

            String[] mime = getMimeType(context, imageUri).split(",");
            imgContentType = mime[1];

            imageView.setImageBitmap(thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getMimeType(Context context, Uri uri) {
        String extension, type = "-";

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
            type = mime.getMimeTypeFromExtension(extension);
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension + "," + type;
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo)
    {
        String path = MediaStore.Images.Media.insertImage(AddDriverActivity.this.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                ContextWrapper contextWrapper = new ContextWrapper(context);
                File downloadsDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                imgFileName = "DRV_IMG_" + System.currentTimeMillis() + ".jpg";
                File imageFile = new File(downloadsDirectory, imgFileName);

                OutputStream out = new FileOutputStream(imageFile.getPath());
                out.write(bytes.toByteArray());
                out.close();

                Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFile);
                byte[] byteArray = getBytes(context, imageUri);
                byte[] encoded = Base64.encodeBase64(byteArray);
                imgBase64String = new String(encoded);

                String[] mime = getMimeType(context, imageUri).split(",");
                imgContentType = mime[1];

                imageView.setImageBitmap(thumbnail);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(thumbnail);
    }


    public class AddNewDriver extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(context);
            progDailog.setMessage("Loading...");
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.setCanceledOnTouchOutside(false);
            progDailog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                progDailog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            if (drvType.equalsIgnoreCase("D")) {
                                Toast.makeText(context, "Driver details added successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Maid details added successfully.", Toast.LENGTH_SHORT).show();
                            }

                            //divert home screen
                            Intent intent = new Intent(AddDriverActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(context, "Failed to add the details!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Failed to add the details!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to add the details!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = util.addDriver(orgId, depId, drvName, drvContactNumber, drvAddress, imgBase64String, imgContentType, imgFileName, drvType);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (progDailog != null) {
                progDailog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (progDailog != null) {
                progDailog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (progDailog != null) {
                progDailog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
