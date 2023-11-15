package unsw.blackout;

import unsw.utils.Angle;

public class LaptopDevice extends Devices {
  public LaptopDevice(String id, String type, Angle position) {
    super(id, type, position, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
  }
}
