package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(64);

    public void addHandler(String method, String path, Handler handler) {
        handlers.putIfAbsent(method, new HashMap<>());
        handlers.get(method).put(path, handler);
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                executorService.submit(() -> handleRequest(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket socket) {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            final var requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }

            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }

            final var method = parts[0];
            final var path = parts[1];

            Map<String, String> headers = new HashMap<>();
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                String[] headerParts = line.split(": ");
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            StringBuilder body = new StringBuilder();
            if ("POST".equalsIgnoreCase(method)) {
                String contentLength = headers.get("Content-Length");
                if (contentLength != null) {
                    int length = Integer.parseInt(contentLength);
                    char[] buffer = new char[length];
                    in.read(buffer, 0, length);
                    body.append(buffer);
                }
            }

            Request request = new Request(method, path, headers, body.toString());

            Map<String, Handler> methodHandlers = handlers.get(method);
            if (methodHandlers != null) {
                Handler handler = methodHandlers.get(path);
                if (handler != null) {
                    handler.handle(request, out);
                } else {
                    out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                }
            } else {
                out.write("HTTP/1.1 405 Method Not Allowed\r\n\r\n".getBytes());
            }
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
