import javax.sound.sampled.*;

public class AmplitudeLogger {
    // 修正后的设备名称（根据上次调试输出）
    private final String TARGET_DEVICE_NAME = "CABLE Output (VB-Audio Virtual Cable)";
    public static void main(String[] args) {
        new AmplitudeLogger().startLogging();
    }
    public void startLogging() {
        // 使用修正后的 48000 Hz, 16位, 2声道 格式
        AudioFormat format = new AudioFormat( 48000.0F, 16, 2, true, false );
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        Mixer mixer = findMixer(TARGET_DEVICE_NAME);
        if (mixer == null) {
            System.err.println("错误：未找到音频设备: " + TARGET_DEVICE_NAME);
            return;
        }
        try (TargetDataLine line = (TargetDataLine) mixer.getLine(info)) {
            line.open(format);
            line.start();
            System.out.println("成功连接设备。开始实时振幅监听 (Ctrl+C 停止)...");
            int bufferSize = (int) format.getSampleRate() * format.getFrameSize() / 10;
            // 0.1秒缓冲区
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                int maxAmplitude = getMaxAmplitude(buffer, bytesRead);
                System.out.println("当前音频块最大振幅: " + maxAmplitude);
            }
        } catch (Exception e) {
            System.err.println("捕获 Line 错误。请检查 Windows 声音配置是否匹配 48000 Hz / 16位 / 立体声。");
            e.printStackTrace();
        }
    }
    // 辅助方法：查找 Mixe
    private Mixer findMixer(String name) {
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            if (mixerInfo.getName().contains(name)) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                // 确保它是一个支持 TargetDataLine 的 Mixer
                if (mixer.getTargetLineInfo().length > 0) {
                    return mixer;
                }
            }
        } return null;
    }
    // 辅助方法：计算音频块中的最大振幅
    private int getMaxAmplitude(byte[] audioData, int bytesRead) {
        int maxAmplitude = 0; for (int i = 0; i < bytesRead - 1; i += 2) {
            // 从字节转换为16位有符号整数
            int amplitude = (audioData[i+1] << 8) | (audioData[i] & 0xff);
            if (Math.abs(amplitude) > maxAmplitude) {
                maxAmplitude = Math.abs(amplitude);
            }
        }
        return maxAmplitude;
    }
}
