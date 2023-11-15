package unsw.blackout;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static unsw.utils.MathsHelper.getDistance;

public class DeviceSatellite {
  private String id;
  private String type;
  private Angle position;
  private double height;
  private List<Files> files;
  private int uploadBandwith;
  private int downloadBandwith;
  private int maxFiles;
  private int maxBytes;
  private int numFilesUploading;
  private int numFilesDownloading;

  private static final int STANDARD_SATELLITE_RANGE = 150000;
  private static final int TELEPORTING_SATELLITE_RANGE = 200000;
  private static final int RELAY_SATELLITE_RANGE = 300000;
  private static final int HANDHELD_DEVICE_RANGE = 50000;
  private static final int LAPTOP_DEVICE_RANGE = 100000;
  private static final int DESKTOP_DEVICE_RANGE = 200000;

  /**
   * Constructs a new DeviceSatellite object with the specified parameters.
   *
   * @param id                  The identifier of the device satellite.
   * @param type                The type of the device satellite.
   * @param position            The position of the device satellite.
   * @param height              The height of the device satellite.
   * @param maxFiles            The maximum number of files that the device
   *                            satellite can store.
   * @param maxBytes            The maximum number of bytes that the device
   *                            satellite can store.
   * @param uploadBandwith      The upload bandwidth of the device satellite.
   * @param downloadBandwith    The download bandwidth of the device satellite.
   * @param numFilesUploading   The number of files currently being uploaded by
   *                            the device satellite.
   * @param numFilesDownloading The number of files currently being downloaded by
   *                            the device satellite.
   */
  public DeviceSatellite(String id, String type, Angle position, double height, int maxFiles, int maxBytes,
      int uploadBandwith, int downloadBandwith, int numFilesUploading, int numFilesDownloading) {
    this.id = id;
    this.type = type;
    this.position = position;
    this.height = height;
    this.setFiles(new ArrayList<Files>());
    this.maxFiles = maxFiles;
    this.maxBytes = maxBytes;
    this.uploadBandwith = uploadBandwith;
    this.downloadBandwith = downloadBandwith;
    this.numFilesUploading = numFilesUploading;
    this.numFilesDownloading = numFilesDownloading;
  }

  public String getId() {
    return id;
  }

  public int getUploadBandwith() {
    return uploadBandwith;
  }

  public void setUploadBandwith(int uploadBandwith) {
    this.uploadBandwith = uploadBandwith;
  }

  public int getDownloadBandwith() {
    return downloadBandwith;
  }

  public void setDownloadBandwith(int downloadBandwith) {
    this.downloadBandwith = downloadBandwith;
  }

  public int getMaxFiles() {
    return maxFiles;
  }

  public void setMaxFiles(int maxFiles) {
    this.maxFiles = maxFiles;
  }

  public int getMaxBytes() {
    return maxBytes;
  }

  public void setMaxBytes(int maxBytes) {
    this.maxBytes = maxBytes;
  }

  public int getNumFilesUploading() {
    return numFilesUploading;
  }

  public void setNumFilesUploading(int numFilesUploading) {
    this.numFilesUploading = numFilesUploading;
  }

  public int getNumFilesDownloading() {
    return numFilesDownloading;
  }

