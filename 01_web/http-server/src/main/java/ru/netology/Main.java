package ru.netology;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();

    // Добавление обработчиков (хендлеров)
    server.addHandler("GET", "/messages", (request, responseStream) -> {
      String responseBody = "GET messages response";
      responseStream.write(("HTTP/1.1 200 OK\r\n" +
              "Content-Length: " + responseBody.length() + "\r\n" +
              "Connection: close\r\n" +
              "\r\n").getBytes());
      responseStream.write(responseBody.getBytes());
      responseStream.flush();
    });

    server.addHandler("POST", "/messages", (request, responseStream) -> {
      String responseBody = "POST messages response: " + request.getBody();
      responseStream.write(("HTTP/1.1 200 OK\r\n" +
              "Content-Length: " + responseBody.length() + "\r\n" +
              "Connection: close\r\n" +
              "\r\n").getBytes());
      responseStream.write(responseBody.getBytes());
      responseStream.flush();
    });

    server.listen(9999);
  }
}
