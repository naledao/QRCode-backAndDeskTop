package xqq.kangnasi.xyz.webviewdemo.util;

public class OpenWeb {
    private static final String url = "http://127.0.0.1:8080";
    public static void openWeb(){
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux/Unix
                Runtime.getRuntime().exec("xdg-open " + url);
            } else {
                System.out.println("操作系统不支持");
            }
        } catch (Exception ignored) {
        }
    }
}
