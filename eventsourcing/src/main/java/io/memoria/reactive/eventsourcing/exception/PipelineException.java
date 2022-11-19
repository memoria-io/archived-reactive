package io.memoria.reactive.eventsourcing.exception;

import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.eventsourcing.exception.ESException;

/**
 * Eventsourcing Exception
 */
public interface PipelineException extends ESException {

  class MismatchingStateId extends Exception implements PipelineException {
    private MismatchingStateId(StateId cmdStateId, StateId stateId) {
      super("The Command's stateId:%s doesn't match stream stateId:%s".formatted(cmdStateId.value(), stateId.value()));
    }

    public static MismatchingStateId create(StateId cmdStateId, StateId stateId) {
      return new MismatchingStateId(cmdStateId, stateId);
    }
  }
}
