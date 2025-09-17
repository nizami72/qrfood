package az.qrfood.backend.selenium;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Key;

@Config.Sources({"classpath:selenium-test.properties"})
public interface TestConfig extends Config {
    @Key("host")
    String host();
    @Key("how.fast")
    String howFast();
    @Key("json.source")
    String fileWithData();
    @Key("login.url")
    String loginUrl();
    @Key("menu.urls")
    String [] menuUrls();
    @Key("orders.url")
    String ordersUrl();
}