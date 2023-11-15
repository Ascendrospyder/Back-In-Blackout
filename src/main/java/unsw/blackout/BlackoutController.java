package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

public class BlackoutController {
  private List<Devices> devices = new ArrayList<>();
  private List<Satellite> satellites = new ArrayList<>();

  public void createDevice(String deviceId, String type, Angle position) {
    Devices newDevice = createNewDevice(deviceId, type, position);
    devices.add(newDevice);
  }

  public void removeDevice(String deviceId) {
    List<Devices> devicesToRemove = new ArrayList<>();

    for (Devices device : devices) {
      if (device.getDeviceId().equals(deviceId)) {
        devicesToRemove.add(device);
      }
    }

    devices.removeAll(devicesToRemove);
  }

  public void createSatellite(String satelliteId, String type, double height, Angle position) {
    Satellite newSatellite = createNewSatellite(satelliteId, type, height, position);
    satellites.add(newSatellite);
  }

  public void removeSatellite(String satelliteId) {
    List<Satellite> satellitesToRemove = new ArrayList<>();

    for (Satellite satellite : satellites) {
      if (satellite.getSatelliteId().equals(satelliteId)) {
        satellitesToRemove.add(satellite);
      }
    }

    satellites.removeAll(satellitesToRemove);
  }

  public List<String> listDeviceIds() {
    List<String> listOfDeviceIds = new ArrayList<>();

    for (Devices device : devices) {
      listOfDeviceIds.add(device.getDeviceId());
    }

    return listOfDeviceIds;
  }

  public List<String> listSatelliteIds() {
    List<String> listOfSatelliteIds = new ArrayList<>();

    for (Satellite satellite : satellites) {
      listOfSatelliteIds.add(satellite.getSatelliteId());
    }

    return listOfSatelliteIds;
  }

  public void addFileToDevice(String deviceId, String filename, String content) {
    Devices device = findDeviceById(deviceId);
    if (device != null) {
      device.setFilesList(filename, content, device, device);
    }
  }

  public EntityInfoResponse getInfo(String id) {
    Devices device = findDeviceById(id);

    if (device != null) {
      return new EntityInfoResponse(device.getDeviceId(), device.getPosition(), device.getHeight(), device.getType(),
          device.mapGivenFile());
    }

    Satellite satellite = findSatelliteById(id);
    if (satellite != null) {
      return new EntityInfoResponse(satellite.getSatelliteId(), satellite.getPosition(), satellite.getHeight(),
          satellite.getType(), satellite.mapGivenFile());
    }

    return null;
  }

  public void simulate() {
    for (Satellite satellite : satellites) {
      satellite.moveByOneMinute();
    }

    for (Devices device : devices) {
      device.transferFiles();
    }

    for (Satellite satellite : satellites) {
      satellite.transferFiles();
    }
  }

  /**
   * Simulate for the specified number of minutes. You shouldn't need to modify
   * this function.
   */
  public void simulate(int numberOfMinutes) {
    for (int i = 0; i < numberOfMinutes; i++) {
      simulate();
    }
  }

  public List<String> communicableEntitiesInRange(String id) {
    // TODO: Task 2 b) implement relay satellite logic

    List<String> communicableEntityIds = new ArrayList<String>();
    DeviceSatellite object = getObject(id);

    // Abides law of demeter
    for (Satellite satellite : satellites) {
      if (satellite.canCommunicate(object)) {
        communicableEntityIds.add(satellite.getId());
      }
    }

    for (Devices device : devices) {
      if (device.canCommunicate(object)) {
        communicableEntityIds.add(device.getId());
      }
    }

    return communicableEntityIds;
  }

  public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
    // TODO: Task 2 c)
    DeviceSatellite getSender = getObject(fromId);
    DeviceSatellite getReciever = getObject(toId);
    Files senderFile = getSender.findFileinList(fileName);
    Files recieverFile = getReciever.findFileinList(fileName);
    List<Files> recieverFilesList = getReciever.getFiles();
    int currentBytesInFile = 0;

    // File doesn't exist on fromId or it's a partial file (hasn't finished
    // transferring)
    if (senderFile == null || senderFile.getSize() != senderFile.getBytesTransmitted()) {
      throw new FileTransferException.VirtualFileNotFoundException(
          fileName + " doesn't exist or hasn't finished downloading yet, please try again.");
    }

    // File already exists on toId or is currently downloading to the
    if (recieverFile != null) {
      throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
    }

    if (getSender instanceof RelaySatellite || getReciever instanceof RelaySatellite) {
      throw new FileTransferException.VirtualFileNoBandwidthException(
          "Relay Satellites do not have any upload or download bandwith");
    }

    for (Files file : getReciever.getFiles()) {
      currentBytesInFile += file.getSize();
    }

    if (currentBytesInFile + senderFile.getSize() > getReciever.getMaxBytes()) {
      throw new FileTransferException.VirtualFileNoBandwidthException(
          toId + " doesn't have enough bandwith to recieve " + senderFile.getFilename());
    }

    if (getReciever.getFiles().size() + 1 > getReciever.getMaxFiles()) {
      throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
    }

    startDownloadUpload(getSender, getReciever, senderFile, recieverFilesList);
  }

  public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
    createDevice(deviceId, type, position);
    // TODO: Task 3
  }

  public void createSlope(int startAngle, int endAngle, int gradient) {
    // TODO: Task 3
    // If you are not completing Task 3 you can leave this method blank :)
  }

  ////////////////////////////////////// HELPER FUNCTIONS
  ////////////////////////////////////// /////////////////////////////////////////////

  private Devices createNewDevice(String deviceId, String type, Angle position) {
    switch (type) {
    case "HandheldDevice":
      return new HandheldDevice(deviceId, type, position);
    case "DesktopDevice":
      return new DesktopDevice(deviceId, type, position);
    default:
      return new LaptopDevice(deviceId, type, position);
    }
  }

  private Satellite createNewSatellite(String satelliteId, String type, double height, Angle position) {
    switch (type) {
    case "StandardSatellite":
      return new StandardSatellite(satelliteId, type, height, position);
    case "TeleportingSatellite":
      return new TeleportingSatellite(satelliteId, type, height, position);
    case "RelaySatellite":
      return new RelaySatellite(satelliteId, type, height, position);
    default:
      return new StandardSatellite(satelliteId, "StandardSatellite", height, position);
    }
  }

  private Devices findDeviceById(String deviceId) {
    for (Devices device : devices) {
      if (device.getDeviceId().equals(deviceId)) {
        return device;
      }
    }
    return null;
  }

  private Satellite findSatelliteById(String satelliteId) {
    for (Satellite satellite : satellites) {
      if (satellite.getSatelliteId().equals(satelliteId)) {
        return satellite;
      }
    }
    return null;
  }

  public DeviceSatellite getObject(String id) {
    Devices device = findDeviceById(id);

    if (device == null) {
      return findSatelliteById(id);
    } else {
      return device;
    }
  }

  private void startDownloadUpload(DeviceSatellite getSender, DeviceSatellite getReciever, Files senderFile,
      List<Files> recieverFilesList) {
    Files fileToSend = new Files(senderFile.getFilename(), "", 0, senderFile.getFutureContent(), getSender,
        getReciever);
    recieverFilesList.add(fileToSend);
    getReciever.setFiles(recieverFilesList);
    getReciever.setNumFilesDownloading(getReciever.getNumFilesDownloading() + 1);
    getSender.setNumFilesUploading(getSender.getNumFilesUploading() + 1);
  }
}
