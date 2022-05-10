package serialreader;

import java.lang.String;
import java.io.*;
import com.fazecast.jSerialComm.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Elias
 */
public class SerialReader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FileOutputStream outfile;
        System.out.println("Co2 logger");
	var a = SerialPort.getCommPorts();
	System.out.println(a.length);
        SerialPort comPort = SerialPort.getCommPorts()[1];
        comPort.openPort();
        long lastWrite = 0;
        try {
            String timeStampa = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime());
            outfile = new FileOutputStream(timeStampa + "output.csv");
            String currentco2 = "";
            String currentIoState = "";
            long seconds = 0;
            int intervalStore = 60;

            //eternal software until exit
            while (true) {
                //whait avaliable
                while (comPort.bytesAvailable() < 1) {
                    Thread.sleep(20);
                }

                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                String msgDecode = new String(readBuffer, "UTF-8");
//                System.out.print(msgDecode);
//                System.out.println(currentIoState);
                var parts = msgDecode.split(":");
                if (parts.length > 1 && parts[0].trim().getBytes().length > 0) {

//                    System.out.println(parts[0].trim().getBytes()[0]);;
//                    System.out.println("co2".getBytes()[0]);
//                    System.out.println(parts[0].trim().getBytes() == "co2".getBytes());
                    //co2                    
                    if (parts[0].trim().getBytes()[0] == 99) {
                        currentco2 = parts[1];
//                        System.out.println("entrou");
                    }
                    //ios
                    if (parts[0].trim().getBytes()[0] == 105) {
//                        System.out.println("e2");;
                        currentIoState = parts[1];
                    }
                }
                seconds = System.currentTimeMillis() / 1000;
//                System.out.println("seconds:" + seconds)
//
                if (seconds % intervalStore == 0 && seconds != lastWrite) {
                    lastWrite = seconds;
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                    String csvLine = currentIoState.replace("\n", "").replace("\r", "") + "," + currentco2.replace("\n", "").replace("\r", "") + "," + timeStamp + "\n";
                    System.out.println(csvLine);
                    if (!csvLine.trim().isEmpty()) {
                        outfile.write(csvLine.getBytes());
                    }
                }
//                System.out.println("Read " + numRead + " bytes.");;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        comPort.closePort();
        // TODO code application logic here
    }

}
