package net.monstersb.bluetoothproxy;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Hooker implements IXposedHookLoadPackage {

	static Hooker hooker = null;
	static ClassLoader clsldr = null;

	@SuppressLint("SimpleDateFormat")
	static public void log(String str) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		XposedBridge.log("BlueTooth Proxy[" + df.format(new Date()) + "]:  "
				+ str);
	}

	public void handleLoadPackage(final LoadPackageParam lpparam)
			throws Throwable {
		if (lpparam.packageName.equals("net.monstersb.bluetoothproxy")) {
			return;
		}
		beforeTest(lpparam);
		hooker = this;
		hook(lpparam.classLoader);
		moniter(lpparam.classLoader);
		afterTest(lpparam);
	}

	void beforeTest(final LoadPackageParam lpparam) {
	}

	void afterTest(final LoadPackageParam lpparam) {
	}

	static void moniter(ClassLoader classLoader) {
		String[][] methods = {/*
							 * { "android.bluetooth.BluetoothInputStream",
							 * "read" }, {
							 * "android.bluetooth.BluetoothOutputStream",
							 * "write" }
							 */};
		String[] classes = { /*
							 * "android.bluetooth.BluetoothSocket",
							 * "android.bluetooth.BluetoothServerSocket"
							 */};
		for (int i = 0; i < methods.length; i++) {
			try {
				Class.forName(methods[i][0], false, classLoader);
				XposedBridge.hookAllMethods(
						XposedHelpers.findClass(methods[i][0], classLoader),
						methods[i][1],
						Callbacks.moniterMethodArgumentsAndResults);
			} catch (Exception e) {
			}
		}
		for (int i = 0; i < classes.length; i++) {
			try {
				Class.forName(classes[i], false, classLoader);
				XposedBridge.hookAllConstructors(
						XposedHelpers.findClass(classes[i], classLoader),
						Callbacks.moniterConstructor);
			} catch (Exception e) {
			}
		}
	}

	static void hook(ClassLoader classLoader) {
		try {
			XposedBridge.hookAllConstructors(XposedHelpers.findClass(
					"dalvik.system.DexClassLoader", classLoader),
					Callbacks.dexClassLoader);
			XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
					"android.bluetooth.BluetoothInputStream", classLoader),
					"read", Callbacks.read);
			XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
					"android.bluetooth.BluetoothInputStream", classLoader),
					"read", byte[].class, int.class, int.class, Callbacks.read);
			XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
					"android.bluetooth.BluetoothOutputStream", classLoader),
					"write", int.class, Callbacks.write);
			XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
					"android.bluetooth.BluetoothOutputStream", classLoader),
					"write", byte[].class, int.class, int.class,
					Callbacks.write);
		} catch (Exception e) {
		}
	}
}