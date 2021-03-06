package com.coreos.jetcd.exception;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.Status.fromThrowable;

import io.grpc.Status;

/**
 * A factory for creating instances of {@link EtcdException} and its subtypes.
 */
public final class EtcdExceptionFactory {

  public static EtcdException newEtcdException(ErrorCode code, String message) {
    return new EtcdException(code, message, null);
  }

  public static EtcdException newEtcdException(ErrorCode code, String message, Throwable cause) {
    return new EtcdException(code, message, cause);
  }

  public static CompactedException newCompactedException(
      long compactedRev) {
    return new CompactedException(
        ErrorCode.OUT_OF_RANGE,
        "etcdserver: mvcc: required revision has been compacted",
        compactedRev
    );
  }

  public static ClosedWatcherException newClosedWatcherException() {
    return new ClosedWatcherException();
  }

  public static ClosedClientException newClosedWatchClientException() {
    return new ClosedClientException("Watch Client has been closed");
  }

  public static ClosedClientException newClosedLeaseClientException() {
    return new ClosedClientException("Lease Client has been closed");
  }

  public static ClosedKeepAliveListenerException newClosedKeepAliveListenerException() {
    return new ClosedKeepAliveListenerException();
  }

  public static ClosedSnapshotException newClosedSnapshotException() {
    return new ClosedSnapshotException();
  }

  public static EtcdException handleInterrupt(InterruptedException e) {
    Thread.currentThread().interrupt();
    return newEtcdException(ErrorCode.CANCELLED, "Interrupted", e);
  }

  public static EtcdException toEtcdException(Throwable cause) {
    checkNotNull(cause, "cause can't be null");
    return toEtcdException(fromThrowable(cause));
  }

  public static EtcdException toEtcdException(Status status) {
    checkNotNull(status, "status can't be null");
    return fromStatus(status);
  }

  private static EtcdException fromStatus(Status status) {
    return newEtcdException(
        ErrorCode.fromGrpcStatus(status),
        status.getDescription(),
        status.getCause()
    );
  }
}
