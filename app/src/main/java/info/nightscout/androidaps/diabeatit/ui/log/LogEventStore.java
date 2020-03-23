package info.nightscout.androidaps.diabeatit.ui.log;

import java.util.ArrayList;
import java.util.List;

public class LogEventStore {

	public interface LogEventStoreListener {

		void onDatasetChange(LogEvent e);

	}

	private static List<LogEventStoreListener> listeners = new ArrayList<>();

	private static List<LogEvent> events = new ArrayList<>();

	public static void attachListener(LogEventStoreListener listener) {

		listeners.add(listener);

	}

	public static void addEvent(LogEvent event) {

		events.add(event);
		events.sort((a, b) -> a.TIMESTAMP.compareTo(a.TIMESTAMP));

		for (LogEventStoreListener l : listeners)
			l.onDatasetChange(event);

	}

	public static List<LogEvent> getEvents() {

		return new ArrayList<>(events);

	}

}