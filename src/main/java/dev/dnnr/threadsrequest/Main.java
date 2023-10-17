package dev.dnnr.threadsrequest;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String... args) throws Exception {

        final String pathArquivoCsvInput = "base.csv";
        final int threads = 1000; // Numero de threads

        final HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1) // http 1 pois o 2 fazia o server PHP negar conexão "too many
                                                      // concurrent streams"
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> todo = Collections.synchronizedList(new LinkedList<Future<?>>());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathArquivoCsvInput));
            String line;
            while ((line = reader.readLine()) != null) {
                final String currentLine = line;
                System.out.println("[!] Current File Line:" + currentLine);

                try {
                    todo.add(executor.submit(() -> {
                        try {
                            var request = HttpRequest.newBuilder()
                                    .uri(new URI("MINHA URL AQUI"))
                                    .header("Content-Type", "application/json")
                                    .header("Accept", "application/json")
                                    .GET()
                                    .build();

                            var httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                            if (httpResponse.statusCode() == 200) {

                                ObjectMapper mapper = new ObjectMapper();
                                String respJson = httpResponse.body();
                                IResponse response = mapper.readValue(respJson, IResponse.class);
                                System.out.println("[V] Response: " + response);
                            }
                        } catch (Exception error) {
                            System.out.println("[!] Exception Request Internal Error: " + error);
                        }
                    }));
                } catch (Exception error) {
                    System.out.println("[!] Exception Thread Internal Error: " + error);
                }
            }

            waitAll(todo);
            todo.clear();

        } catch (Exception error) {
            System.out.println("[!] Exception Internal Error: " + error);
        } finally {
            executor.shutdown();
        }
    }

    /* CLASSES DE TIPOS APENAS PARA EXEMPLO */
    static class Items {
        public String LockId;
        public String ItemId;
        public int Quantity;
        public String SalesChannel;
        public String ReservationDateUtc;
        public String MaximumConfirmationDateUtc;
        public String ConfirmedDateUtc;
        public String Status;
        public String DateUtcAcknowledgedOnBalanceSystem;
        public String InternalStatus;

        public String getLockId() {
            return LockId;
        }

        public void setLockId(String lockId) {
            LockId = lockId;
        }
    }

    static class Paging {
        public int page;
        public int perPage;
        public int total;
        public int pages;
    }

    static class IResponse {
        public ArrayList<Items> items;
        public Paging paging;

        public ArrayList<Items> getItems() {
            return items;
        }

        public void setItems(ArrayList<Items> items) {
            this.items = items;
        }
    }

    private static void waitAll(Collection<Future<?>> todo) {
        for (Future<?> future : todo) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
