package de.rieckpil.courses.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient openLibraryWebClient(
      @Value("${clients.open-library.base-url}") String openLibraryBaseUrl,
      WebClient.Builder webClientBuilder) {

    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2_000)
            .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).address(new InetSocketAddress("127.0.0.1", 7890)))
            .doOnConnected(
                connection ->
                    connection
                        .addHandlerLast(new ReadTimeoutHandler(2))
                        .addHandlerLast(new WriteTimeoutHandler(2)));

    return webClientBuilder
        .baseUrl(openLibraryBaseUrl)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
