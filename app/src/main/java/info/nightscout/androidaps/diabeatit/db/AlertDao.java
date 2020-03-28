package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import retrofit2.http.DELETE;

@Dao
public interface AlertDao {
    @Query("SELECT * FROM Alert")
    List<Alert> getAll();

    @Query("SELECT * FROM Alert WHERE active = '1' ORDER BY timestamp")
    List<Alert> getActive();

    @Query("SELECT * FROM Alert WHERE active = '0' ORDER BY timestamp LIMIT 128")
    List<Alert> getDismissedLimited();

    @Insert
    void insertAll(Alert... alerts);

    @Delete
    void delete(Alert alert);

    @Update
    void update(Alert... alerts);
}
