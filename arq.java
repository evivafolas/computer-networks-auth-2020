import ithakimodem.Modem;

import java.util.ArrayList;
import java.util.List;

public class arq {

    public static void arqApplication (String request) {

        int k = -1;
        request += "\r";
        String response = "";

        Modem modem = new Modem();
        modem.setSpeed(80000);
        modem.setTimeout(1000);
        modem.open("ithaki");

        modem.write(request.getBytes());

        for (;;) {

            try {
                k = modem.read();

                if (k == -1) {
                    System.out.println("Connection Closed");
                    break;
                }

                if (response.indexOf("PSTART") > 0) {
                    System.out.print((char) k);
                }
                response += (char)k;

                if (response.indexOf("PSTOP") > 0) {
                    System.out.println("\n");

                    break;
                }

            } catch (Exception x) {
                System.out.println(x);
                break;
            }
        }
    }

    public static boolean autoRepeatRequest (String request) {

        int k = -1;
        request += "\r";
        String response = "";

        String fcs = "";
        String checkSequence = "";
        int fcsCounter = 0;


        Modem modem = new Modem();
        modem.setSpeed(80000);
        modem.setTimeout(1000);
        modem.open("ithaki");

        modem.write(request.getBytes());

        for (; ; ) {

            try {
                k = modem.read();

                if (k == -1) {
                    System.out.println("Connection Closed");
                    return false;
                }

                response += (char) k;

                if (response.indexOf("PSTART") > 0) {
                    System.out.print((char) k);
                }

                if (response.indexOf("<") > 0 && response.indexOf(">") < 0) {
                    checkSequence += (char) k;
                }

                if (response.indexOf("> ") > 0 && fcsCounter < 4) {
                    fcs += (char) k;
                    fcsCounter++;
                }

                if (response.indexOf("PSTOP") > 0) {
                    System.out.println("\n");
                    //System.out.println("Check Sequence: " + checkSequence);
                    //System.out.println("FCS: " + fcs);
                    break;
                }

            } catch (Exception x) {
                System.out.println(x);
                break;
            }
        }
        String checkSequenceCorrected = "";
        String fcsCorrected = "";

        for (int i = 1; i < 17; i++) {
            checkSequenceCorrected += checkSequence.charAt(i);
        }

        for (int i = 1; i < 4; i++) {
            fcsCorrected += fcs.charAt(i);
        }

        System.out.println("Corrected Sequence Sting: " + checkSequenceCorrected);
        System.out.println("Corrected FCS String: " + fcsCorrected);

        int fcsNumber = 0;

        fcsNumber = (((int) fcsCorrected.charAt(0) % 48) * 100) + (((int) fcsCorrected.charAt(1) % 48) * 10) + (int) fcsCorrected.charAt(2) % 48;

        int fcsCalculated = 0;

        char[] sArr = new char[16];

        for (int i = 0; i < 16; i++) {
            sArr[i] = checkSequenceCorrected.charAt(i);
        }

        fcsCalculated = sArr[0] ^ sArr[1] ^ sArr[2] ^ sArr[3] ^ sArr[4] ^ sArr[5] ^ sArr[6] ^ sArr[7] ^ sArr[8] ^ sArr[9] ^ sArr[10] ^ sArr[11] ^ sArr[12] ^ sArr[13] ^ sArr[14] ^ sArr[15];

        System.out.println("The calculated FCS is: " + fcsCalculated);

        //modem.close();

        if (fcsCalculated == fcsNumber) {
            modem.close();
            return true;
        } else {
            return false;
        }
    }

