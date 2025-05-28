package io.github.a01fe.yp

import io.github.a01fe.yp.common_profile.*
import os.RelPath

case class OptionNullTest(
  field: Option[String]
) derives ReadWriter

case class OptionNullDefaultTest(
  field: Option[String] = None
) derives ReadWriter

case class RelPathTest(
  field: os.RelPath
) derives ReadWriter

class OptionNullSpec extends munit.FunSuite:
  test("case class with Option[T] field with Some value serializes as T"):
    val observed = write(OptionNullTest(Some("hello")))
    val expected = """{"field":"hello"}"""
    assertEquals(observed, expected)

  test("case class with Option[T] field with None value serializes as null"):
    val observed = write(OptionNullTest(None))
    val expected = """{"field":null}"""
    assertEquals(observed, expected)

  test("case class with Option[T] field with default None and Some value serializes as T"):
    val observed = write(OptionNullDefaultTest(Some("hello")))
    val expected = """{"field":"hello"}"""
    assertEquals(observed, expected)

  test("case class with Option[T] field with default None and None value serializes with field omitted"):
    val observed = write(OptionNullDefaultTest())
    val expected = """{}"""
    assertEquals(observed, expected)

  test("JSON object with T field deserializes to Some[T]"):
    val expected = OptionNullTest(Some("hello"))
    val observed = read[OptionNullTest]("""{"field":"hello"}""")
    assertEquals(observed, expected)

  test("JSON object with null field deserializes to None"):
    val expected = OptionNullTest(None)
    val observed = read[OptionNullTest]("""{"field":null}""")
    assertEquals(observed, expected)

  test("JSON object with T field with default None deserializes to Some[T]"):
    val expected = OptionNullDefaultTest(Some("hello"))
    val observed = read[OptionNullDefaultTest]("""{"field":"hello"}""")
    assertEquals(observed, expected)

  test("JSON object with null field deserializes to None"):
    val expected = OptionNullDefaultTest()
    val observed = read[OptionNullDefaultTest]("""{"field":null}""")
    assertEquals(observed, expected)

  test("JSON object with missing field with default None deserializes to None"):
    val expected = OptionNullDefaultTest()
    val observed = read[OptionNullDefaultTest]("""{}""")
    assertEquals(observed, expected)

  test("case class with RelPath field serializes as string"):
    val path = "foo/bar"
    val expected = s"""{"field":"$path"}"""
    val observed = write(RelPathTest(RelPath(path)))
    assertEquals(observed, expected)

  test("JSON object with RelPath field deserializes to "):
    val path = "foo/bar"
    val observed = read[RelPathTest](s"""{"field":"$path"}""")
    val expected = RelPathTest(RelPath(path))
    assertEquals(observed, expected)
