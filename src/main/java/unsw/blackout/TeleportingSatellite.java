package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class TeleportingSatellite extends Satellite {
  private static final int TELEPORTING_SATELLITE_SPEED = 1000;
  private int direction;
  private boolean teleportIsComplete;

  public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
    super(satelliteId, type, height, position, 200, 200, 10, 15, 0, 0);
    this.direction = MathsHelper.ANTI_CLOCKWISE;
    this.teleportIsComplete = false;
  }

  public int getDirection() {
    return direction;
  }

  public boolean isTeleportIsComplete() {
    return teleportIsComplete;
  }

  public void setTeleportIsComplete(boolean teleportIsComplete) {
    this.teleportIsComplete = teleportIsComplete;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  @Override
  public void moveByOneMinute() {
    double angularVelocity = getAngularVelocity();
    Angle currentPosition = getPosition();
    Angle updatedPosition;

    if (getDirection() == MathsHelper.ANTI_CLOCKWISE) {
      updatedPosition = currentPosition.add(Angle.fromDegrees(angularVelocity));

      // if the position exceedds 360 degrees subtract 360 to make sure
      // we stay in the valid zone
      if (updatedPosition.compareTo(Angle.fromDegrees(360)) > 0) {
        updatedPosition = updatedPosition.subtract(Angle.fromDegrees(360));
      }

      // if the updatedPosition is greater than 180, set the position to
      // 0 and change the direction as a result of a sucessful teleportation
      if (updatedPosition.compareTo(Angle.fromDegrees(180)) > 0) {
        updatedPosition = Angle.fromDegrees(0);
        setDirection(MathsHelper.CLOCKWISE);
        setTeleportIsComplete(true);
      }
    } else {
      updatedPosition = getPosition().subtract(Angle.fromDegrees(angularVelocity));

      // if our position is in the negatives add 360 to bring it
      // to the valid range
      if (updatedPosition.compareTo(Angle.fromDegrees(0)) < 0) {
        updatedPosition = updatedPosition.add(Angle.fromDegrees(360));
      }

      // if the position is less than 180 then teleport to 0
      // changing the direction as a result of a successful teleportation
      if (updatedPosition.compareTo(Angle.fromDegrees(180)) < 0) {
        updatedPosition = Angle.fromDegrees(0);
        setDirection(MathsHelper.ANTI_CLOCKWISE);
        setTeleportIsComplete(true);
      }
    }
    setPosition(updatedPosition);
  }

  @Override
  public double getAngularVelocity() {
    return Math.toDegrees(TELEPORTING_SATELLITE_SPEED / super.getHeight());
  }

}
