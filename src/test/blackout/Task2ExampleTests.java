package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExampleTests {
  @Test
  public void testEntitiesInRange() {
    // Task 2
    // Example from the specification
    BlackoutController controller = new BlackoutController();

    // Creates 1 satellite and 2 devices
    // Gets a device to send a file to a satellites and gets another device to
    // download it.
    // StandardSatellites are slow and transfer 1 byte per minute.
    controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
    controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
    controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
    controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
    controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
    controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));

    assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"),
        controller.communicableEntitiesInRange("Satellite1"));
    assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"),
        controller.communicableEntitiesInRange("Satellite2"));
    assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceB"));

    assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
  }

  @Test
  public void testSomeExceptionsForSend() {
    // just some of them... you'll have to test the rest
    BlackoutController controller = new BlackoutController();

    // Creates 1 satellite and 2 devices
    // Gets a device to send a file to a satellites and gets another device to
    // download it.
    // StandardSatellites are slow and transfer 1 byte per minute.
    controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
    controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
    controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

    String msg = "Hey";
    controller.addFileToDevice("DeviceC", "FileAlpha", msg);
    assertThrows(FileTransferException.VirtualFileNotFoundException.class,
        () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

    assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
        controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
    controller.simulate(msg.length() * 2);
    assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
        () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
  }

  @Test
  public void testMovement() {
    // Task 2
    // Example from the specification
    BlackoutController controller = new BlackoutController();

    // Creates 1 satellite and 2 devices
    // Gets a device to send a file to a satellites and gets another device to
    // download it.
    // StandardSatellites are slow and transfer 1 byte per minute.
    controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER, "StandardSatellite"),
        controller.getInfo("Satellite1"));
    controller.simulate();
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER, "StandardSatellite"),
        controller.getInfo("Satellite1"));
  }

  @Test
  public void testExample() {
    // Task 2
    // Example from the specification
    BlackoutController controller = new BlackoutController();

    // Creates 1 satellite and 2 devices
    // Gets a device to send a file to a satellites and gets another device to
    // download it.
    // StandardSatellites are slow and transfer 1 byte per minute.
    controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
    controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
    controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

    String msg = "Hey";
    controller.addFileToDevice("DeviceC", "FileAlpha", msg);
    assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
        controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

    controller.simulate(msg.length() * 2);
    assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
        controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

    assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
    assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
        controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

    controller.simulate(msg.length());
    assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
        controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

    // Hints for further testing:
    // - What about checking about the progress of the message half way through?
    // - Device/s get out of range of satellite
    // ... and so on.
  }

  @Test
  public void testRelayMovement() {
    // Task 2
    // Example from the specification
    BlackoutController controller = new BlackoutController();

    // Creates 1 satellite and 2 devices
    // Gets a device to send a file to a satellites and gets another device to
    // download it.
    // StandardSatellites are slow and transfer 1 byte per minute.
    controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));

    // moves in negative direction
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
    controller.simulate();
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
    controller.simulate();
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
    controller.simulate();
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));

    controller.simulate(5);
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
    controller.simulate(24);
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
    // edge case
    controller.simulate();
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
    // coming back
    controller.simulate(1);
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
    controller.simulate(5);
    assertEquals(
        new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
        controller.getInfo("Satellite1"));
  }

  @Test
  public void testTeleportingMovement() {
    // Test for expected teleportation movement behaviour
    BlackoutController controller = new BlackoutController();

    controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));

    controller.simulate();
    Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
    controller.simulate();
    Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
    assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

    // It should take 250 simulations to reach theta = 180.
    // Simulate until Satellite1 reaches theta=180
    controller.simulate(250);

    // Verify that Satellite1 is now at theta=0
    assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);
  }

  @Test
  public void testTeleportingSatelliteRemainingTsRemovedSatelliteToDevice() {
    BlackoutController controller = new BlackoutController();

    // creates a teleporting satellite at 174 degrees, which means it will move
    // anticlockwise
    // initially
    controller.createSatellite("TeleportSatellite", "TeleportingSatellite", 1000 + RADIUS_OF_JUPITER,
        Angle.fromDegrees(174));

    // I go ahead and create a device called DeviceA which is handheld and at
    // 176 degrees
    controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(176));

    // create another device called DeviceB at 178 degrees
    controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(178));

    // Adding the file to the device
    String fileContent = "testing if the teleport tests testability to test";
    controller.addFileToDevice("DeviceA", "FileA", fileContent);

    // Should not throw any errors when sending file
    assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "TeleportSatellite"));
    controller.simulate(5);

    // should not throw any errors when sending file
    assertDoesNotThrow(() -> controller.sendFile("FileA", "TeleportSatellite", "DeviceB"));

    controller.simulate(5);

    // the result file in the reciever DeviceB
    // should transfer bytes normally until if the teleporting satellite sender
    // teleports, where the file will automatically get all the contents of the file
    // except the remaining bytes should have their t's removed
    String result = "testing if the telepor ess esabiliy o es";
    assertEquals(new FileInfoResponse("FileA", result, result.length(), true),
        controller.getInfo("DeviceB").getFiles().get("FileA"));
  }

  @Test
  public void testTeleportingSatelliteRemainingTsRemovedSatelliteToSatellite() {
    BlackoutController controller = new BlackoutController();

    // creates a teleporting satellite at 174 degrees, which means it will move
    // anticlockwise
    // initially
    controller.createSatellite("TeleportSatellite", "TeleportingSatellite", 1000 + RADIUS_OF_JUPITER,
        Angle.fromDegrees(174));

    // I go ahead and create a device called DeviceA which is handheld and at
    // 176 degrees
    controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(176));

    // Adding the file to the device
    String fileContent = "testing if the teleport tests testability to test";
    controller.addFileToDevice("DeviceA", "FileA", fileContent);

    // Should not throw any errors when sending file
    assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "TeleportSatellite"));

    controller.simulate(5);

    // creates a standard satellite at 182 degrees
    controller.createSatellite("Standard", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(182));

    // Should not throw any errors when sending file
    assertDoesNotThrow(() -> controller.sendFile("FileA", "TeleportSatellite", "Standard"));

    controller.simulate(5);

    // the result file in Standard should transfer bytes normally
    // until if the sending satellite teleports
    // where the file will automatically get all the contents of the file
    // except the remaining bytes should have their t's removed
    String result = "tesing if he elepor ess esabiliy o es";
    assertEquals(new FileInfoResponse("FileA", result, result.length(), true),
        controller.getInfo("Standard").getFiles().get("FileA"));
  }

  @Test
  public void testTeleportingSatelliteAllTsRemovedDeviceToSatellite() {
    BlackoutController controller = new BlackoutController();

    // creates a teleporting satellite at 174 degrees, which means it will move
    // anticlockwise
    // initially
    controller.createSatellite("TeleportSatellite", "TeleportingSatellite", 1000 + RADIUS_OF_JUPITER,
        Angle.fromDegrees(179));

    // I go ahead and create a device called DeviceA which is handheld and at
    // 176 degrees
    controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(178));

    // Adding the file to the device
    String fileContent = "testing if the teleport tests testability to test";
    controller.addFileToDevice("DeviceA", "FileA", fileContent);

    // Should not throw any errors when sending file
    assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "TeleportSatellite"));

    controller.simulate(5);

    // The result file in DeviceA (the sender)
    // should have all its t's removed from it
    // if the reciever teleports during a transfer
    String result = "esing if he elepor ess esabiliy o es";
    assertEquals(new FileInfoResponse("FileA", result, result.length(), true),
        controller.getInfo("DeviceA").getFiles().get("FileA"));

    // check if FileA didn't get created in teleportSatellite
    // after a failed partial transfer
    assertNull(controller.getInfo("TeleportSatellite").getFiles().get("FileA"));
  }

  @Test
  public void testExceptionMaxFilesReached() {
    BlackoutController controller = new BlackoutController();
    String fileAContent = "Hey";
    String fileBContent = "Hola";
    String fileCContent = "Ho";
    String fileDContent = "Him";

    controller.createSatellite("Standard", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(183));

    controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(180));
    controller.addFileToDevice("DeviceA", "FileA", fileAContent);
    assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "Standard"));

    controller.simulate(5);

    controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(167));
    controller.addFileToDevice("DeviceB", "FileB", fileBContent);
    assertDoesNotThrow(() -> controller.sendFile("FileB", "DeviceB", "Standard"));

    controller.simulate(5);

    controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(156));
    controller.addFileToDevice("DeviceC", "FileC", fileCContent);
    assertDoesNotThrow(() -> controller.sendFile("FileC", "DeviceC", "Standard"));

    controller.simulate(5);

    controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(143));
    controller.addFileToDevice("DeviceD", "FileD", fileDContent);
    assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
        () -> controller.sendFile("FileD", "DeviceD", "Standard"));

  }

  @Test
  public void sendFileIncompleteTransfer() {
    BlackoutController controller = new BlackoutController();

    controller.createSatellite("Standard1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(183));
    controller.createSatellite("Standard2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(185));
    controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(184));

    String fileContent = "Hello World";
    controller.addFileToDevice("DeviceA", "FileA", fileContent);

    assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "Standard1"));
    controller.simulate();

    assertThrows(FileTransferException.VirtualFileNotFoundException.class,
        () -> controller.sendFile("FileA", "Standard1", "Standard2"));
  }

  @Test
  public void testExceptionMaxByteReached() {
    BlackoutController controller = new BlackoutController();

    controller.createSatellite("Standard1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(183));
    controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(184));

    String fileContent = "2j7P5f1bKsN9G4y3T6zRqXw0lCvA8oBhVpU5nI2dM6sF9jL1eO4gYtH7mW3xZrQ sqwwqdqw dqwdq dqwdqwdw";
    controller.addFileToDevice("DeviceA", "FileA", fileContent);

    assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
        () -> controller.sendFile("FileA", "DeviceA", "Standard1"));
  }

  @Test
  public void testSendRelaySatellite() {
    BlackoutController controller = new BlackoutController();

    controller.createSatellite("Relay1", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(183));
    controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(184));

    String fileContentA = "Hi";
    controller.addFileToDevice("DeviceA", "FileA", fileContentA);

    assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
        () -> controller.sendFile("FileA", "DeviceA", "Relay1"));
  }

  @Test
  public void testRelayOnThreshold() {
    BlackoutController controller = new BlackoutController();

    // if the relay satellite starts at the threshold angle it must take the
    // positive direction
    controller.createSatellite("Relay1", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(345));
    controller.simulate();
    Angle position = controller.getInfo("Relay1").getPosition();
    assertTrue(position.compareTo(Angle.fromDegrees(345)) > 0);
  }

}
