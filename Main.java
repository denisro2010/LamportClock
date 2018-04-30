import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String input;

        System.setProperty("java.net.preferIPv4Stack" , "true");

        try {
            int n = Integer.parseInt(args[0]);
            LamportClock[] clocks = new LamportClock[n];
            InetAddress group = InetAddress.getByName("224.255.255.255");
            for (int i = 0; i < n; ++i) {
                int port = 8888;
                LamportClock lc = new LamportClock(group, port);
                lc.start();
                clocks[i] = lc;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                input = in.readLine();
                if (input.equals("exit"))
                    return;

                /**
                 * A message format is of the following:
                 * EVENT_NAME ID_OF_SENDER (ID_OF_RECEIVER)
                 * 
                 * EVENT_NAME is of the following:
                 *  - SEND
                 *  - LOCAL
                 * 
                 * For example:
                 * SEND 1 2 (process 1 sends a message to process 2)
                 * LOCAL 3 (process 3 performs a local event)
                 */
                // perform a string split operation based on space
                String[] splits = input.split(" ");
                if (splits.length == 0) {
                    continue;
                }
                switch(splits[0].toUpperCase()) {

                    case "SEND":
                        int clockArrayId = Integer.parseInt(splits[1]);
                        long firstProcessId = clocks[clockArrayId].getId();
                        int senderLocalTime = clocks[clockArrayId].getTime();
                        long secondProcessId = clocks[Integer.parseInt(splits[2])].getId();
                        String messageContent = "";
                        if (splits.length >= 3) {
                            List<String> wordsList = Arrays.asList(
                                Arrays.copyOfRange(splits, 3, splits.length));
                            messageContent = String.join(" ", wordsList);
                        }

                        Event e = new Event(1, firstProcessId, secondProcessId, senderLocalTime, messageContent);
                        clocks[clockArrayId].updateTime(e);
                        break;

                    case "LOCAL":
                        clockArrayId = Integer.parseInt(splits[1]);
                        firstProcessId = clocks[clockArrayId].getId();
                        senderLocalTime = clocks[clockArrayId].getTime();
                        secondProcessId = 0;
                        messageContent = "";

                        e = new Event(0, firstProcessId, secondProcessId, senderLocalTime, messageContent);
                        clocks[clockArrayId].updateTime(e);
                        break;

                    default:
                        throw new RuntimeException("Invalid event name");

                }
            }
        } catch(Exception e) {
            System.err.println(e);
        }
    }

}