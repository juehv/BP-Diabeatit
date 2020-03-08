package info.nightscout.androidaps.diabeatit.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileDownloader {

  public interface DownloadCallback {

    void onDownloadCompleted(String filePath);
    void onDownloadFailed(Exception error);

  }

	public static void download(Context context, String webUri, @Nullable String fileName, DownloadCallback callback) {

		try {

			DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

			long id = dm.enqueue(buildRequest(webUri, fileName));
			context.registerReceiver(buildReceiver(dm, id, callback), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		} catch (Exception e) {

			callback.onDownloadFailed(e);

		}

	}

  private static DownloadManager.Request buildRequest(String uri, String fileName) {

		if (fileName == null)
			fileName = URLUtil.guessFileName(uri, null, null);

		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
		request.setTitle(fileName);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName == null ? "data" : fileName);

		return request;

  }

  private static BroadcastReceiver buildReceiver(DownloadManager dm, long id, DownloadCallback callback) {

    return new BroadcastReceiver() {

		  @Override
		  public void onReceive(Context context, Intent intent) {

		    try {

					// Check if our download completed; unregister receiver if necessary
					if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) != id) return;
					context.unregisterReceiver(this);

					// Get status information
			    Cursor c = dm.query(new DownloadManager.Query().setFilterById(id));
			    c.moveToFirst();

			    // Check if the download failed
			    if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {

			      // Return error
				    int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
						throw new Exception((reason < 1000 ? "HTTP Error " : "Internal Error ") + reason);

			    }

			    String filePath = Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath();
			    c.close();

			    // Successful; move file to internal storage
			    File from = new File(filePath);
			    Path result = Files.move(Paths.get(from.toURI()), Paths.get(new File(context.getFilesDir(), from.getName()).toURI()), StandardCopyOption.REPLACE_EXISTING);

			    callback.onDownloadCompleted(result.toString());

				} catch (Exception e) {

			      callback.onDownloadFailed(e);

				}

		  }

		};

  }

}