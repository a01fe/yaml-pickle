package io.github.a01fe.yp

import os.RelPath
import scala.util.matching.Regex

object common_profile extends upickle.AttributeTagged:

  implicit val regexReadWrite: ReadWriter[Regex] =
    this.readwriter[String].bimap[Regex](_.toString(), _.r)

  implicit val relPathReadWrite: this.ReadWriter[RelPath] =
    this.readwriter[String].bimap[RelPath](_.toString(), RelPath(_))
