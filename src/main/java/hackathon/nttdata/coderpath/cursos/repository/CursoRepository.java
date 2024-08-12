package hackathon.nttdata.coderpath.cursos.repository;



import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import hackathon.nttdata.coderpath.cursos.documents.Cursos;
import reactor.core.publisher.Mono;

@Repository
public interface CursoRepository extends ReactiveMongoRepository<Cursos, String> {
	
	  @Query("{ 'id' : ?0 }")
	    Mono<Cursos> findById(String id);

}
