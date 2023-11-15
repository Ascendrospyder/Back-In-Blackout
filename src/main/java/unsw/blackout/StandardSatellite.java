package unsw.blackout;


import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class StandardSatellite extends Satellite {
  private static final int STANDARD_SATELLITE_SPEED = 2500;
  private int direction;

  public StandardSatellite(String satelliteId, String type, double height, Angle position) {
    super(satelliteId, type, height, position, 3, 80, 1, 1, 0, 0);
    this.direction = MathsHelper.CLOCKWISE;
  }

  @Override
  public void moveByOneMinute() {
    double angularVelocity = getAngularVelocity();
    double updatedPosition = getPosition().toDegrees() - (angularVelocity);
    setPosition(Angle.fromDegrees(updatedPosition));
  }

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  @Override
  public double getAngularVelocity() {
    return Math.toDegrees(STANDARD_SATELLITE_SPEED / super.getHeight());
  }

}
