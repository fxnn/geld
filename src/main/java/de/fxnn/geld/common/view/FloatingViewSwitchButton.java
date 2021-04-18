package de.fxnn.geld.common.view;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

public class FloatingViewSwitchButton extends FloatingActionButton {
  public FloatingViewSwitchButton(MaterialDesignIcon icon, String viewName) {
    super(icon.text, e -> MobileApplication.getInstance().switchView(viewName));
  }
}
