import java.awt.AWTException;

public class Main {
    public static void main(String[] args) {
        System.out.println("自动钓鱼启动");
        try {
            RobotAction robotAction = new RobotAction();
            AudioDetector detector = new AudioDetector();
            Thread listenerThread = new Thread(() -> {
                detector.startListenning(robotAction);
            });
            listenerThread.start();
        } catch (AWTException e) {
            System.err.println("无法创建Robot实例，请确保是否以管理员权限运行");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
