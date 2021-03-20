package io.memoria.jutils.jes.socialnetwork;

import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.memoria.jutils.jes.r2.R2CommandHandler;
import io.memoria.jutils.jes.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.jes.socialnetwork.domain.UserCommand;
import io.memoria.jutils.jes.socialnetwork.domain.UserDecider;
import io.memoria.jutils.jes.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.jes.socialnetwork.transformer.SNTransformer;
import io.r2dbc.spi.ConnectionFactories;

public class SNTestUtils {
  public static CommandHandler<UserCommand> r2CH(IdGenerator idGenerator) {
    var connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///testR2");
    return new R2CommandHandler<>(connectionFactory,
                                  new SNTransformer(),
                                  new Visitor(),
                                  new UserEvolver(),
                                  new UserDecider(idGenerator));
  }

  private SNTestUtils() {}
}
