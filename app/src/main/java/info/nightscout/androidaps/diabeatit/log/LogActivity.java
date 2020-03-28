package info.nightscout.androidaps.diabeatit.log;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import info.nightscout.androidaps.R;

public class LogActivity extends AppCompatActivity {

    private LogEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_activity_log);

        getSupportActionBar().setTitle(getResources().getString(R.string.nav_navigation_log));
        setTheme(R.style.diabeatit);

        adapter = new LogEventAdapter(this, LogEventStore.getEvents());

        RecyclerView recycler = findViewById(R.id.event_log_layout);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.event_log_empty_notice).setVisibility(adapter.events.isEmpty() ? View.VISIBLE : View.GONE);

        LogEventStore.attachListener(this::change);

    }

    private void change(LogEvent... e) {

        adapter.events.addAll(Arrays.asList(e));
        adapter.events.sort((a, b) -> b.TIMESTAMP.compareTo(a.TIMESTAMP));

        adapter.notifyDataSetChanged();

        findViewById(R.id.event_log_empty_notice).setVisibility(View.GONE);

    }

}

class LogEventAdapter extends RecyclerView.Adapter<LogEventAdapter.LogEventViewHolder> {

    private final Context CONTEXT;
    public List<LogEvent> events;

    public static class LogEventViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public RelativeLayout view;

        public LogEventViewHolder(RelativeLayout view) {

            super(view);
            this.view = view;

        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }

    }

    public LogEventAdapter(Context context, List<LogEvent> events) {

        CONTEXT = context;
        this.events = events;

    }

    @Override
    public LogEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.d_log_event, parent, false);

        return new LogEventViewHolder(view);

    }

    @Override
    public void onBindViewHolder(LogEventViewHolder holder, int position) {

        LogEvent event = events.get(position);
        event.createLayout(CONTEXT, holder.view);

    }

    @Override
    public int getItemCount() { return events.size(); }

}