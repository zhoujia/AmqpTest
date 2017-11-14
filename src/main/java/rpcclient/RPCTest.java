package rpcclient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RPCTest {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        RPClient fibonacciRpc = new RPClient();

        System.out.println(" [x] Requesting fib(25)");
        String response = fibonacciRpc.call("25");
        System.out.println(" [.] Got '" + response + "'");

        fibonacciRpc.close();
    }
}
