import javax.sound.sampled.*;

public class DebugMixers {
    public static void main(String[] args) {
        System.out.println(" --- 系统所有录音设备信息 ---");
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] targetInfos = mixer.getTargetLineInfo();
            if (targetInfos.length > 0) {
                System.out.println(" 设备名称: " + mixerInfo.getName());
                System.out.println("  描述: " + mixerInfo.getDescription());
                for (Line.Info info : targetInfos) {
                    if (info instanceof DataLine.Info) {
                        System.out.println("  支持格式:");
                        for (AudioFormat format : ((DataLine.Info) info).getFormats()) {
                            System.out.println("    - " + format.toString());
                        }
                    }
                }
            }
        }
        System.out.println(" ----------------------------------");
    }
}
