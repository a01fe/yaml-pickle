package io.github.a01fe.yp

import scala.annotation.nowarn
import scala.jdk.CollectionConverters.*

object SnakeYamlEngineTransformer extends ujson.AstTransformer[Object]:

  // Suppress warning about Map and List c
  @nowarn
  def transform[T](j: Object, f: upickle.core.Visitor[?, T]): T =
    j match
      case o: java.util.Map[String, Object] =>
        transformObject(f, o.entrySet().asScala.map(e => (e.getKey(), e.getValue())))
      case l: java.util.List[Object] => transformArray(f, l.asScala)
      case b: java.lang.Boolean      => if b then f.visitTrue(-1) else f.visitFalse(-1)
      case i: java.lang.Integer      => f.visitFloat64StringParts(i.toString, -1, -1, -1)
      case d: java.lang.Double       => f.visitFloat64(d, -1)
      case s: java.lang.String       => f.visitString(s, -1)
      case null                      => f.visitNull(-1)

  def visitJsonableObject(length: Int, index: Int): upickle.core.ObjVisitor[Object, Object] = ???

  // Members declared in upickle.core.Visitor
  def visitArray(length: Int, index: Int): upickle.core.ArrVisitor[Object, Object] = ???

  def visitFalse(index: Int): Object = java.lang.Boolean.FALSE

  def visitFloat64StringParts(s: CharSequence, decIndex: Int, expIndex: Int, index: Int): Object =
    if decIndex == -1 && expIndex == -1 then java.lang.Integer.parseInt(s.toString()).asInstanceOf[Object]
    else java.lang.Double.parseDouble(s.toString()).asInstanceOf[Object]

  def visitNull(index: Int): Object = null

  def visitString(s: CharSequence, index: Int): Object = s.toString()

  def visitTrue(index: Int): Object = java.lang.Boolean.TRUE
