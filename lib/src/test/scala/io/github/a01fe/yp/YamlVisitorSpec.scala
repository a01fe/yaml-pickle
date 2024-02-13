package io.github.a01fe.yp

import io.github.a01fe.yp.*

class YamlVisitorSpec extends munit.FunSuite:

  test("A YamlVisitor should render JSON scalars"):
    val v = YamlVisitor()

    val r1 = v.visitFalse(0)
    assert(r1.isInstanceOf[ScalarValue])
    assertEquals(r1.get, "false")

    val r2 = v.visitTrue(0)
    assert(r2.isInstanceOf[ScalarValue])
    assertEquals(r2.get, "true")

    val r3 = v.visitNull(0)
    assert(r3.isInstanceOf[ScalarValue])
    assertEquals(r3.get, "null")

    val r4 = v.visitString("hello", 0)
    assert(r4.isInstanceOf[ScalarValue])
    assertEquals(r4.get, "hello")

    val r5 = v.visitFloat64StringParts("1234", 0, 0, 0)
    assert(r5.isInstanceOf[ScalarValue])
    assertEquals(r5.get, "1234")

  test("A YamlVisitor should delegate JSON arrays and objects"):
    val v = YamlVisitor()

    val r1 = v.visitJsonableObject(0, 0)
    assert(r1.isInstanceOf[YamlObjVisitor])

    val r2 = v.visitArray(0, 0)
    assert(r2.isInstanceOf[YamlArrVisitor])

  test("A YamlVisitor should quote strings with special YAML meaning"):
    val v = YamlVisitor()
    val r1 = v.visitString("simple string", 0)
    assertEquals(r1, ScalarValue("simple string"))

    for s <- Seq(
        " quote strings with leading whitespace",
        "quote strings with trailing whitespace ",
        "quote strings with : in them",
        "quote strings with # in them"
      )
    do assertEquals(v.visitString(s, 0).get, s"\"$s\"")

  test("A YamlVisitor should quote strings that look like integers"):
    val v = YamlVisitor()
    for s <- Seq(
        "0",
        "-1",
        "123",
        "-123"
      )
    do assertEquals(v.visitString(s, 0).get, s"\"$s\"")

  test("A YamlVisitor should quote strings that look like floating point numbers"):
    val v = YamlVisitor()
    for s <- Seq(
        "1.234",
        "-1e10",
        "1e-10",
        "1.23e+10"
      )
    do assertEquals(v.visitString(s, 0).get, s"\"$s\"")

    for c <- "?:,[]{}#&*!'%@|>-" do
      // Note that " should be in the list, but is left off because it will be
      // escaped, too. This is (sorta) tested in the next test.
      val s = s"$c at start must quote"
      assertEquals(v.visitString(s, 0).get, s"\"$s\"")

  test("A YamlVisitor should escape special characters in quoted strings"):
    val v = YamlVisitor()
    val r1 = v.visitString(" quote \" should be escaped", 0)
    assertEquals(r1, ScalarValue("\" quote \\\" should be escaped\""))
    val r2 = v.visitString(" backslash \\ should be escaped", 0)
    assertEquals(r2, ScalarValue("\" backslash \\\\ should be escaped\""))

  test("A YamlArrVisitor should render an empty JSON array inline"):
    val a1 = YamlArrVisitor()
    val r1 = a1.visitEnd(0)
    assert(r1.isInstanceOf[ScalarValue])
    assertEquals(r1.get, "[]")

  test("A YamlArrVisitor should render a non-empty JSON array as a block"):
    val a1 = YamlArrVisitor()
    a1.visitValue(a1.subVisitor.visitString("foo", 0), 0)
    val r1 = a1.visitEnd(0)
    assert(r1.isInstanceOf[CollectionValue])
    assertEquals(r1.get, "- foo".stripMargin)

    val a2 = YamlArrVisitor()
    a2.visitValue(a2.subVisitor.visitString("foo", 0), 0)
    a2.visitValue(a2.subVisitor.visitString("bar", 0), 0)
    a2.visitValue(a2.subVisitor.visitString("baz", 0), 0)
    val r2 = a2.visitEnd(0)
    assert(r2.isInstanceOf[CollectionValue])
    assertEquals(
      r2.get,
      """- foo
        |- bar
        |- baz""".stripMargin
    )

  test("A YamlArrVisitor should render a nested array with proper indentation"):
    val a1 = YamlArrVisitor()
    a1.visitValue(a1.subVisitor.visitString("foo", 0), 0)
    val a2 = a1.subVisitor.visitArray(0, 0)
    a2.visitValue(a2.subVisitor.visitString("bar", 0), 0)
    a1.visitValue(a2.visitEnd(0), 0)
    a1.visitValue(a1.subVisitor.visitString("baz", 0), 0)
    val r1 = a1.visitEnd(0)
    assert(r1.isInstanceOf[CollectionValue])
    assertEquals(
      r1.get,
      """- foo
        |- - bar
        |- baz""".stripMargin
    )

  test("A YamlObjVisitor should render an empty object inline"):
    val o1 = YamlObjVisitor()
    val r1 = o1.visitEnd(0)
    assert(r1.isInstanceOf[ScalarValue])
    assertEquals(r1.get, "{}")

  test("A YamlObjVisitor should render a non-empty JSON object as a block"):
    val o1 = YamlObjVisitor()
    o1.visitKeyValue(o1.visitKey(0).visitString("foo", 0))
    o1.visitValue(o1.subVisitor.visitString("bar", 0), 0)
    val r1 = o1.visitEnd(0)
    assert(r1.isInstanceOf[CollectionValue])
    assertEquals(r1.get, "foo: bar")
