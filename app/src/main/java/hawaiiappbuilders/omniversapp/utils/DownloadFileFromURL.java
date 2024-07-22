package hawaiiappbuilders.omniversapp.utils;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

// -------------------------- Download Task -------------------------------------

/**
 * Background Async Task to download file
 */
public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    /**
     * Before starting background thread Show Progress Bar Dialog
     */

    Context context;
    BaseActivity activity;
    File folder;
    String title;

    ProgressDialog pDialog;

    String fileUrl;
    String outputFile;

    boolean bContinueDonwload = true;

    public DownloadFileFromURL(Context context, File folder, String title) {
        this.context = context;
        this.activity = (BaseActivity) context;

        this.folder = folder;
        this.title = title;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.title_download_progress));
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_states));
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        /*pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel_download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bContinueDonwload = false;
                dialog.dismiss();
            }
        });*/

        pDialog.show();
        bContinueDonwload = true;
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            publishProgress("0");

            fileUrl = f_url[0];
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            fileName = URLDecoder.decode(fileName, "UTF-8");
            int dotIndex = fileName.indexOf(".");
            if (dotIndex > 0) {
                fileName = fileName.substring(0, dotIndex);
            } else {
                fileName = title;
            }

            int fileIndex = 1;
            File candidateFile = new File(folder, String.format("%s.apk", fileName));
            if (candidateFile.exists()) {
                do {
                    candidateFile = new File(folder, String.format("%s%d.apk", fileName, fileIndex++));
                } while(candidateFile.exists());
            }

            URL url = new URL(fileUrl);
            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            // String outputFile = Environment.getExternalStorageDirectory().toString() + "/test.pdf";
            // outputFile = new File(folder, String.format("%d.pdf", System.currentTimeMillis())).getAbsolutePath();
            outputFile = candidateFile.getAbsolutePath();

            OutputStream output = new FileOutputStream(outputFile);

            byte data[] = new byte[1024];

            long total = 0;

            while (bContinueDonwload && (count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            if (bContinueDonwload) {
                return outputFile;
            } else {
                new File(outputFile).delete();
                return "canceled";
            }
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        pDialog.dismiss();

        if (TextUtils.isEmpty(file_url)) {
            activity.showToastMessage(R.string.error_download);
        } else {
            if (!"canceled".equalsIgnoreCase(file_url)) {
                File downloadedFile = new File(file_url);
                if (downloadedFile.exists()) {
                    Uri fileUri = activity.getFileUri(downloadedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Log.e("appInstall", "Error in opening the file!");
                    }
                }
            }
        }
    }
}
