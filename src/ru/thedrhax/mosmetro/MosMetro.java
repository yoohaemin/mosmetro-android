package ru.thedrhax.mosmetro;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class MosMetro extends Activity {
	// UI Elements
	TextView text_messages;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		text_messages = (TextView)findViewById(R.id.text_messages);
	}

	// Push received messages to the UI thread
	private final Handler handler = new Handler() {
		public void handleMessage(Message message) {
			String text = message.getData().getString("text");
			if (text == null) return;

			text_messages.append(text);
		}
	};

	// Connection sequence
	final MosMetroConnection connection = new MosMetroConnection() {
		// Send log messages to Handler
		public void log (String message) {
			Message msg = handler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putString("text", message + "\n");
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	};

	// Run connection sequence in background thread
	private Thread thread;
	private final Runnable task = new Runnable() {
		public void run () {
			connection.connect();
		}
	};

	// Handle connection button
	public void connect (View view) {
		// Start connection thread if not already running
		if ((thread == null) || (!thread.isAlive())) {
			thread = new Thread(task);
			thread.start();
		}
	}
}
