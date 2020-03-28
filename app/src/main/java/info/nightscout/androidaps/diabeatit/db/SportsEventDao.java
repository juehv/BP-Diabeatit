package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import info.nightscout.androidaps.diabeatit.ui.log.event.SportsEvent;

@Dao
public interface SportsEventDao {
    @Query("SELECT * FROM SportsEvent")
    List<SportsEvent> getAll();

    @Query("SELECT * FROM SportsEvent ORDER BY timestamp DESC LIMIT 512")
    List<SportsEvent> getLimited();

    @Insert
    void insertAll(SportsEvent... events);

    @Delete
    void delete(SportsEvent event);
}
