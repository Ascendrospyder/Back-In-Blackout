package unsw.blackout;

import unsw.utils.Angle;

public class HandheldDevice extends Devices {
  public HandheldDevice(String id, String type, Angle position) {
    super(id, type, position, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
  }
}
