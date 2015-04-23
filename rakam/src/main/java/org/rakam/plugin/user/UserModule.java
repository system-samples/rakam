package org.rakam.plugin.user;

import com.google.auto.service.AutoService;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import io.airlift.configuration.ConfigurationFactory;
import org.rakam.plugin.ConditionalModule;
import org.rakam.plugin.RakamModule;
import org.rakam.plugin.UserPluginConfig;
import org.rakam.server.http.HttpService;
import org.rakam.server.http.WebSocketService;

/**
 * Created by buremba <Burak Emre Kabakcı> on 14/03/15 16:17.
 */
@AutoService(RakamModule.class)
public class UserModule extends RakamModule implements ConditionalModule {
    @Override
    protected void setup(Binder binder) {
        Multibinder<WebSocketService> webSocketServices = Multibinder.newSetBinder(binder, WebSocketService.class);
        webSocketServices.addBinding().to(MailBoxWebSocketService.class).in(Scopes.SINGLETON);

        Multibinder<HttpService> httpServices = Multibinder.newSetBinder(binder, HttpService.class);
        httpServices.addBinding().to(UserHttpService.class).in(Scopes.SINGLETON);

        UserPluginConfig userPluginConfig = buildConfigObject(UserPluginConfig.class);

        if(userPluginConfig.isMailboxEnabled()) {
            httpServices.addBinding().to(UserMailboxHttpService.class).in(Scopes.SINGLETON);
        }
    }

    @Override
    public String name() {
        return "Customer Analytics Module";
    }

    @Override
    public String description() {
        return "Analyze your users";
    }

    @Override
    public boolean shouldInstall(ConfigurationFactory config) {
        return true;
    }
}