    public static int arqApp (String ackRequest, String nackRequest){

        int k = -1;
        ackRequest += "\r";
        nackRequest += "\r";
        String response = "";

        String fcs = "";
        String checkSequence = "";
        int fcsCounter = 0;

        String checkSequenceCorrected = "";
        String fcsCorrected = "";

        int fcsNumber = 0;
        int fcsCalculated = 0;

        char[] sArr = new char[16];

        Modem modem = new Modem();
        modem.setSpeed(80000);
        modem.setTimeout(5000);
        modem.open("ithaki");

        modem.write(ackRequest.getBytes());

        for (; ; ) {

            try {
                k = modem.read();

                if (k == -1) {
                    System.out.println("Connection Closed");
                    modem.close();
                    return -1;
                }

                response += (char) k;

                if (response.indexOf("PSTART") > 0) {
                    System.out.print((char) k);
                }

                if (response.indexOf("<") > 0 && !response.contains(">")) {
                    checkSequence += (char) k;
                }

                if (response.indexOf("> ") > 0 && fcsCounter < 4) {
                    fcs += (char) k;
                    fcsCounter++;
                }

                if (response.indexOf("PSTOP") > 0) {
                    System.out.println("\n");
                    break;
                }

            } catch (Exception x) {
                System.out.println(x);
                break;
            }
        }


        for (int i = 1; i < 17; i++) {
            checkSequenceCorrected += checkSequence.charAt(i);
        }

        for (int i = 1; i < 4; i++) {
            fcsCorrected += fcs.charAt(i);
        }

        System.out.println("Corrected Sequence Sting: " + checkSequenceCorrected);
        System.out.println("Corrected FCS String: " + fcsCorrected);

        fcsNumber = (((int) fcsCorrected.charAt(0) % 48) * 100) + (((int) fcsCorrected.charAt(1) % 48) * 10) + (int) fcsCorrected.charAt(2) % 48;

        for (int i = 0; i < 16; i++) {
            sArr[i] = checkSequenceCorrected.charAt(i);
        }

        fcsCalculated = sArr[0] ^ sArr[1] ^ sArr[2] ^ sArr[3] ^ sArr[4] ^ sArr[5] ^ sArr[6] ^ sArr[7] ^ sArr[8] ^ sArr[9] ^ sArr[10] ^ sArr[11] ^ sArr[12] ^ sArr[13] ^ sArr[14] ^ sArr[15];

        System.out.println("The calculated FCS is: " + fcsCalculated);

        if (fcsCalculated == fcsNumber) {
            modem.close();
            System.out.println("Positive Acknowledgement without ARQ");
            return 0;
        }

        int nackCounter = 0;

        while (fcsNumber != fcsCalculated) {

            modem.write(nackRequest.getBytes());

            k = -1;

            response = "";

            checkSequence = "";

            for (; ; ) {

            try {

                k = modem.read();

                if (k == -1) {
                    System.out.println("Connection Closed");
                    modem.close();
                    return -1;
                    }

                response += (char) k;

                if (response.indexOf("PSTART") > 0) {
                    System.out.print((char) k);
                    }

                if (response.indexOf("<") > 0 && !response.contains(">")) {
                    checkSequence += (char) k;
                    }

                if (response.indexOf("PSTOP") > 0) {
                    System.out.println("\n");
                    //System.out.println("Check Sequence: " + checkSequence);
                    //System.out.println("FCS: " + fcs);
                    break;
                }

            } catch (Exception x) {
                        System.out.println(x);
                        break;
                   }
            }

            checkSequenceCorrected = "";

            for (int i = 1; i < 17; i++) {
                checkSequenceCorrected += checkSequence.charAt(i);
            }

            System.out.println("Corrected Sequence Sting: " + checkSequenceCorrected);
            System.out.println("Corrected FCS String: " + fcsCorrected);

            for (int i = 0; i < 16; i++) {
                sArr[i] = checkSequenceCorrected.charAt(i);
            }

            fcsCalculated = sArr[0] ^ sArr[1] ^ sArr[2] ^ sArr[3] ^ sArr[4] ^ sArr[5] ^ sArr[6] ^ sArr[7] ^ sArr[8] ^ sArr[9] ^ sArr[10] ^ sArr[11] ^ sArr[12] ^ sArr[13] ^ sArr[14] ^ sArr[15];

            System.out.println("The calculated FCS is: " + fcsCalculated);

            nackCounter++;
        }

        System.out.println("Positive Acknowledgement Received");

        modem.close();
        return nackCounter;
    }
}

























