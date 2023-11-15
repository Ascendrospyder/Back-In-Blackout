package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class Devices extends DeviceSatellite {
  /**
   * Constructs a new Devices object with the specified device ID, type, and
   * position.
   *
   * @param deviceId            the ID of the device
   * @param type                the type of the device
   * @param position            the position of the device
   * @param maxFiles            maximum number of files held by the device
   * @param maxBytes            the maximum number of bytes the satellite can
   *                            store
   * @param uploadBandwith      the upload bandwidth of the satellite
   * @param downloadBandwith    the download bandwidth of the satellite
   * @param numFilesUploading   the number of files currently being uploaded by
   *                            the satellite
   * @param numFilesDownloading the number of files currently being downloaded by
   *                            the satellite
   */
  public Devices(String deviceId, String type, Angle position, int maxFiles, int maxBytes, int uploadBandwith,
      int downloadBandwith, int numFilesUploading, int numFilesDownloading) {
    super(deviceId, type, position, MathsHelper.RADIUS_OF_JUPITER, maxFiles, maxBytes, uploadBandwith, downloadBandwith,
        numFilesUploading, numFilesDownloading);
  }

  public String getDeviceId() {
    return super.getId();
  }

  public void setDeviceId(String deviceId) {
    super.setId(deviceId);
  }

  public String getType() {
    return super.getType();
  }

  public void setType(String type) {
    super.setType(type);
  }

  public Angle getPosition() {
    return super.getPosition();
  }

  public void setPosition(Angle position) {
    super.setPosition(position);
  }

  public Devices findDeviceById(String deviceId, List<Devices> devices) {
    for (Devices device : devices) {
      if (device.getDeviceId().equals(deviceId)) {
        return device;
      }
    }
    return null;
  }

  public boolean canCommunicate(DeviceSatellite object) {
    return isInRange(object) && getSupportedSatellites().contains(object.getType());
  }

  public ArrayList<String> getSupportedSatellites() {
    switch (getType()) {
    case "DesktopDevice":
      return new ArrayList<String>(Arrays.asList("TeleportingSatellite", "RelaySatellite"));
    default:
      return new ArrayList<String>(Arrays.asList("StandardSatellite", "TeleportingSatellite", "RelaySatellite"));
    }
  }
}
