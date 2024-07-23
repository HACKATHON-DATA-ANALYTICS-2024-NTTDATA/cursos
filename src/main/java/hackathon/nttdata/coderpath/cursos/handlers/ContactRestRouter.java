package hackathon.nttdata.coderpath.cursos.handlers;




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ContactRestRouter {

	 @Bean
	  public RouterFunction<ServerResponse> routeContact(CursoRestHandler contactRestHandler) {
	    return RouterFunctions
	      .route(GET("/functional/cursos")
	        , contactRestHandler::getAllContacts)
	      .andRoute(GET("/functional/id/{id}")
	        , contactRestHandler::getById)
				/*
				 * .andRoute(GET("/functional/contacts/byEmail/{email}") ,
				 * contactRestHandler::getByEmail)
				 */
	      .andRoute(POST("/functional/cursos")
	        , contactRestHandler::insertContact)
	      .andRoute(PUT("functional/id/{id}")
	        , contactRestHandler::updateCurso)
	      .andRoute(DELETE("/functional/id/{id}")
	        , contactRestHandler::deleteContact);
	  }
	
	
}
