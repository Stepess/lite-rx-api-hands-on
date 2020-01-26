package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Learn how to control the demand.
 *
 * @author Sebastien Deleuze
 */
public class Part06Request {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	// Create a StepVerifier that initially requests all values and expect 4 values to be received
	StepVerifier requestAllExpectFour(Flux<User> flux) {
		return StepVerifier.create(flux)
				.expectNextCount(4L)
				.expectComplete();
	}

//========================================================================================

	// Create a StepVerifier that initially requests 1 value and expects User.SKYLER then requests another value and expects User.JESSE.
	StepVerifier requestOneExpectSkylerThenRequestOneExpectJesse(Flux<User> flux) {
		return StepVerifier.create(flux)
				.thenRequest(1L)
				.expectNextMatches(User.SKYLER::equals)
				.thenRequest(1L)
				.expectNextMatches(User.JESSE::equals)
				.thenCancel();
	}

//========================================================================================

	// Return a Flux with all users stored in the repository that prints automatically logs for all Reactive Streams signals
	Flux<User> fluxWithLog() {
		return repository.findAll()
				.log();
	}

//========================================================================================

	// Return a Flux with all users stored in the repository that prints "Starring:" on subscribe, "firstname lastname" for all values and "The end!" on complete
	Flux<User> fluxWithDoOnPrintln() {
		return repository.findAll()
				.doOnSubscribe(sub -> log.info("Starring:"))
				.doOnNext(user -> log.info(user.getFirstname() + " " + user.getLastname()))
				.doOnComplete(() -> log.info("The end!"));
	}

}
