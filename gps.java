import ithakimodem.Modem;

public class gps {

    public static void gpsApp (String request) {

        int k = -1;
        request += "\r";
        String response = "";

        Modem modem = new Modem();
        modem.setSpeed(80000);
        modem.setTimeout(1000);
        modem.open("ithaki");

        modem.write(request.getBytes());

        for(;;) {

            try{
                k = modem.read();

                if ( k == -1 ){
                    System.out.println("Connection Closed");
                    break;
                }

                System.out.print((char)k);
                response += (char)k;

                if (response.indexOf("STOP ITHAKI GPS TRACKING") > 0) {
                    break;
                }

            } catch (Exception x) {
                System.out.println(x);
                break;
            }
        }
    }

    public static void gpsImage (String request, String imagePath) {

        //  Receiving variables initialization

        int k = -1;
        int j = 0;
        String imageRequest = request;
        request += "R=1003090\r";                   // 1: route, 0000: Starting point, 50: samples

        String response = "";

        //Virtual Modem Initialization

        Modem modem = new Modem();
        modem.setSpeed(80000);
        modem.setTimeout(1000);
        modem.open("ithaki");

        modem.write(request.getBytes());

        char timeSeconds10 = 'z';

        String longitude = "";
        String latitude = "";

        int longSec = 0;
        int latSec = 0;

        int counter = 0;

        for (; ; ) {

            try {

                k = modem.read();
                //System.out.print((char)k);

                if (k == -1) {
                    System.out.println("Connection Closed");
                    break;
                }

                response += (char) k;

                //System.out.print((char)k);

                if ((char)k == '$' ) {
                    if (counter == 0) {

                        response = "";
                        counter++;
                        continue;

                    } else {

                        int sampleTime = 0;
                        sampleTime += (int)response.charAt(11) % 48;
                        sampleTime += ((int)response.charAt(10) % 48) * 10;

                        if (counter == 1) {

                            timeSeconds10 = response.charAt(10);

                            for(int i = 0; i < 4; i++) {

                                longitude += response.charAt(30+i);
                                latitude += response.charAt(17+i);

                            }

                            //  Conversion of the information from strings to integers

                            longSec += ((int)response.charAt(35) % 48) * 1000;
                            longSec += ((int)response.charAt(36) % 48) * 100;
                            longSec += ((int)response.charAt(37) % 48) * 10;
                            longSec += (int)response.charAt(38) % 48;
                            longSec = (int)((double)longSec * 0.006);

                            longitude += (char)((longSec / 10) + 48);
                            longitude += (char)((longSec % 10) + 48);

                            latSec += ((int)response.charAt(22) % 48) * 1000;
                            latSec += ((int)response.charAt(23) % 48) * 100;
                            latSec += ((int)response.charAt(24) % 48) * 10;
                            latSec += (int)response.charAt(25) % 48;
                            latSec = (int)((double)latSec * 0.006);

                            latitude += (char)((latSec / 10) + 48);
                            latitude += (char)((latSec % 10) + 48);

                            imageRequest += "T=";
                            imageRequest += (longitude + latitude);

                            counter++;
                            continue;

                        } else if (counter % 10 == 7) {

                            for (int i = 0; i < 4; i++) {
                                longitude += response.charAt(30+i);
                                latitude += response.charAt(17+i);
                            }

                            //  Conversion of the information from strings to integers

                            longSec += ((int)response.charAt(35) % 48) * 1000;
                            longSec += ((int)response.charAt(36) % 48) * 100;
                            longSec += ((int)response.charAt(37) % 48) * 10;
                            longSec += (int)response.charAt(38) % 48;
                            longSec = (int)((double)longSec * 0.006);

                            longitude += (char)((longSec / 10) + 48);
                            longitude += (char)((longSec % 10) + 48);

                            latSec += ((int)response.charAt(22) % 48) * 1000;
                            latSec += ((int)response.charAt(23) % 48) * 100;
                            latSec += ((int)response.charAt(24) % 48) * 10;
                            latSec += (int)response.charAt(25) % 48;
                            latSec = (int)((double)latSec * 0.006);

                            latitude += (char)((latSec / 10) + 48);
                            latitude += (char)((latSec % 10) + 48);

                            imageRequest += "T=";
                            imageRequest += (longitude + latitude);
                            counter++;
                            continue;

                        }
                    }

                    longitude = "";
                    latitude = "";
                    longSec = 'z';
                    latSec = 'z';
                    response = "";
                    counter++;

                }

                //  Condition to stop receiving information from the virtual modem

                if (response.indexOf("STOP ITHAKI GPS TRACKING") > 0) {
                    System.out.println(response);
                    break;
                }

            } catch (Exception x) {
                System.out.println(x);
                break;
            }
        }

        modem.close();

        //  Calling the imageApp from the image class to save the image from the gps tracking

        image.imageApp(imageRequest,imagePath);

        System.out.println("\nGPS Tracking Image Saved");
    }
}
