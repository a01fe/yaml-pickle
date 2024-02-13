package io.github.a01fe.yp

import os.RelPath

object option_null extends upickle.AttributeTagged:
  override implicit def OptionWriter[T: Writer]: Writer[Option[T]] =
    implicitly[Writer[T]].comap[Option[T]] {
      case None    => null.asInstanceOf[T]
      case Some(x) => x
    }

  override implicit def OptionReader[T: Reader]: Reader[Option[T]] =
    new Reader.Delegate[Any, Option[T]](implicitly[Reader[T]].map(Some(_))) {
      override def visitNull(index: Int) = None
    }

  implicit val relPathReadWrite: this.ReadWriter[RelPath] =
    this.readwriter[String].bimap[RelPath](_.toString(), RelPath(_))
