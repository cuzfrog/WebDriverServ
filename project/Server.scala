import sbt._
import sbt.Keys._


object Server {
  private val compileExDep = SettingKey[sbt.File]("compile-ex-dep")
  private val testExDep = SettingKey[sbt.File]("test-ex-dep")

  val settings = Seq(
    compileExDep := (resourceDirectory in Compile).value,
    testExDep := (resourceDirectory in Test).value,
    libraryDependencies ++= {
      val extDeps = Seq(
        "compile" -> compileExDep.value.listFiles().toList,
        "test" -> testExDep.value.listFiles().toList
      ).flatMap { defSeq =>
        defSeq._2.find(_.name == "server-extra-dependencies").toList.flatMap { f =>
          IO.readLines(f).filter(_.nonEmpty).map(dep => parseDependency(dep, defSeq._1))
        }
      }
      extDeps.foreach(ed => println(s"[info] Extra dependency added:$ed"))
      extDeps
    },
    resourceGenerators in Compile += Def.task {
      val file = resourceManaged.value / "build-info.properties"
      val contents = "name=%s\nversion=%s".format(name.value, version.value)
      IO.write(file, contents)
      Seq(file)
    }.taskValue
  )

  private lazy val DepExtractor ="""^"(.+)"\s+(%+)\s+"(.+)"\s+%\s+"(.+)"(\s+%\s+".+")?$""".r
  private def parseDependency(in: String, _scope: String = "compile") = try {
    in.trim match {
      case DepExtractor(org, div, nme, ver, _*) => div match {
        case "%%" => org %% nme % ver % _scope
        case "%" => org % nme % ver % _scope
      }
    }
  } catch {
    case e: MatchError => throw new IllegalArgumentException(s"Extra dependency cannot be parsed:$in")
  }
}