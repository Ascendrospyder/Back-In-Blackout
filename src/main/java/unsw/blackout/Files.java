package unsw.blackout;

public class Files {
  private String filename;
  private String presentContent;
  private int size;
  private int bytesTransmitted;
  private String futureContent;
  private DeviceSatellite sender;
  private DeviceSatellite reciever;

  /**
   * Constructs a new Files object between a file being transferred from a sender
   * to a reciever.
   *
   * @param filename         The name of the file.
   * @param presentContent   The content of the file that has already been
   *                         transmitted.
   * @param bytesTransmitted The number of bytes of the file that have been
   *                         transmitted.
   * @param futureContent    The remaining content of the file that is yet to be
   *                         transmitted.
   * @param sender           The device or satellite that is sending the file.
   * @param receiver         The device or satellite that is receiving the file.
   */
  public Files(String filename, String presentContent, int bytesTransmitted, String futureContent,
      DeviceSatellite sender, DeviceSatellite reciever) {
    this.filename = filename;
    this.presentContent = presentContent;
    this.size = futureContent.length();
    this.bytesTransmitted = bytesTransmitted;
    this.futureContent = futureContent;
    this.sender = sender;
    this.reciever = reciever;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public int getBytesTransmitted() {
    return bytesTransmitted;
  }

  public void setBytesTransmitted(int bytesTransmitted) {
    this.bytesTransmitted = bytesTransmitted;
  }

  public String getFutureContent() {
    return futureContent;
  }

  public void setFutureContent(String futureContent) {
    this.futureContent = futureContent;
  }

  public DeviceSatellite getSender() {
    return sender;
  }

  public void setSender(DeviceSatellite sender) {
    this.sender = sender;
  }

  public DeviceSatellite getReciever() {
    return reciever;
  }

  public void setReciever(DeviceSatellite reciever) {
    this.reciever = reciever;
  }

  public String getPresentContent() {
    return presentContent;
  }

  public void setPresentContent(String presentContent) {
    this.presentContent = presentContent;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  /**
   * Removes the remaining content that is yet to be transferred from a sender to
   * the reciever. It does this by: 1. getting the remaining content by generating
   * the substring from the position of bytes presently transferred from the
   * resulting content 2. our final content will be the present content, appended
   * with the remaining content but without the t's 3. This method updates the
   * present content, future content, bytes transmitted, and size of the file
   * being transferred. 4. It also adjusts the number of files downloading for the
   * receiver and the number of files uploading for the sender by decrementing
   * both since the file has been downloaded and uploaded
   *
   * @param getSender   The device or satellite that is sending the file.
   * @param getReceiver The device or satellite that is receiving the file.
   */
  public void removeTsRemainingAfterTeleport(DeviceSatellite getSender, DeviceSatellite getReciever) {
    String remainingContent = getFutureContent().substring(getPresentContent().length());
    String updatedContent = presentContent + remainingContent.replace("t", "");

    setPresentContent(updatedContent);
    setFutureContent(updatedContent);
    setBytesTransmitted(updatedContent.length());
    setSize(updatedContent.length());

    getReciever.setNumFilesDownloading(getReciever.getNumFilesDownloading() - 1);
    getSender.setNumFilesUploading(getSender.getNumFilesUploading() - 1);
  }

  /**
   * Removes the remaining content that is yet to be transferred from a sender to
   * the receiver. It does this by: 1. Getting the future content 2. The final
   * content will be future content but removing the t's 3. This method updates
   * the present content, future content, bytes transmitted, and size of the file
   * being transferred. 4. It also adjusts the number of files downloading for the
   * receiver and the number of files uploading for the sender by decrementing
   * both since the file has been downloaded and uploaded.
   *
   * @param getSender   The device or satellite that is sending the file.
   * @param getReceiver The device or satellite that is receiving the file.
   */
  public void removeAllTsAfterTeleport(DeviceSatellite getSender, DeviceSatellite getReciever) {
    String updatedContent = getFutureContent();
    updatedContent = updatedContent.replace("t", "");

    setPresentContent(updatedContent);
    setFutureContent(updatedContent);
    setBytesTransmitted(updatedContent.length());
    setSize(updatedContent.length());

    getReciever.setNumFilesDownloading(getReciever.getNumFilesDownloading() - 1);
    getSender.setNumFilesUploading(getSender.getNumFilesUploading() - 1);
  }

  /**
   * Updates the file transfer progress between a sender and a receiver.
   *
   * @see DeviceSatellite
   * @see TeleportingSatellite
   */
  public void updateFileTransfer() {
    int downloadSpeed = 0;
    int uploadSpeed = 0;
    DeviceSatellite getSender = getSender();
    DeviceSatellite getReciever = getReciever();

    TeleportingSatellite teleportingSender = getSender instanceof TeleportingSatellite
        ? (TeleportingSatellite) getSender
        : null;
    TeleportingSatellite teleportingReciever = getReciever instanceof TeleportingSatellite
        ? (TeleportingSatellite) getReciever
        : null;

    // in the case where the sender is a device and reciever is a teleporting
    // satellite
    // if we have teleported, we remove the partial file from the reciever
    // and remove all t's from the sender file and exit the function
    if ((getSender instanceof Devices) && teleportingReciever != null) {
      if (teleportingReciever.isTeleportIsComplete()) {
        getReciever.removeIncompleteFile(this, getReciever, getSender);
        Files fileOfInterest = getSender.findFileinList(getFilename());
        fileOfInterest.removeAllTsAfterTeleport(getSender, getReciever);
        return;
      }
    }

    // if the case where the sender is a teleporting satellite and
    // reciever is a device, we check if the teleport has completed
    // if it has then we go ahead an remove the remaining t's
    // and exit the function
    if (teleportingSender != null && getReciever instanceof Devices) {
      if (teleportingSender.isTeleportIsComplete()) {
        removeTsRemainingAfterTeleport(getSender, getReciever);
        return;
      }
    }

    // if the case where the sender or reciever is a teleportingSatellite
    // and the reciever or sender is a satellite, meaning we have a satellite
    // to satellite transfer, we remove the t's remaining that haven't been
    // transmitted
    // and exit the function
    if ((teleportingSender != null && getReciever instanceof Satellite)
        || (teleportingReciever != null && getSender instanceof Satellite)) {
      if (teleportingSender != null && teleportingSender.isTeleportIsComplete()) {
        removeTsRemainingAfterTeleport(getSender, getReciever);
        return;
      } else if (teleportingReciever != null && teleportingReciever.isTeleportIsComplete()) {
        removeTsRemainingAfterTeleport(getSender, getReciever);
        return;
      }
    }

    if (!getReciever.isInRange(getSender)
        && ((getSender instanceof Devices) || (getReciever instanceof Devices))) {
      // remove File and exit
      getReciever.removeIncompleteFile(this, getReciever, getSender);
      return;
    } else {
      downloadSpeed = getReciever.downloadSpeed();
      uploadSpeed = getSender.uploadSpeed();
    }

    updateContentTransmitted(downloadSpeed, uploadSpeed);

    checkFinishedDownloadUpload(getSender, getReciever);
  }

  /**
   * Checks if the file transfer has been completed and updates the download and
   * upload counts accordingly. If the bytes transmitted is equal to the size of
   * the file, the file is transferred and we decrement the number of files
   * uploading and downloading.
   *
   * @param getSender   The device or satellite that is sending the file.
   * @param getReceiver The device or satellite that is receiving the file.
   */
  private void checkFinishedDownloadUpload(DeviceSatellite getSender, DeviceSatellite getReciever) {
    if (getBytesTransmitted() == getSize()) {
      getReciever.setNumFilesDownloading(getReciever.getNumFilesDownloading() - 1);
      getSender.setNumFilesUploading(getSender.getNumFilesUploading() - 1);
    }
  }

  /**
   * Updates the content transmitted based on the transmittedByterate through the
   * download and upload speeds. if the download or upload speed are not equal we
   * calculate the transmittedByteRate using the bottleneck. We then set the bytes
   * transmitted by making sure the transmittedByteRate doesn't exceed the size of
   * the file and append the present content based on the bytes we have recieved
   *
   * @param downloadSpeed The download speed for the file transfer.
   * @param uploadSpeed   The upload speed for the file transfer.
   */
  private void updateContentTransmitted(int downloadSpeed, int uploadSpeed) {
    int transmittedByteRate = getBytesTransmitted() + Math.min(downloadSpeed, uploadSpeed);
    setBytesTransmitted(Math.min(transmittedByteRate, getSize()));
    setPresentContent(new String(getFutureContent().toCharArray(), 0, getBytesTransmitted()));
  }

}
