package com.jackhenry.codingchallenge

import io.circe.generic.JsonCodec

sealed trait AppError extends Exception {
  def code: Int
  def msg: String

  override def getMessage: String = s"$msg with stack trace: ${super.getStackTrace().mkString("\n")}"

}

object AppError {

  @JsonCodec
  case class InternalServerError(code: Int = 500, msg: String) extends AppError

}
