package cn.modificator.launcher.model;

import android.content.Context;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import cn.modificator.launcher.ftpservice.FTPService;

public class AdbUtils {
  public static final int ADB_PORT = 5555;

  public static boolean isAdbRunning() {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress("127.0.0.1", ADB_PORT), 250);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean toggleAdbWifi(boolean enable) {
    String cmd = enable
        ? "setprop service.adb.tcp.port " + ADB_PORT + "\nstop adbd\nstart adbd\n"
        : "setprop service.adb.tcp.port -1\nstop adbd\nstart adbd\n";
    return execRootCmd(cmd);
  }

  public static String getAdbAddress(Context context) {
    if (FTPService.getLocalInetAddress(context) == null) {
      return null;
    }
    return FTPService.getLocalInetAddress(context).getHostAddress() + ":" + ADB_PORT;
  }

  public static boolean execRootCmd(String cmd) {
    Process process = null;
    try {
      process = Runtime.getRuntime().exec("su");
      DataOutputStream os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(cmd);
      os.writeBytes("exit\n");
      os.flush();
      return process.waitFor() == 0;
    } catch (Exception e) {
      return false;
    } finally {
      if (process != null) {
        process.destroy();
      }
    }
  }
}
