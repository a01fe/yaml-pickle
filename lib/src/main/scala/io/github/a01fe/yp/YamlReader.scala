package io.github.a01fe.yp

import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings

import io.github.a01fe.yp.option_null
import io.github.a01fe.yp.option_null.reader

object YamlReader:
  def read[T: option_null.Reader](s: String, trace: Boolean = false): T =
    val settings = LoadSettings.builder().build()
    val load = Load(settings)
    val ast = load.loadFromString(s)
    SnakeYamlEngineTransformer.transform(ast, reader[T])
