package net.monstersb.bluetoothproxy;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {

	public static String bytes2Hex(byte[] src) {
		if (src == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder("0x");
		for (int i = 0; i < src.length; i++) {
			sb.append(String.format("%02X", src[i] & 0xFF));
		}

		return sb.toString();
	}

	public static String bytes2Hex(int[] src) {
		if (src == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder("0x");
		for (int i = 0; i < src.length; i++) {
			sb.append(String.format("%02X", src[i] & 0xFF));
		}

		return sb.toString();
	}

	public static byte[] hex2bytes(String src) {
		byte[] dst = new byte[(src.length() - 2) / 2];
		char[] chs = src.substring(2).toCharArray();
		for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
			dst[c] = (byte) (Integer.parseInt(new String(chs, i, 2), 16));
		}
		return dst;
	}

	public static int[] hex2ints(String src) {
		int[] dst = new int[(src.length() - 2) / 2];
		char[] chs = src.substring(2).toCharArray();
		for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
			dst[c] = (Integer.parseInt(new String(chs, i, 2), 16));
		}
		return dst;
	}

	static String callsStack() {
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		StringBuilder sb = new StringBuilder();
		if (stackElements != null) {
			for (int i = 4; i < stackElements.length; i++) {
				if (i != 4) {
					sb.append(" => ");
				}
				sb.append(stackElements[i]);
			}
		}
		return sb.toString();
	}

	static void printArguments(MethodHookParam param) {
		try {
			Hooker.log("Call Method => " + param.method.getName());
			Hooker.log("CallsStack => " + callsStack());
			for (int i = 0; i < param.args.length; i++) {
				if (param.args[i] instanceof byte[]) {
					Hooker.log("Method => " + param.method.getName()
							+ " : Args => " + bytes2Hex((byte[]) param.args[i]));
				} else {
					Hooker.log("Method => " + param.method.getName()
							+ " : Args => " + param.args[i]);
				}
			}
		} catch (Exception e) {

		}
	}

	static void printResult(MethodHookParam param) {
		Object result = param.getResult();
		try {
			if (param.getResult() instanceof byte[]) {
				result = bytes2Hex((byte[]) result);
			} else if (param.getResult() instanceof int[]) {
				result = bytes2Hex((int[]) result);
			} else if (param.getResult() instanceof Integer) {
				result = String.format("%08X", result);
			}
		} catch (Exception e) {
		}
		Hooker.log("Method => " + param.method.getName() + " : Result => "
				+ result);
	}

	static byte[] http(String url, byte[] data) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpHost httpHost = new HttpHost(getSetting("proxy_ip"),
				Integer.parseInt(getSetting("proxy_port")));
		httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
				httpHost);
		HttpPost httpPost = new HttpPost(getSetting("server_base") + "?" + url);
		HttpEntity entity = new ByteArrayEntity(data);
		httpPost.setEntity(entity);
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			return EntityUtils.toByteArray(httpResponse.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static byte[] httpRead(byte[] data) {
		return http("recv", data);
	}

	static byte[] httpWrite(byte[] data) {
		return http("send", data);
	}

	static Context getContext() {
		try {
			return (Context) Class.forName("android.app.ActivityThread")
					.getMethod("currentApplication")
					.invoke(null, (Object[]) null);
		} catch (final Exception e1) {
			try {
				return (Context) Class.forName("android.app.AppGlobals")
						.getMethod("getInitialApplication")
						.invoke(null, (Object[]) null);
			} catch (final Exception e2) {
				throw new RuntimeException("Failed to get application instance");
			}
		}

	}

	static String getSetting(String key) {
		try {
			Context context = getContext().createPackageContext(
					"net.monstersb.bluetoothproxy",
					Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences settings = context.getSharedPreferences(
					"BlutToothProxySettings", Context.MODE_WORLD_READABLE| Context.MODE_MULTI_PROCESS);
			return settings.getString(key, "");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	static Boolean getSettingSwitch() {
		try {
			Context context = getContext().createPackageContext(
					"net.monstersb.bluetoothproxy",
					Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences settings = context.getSharedPreferences(
					"BlutToothProxySettings", Context.MODE_WORLD_READABLE| Context.MODE_MULTI_PROCESS);
			return settings.getBoolean("switch", false);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
