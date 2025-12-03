import javax.sound.sampled.*;

public class AudioDetector {
    private final String TARGET_DEVICE_NAME = "CABLE Output (VB-Audio Virtual Cable)";
    private final int THRESHOLD = 2300;

    public void startListenning(RobotAction action) {
        // 使用 48000.0 Hz 采样率，16位，2声道 (Stereo)
        AudioFormat format = new AudioFormat(
                48000.0F, // **改为 48000.0 Hz**
                16,
                2, // **确保是 2 声道（立体声）**
                true, // Signed PCM
                false // Little-Endian (保持不变)
        );
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        Mixer mixer = findMixer(TARGET_DEVICE_NAME, info);
        if (mixer == null) {
            System.err.println("未找到设备:" + TARGET_DEVICE_NAME);
            return;
        }
        try (TargetDataLine line = (TargetDataLine) mixer.getLine(info)) {
            line.open(format);
            line.start();
            int bufferSize = (int) format.getSampleRate() * format.getFrameSize() / 5;
            byte[] buffer = new byte[bufferSize];

            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                if (checkAmplitude(buffer, bytesRead, THRESHOLD)) {
                    action.rightClick();
                    Thread.sleep(5000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Mixer findMixer(String name, DataLine.Info info) {
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            if (mixerInfo.getName().contains(name)) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                if (mixer.getTargetLineInfo().length > 0) {
                    return mixer;
                }
            }
        }
        return null;
    }
    private boolean checkAmplitude(byte[] audioData, int bytesRead, int threshold) {
        for (int i = 0; i < bytesRead - 1; i += 2) {
            int amplitude = (audioData[i + 1] << 8) | (audioData[i] & 0xff);
            if (Math.abs(amplitude) > threshold) {
                System.out.println("!!! 发现振幅峰值: " + Math.abs(amplitude) + "!!!");
                return true;
            }
        }
        return false;
    }
}
