# yaml-pickle

`yaml-pickle` is a Scala library for (de)serializing YAML based on
[uPickle](https://com-lihaoyi.github.io/upickle/).
It also provides a customized uPickle API that maps `Option[T]` to `null`.

## Release Notes

### 0.2.0

Change `YamlWriter.writeFile()` so that by default it will overwrite existing files,
Pass `overwrite = false` to `YamlWriter.writeFile()` to get the old behavior.

Update dependencies:

* Scala 3.5.2
* snakeyaml-engine 2.8
* upickle 4.0.2
* os-lib 0.11.3

### 0.1.0

Update uPickle to 4.0.0 which introduces breaking changes:

* Serialization format has change
* Now requires Scala 3.4.0 or later

## Publishing to-do

* Documentation - <https://central.sonatype.org/publish-ea/publish-ea-guide/>
* Choose publishing plugin
  * Publishing plugin - <https://github.com/Im-Fran/SonatypeCentralUpload>
  * Publishing plugin - <https://gitlab.com/thebugmc/sonatype-central-portal-publisher>
* Requirements
  * Javadocs and sources
  * GPG signing
  * See <https://central.sonatype.org/publish/requirements/>
