package net.monstersb.bluetoothproxy;

import java.util.Arrays;
import android.annotation.SuppressLint;
import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;

public class Callbacks {

	static XC_MethodHook moniterMethodArgumentsAndResults = new XC_MethodHook() {

		@Override
		protected void beforeHookedMethod(MethodHookParam param)
				throws Throwable {
			Utils.printArguments(param);
		}

		@Override
		protected void afterHookedMethod(MethodHookParam param)
				throws Throwable {
			Utils.printResult(param);
		}
	};

	static XC_MethodHook moniterConstructor = new XC_MethodHook() {

		@Override
		protected void beforeHookedMethod(MethodHookParam param)
				throws Throwable {
			Utils.printArguments(param);
		}
	};

	static XC_MethodHook dexClassLoader = new XC_MethodHook() {

		protected void afterHookedMethod(MethodHookParam param) {
			Hooker.hook((DexClassLoader) param.thisObject);
			Hooker.moniter((DexClassLoader) param.thisObject);
		}
	};

	static XC_MethodHook read = new XC_MethodHook() {

		protected void afterHookedMethod(MethodHookParam param)
				throws Throwable {
			Hooker.log("recv" + Utils.getSettingSwitch());
			if (Utils.getSettingSwitch()) {
				if ((Integer) param.getResult() > 0) {
					byte[] data;
					if (param.args.length == 0) {
						data = new byte[1];
						data[0] = (byte) (int) (Integer) param.getResult();
						data = Utils.httpRead(data);
						if (data != null && data.length == 1) {
							param.setResult((Byte) data[0]);
						}
					} else if (param.args.length == 3) {
						byte[] b = (byte[]) param.args[0];
						int offset = (Integer) param.args[1];
						int count = (Integer) param.args[2];
						data = Arrays.copyOfRange(b, offset, offset + count);
						data = Utils.httpRead(data);
						if (data != null && data.length == count) {
							for (int i = 0; i < count; i++) {
								b[offset + i] = data[i];
							}
						}
					}
				}
			}
		}
	};

	static XC_MethodHook write = new XC_MethodHook() {

		protected void beforeHookedMethod(MethodHookParam param)
				throws Throwable {
			Hooker.log("send" + Utils.getSettingSwitch());
			if (Utils.getSettingSwitch()) {
				byte[] data;
				if (param.args.length == 1) {
					data = new byte[1];
					data[0] = (byte) (int) (Integer) param.args[0];
					data = Utils.httpWrite(data);
					if (data != null && data.length == 1) {
						param.args[0] = (Byte) data[0];
					}
				} else if (param.args.length == 3) {
					byte[] b = (byte[]) param.args[0];
					int offset = (Integer) param.args[1];
					int count = (Integer) param.args[2];
					data = Arrays.copyOfRange(b, offset, offset + count);
					data = Utils.httpWrite(data);
					if (data != null && data.length == count) {
						for (int i = 0; i < count; i++) {
							b[offset + i] = data[i];
						}
					}
				}
			}
		}
	};

}
