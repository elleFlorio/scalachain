package com.elleflorio.scalachain.crypto

import java.math.BigInteger
import java.security.MessageDigest

object Crypto {

  def sha256Hash(value: String) = String.format("%064x",
    new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8"))))

}
