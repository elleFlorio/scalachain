package com.elleflorio.scalachain.exception

final class InvalidProofException(val hash: String,
                                  val proof: Long,
                                  val message: String = "",
                                  val cause: Throwable = None.orNull)
  extends Exception(message, cause)
