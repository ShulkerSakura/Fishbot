import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.InputEvent;

public class RobotAction {
    private Robot robot;
    public RobotAction() throws AWTException {
        this.robot = new Robot();
    }
    public void rightClick() {
        //模拟鼠标右键点击
        robot.delay(500);
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.delay(50);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        System.out.println("-> 触发右键点击");
    }
}