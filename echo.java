import ithakimodem.Modem;

public class echo {

    public static void echoApp (String request){

        int k;
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

                System.out.print((char)k);
                response += (char)k;

                if (response.indexOf("PSTOP") > 0) {
                    System.out.println(" Packet is here. ");

                    break;
                }

            } catch (Exception x) {
                System.out.println(x);
                break;
            }
        }
        modem.close();
    }

    public static long echoResponse (String request) {

        int k;
        request += "\r";
        String response = "";

        //  Time Monitoring Variables

        long startTime = 0;
        long endTime = 0;
        long deltaTime = 0;

        //  Modem Initialization and speed and timeout values

        Modem modem = new Modem();
        modem.setSpeed(8000);
        modem.setTimeout(1000);
        modem.open("ithaki");

        modem.write(request.getBytes());

        startTime = System.currentTimeMillis();

        //  Endless Loop to receive modem information

        for (;;) {

            try {
                k = modem.read();

                if (k == -1) {
                    System.out.println("Connection Closed");
                    modem.close();
                    return -1;
                }

                //System.out.print((char)k);
                response += (char)k;

                if (response.indexOf("PSTOP") > 0) {
                    endTime = System.currentTimeMillis();
                    //System.out.println("\n" + "Packet is here.");
                    break;
                }

            } catch (Exception x) {
                System.out.println(x);
                return -1;
            }
        }

        modem.close();

        deltaTime = endTime - startTime;
        return deltaTime;
    }

    public static long echoResponseAverage (int samples, String request) {

        int k;
        request += "\r";
        String response = "";

        long startTime = 0;
        long endTime = 0;
        long deltaTime = 0;
        long averageResponseTime = 0;
        long totalTime = 0;

        Modem modem = new Modem();
        modem.setSpeed(80000);
        modem.setTimeout(1000);
        modem.open("ithaki");

        modem.write(request.getBytes());

        for (int i = 0; i < samples; i++) {

            startTime = System.currentTimeMillis();

            for (; ; ) {

                try {
                    k = modem.read();

                    if (k == -1) {
                        System.out.println("Connection Closed");
                        break;
                    }

                    System.out.print((char) k);
                    response += (char) k;

                    if (response.indexOf("PSTOP") > 0) {
                        endTime = System.currentTimeMillis();

                        System.out.println(" Packet is here. ");

                        break;
                    }

                } catch (Exception x) {
                    System.out.println(x);
                    break;
                }
            }

            deltaTime = endTime - startTime;
            totalTime += deltaTime;

        }

        averageResponseTime = totalTime / samples;

        return averageResponseTime;
    }
}
