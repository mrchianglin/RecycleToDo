package chiangli.edu.calpoly.edu.RecycleToDo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jonathan Chianglin on 10/19/2016.
 */

public class EntryUtil {

    private static ArrayList<Entry> entries = new ArrayList<>();

    public static class Entry implements Parcelable {
        private String taskName;
        private boolean isChecked;
        private String path;

        public Entry(String taskName, boolean isChecked) {
            this.taskName = taskName;
            this.isChecked = isChecked;
        }

        protected Entry(Parcel in) {
            taskName = in.readString();
            isChecked = in.readByte() != 0;
        }

        public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
            @Override
            public Entry createFromParcel(Parcel in) {
                Entry e = new Entry(in.readString(), false);
                boolean[] arr = new boolean[1];
                in.readBooleanArray(arr);
                e.setChecked(arr[0]);

                return e;
            }

            @Override
            public Entry[] newArray(int size) {
                return new Entry[size];
            }
        };

        public String getPath() { return path; }

        public void setPath(String path) { this.path = path; }

        public void removePath() {this.path = null; }

        public String getTaskName() {
            return taskName;
        }

        public boolean getIsChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            this.isChecked = checked;
        }

        public void setTaskName(String taskName) { this.taskName = taskName; }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(taskName);
            boolean[] arr = new boolean[1];
            arr[0] = isChecked;
            dest.writeBooleanArray(arr);
        }
    }
}
