package code.demo.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Background daemon that periodically checks internet connectivity.
 * It exposes a read-only boolean property indicating if the app is connected.
 */
public class ConnectivityMonitor extends ScheduledService<Boolean> {

    private static ConnectivityMonitor instance;
    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper(true);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ConnectivityMonitor");
        t.setDaemon(true);
        return t;
    });

    // Endpoint used for lightweight connectivity checks. Can be overridden via env.
    private final String pingUrl = System.getenv().getOrDefault(
            "LMS_PING_URL",
            "https://www.google.com/"
    );

    private ConnectivityMonitor() {
        setPeriod(Duration.seconds(7));
        setRestartOnFailure(true);
        setExecutor(executor);

        valueProperty().addListener((obs, oldVal, newVal) -> connected.set(newVal != null && newVal));
        setOnFailed(e -> connected.set(false));
    }

    public static synchronized ConnectivityMonitor getInstance() {
        if (instance == null) instance = new ConnectivityMonitor();
        return instance;
    }

    public ReadOnlyBooleanProperty connectedProperty() { return connected.getReadOnlyProperty(); }
    public boolean isConnected() { return connected.get(); }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                return checkInternet();
            }
        };
    }

    private boolean checkInternet() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(pingUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // إضافة تعريف المتصفح لتجنب حظر السيرفرات للطلبات البرمجية
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            conn.setConnectTimeout(5000); // زيادة الوقت لـ 5 ثواني للتأكد
            conn.setReadTimeout(5000);

            int code = conn.getResponseCode();
            System.out.println("DEBUG: Connection success. Response Code: " + code);
            return code >= 200 && code < 400;

        } catch (java.net.UnknownHostException e) {
            System.err.println("DEBUG: فشل الاتصال - لا يمكن الوصول لعنوان الموقع (قد يكون الـ DNS معطل أو لا يوجد نت فعلياً)");
        } catch (javax.net.ssl.SSLHandshakeException e) {
            System.err.println("DEBUG: فشل الاتصال - مشكلة في شهادة الأمان SSL (تأكد من تاريخ الجهاز)");
        } catch (java.net.SocketTimeoutException e) {
            System.err.println("DEBUG: فشل الاتصال - انتهت المهلة (النت بطيء جداً)");
        } catch (Exception e) {
            System.err.println("DEBUG: حدث خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace(); // هذا سيطبع لك تفاصيل الخطأ كاملة
        } finally {
            if (conn != null) conn.disconnect();
        }
        return false;
    }
}
