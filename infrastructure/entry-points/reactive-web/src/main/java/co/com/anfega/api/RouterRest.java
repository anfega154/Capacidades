package co.com.anfega.api;

import co.com.anfega.api.config.AbilityPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final AbilityPath abilityPath;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(abilityPath.getAbilities()), handler::listenSaveAbility)
                .andRoute(GET(abilityPath.getAbilities()), handler::listenListAbilities)
                .andRoute(DELETE(abilityPath.getAbilities()), handler::listenDeleteAbilitiesByIds)
                .andRoute(POST(abilityPath.getAllAbilities()), handler::listenAllAbilities);
    }
}
