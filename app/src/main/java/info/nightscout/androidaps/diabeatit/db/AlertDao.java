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

    @Query("SELECT * FROM Alert ORDER BY timestamp LIMIT 512")
    List<Alert> getLimited();

    @Insert
    void insertAll(Alert... alerts);

    @Delete
    void delete(Alert alert);

    @Update
    void update(Alert... alerts);
}
