import sbt._
import tut.Plugin._

object GitBook extends NpmCliBase {
  val gitbookBin = nodeBin / "gitbook"

  sealed trait Format {def command: String}
  object Format {
    case object Html extends Format { def command = "build" }
    case object Epub extends Format { def command = "epub" }
    case object Pdf extends Format { def command = "pdf" }
  }

  def buildBook(format: Format) = Def.inputTask[Unit] {
    val options = rawStringArg("<gitbook command>").parsed
    printRun(Process(s"$gitbookBin  ${format.command} $bookBuildDir $options"))
  }

  lazy val pluginInstall = taskKey[Unit]("install GitBook plugin")
  lazy val helpGitBook = taskKey[Unit]("help GitBook")
  lazy val build = taskKey[Unit]("build GitBook to html (an alias of html)")
  lazy val html = inputKey[Unit]("build GitBook to html")
  lazy val epub = inputKey[Unit]("build GitBook to epub")
  lazy val buildAll = taskKey[Unit]("build GitBook to all format")

  val settings = Seq(
    pluginInstall := printRun(Process(s"$gitbookBin install")),
    helpGitBook := printRun(Process(s"$gitbookBin help")),
    html := buildBook(Format.Html).dependsOn(tut).evaluated,
    build <<= html.toTask(""),
    epub := buildBook(Format.Epub).dependsOn(tut).evaluated,
    buildAll <<= epub.toTask("") dependsOn html.toTask("")
  )
}
