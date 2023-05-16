import ithakimodem.Modem;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

public class image {

    public static void imageApp(String request, String fileName){
        try {

            //  Initialization of the receiving variables & lists

            int k = -1;
            request += "\r";

            List<Byte> byteList = new ArrayList<Byte>();
            byteList.add((byte) 255);
            byteList.add((byte) 216);

            String inputHexString = "";

            //  Initializing an output stream in order to save the image

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStream outputStream = new FileOutputStream(fileName);

            //  Initializing the virtual modem

            Modem modem = new Modem();
            modem.setSpeed(80000);
            modem.setTimeout(1000);

            modem.open("ithaki");

            modem.write(request.getBytes());

            System.out.println("Connection with virtual modem being established");

            masterloop:
            for (;;) {

                try {

                    k = modem.read();
                    inputHexString += (Integer.toHexString(k) + "_");		//listen and wait until ff_d8

                    if (k == -1) {
                        System.out.println("Connection closed");
                        break;
                    }

                    //Condition to START saving the image

                    if(inputHexString.contains("ff_d8")) {

                        System.out.println("Image downloading...");

                        for (;;) {

                            k = modem.read();
                            inputHexString += (Integer.toHexString(k) + "_");

                            if (k == -1) {
                                System.out.println("Connection closed");
                                break masterloop;
                            }

                            byteList.add((byte)k);

                            //Condition to STOP saving the image

                            if(inputHexString.contains("ff_d9")) {

                                //convert byte list to byte array and export to .jpeg file
                                byte[] byteArray = new byte[byteList.size()];
                                for(int i = 0 ; i < byteList.size() ; i++)
                                {
                                    byteArray[i] = byteList.get(i);
                                }

                                byteArrayOutputStream.write(byteArray);
                                byteArrayOutputStream.writeTo(outputStream);
                                System.out.println("Image saved as " + fileName);
                                break masterloop;
                            }
                        }
                    }

                } catch (Exception x) {
                    System.out.println(x);
                    break masterloop;
                }
            }


        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}