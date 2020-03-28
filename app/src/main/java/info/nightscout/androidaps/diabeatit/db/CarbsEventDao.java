package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import info.nightscout.androidaps.diabeatit.ui.log.event.CarbsEvent;

@Dao
public interface CarbsEventDao {
    @Query("SELECT * FROM carbsevent")
    List<CarbsEvent> getAll();

    @Query("SELECT * FROM CarbsEvent ORDER BY timestamp DESC LIMIT 512")
    List<CarbsEvent> getLimited();

    @Insert
    void insertAll(CarbsEvent... events);

    @Delete
    void delete(CarbsEvent event);
}
