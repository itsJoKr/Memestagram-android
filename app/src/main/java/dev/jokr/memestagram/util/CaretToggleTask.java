package dev.jokr.memestagram.util;

import android.os.AsyncTask;

/**
 * Created by JoKr on 10/6/2016.
 */

public class CaretToggleTask extends AsyncTask<Void, Void, Void> {

    private CaretToggleCallback callback;

    public CaretToggleTask(CaretToggleCallback callback) {
        super();
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(true) {
            try {
                Thread.sleep(550);
                publishProgress();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        callback.toggleCaret();
    }

    public interface CaretToggleCallback {

        public void toggleCaret();

    }
}
