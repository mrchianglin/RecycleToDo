package chiangli.edu.calpoly.edu.RecycleToDo;

    import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static ArrayList<EntryUtil.Entry> entryArrayList;
    private MyAdapter a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate() called");

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        assert rv != null;
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState == null) {
            entryArrayList = new ArrayList<EntryUtil.Entry>();
        } else {
            entryArrayList = savedInstanceState.getParcelableArrayList("ENTRIES");
        }
        Gson gson = new Gson();
        try {
            String path = getFilesDir().getAbsolutePath();
            BufferedReader br = new BufferedReader(new FileReader(path+".json"));
            entryArrayList = gson.fromJson(br, ListWrapper.class).entries;
            for (EntryUtil.Entry entry : entryArrayList) {
                System.out.println(entry.getTaskName() + entry.getIsChecked());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        a = new MyAdapter(entryArrayList);
        rv.setAdapter(a);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                EntryViewHolder evh = (EntryViewHolder) viewHolder;
                if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                    int index = viewHolder.getAdapterPosition();
                    confirmDialog(index);
                }
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rv);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Button clickButton = (Button) findViewById(R.id.submit);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EditText header = (EditText) findViewById(R.id.header);
                String task = header.getText().toString();
                Log.d(TAG, "clickButton onClick called" + task);

                if (task.trim().length() > 0) {
                    header.setText("Task Name");

                    EntryUtil.Entry entry = new EntryUtil.Entry(task, false);
                    entryArrayList.add(entry);
                    a.notifyDataSetChanged();
                    for (EntryUtil.Entry entry1 : entryArrayList) {
                        System.out.println(entry1.getTaskName() + entry1.getIsChecked());
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Enter a task name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        EditText header = (EditText) findViewById(R.id.header);
        header.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "header onKey called");
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    EditText header = (EditText) findViewById(R.id.header);
                    String task = header.getText().toString();

                    if (task.trim().length() > 0) {
                        header.setText("Task Name");

                        EntryUtil.Entry entry = new EntryUtil.Entry(task, false);
                        entryArrayList.add(entry);
                        a.notifyDataSetChanged();

                        for (EntryUtil.Entry entry1 : entryArrayList) {
                            System.out.println(entry1.getTaskName() + entry1.getIsChecked());
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Enter a task name", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
                return false;
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<EntryViewHolder> {
        private ArrayList<EntryUtil.Entry> entries;

        public MyAdapter(ArrayList<EntryUtil.Entry> entries) {
            this.entries = entries;
        }

        @Override
        public int getItemCount() {
            if (entries == null) {
                return 0;
            }
            return entries.size();
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.entry_item;
        }

        @Override
        public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EntryViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        }

        @Override
            public void onBindViewHolder(EntryViewHolder holder, int position) {
            holder.bind(entries.get(position));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("ENTRIES", entryArrayList);
        super.onSaveInstanceState(outState);
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        private CheckBox cb;
        public EntryUtil.Entry entry;

        public EntryViewHolder(final View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tvTask);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), EditEntry.class);
                    int index = getAdapterPosition();
                    i.putExtra("INDEX", index);
                    i.putExtra("ENTRYNAME", entry.getTaskName());
                    i.putExtra("ENTRYCHECK", entry.getIsChecked());
                    itemView.getContext().startActivity(i);
                }
            });
            cb = (CheckBox) itemView.findViewById(R.id.cbCheck);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    entry.setChecked(isChecked);
                }

            });
        }

        public void bind(EntryUtil.Entry e) {
            this.entry = e;
            tv.setText(entry.getTaskName());
            cb.setChecked(entry.getIsChecked());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        a.notifyDataSetChanged();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
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
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            String taskString = "";
            Log.d(TAG, "onOptionsItemSelected; action_share");
            for (int i = 0; i < entryArrayList.size(); i++) {
                String name = entryArrayList.get(i).getTaskName();
                taskString += name + "\t ";
                if (entryArrayList.get(i).getIsChecked()) {
                    taskString += "1 \n";
                } else {
                    taskString += "0 \n";
                }
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, taskString);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
// content URI or Bundles with Intents
// send ID of the index in list in Bundle
    //global way of referencing that list
    // create todoManager that holds reference to the list
    // static reference/global variable (won    t bekilled)
    //

    private void confirmDialog(final int position) {
        final EntryUtil.Entry e = entryArrayList.get(position);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to delete this entryArrayList?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        entryArrayList.remove(position);
                        a.notifyItemRemoved(position);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
