import sbt._

object CustomResolvers {

  val sonatypeSTArch = "scalaTools Archive" at "https://oss.sonatype.org/content/groups/scala-tools"
  val mavenOrgRepo   = "Maven.Org Repository" at "http://repo1.maven.org/maven2/org"
  val typeSafeRels   = Resolver.url("Typesafe Releases", url("http://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

  val rediscalRepo = "Rediscala Repo" at "http://dl.bintray.com/etaty/maven"

  /** CustomResolvers */
  val customResolvers = Seq(
                             Resolver.mavenLocal,
                             Resolver.sonatypeRepo("release"),
                             Resolver.sonatypeRepo("snapshots"),
                             rediscalRepo
                           )
}