  public void setNumFilesDownloading(int numFilesDownloading) {
    this.numFilesDownloading = numFilesDownloading;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public List<Files> getFiles() {
    return files;
  }

  public void setFiles(List<Files> files) {
    this.files = files;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Angle getPosition() {
    return position;
  }

  public void setPosition(Angle position) {
    this.position = position;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public int getRangeOfDevice(DeviceSatellite objectOfInterest) {
    switch (objectOfInterest.getType()) {
    case "LaptopDevice":
      return LAPTOP_DEVICE_RANGE;
    case "DesktopDevice":
      return DESKTOP_DEVICE_RANGE;
    default:
      return HANDHELD_DEVICE_RANGE;
    }
  }

  public int getRangeOfSatellite(DeviceSatellite objectOfInterest) {
    switch (objectOfInterest.getType()) {
    case "TeleportingSatellite":
      return TELEPORTING_SATELLITE_RANGE;
    case "RelaySatellite":
      return RELAY_SATELLITE_RANGE;
    default:
      return STANDARD_SATELLITE_RANGE;
    }
  }

  /**
   * Checks if the current device or satellite is within range of the specified
   * object of interest by using the isVisibile and getDistance helper functions
   * It does this by first if the object is a device we check if it is visible and
   * compare the distance between the object and the current instance with the
   * range The other instance is comparing the range of a satellite and device and
   * finally we check satellite and satellite range
   *
   * @param objectOfInterest The object of interest to check the range against.
   * @return true if the current device or satellite is within range of the object
   *         of interest, false otherwise.
   */
  public boolean isInRange(DeviceSatellite objectOfInterest) {
    if (objectOfInterest instanceof Devices) {
      return (isVisible(getHeight(), getPosition(), objectOfInterest.getPosition()) && getDistance(getHeight(),
          getPosition(), objectOfInterest.getPosition()) < objectOfInterest.getRangeOfDevice(objectOfInterest));
    } else if (objectOfInterest instanceof Satellite && this instanceof Devices) {
      return (isVisible(objectOfInterest.getHeight(), objectOfInterest.getPosition(), getPosition())
          && getDistance(objectOfInterest.getHeight(), objectOfInterest.getPosition(), position) < objectOfInterest
              .getRangeOfSatellite(objectOfInterest));
    } else {
      return (isVisible(getHeight(), getPosition(), objectOfInterest.getHeight(), objectOfInterest.getPosition())
          && getDistance(getHeight(), getPosition(), objectOfInterest.getHeight(),
              objectOfInterest.getPosition()) < objectOfInterest.getRangeOfSatellite(objectOfInterest));
    }
  }

  public void setFilesList(String fileName, String content, DeviceSatellite sender, DeviceSatellite reciever) {
    Files file = new Files(fileName, content, content.length(), content, sender, reciever);
    files.add(file);
  }

  public Map<String, FileInfoResponse> mapGivenFile() {
    Map<String, FileInfoResponse> mapFiles = new HashMap<String, FileInfoResponse>();

    for (Files file : files) {
      mapFiles.put(file.getFilename(), new FileInfoResponse(file.getFilename(), file.getPresentContent(),
          file.getSize(), (file.getSize() == file.getBytesTransmitted())));
    }
    return mapFiles;
  }

  public Files findFileinList(String fileName) {
    List<Files> filesList = getFiles();

    for (Files file : filesList) {
      if (file.getFilename().equals(fileName)) {
        return file;
      }
    }
    return null;
  }

  public boolean checkDownload() {
    if (numFilesDownloading >= downloadBandwith)
      return false;
    return true;
  }

  public boolean checkUpload() {
    if (numFilesUploading >= uploadBandwith)
      return false;
    return true;
  }

  public int downloadSpeed() {
    return getDownloadBandwith() / getNumFilesDownloading();
  }

  public int uploadSpeed() {
    return getUploadBandwith() / getNumFilesUploading();
  }

  /**
   * Transfers files by updating the file transfer status for each file in the
   * device's file list. Only files that have not been completely transmitted will
   * be updated.
   */
  public void transferFiles() {
    List<Files> filesToTransfer = getFiles();
    List<Files> pendingFiles = new ArrayList<>();
    filesToTransfer.stream().forEach((file) -> {
      if (file.getSize() != file.getBytesTransmitted())
        pendingFiles.add(file);
    });

    for (Files file : pendingFiles) {
      file.updateFileTransfer();
    }
    setFiles(filesToTransfer);
  }

  public void removeIncompleteFile(Files toDelete, DeviceSatellite getReciever, DeviceSatellite getSender) {
    List<Files> filesInList = getFiles();

    for (Files file : filesInList) {
      if (toDelete == file) {
        filesInList.remove(file);
        getReciever.setNumFilesDownloading(getReciever.getNumFilesDownloading() - 1);
        getSender.setNumFilesUploading(getSender.getNumFilesUploading() - 1);
        break;
      }
    }
    setFiles(filesInList);
  }
}
