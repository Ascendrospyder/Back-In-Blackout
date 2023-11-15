package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unsw.utils.Angle;

public abstract class Satellite extends DeviceSatellite {
  /**
   * Constructs a Satellite object with the specified parameters.
   *
   * @param satelliteId         the ID of the satellite
   * @param type                the type of the satellite
   * @param height              the height of the satellite
   * @param position            the position of the satellite
   * @param maxFiles            the maximum number of files the satellite can
   *                            store
   * @param maxBytes            the maximum number of bytes the satellite can
   *                            store
   * @param uploadBandwith      the upload bandwidth of the satellite
   * @param downloadBandwith    the download bandwidth of the satellite
   * @param numFilesUploading   the number of files currently being uploaded by
   *                            the satellite
   * @param numFilesDownloading the number of files currently being downloaded by
   *                            the satellite
   */
  public Satellite(String satelliteId, String type, double height, Angle position, int maxFiles, int maxBytes,
      int uploadBandwith, int downloadBandwith, int numFilesUploading, int numFilesDownloading) {
    super(satelliteId, type, position, height, maxFiles, maxBytes, uploadBandwith, downloadBandwith, numFilesUploading,
        numFilesDownloading);
  }

  public String getSatelliteId() {
    return super.getId();
  }

  public void setSatelliteId(String satelliteId) {
    super.setId(satelliteId);
  }

  public String getType() {
    return super.getType();
  }

  public void setType(String type) {
    super.setType(type);
  }

  public double getHeight() {
    return super.getHeight();
  }

  public void setHeight(double height) {
    super.setHeight(height);
  }

  public Angle getPosition() {
    return super.getPosition();
  }

  public void setPosition(Angle position) {
    super.setPosition(position);
  }

  public ArrayList<String> getSupportedDevices() {
    switch (getType()) {
    case "StandardSatellite":
      return new ArrayList<String>(Arrays.asList("HandheldDevice", "LaptopDevice", "StandardSatellite",
          "TeleportingSatellite", "RelaySatellite"));
    default:
      return new ArrayList<String>(Arrays.asList("HandheldDevice", "LaptopDevice", "DesktopDevice", "StandardSatellite",
          "TeleportingSatellite", "RelaySatellite"));
    }
  }

  public Satellite findSatelliteById(String satelliteId, List<Satellite> satellites) {
    for (Satellite satellite : satellites) {
      if (satellite.getSatelliteId().equals(satelliteId)) {
        return satellite;
      }
    }
    return null;
  }

  public boolean canCommunicate(DeviceSatellite object) {
    return isInRange(object) && getSupportedDevices().contains(object.getType()) && !getId().equals(object.getId());
  }

  public abstract void moveByOneMinute();

  public abstract double getAngularVelocity();
}
