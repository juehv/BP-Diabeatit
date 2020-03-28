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

    @Insert
    void insertAll(CarbsEvent... events);

    @Delete
    void delete(CarbsEvent event);
}
