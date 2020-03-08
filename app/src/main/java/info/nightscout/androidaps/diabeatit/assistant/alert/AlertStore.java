package info.nightscout.androidaps.diabeatit.assistant.alert;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlertStore {

  public static DismissedAlertsManager dismissedAlerts;
  public static AlertsManager activeAlerts;

  private static List<Alert> alerts = new ArrayList<>();
  private static List<AlertStoreListener> listeners = new ArrayList<>();

  /**
   * Attaches an {@link AlertStoreListener} to the AlertStore.
   * It will receive an initial {@link AlertStoreListener#onDataSetInit()} call and future data set updates
   * @param listener The listener to add
   */
  public static void attachListener(@NonNull AlertStoreListener listener) {

    listeners.add(listener);
    listener.onDataSetInit();

  }

  /**
   * Detaches the given {@link AlertStoreListener} from the AlertStore.
   * It will not receive any further notification calls
   * @param listener The listener to remove
   */
  public static void detachListener(AlertStoreListener listener) {

    if (listeners.contains(listener))
      listeners.remove(listener);

  }

  /**
   * Sets the list of active and dismissed alerts to the given array.
   * This will overwrite all currently stored alerts.
   * Distinction of active/dismissed is made on basis of {@link Alert#active}
   * @param alertBundle The new data set
   */
  public static void initAlerts(@NonNull Alert[] alertBundle) {

    alerts = new ArrayList<>();
    alerts.addAll(Arrays.asList(alertBundle));

    for (Alert alert : alerts)
      alert.send();

    for (AlertStoreListener l : listeners)
      l.onDataSetInit();

  }

  /**
   * Adds the given alert to the list of active alerts. This will notify the listeners via
   * {@link AlertStoreListener#onNewAlert(Alert)}.
   * It's {@link Alert#active} flag will be set to true
   * @param alert The alert to add
   */
  public static void newAlert(@NonNull Alert alert) {

    alert.active = true;

    alerts.add(alert);
    alert.send();

    for (AlertStoreListener l : listeners)
      l.onNewAlert(alert);

  }

  /**
   * This will move the given alert from the active to the dismissed alerts. It will notify the listeners via
   * {@link AlertStoreListener#onAlertDismissed(Alert)}.
   * If the given alert was the last active alert, this will also trigger a notification for
   * {@link AlertStoreListener#onAlertsCleared()}.
   * It's {@link Alert#active} flag will be set to false.
   * If the given alert is not present in the list of active alerts, this call will be ignored
   * @param alert The alert to dismiss
   */
  public static void dismissAlert(@NonNull Alert alert) {

    if (!alerts.contains(alert)) return;

    int index = alerts.indexOf(alert);
    alerts.get(index).active = false;
    alert.destroy();

    for (AlertStoreListener l : listeners)
      l.onAlertDismissed(alert);

    if (getActiveAlerts().length == 0)
      for (AlertStoreListener l : listeners)
        l.onAlertsCleared();

  }

  /**
   * This will move the given alert from the dismissed to the active alerts. It will notify the listeners via
   * {@link AlertStoreListener#onAlertRestored(Alert)}.
   * It's {@link Alert#active} flag will be set to false.
   * If the given alert is not present in the list of dismissed alerts, this call will be redirected to
   * {@link #newAlert(Alert)}
   * @param alert The alert to restore
   */
  public static void restoreAlert(@NonNull Alert alert) {

    if (!alerts.contains(alert)) {

      newAlert(alert);
      return;

    }

    int index = alerts.indexOf(alert);
    alerts.get(index).active = true;
    alert.send();

    for (AlertStoreListener l : listeners)
      l.onAlertRestored(alert);

  }

  /**
   * This will remove all active alerts by calling {@link #dismissAlert(Alert)} in sequence.
   * These calls will trigger dismissal notifications
   */
  public static void clearAlerts() {

    for (Alert alert : getActiveAlerts())
      dismissAlert(alert);

  }

  /**
   * This will return a list of all alerts with the {@link Alert#active} flag set
   * @return all active alerts as an array
   */
  public static Alert[] getActiveAlerts() {

    return alerts.stream().filter(a -> a.active).toArray(Alert[]::new);

  }

  /**
   * This will return a list of all alerts with the {@link Alert#active} flag not set
   * @return all dismissed alerts as an array
   */
  public static Alert[] getDismissedAlerts() {

    return alerts.stream().filter(a -> !a.active).toArray(Alert[]::new);

  }

}