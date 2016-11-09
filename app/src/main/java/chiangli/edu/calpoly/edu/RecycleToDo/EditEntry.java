package chiangli.edu.calpoly.edu.RecycleToDo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import com.google.gson.Gson;

import static chiangli.edu.calpoly.edu.RecycleToDo.MainActivity.entryArrayList;
import static chiangli.edu.calpoly.edu.RecycleToDo.R.styleable.Toolbar;


public class EditEntry extends AppCompatActivity {

    public int position;
    public EntryUtil.Entry entry;
    private String selectedImagePath;
    private static final String TAG = EditEntry.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        final int index = bundle.getInt("INDEX");
        position = index;
        System.out.println("INDEX IS "+ index);
        String entryName = bundle.getString("ENTRYNAME");
        boolean entryCheck = bundle.getBoolean("ENTRYCHECK");
        entry = entryArrayList.get(position);  // edit this thing

        final EditText et = (EditText) findViewById(R.id.editET);
        final CheckBox cb = (CheckBox) findViewById(R.id.editCB);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entry.setChecked(isChecked);
            }

        });
        Button addPhoto = (Button) findViewById(R.id.editAddB);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select a picture"), 1);
            }
        });
        Button removePhoto = (Button) findViewById(R.id.editRemoveB);
        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView) findViewById(R.id.editIV);
                iv.setImageURI(null);
                entry.removePath();

            }
        });

        ImageView iv= (ImageView)findViewById(R.id.editIV);
        if(entry.getPath() != null) {
            Log.d(TAG, "PATH IS " + entry.getPath());
            File imgFile = new  File(entry.getPath());
            iv.setImageURI(Uri.fromFile(imgFile));
        }
        et.setText(entryName);
        cb.setChecked(entryCheck);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                ImageView iv = (ImageView) findViewById(R.id.editIV);
                iv.setImageURI(uri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    File dir = getFilesDir(); // createDir
                    File image = new File(dir, "" + System.currentTimeMillis());
                    String path = image.getAbsolutePath();
                    entry.setPath(path);
                    image.createNewFile();
                    OutputStream out = new FileOutputStream(image);
                    // From StackOverflow
                    byte[] buffer = new byte[1024]; // Adjust if you want
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1)
                    {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.close();
                    Log.d(TAG, "Path set to " + entry.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == 2) {
                ImageView iv = (ImageView) findViewById(R.id.editIV);
                iv.setImageURI(null);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isChangingConfigurations()) {
            Gson gson = new Gson();
            String json = gson.toJson(new ListWrapper(entryArrayList));
            try {
                File dir = getFilesDir(); // createDir
                String path = dir.getAbsolutePath();
                FileWriter writer = new FileWriter(path+".json");
                writer.write(json);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "EditEntry onPause() is called");
        EditText et = (EditText)findViewById(R.id.editET);
        CheckBox cb = (CheckBox)findViewById(R.id.editCB);

        entry.setChecked(cb.isChecked());
        entry.setTaskName(et.getText().toString());
        entryArrayList.set(position, entry);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("ENTRIES", entryArrayList);
        super.onSaveInstanceState(outState);
    }
}
