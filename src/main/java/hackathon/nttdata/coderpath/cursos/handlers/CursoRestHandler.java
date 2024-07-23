package hackathon.nttdata.coderpath.cursos.handlers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import hackathon.nttdata.coderpath.cursos.documents.Cursos;
import hackathon.nttdata.coderpath.cursos.repository.CursoRepository;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.*;


@Component
public class CursoRestHandler {
	
	 private final CursoRepository cursoRepository;
	

	  private Mono<ServerResponse> response404
	    = ServerResponse.notFound().build();

	  private Mono<ServerResponse> response406
	    = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();

	  @Autowired //optional
	  public CursoRestHandler(CursoRepository cursoRepository) {
	    this.cursoRepository = cursoRepository;
	  }

	  //GET - find a contact by id
	  public Mono<ServerResponse> getById(ServerRequest request) {
	    String id = request.pathVariable("id");

	    return cursoRepository.findById(id)
	      .flatMap(contact ->
	        ServerResponse.ok()
	          .contentType(MediaType.APPLICATION_JSON)
	          .body(fromValue(contact))
	      ).switchIfEmpty(response404);
	  }

	  //List all contacts
	  public Mono<ServerResponse> getAllContacts(ServerRequest request) {
	    return ServerResponse.ok()
	      .contentType(MediaType.APPLICATION_JSON)
	      .body(cursoRepository.findAll(), Cursos.class);
	  }

	  //Find a Contact by email address.
		/*
		 * public Mono<ServerResponse> getByEmail(ServerRequest request) { String email
		 * = request.pathVariable("email");
		 * 
		 * return cursoRepository.findFirstByEmail(email) .flatMap(contact ->
		 * ServerResponse.ok() .contentType(MediaType.APPLICATION_JSON)
		 * .body(fromValue(contact)) ).switchIfEmpty(response404); }
		 */

	  //Save a new Contact
	  public Mono<ServerResponse> insertContact(ServerRequest request) {
	    Mono<Cursos> unsavedContact = request.bodyToMono(Cursos.class);

	    return unsavedContact
	      .flatMap(contact -> cursoRepository.save(contact)
	        .flatMap(savedContact -> ServerResponse.accepted()
	          .contentType(MediaType.APPLICATION_JSON)
	          .body(fromValue(savedContact)))
	      ).switchIfEmpty(response406);
	  }

	  //Update an existing contact
	  public Mono<ServerResponse> updateCurso(ServerRequest request) {
	    Mono<Cursos> contact$ = request.bodyToMono(Cursos.class);
	    String id = request.pathVariable("id");

	    //TODO - additional id match
	    Mono<Cursos> updatedContact$ = contact$.flatMap(curso ->
	    cursoRepository.findById(id)
	        .flatMap(oldcurso -> {
	        	oldcurso
	            .setNombre(curso.getNombre());
	      
	          return cursoRepository.save(oldcurso);
	        })
	    );

	    return updatedContact$.flatMap(contact ->
	      ServerResponse.accepted()
	        .contentType(MediaType.APPLICATION_JSON)
	        .body(fromValue(contact))
	    ).switchIfEmpty(response404);
	  }

	  //Delete a Contact
	  public Mono<ServerResponse> deleteContact(ServerRequest request) {
	    String id = request.pathVariable("id");
	    Mono<Void> deleted = cursoRepository.deleteById(id);

	    return ServerResponse.ok()
	      .contentType(MediaType.APPLICATION_JSON)
	      .body(deleted, Void.class);
	  }
	}
