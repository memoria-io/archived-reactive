package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.reactive.core.eventsourcing.pipeline.StateDecider;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.Acc;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CloseAccount;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateAcc;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateInboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.MarkMsgAsSeen;
import io.vavr.control.Try;

record AccountStateDecider() implements StateDecider<Account, AccountCommand, AccountEvent> {
  @Override
  public Try<AccountEvent> apply(Account account, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case CreateAcc cmd && account instanceof Visitor -> accountCreated(cmd);
      case CreateOutboundMsg cmd && account instanceof Acc -> outboundCreated(cmd);
      case CreateInboundMsg cmd && account instanceof Acc -> inboundMessageCreated(cmd);
      case MarkMsgAsSeen cmd && account instanceof Acc -> outboundSeen(cmd);
      case CloseAccount cmd && account instanceof Acc -> accountClosed(cmd);
      default -> Try.failure(InvalidOperation.create(account, accountCommand));
    };
  }

  private Try<AccountEvent> accountCreated(CreateAcc cmd) {
    return Try.success(AccountEvent.accountCreated(cmd.commandId(), cmd.accountId(), cmd.accountname()));
  }

  private Try<AccountEvent> outboundCreated(CreateOutboundMsg cmd) {
    return Try.success(AccountEvent.outboundMsgCreated(cmd.commandId(),
                                                       cmd.msgSender(),
                                                       cmd.msgReceiver(),
                                                       cmd.message()));
  }

  private Try<AccountEvent> inboundMessageCreated(CreateInboundMsg cmd) {
    return Try.success(AccountEvent.inboundMsgCreated(cmd.commandId(),
                                                      cmd.msgSender(),
                                                      cmd.msgReceiver(),
                                                      cmd.message()));
  }

  private Try<AccountEvent> outboundSeen(MarkMsgAsSeen cmd) {
    return Try.success(AccountEvent.outboundSeen(cmd.commandId(), cmd.msgSender(), cmd.msgReceiver()));
  }

  private Try<AccountEvent> accountClosed(CloseAccount cmd) {
    return Try.success(AccountEvent.accountClosed(cmd.commandId(), cmd.accountId()));
  }
}
