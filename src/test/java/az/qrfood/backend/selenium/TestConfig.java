package az.qrfood.backend.selenium;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"classpath:selenium-test.properties", "classpath:application.properties"})
public interface TestConfig extends Config {

    @Key("host")
    String host();

    @Key("how.fast")
    String howFast();

    @Key("json.source")
    String fileWithData();

    @Key("auth.login")
    String loginUrl();

    @Key("frontend.orders.url")
    String feOrdersUrl();

    @Key("admin.eatery.admin.eateries")
    String eateryAdminEateries();

    @Key("user.and.eatery")
    String eateryAdminUrl();

    @Key("api.qr-code.contents")
    String qrContentUrls();

    @Key("frontend.login.url")
    String feLoginUrl();

}