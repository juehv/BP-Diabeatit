package info.nightscout.androidaps.diabeatit.ui.log;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.diabeatit.StaticData;
import info.nightscout.androidaps.diabeatit.db.DiabeatitDatabase;
import info.nightscout.androidaps.diabeatit.ui.log.event.BolusEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.CarbsEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.NoteEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.SportsEvent;

public class LogEventStore {

	private static List<LogEventStoreListener> listeners = new ArrayList<>();
	private static List<LogEvent> events = new ArrayList<>();

	static {
		DiabeatitDatabase db = Room.databaseBuilder(
					MainApp.instance().getApplicationContext(),
					DiabeatitDatabase.class,
					StaticData.ROOM_DATABASE_NAME)
				.allowMainThreadQueries()
				.build();

		events.addAll(db.bolusEventDao().getLimited());
		events.addAll(db.carbsEventDao().getLimited());
		events.addAll(db.sportsEventDao().getLimited());
		events.addAll(db.noteEventDao().getLimited());

		events.sort((a, b) -> b.TIMESTAMP.compareTo(a.TIMESTAMP));

		for (LogEventStoreListener l : listeners) {
			l.onDatasetChange((LogEvent[]) events.toArray());
		}
	}

	public interface LogEventStoreListener {

		void onDatasetChange(LogEvent... e);

	}

	public static void attachListener(LogEventStoreListener listener) {

		listeners.add(listener);

	}

	public static void addEvent(LogEvent event) {

		events.add(event);
		events.sort((a, b) -> b.TIMESTAMP.compareTo(a.TIMESTAMP));

		for (LogEventStoreListener l : listeners)
			l.onDatasetChange(event);

		Thread t = new Thread(() -> {
			DiabeatitDatabase db = Room.databaseBuilder(
						MainApp.instance().getApplicationContext(),
						DiabeatitDatabase.class,
						StaticData.ROOM_DATABASE_NAME)
					.build();
			if (event instanceof BolusEvent) {
				db.bolusEventDao().insertAll((BolusEvent) event);
			} else if (event instanceof CarbsEvent) {
				db.carbsEventDao().insertAll((CarbsEvent) event);
			} else if (event instanceof SportsEvent) {
				db.sportsEventDao().insertAll((SportsEvent) event);
			} else if (event instanceof NoteEvent) {
				db.noteEventDao().insertAll((NoteEvent) event);
			}
		});
		t.start();
	}

	public static List<LogEvent> getEvents() {

		return new ArrayList<>(events);

	}

}