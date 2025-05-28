package io.github.a01fe.yp

import scala.annotation.unused

import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings

import io.github.a01fe.yp.common_profile
import io.github.a01fe.yp.common_profile.*

object YamlReader:
  def read[T: common_profile.Reader](s: String, @unused trace: Boolean = false): T =
    val settings = LoadSettings.builder().build()
    val load = Load(settings)
    val ast = load.loadFromString(s)
    SnakeYamlEngineTransformer.transform(ast, reader[T])
