package frc.robot.subsystems;

import edu.wpi.first.wpilibj.SPI;

public class PixyCam {

    private SPI spi = new SPI(SPI.Port.kMXP);

    public void requestVersion() {
        byte[] request = new byte[] {
                (byte) 0xC1, (byte) 0xAE,   // 0 - 1	16-bit sync
                (byte) 0x0E,         // Type of packet	14
            0x00          // Length of payload
        };

        spi.write(request, request.length);

        byte[] packet = readPacket((byte) 0x0F);
        short hardwareVersion = convertToWord(packet, 0);
        byte firmwareMajorVersion = packet[2];
        byte firmwareMinorVersion = packet[3];
        short firmwareBuild = convertToWord(packet, 4);
        String firmwareType = new String(packet, 6, packet.length - 6);

    }

    private byte[] readPacket(byte responseType) {
        byte[] header = new byte[4];
        spi.read(false, header, 4);
        if (header[0] == responseType) {
            System.out.println(String.format("Unexepcted type: %02X", header[0]));
            return null;
        }

        byte length = header[1];
        short checksum = convertToWord(header, 2);

        byte[] packet = new byte[length];
        spi.read(false, packet, length);

        return packet;
    }

    private boolean readSync() {
        byte[] word = new byte[2];
        spi.read(false, word, 2);

        System.out.println(String.format("Sync word: %02X %02X", word[0], word[1]));

        return (word[0] == 0xC1) && (word[1] == 0xAF);
    }

    private short convertToWord(byte[] data, int offset) {
        return (short) ((((int) data[offset]) << 8) + ((int) data[offset + 1]));
    }
}
