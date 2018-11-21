package team3543.client;

import edu.wpi.first.networktables.*;
import team3543.robot.OI;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Client application.  If started with an argument, loads the playback script and then exits.
 *
 * If started without an argument, listens for the SAVE channel and writes the last recording to "latest.json"
 * in the current directory.
 *
 */
public class PlaybackClient {
    enum Mode { RECORD, PLAY }

    final NetworkTable networkTable;
    boolean done = false;
    String recording = null;

    /**
     *
     * @param args
     */
    public static void main(String [] args) throws Exception {

//        if (args.length < 1) {
//            usage();
//        }

        Mode mode = Mode.RECORD;
        if (args.length > 0) {
            try {
                mode = Mode.valueOf(args[0]);
            }
            catch (Exception ex) {
                usage();
            }
        }

        PlaybackClient client = new PlaybackClient();

        if (mode == Mode.RECORD) {
            client.record(args.length > 1 ? new FileOutputStream(args[1]) : System.out);
        }
        else {
            client.play(args.length > 1 ? new FileInputStream(args[1]) : System.in);
        }
    }

    static void usage() {
        System.err.println("Usage:");
        System.err.println("java team3543.client.PlaybackClient rec <filename=robotscript.json>");
        System.err.println("java team3543.client.PlaybackClient play <filename>");
        System.exit(1);
    }

    PlaybackClient() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        networkTable = inst.getTable(OI.NETWORK_TABLE);
        inst.startClientTeam(3543);
    }

    void record(OutputStream os) throws IOException {
        // open a stream for writing
        // now wait on the network table for an entry change
        networkTable.addEntryListener(OI.RECORD_SAVE_CHANNEL, (table, key, entry, value, flags) -> {
            recording = value.getString();
            done = true;
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate );
        while (recording == null && !done) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                done = true;
            }
        }
        if (recording != null) {
            os.write(recording.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }

    void play(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 4096, bread = 0;
        byte [] bytes = new byte[len];
        while (-1 != (bread = is.read(bytes))) {
            bos.write(bytes,0, bread);
        }
        String json = new String(bos.toByteArray(), StandardCharsets.UTF_8);
        System.out.println("read: "+json);
        networkTable.getEntry(OI.RECORD_LOAD_CHANNEL).setString(json);
    }
}
