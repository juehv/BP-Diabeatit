package de.tu_darmstadt.informatik.tk.diabeatit.assistant.alert;

public interface AlertManagementListener {

  void onAlertsCleared();
  void onAlertAdded(int totalAlerts);

}
