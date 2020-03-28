package info.nightscout.androidaps.diabeatit.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import org.intellij.lang.annotations.JdkConstants;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Date;

import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;

public class TypeConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Integer urgencyToInteger(Alert.Urgency value) {
        return value == null ? null : value.ordinal();
    }

    @TypeConverter
    public static Alert.Urgency integerToUrgency(Integer value) {
        return value == null ? null : Alert.Urgency.values()[value];
    }

    @TypeConverter
    public static Long instantToTimestamp(Instant value) {
        return value == null ? null : value.toEpochMilli();
    }

    @TypeConverter
    public static Instant timestampToInstant(Long value) {
        return value == null ? null : Instant.ofEpochMilli(value);
    }

    @TypeConverter
    public static byte[] bitmapToBlob(Bitmap value) {
        if (value == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @TypeConverter
    public static Bitmap blobToBitmap(byte[] value) {
        if (value == null) return null;

        Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length);
        return bmp;
    }
}
