import sbt._
import sbt.Keys._
import tut.Plugin._

object build extends Build with NpmCliBase {
  lazy val lintAll = taskKey[Unit]("lint text, html")
  lazy val testAll = taskKey[Unit]("test scala, links")
  lazy val buildWithCheck = taskKey[Unit]("lintAll testAll build")

  lazy val root = (project in file(".")).
    settings(
      tutSettings,
      tutSourceDirectory := srcDir,
      tutTargetDirectory := compiledSrcDir
    ).settings(
      GitBook.settings ++ TextLint.settings ++ LinkTest.settings
    ).settings(
      lintAll := Def.sequential(LinkTest.eslint, TextLint.textlint.toTask("")).value,
      testAll := Def.sequential(compile in Test, LinkTest.linkTest).value,
      buildWithCheck := Def.sequential(lintAll, testAll, GitBook.build)
    )
}
