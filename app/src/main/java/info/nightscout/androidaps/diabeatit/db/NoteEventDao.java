package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import info.nightscout.androidaps.diabeatit.ui.log.event.NoteEvent;

@Dao
public interface NoteEventDao {
    @Query("SELECT * FROM noteevent")
    List<NoteEvent> getAll();

    @Query("SELECT * FROM NoteEvent ORDER BY timestamp DESC LIMIT 512")
    List<NoteEvent> getLimited();

    @Insert
    void insertAll(NoteEvent... events);

    @Delete
    void delete(NoteEvent event);
}
