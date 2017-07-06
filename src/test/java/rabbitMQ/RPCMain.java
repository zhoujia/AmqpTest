package rabbitMQ;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zhoujia on 2017/7/4.
 */
public class RPCMain {
    private final static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws Exception {
        Random r = new Random();
        RPCClient rpcClient = new RPCClient();
        List<Future<String>> list = new ArrayList<>();
        Map<Integer, Future<String>> map = new HashMap<>();
        for(int i=0;i<100;i++) {
            int rnum = (int)(r.nextDouble() * 1000 + 1);
            Future<String> submit = executor.submit(() -> {
                String msg = String.valueOf(rnum);
                System.out.println(" [x] Requesting getMd5String(" + msg + ")");
                String response = null;
                try {
                    response = rpcClient.call(msg);
                    System.out.println(" [.] Got '" + response + "'");
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
            map.put(rnum, submit);
        }

        map.forEach((k,v)->{
            try {
                System.out.println(k + " " + v.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        //rpcClient.close();
    }
}