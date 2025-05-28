# yaml-pickle

`yaml-pickle` is a Scala library for (de)serializing YAML based on
[uPickle](https://com-lihaoyi.github.io/upickle/)
and [SnakeYAML Engine](https://bitbucket.org/snakeyaml/snakeyaml-engine/src/master/).
Additionally, it also provides a uPickle profile that includes common conversions.

## Examples

```scala
import io.github.a01fe.yp.common_profile.*
import io.github.a01fe.yp.YamlWriter
import io.github.a01fe.yp.YamlReader

// Derive ReadWriter to add support for deserialization of enums and case classes
enum Species derives ReadWriter:
  case CAT, DOG, RABBIT

case class Pet(
  name: String,
  species: Species,
  age: Int
) derives ReadWriter

var squid = Pet("Squid", Species.CAT, 5)
// squid: Pet = Pet(Squid,CAT,5)

// Serialize class class to JSON string
var jsonString = write(squid)
// jsonString: String = {"name":"Squid","species":"CAT","age":5}

// Serialize case class to YAML string
var yamlString = YamlWriter.writeString(writeJs(squid))
// yamlString: String =
//   name: Squid
//   species: CAT
//   age: 5

// Deserialize JSON string to case class
val newJsonSquid = read[Pet](jsonString)
// newJsonSquid: Pet = Pet(Squid,CAT,5)
assert(newJsonSquid == squid)

// Deserialize YAML string to case class
val newYamlSquid = YamlReader.read[Pet](yamlString)
// newYamlSquid: Pet = Pet(Squid,CAT,5)
assert(newYamlSquid == squid)
```

## Release Notes

### 1.0.0

Rename `options_null` to `common_profile` to better describe its purpose.

Update dependencies:

* Scala 3.7.0
* upickle 4.2.1
* os-lib 0.11.4
* munit 1.1.1

### 0.3.0

Update dependencies:

* Scala 3.6.3
* snakeyaml-engine 2.9
* upickle 4.1.0
* os-lib 0.11.3
* munit 1.1.0

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
