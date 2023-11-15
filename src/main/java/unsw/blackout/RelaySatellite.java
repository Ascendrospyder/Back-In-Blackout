package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class RelaySatellite extends Satellite {
  private static final int RELAY_SATELLITE_SPEED = 1500;
  private int direction;

  private static final int UPPER_BOUNDARY = 190;
  private static final int LOWER_BOUNDARY = 140;
  private static final int THRESHOLD = 345;

  public RelaySatellite(String satelliteId, String type, double height, Angle position) {
    super(satelliteId, type, height, position, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
    this.direction = MathsHelper.CLOCKWISE;
  }

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  @Override
  public void moveByOneMinute() {
    Angle currentPosition = getPosition();
    Angle updatedPosition;

    // if the currentPosition is greater than the upper boundary and
    // currentPosition is less than the threshold then we subtract
    // and update the direction to clockwise
    // OR
    // if the currentPosition is less than the lower boundary or greater than
    // or equal to the threshold we add and set the direction to anticlockwise
    // as per the specification
    if ((currentPosition.compareTo(Angle.fromDegrees(UPPER_BOUNDARY)) > 0)
        && (currentPosition.compareTo(Angle.fromDegrees(THRESHOLD)) < 0)) {
      updatedPosition = currentPosition.subtract(Angle.fromDegrees(getAngularVelocity()));
      setDirection(MathsHelper.CLOCKWISE);
    } else if ((currentPosition.compareTo(Angle.fromDegrees(LOWER_BOUNDARY)) < 0)
        || (currentPosition.compareTo(Angle.fromDegrees(THRESHOLD)) >= 0)) {
      updatedPosition = currentPosition.add(Angle.fromDegrees(getAngularVelocity()));
      setDirection(MathsHelper.ANTI_CLOCKWISE);
    }

    if (getDirection() == MathsHelper.CLOCKWISE) {
      updatedPosition = currentPosition.subtract(Angle.fromDegrees(getAngularVelocity()));
    } else {
      updatedPosition = currentPosition.add(Angle.fromDegrees(getAngularVelocity()));
    }
    setPosition(updatedPosition);
  }

  @Override
  public double getAngularVelocity() {
    return Math.toDegrees(RELAY_SATELLITE_SPEED / getHeight());
  }

}
