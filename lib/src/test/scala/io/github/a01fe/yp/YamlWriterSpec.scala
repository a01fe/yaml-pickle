package io.github.a01fe.yp

import ujson.*
import io.github.a01fe.yp.*

class YamlWriterSpec extends munit.FunSuite:

  test("YamlWriter.writeString serializes value to string"):
    val r1 = YamlWriter.writeString(Obj("foo" -> "bar"))
    assertEquals(r1, "foo: bar")

  test("YamlWriter.writeFile serializes value to new file"):
    val f = os.temp.dir() / "test.yaml"
    YamlWriter.writeFile(Obj("foo" -> "bar"), f)
    assertEquals(os.read(f), "foo: bar")

  test("YamlWriter.writeFile fails to overwrite existing file"):
    val f = os.temp.dir() / "test.yaml"
    os.write(f, "existing")
    YamlWriter.writeFile(Obj("foo" -> "bar"), f)
    assertEquals(os.read(f), "foo: bar")

  test("YamlWriter.writeFile(overwrite = false) fails to overwrite existing file"):
    val f = os.temp.dir() / "test.yaml"
    os.write(f, "existing")
    intercept[java.nio.file.FileAlreadyExistsException]:
      YamlWriter.writeFile(Obj("foo" -> "bar"), f, overwrite = false)
