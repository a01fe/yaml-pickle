package io.github.a01fe.yp

import java.nio.file.StandardOpenOption

import scala.collection.mutable.StringBuilder

import ujson.*
import ujson.JsVisitor
import upickle.core.*

/** Wrap string representation of a Yaml value to indicate whether the value is a scalar that should be rendered inline
  * or a block-style collection that should be rendered in separate lines.
  */

sealed abstract class YamlValue(value: String):
  def get = value

case class ScalarValue(value: String) extends YamlValue(value)

case class CollectionValue(value: String) extends YamlValue(value)

class YamlObjVisitor extends ObjVisitor[YamlValue, YamlValue]:

  val r = StringBuilder()

  def visitKey(index: Int): YamlVisitor =
    YamlVisitor()

  def subVisitor: YamlVisitor =
    YamlVisitor()

  def visitEnd(index: Int): YamlValue =
    r match
      case _ if r.isEmpty => ScalarValue("{}")
      case _              => CollectionValue(r.toString)

  def visitValue(v: YamlValue, index: Int): Unit =
    v match
      case ScalarValue(value) => r ++= " " ++= value
      case CollectionValue(value) =>
        r ++= "\n"
        val lines = value.linesWithSeparators
        lines.foreach(r ++= "  " ++= _)

  def visitKeyValue(v: Any): Unit =
    if !r.isEmpty then r ++= "\n"
    v match
      case ScalarValue(value) => r ++= value ++= ":"
      case _                  => throw new java.lang.RuntimeException("Yow!")

class YamlArrVisitor extends ArrVisitor[YamlValue, YamlValue]:

  val r = StringBuilder()

  def subVisitor: YamlVisitor =
    YamlVisitor()

  def visitEnd(index: Int): YamlValue =
    r match
      case _ if r.isEmpty => ScalarValue("[]")
      case _              => CollectionValue(r.toString())

  def visitValue(v: YamlValue, index: Int): Unit =
    if !r.isEmpty then r ++= "\n"
    val lines = v.get.linesWithSeparators
    assert(lines.hasNext)
    r ++= "- " ++= lines.next()
    lines.foreach(r ++= "  " ++= _)

class YamlVisitor extends JsVisitor[YamlValue, YamlValue]:

  def visitJsonableObject(length: Int, index: Int): ObjVisitor[YamlValue, YamlValue] =
    YamlObjVisitor()

  def visitArray(length: Int, index: Int): YamlArrVisitor =
    YamlArrVisitor()

  def visitFalse(index: Int): YamlValue =
    ScalarValue("false")

  def visitFloat64StringParts(s: CharSequence, decIndex: Int, expIndex: Int, index: Int): YamlValue =
    ScalarValue(s.toString())

  def visitNull(index: Int): YamlValue =
    ScalarValue("null")

  private val cannotBePlain = """(?x)                           # COMMENTS mode
                                |(                              # Alternatives, Cannot be empty
                                | | \s.*                        # Cannot start with whitespace
                                | | .*\s                        # Cannot end with whitespace
                                | | [?:,\[\]{}\#&*!'"%@|>-].*   # Cannot start with indicators
                                | | .*[:\#].*                   # Cannot have : or # anywhere
                                | | .*[\r\n].*                  # Quote multiple lines for clarity
                                | | 0 | -? [1-9] [0-9]*         # Cannot look like an integer
                                | | -? [1-9] (?: \. [0-9]* [1-9] )? (?: e [-+] [1-9] [0-9]* )? # Cannot look like a float
                                |)
                              """.stripMargin.r

  def visitString(s: CharSequence, index: Int): YamlValue =
    s.toString() match
      case cannotBePlain(s) =>
        val b = StringBuilder()
        b ++= "\""
        s.foreach { c =>
          c match
            case '"'  => b ++= "\\\"" // " -> \"
            case '\\' => b ++= "\\\\" // \ -> \\
            case x    => b += x
        }
        b ++= "\""
        ScalarValue(b.toString())
      case s => ScalarValue(s)

  def visitTrue(index: Int): YamlValue =
    ScalarValue("true")

object YamlWriter:
  def writeString(r: Value): String =
    transform(r, YamlVisitor()).get

  def writeFile(r: Value, f: os.Path, overwrite: Boolean = true, truncate: Boolean = true): Unit =
    var flags = List(StandardOpenOption.WRITE)
    if overwrite then
      flags = StandardOpenOption.CREATE :: flags
      if truncate then flags = StandardOpenOption.TRUNCATE_EXISTING :: flags
    else flags = StandardOpenOption.CREATE_NEW :: flags
    os.write.write(f, writeString(r), perms = null, flags = flags, offset = 0)
