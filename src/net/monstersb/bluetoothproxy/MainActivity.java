package net.monstersb.bluetoothproxy;

import java.net.InetAddress;
import java.net.URL;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView proxy_ip = (TextView) findViewById(R.id.proxy_ip);
		TextView proxy_port = (TextView) findViewById(R.id.proxy_port);
		TextView server_base = (TextView) findViewById(R.id.server_base);
		proxy_ip.setText(Utils.getSetting("proxy_ip"));
		proxy_port.setText(Utils.getSetting("proxy_port"));
		server_base.setText(Utils.getSetting("server_base"));

		Switch proxy_switch = (Switch) findViewById(R.id.proxy_switch);
		proxy_switch.setChecked(Utils.getSettingSwitch());
		proxy_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				SharedPreferences settings = MainActivity.this
						.getSharedPreferences("BlutToothProxySettings",
								MODE_WORLD_READABLE
										| Context.MODE_MULTI_PROCESS);
				Editor editor = settings.edit();
				editor.putBoolean("switch", arg1);
				Log.d("test", "set " + arg1);
				editor.commit();
				Log.d("test", "get " + Utils.getSettingSwitch());
				Toast.makeText(MainActivity.this, "Saved successfully",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public void save(View v) {
		TextView proxy_ip = (TextView) findViewById(R.id.proxy_ip);
		TextView proxy_port = (TextView) findViewById(R.id.proxy_port);
		TextView server_base = (TextView) findViewById(R.id.server_base);

		try {
			String sip = proxy_ip.getText().toString();
			String sport = proxy_port.getText().toString();
			String server = server_base.getText().toString();
			InetAddress.getByName(sip);
			Integer.parseInt(sport);
			new URL(server);
			@SuppressWarnings("deprecation")
			SharedPreferences settings = this.getSharedPreferences(
					"BlutToothProxySettings", MODE_WORLD_READABLE
							| Context.MODE_MULTI_PROCESS);
			Editor editor = settings.edit();
			editor.putString("proxy_ip", sip);
			editor.putString("proxy_port", sport);
			editor.putString("server_base", server);
			editor.commit();
			Toast.makeText(this, "Saved successfully", Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
