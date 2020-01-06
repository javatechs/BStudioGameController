package com.belithco.bsgamecontroller;

import android.util.Log;

import edu.wpi.SimplePacketComs.server.ByteServer;

public class ScriptControllerServer extends ByteServer {
    private static String TAG = "CommandControllerServer";
//    private byte[] serverData = new byte[60];
    public byte nextSeqNumber = -1;
    public byte lastSeqNumber = -1;
    private String cmd = "";

    public ScriptControllerServer(int id) {
        super(id);
//        initControllerData();
    }
    @Override
    public boolean event(byte[] packet) {
        nextSeqNumber = packet[0];
        packet[0] = (byte)0xff;
        if (  (null!=cmd)
           && (cmd.length()>0)
//           && (nextSeqNumber!=lastSeqNumber)
           ) {
            // Init packet buffer
            for (int i = 0; i < packet.length; i++) {
                packet[i] = 0;
            }
            // Parse next buffer
            String line = "";
            int size = Math.min(cmd.getBytes().length,packet.length-2);
            for (int i = 0; i < size; i++) {
                packet[2+i] = cmd.getBytes()[i];
                line+=cmd.getBytes()[i];
            }
            if (size<cmd.length()) {
                cmd = cmd.substring(size);
            }
            else {
                cmd = "";
            }
            if (cmd.length()>0) {
                packet[1] = 0x1;
            }
            Log.w("A","lastSeqNumber: "+lastSeqNumber);
            Log.w("A","nextSeqNumber: "+nextSeqNumber);
            Log.w("A", line);
            packet[0] = nextSeqNumber;
            lastSeqNumber = nextSeqNumber;
        }
        return true;
    }

//    /**
//     * Initialize
//     */
//    public void initControllerData() {
//        byte[] controllerData = getControllerData();
//        for (int i=0; i<controllerData.length; i++) {
//            controllerData[i] = 0;
//        }
//    }

    /**
     * Trigger send
     */
    public void send(String cmd) {
        this.cmd = cmd;
//        nextSeqNumber = 0xc;
    }
//    public byte[] getControllerData() {
//        return serverData;
//    }
}
