package info.nightscout.androidaps.assistant.alert;

public interface AlertManagementListener {

  void onAlertsCleared();
  void onAlertAdded(int totalAlerts);
  void onAlertRemoved(Alert alert);

}
