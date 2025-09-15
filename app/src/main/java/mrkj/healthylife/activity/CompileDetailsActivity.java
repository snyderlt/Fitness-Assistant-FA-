package mrkj.healthylife.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.util.Map;

import mrkj.healthylife.R;
import mrkj.healthylife.base.BaseActivity;
import mrkj.healthylife.utils.DateUtils;
import mrkj.healthylife.utils.GetPictureFromLocation;
import mrkj.healthylife.utils.SaveKeyValues;
import mrkj.library.wheelview.circleimageview.CircleImageView;

/**
 * Edit user information
 */
public class CompileDetailsActivity extends BaseActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_CAMERA = 1; // Take a photo
    private static final int PHOTO_REQUEST_GALLERY = 2; // Select from gallery
    private static final int PHOTO_REQUEST_GALLERY2 = 4; // Select from gallery (alternative)
    private static final int PHOTO_REQUEST_CUT = 3; // Result
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg"; // Image file name
    // 1. Change avatar
    private CircleImageView head_image; // Display avatar
    private TextView change_image; // Change avatar
    private String path; // Avatar path
    private File tempFile; // Image file path
    // 2. Change nickname
    private String nick_str; // User nickname
    private EditText change_nick; // Change nickname
    // 3. Change gender
    private RadioGroup change_gender; // Change gender
    private String sex_str; // Gender
    // 4. Change birthdate
    private TextView change_birthDay; // Change birthdate
    private String date; // Birthdate
    // Birthdate details
    private int birth_year;
    private int birth_month;
    private int birth_day;
    // Current date
    private int now_year;
    private int now_month;
    private int now_day;
    // 5. Change height
    private EditText change_height; // Change height
    private int height;
    // 6. Change weight
    private EditText change_weight; // Change weight
    private int weight;
    // 7. Change stride length
    private EditText change_length; // Change stride length
    private int length;
    // User's age
    // Confirm and save changes
    private Button change_OK_With_Save; // Save and exit

    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle("Edit Personal Information", this);
        setMyBackGround(R.color.watm_background_gray);
        setTitleTextColor(R.color.theme_blue_two);
        setTitleLeftImage(R.mipmap.mrkj_back_blue);
        setResult(RESULT_OK);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_compile_details);
    }

    @Override
    protected void initValues() {
        path = SaveKeyValues.getStringValues("path", "path");
        nick_str = SaveKeyValues.getStringValues("nick", "Not filled");
        sex_str = SaveKeyValues.getStringValues("gender", "Male");
        // Get current date
        getTodayDate();
        birth_year = SaveKeyValues.getIntValues("birth_year", now_year);
        birth_month = SaveKeyValues.getIntValues("birth_month", now_month);
        birth_day = SaveKeyValues.getIntValues("birth_day", now_day);
        date = birth_year + "-" + birth_month + "-" + birth_day;

        height = SaveKeyValues.getIntValues("height", 0);
        weight = SaveKeyValues.getIntValues("weight", 0);
        length = SaveKeyValues.getIntValues("length", 0);
    }

    /**
     * Get today's date
     */
    private void getTodayDate() {
        Map<String, Object> map = DateUtils.getDate();
        now_year = (int) map.get("year");
        now_month = (int) map.get("month");
        now_day = (int) map.get("day");
    }

    @Override
    protected void initViews() {
        // 1. Change avatar
        head_image = (CircleImageView) findViewById(R.id.head_pic);
        change_image = (TextView) findViewById(R.id.change_image);
        // 2. Change nickname
        change_nick = (EditText) findViewById(R.id.change_nick);
        // 3. Change gender
        change_gender = (RadioGroup) findViewById(R.id.change_gender);
        // 4. Change birthdate
        change_birthDay = (TextView) findViewById(R.id.change_date);
        // Save and exit
        change_OK_With_Save = (Button) findViewById(R.id.change_ok);

        // Modify parameters
        change_height = (EditText) findViewById(R.id.change_height);
        change_weight = (EditText) findViewById(R.id.change_weight);
        change_length = (EditText) findViewById(R.id.change_length);
    }

    @Override
    protected void setViewsListener() {
        change_image.setOnClickListener(this);
        change_OK_With_Save.setOnClickListener(this);
        change_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                hideKeyBoard();
                switch (checkedId) {
                    case R.id.change_girl:
                        sex_str = "Female";
                        break;
                    case R.id.change_boy:
                        sex_str = "Male";
                        break;
                    default:
                        break;
                }
            }
        });
        change_birthDay.setOnClickListener(this);
    }

    @Override
    protected void setViewsFunction() {
        // 1. Set avatar
        if (!"path".equals(path)) {
            Log.e("Image path", path);
            head_image.setImageBitmap(BitmapFactory.decodeFile(path));
        }
        change_nick.setHint(nick_str);
        change_nick.setHintTextColor(getResources().getColor(R.color.btn_gray));
        change_height.setHint(String.valueOf(height));
        change_height.setHintTextColor(getResources().getColor(R.color.btn_gray));
        change_length.setHint(String.valueOf(length));
        change_length.setHintTextColor(getResources().getColor(R.color.btn_gray));
        change_weight.setHint(String.valueOf(weight));
        change_weight.setHintTextColor(getResources().getColor(R.color.btn_gray));
    }

    /**
     * Click event handler
     * @param v
     */
    @Override
    public void onClick(View v) {
        hideKeyBoard();
        switch (v.getId()) {
            case R.id.change_image: // Change avatar
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Image");
                builder.setMessage("You can change the default image by selecting from the gallery or taking a photo!");
                builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                        gallery();
                    }
                });
                builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                        camera();
                    }
                });
                builder.create(); // Create
                builder.show(); // Show
                break;
            case R.id.change_date: // Change birthdate
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        birth_day = year;
                        birth_month = monthOfYear + 1;
                        birth_day = dayOfMonth;
                        date = birth_year + "-" + birth_month + "-" + birth_day;
                        change_birthDay.setText(date);
                    }
                }, birth_year, birth_month - 1, birth_day);
                datePickerDialog.setTitle("Set Birthdate");
                datePickerDialog.show();
                break;
            case R.id.change_ok: // Save and exit
                if (tempFile != null) {
                    SaveKeyValues.putStringValues("path", tempFile.getPath()); // Save image path
                }
                if (!"".equals(change_nick.getText().toString())) {
                    SaveKeyValues.putStringValues("nick", change_nick.getText().toString()); // Save nickname
                }
                SaveKeyValues.putStringValues("gender", sex_str); // Save gender
                SaveKeyValues.putStringValues("birthday", birth_year + " Year " + birth_month + " Month " + birth_day + " Day"); // Save birthdate
                SaveKeyValues.putIntValues("birth_year", birth_year);
                SaveKeyValues.putIntValues("birth_month", birth_month);
                SaveKeyValues.putIntValues("birth_day", birth_day);
                SaveKeyValues.putIntValues("age", now_year - birth_year); // Save age
                if (!"".equals(change_height.getText().toString())) {
                    SaveKeyValues.putIntValues("height", Integer.parseInt(change_height.getText().toString().trim())); // Save height
                }
                if (!"".equals(change_length.getText().toString())) {
                    SaveKeyValues.putIntValues("length", Integer.parseInt(change_length.getText().toString().trim())); // Save stride length
                }
                if (!"".equals(change_weight.getText().toString())) {
                    SaveKeyValues.putIntValues("weight", Integer.parseInt(change_weight.getText().toString().trim())); // Save weight
                }
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * Get image from gallery
     */
    public void gallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        } else {
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY2);
        }
    }

    /**
     * Get image from camera
     */
    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // Check if SD card is available for storage
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    /**
     * Check if SD card is available
     *
     * @return
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set image from result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_REQUEST_GALLERY2) {
            if (data != null) {
                // Get full path of the image
                String path = GetPictureFromLocation.selectImage(getApplicationContext(), data);
                crop(Uri.parse("file://" + path));
            }

        } else if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                // Get full path of the image
                String path = GetPictureFromLocation.getPath(getApplicationContext(), data.getData());
                crop(Uri.parse("file://" + path));
            }

        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            crop(Uri.fromFile(tempFile));
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                Log.e("uri", Uri.fromFile(tempFile).toString());
                head_image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hide keyboard
     */
    private void hideKeyBoard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(CompileDetailsActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Crop image
     *
     * @function:
     * @author:Jerry
     * @date:2013-12-30
     * @param uri
     */
    private void crop(Uri uri) {
        Log.e("URI", uri.getPath());
        Log.e("URI", uri.toString());
        // Crop image intent
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // Crop aspect ratio 1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // Output image size
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true); // Black border
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        // Image format
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // Disable face detection
        intent.putExtra("return-data", true); // true: do not return uri, false: return uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
}
