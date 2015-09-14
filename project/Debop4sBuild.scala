import sbt.Keys._
import sbt._

trait BuildSettings {
  self: Build with Dependencies =>

  /** Common Setting */
  lazy val scalaBuildOptions =
    Seq("-unchecked", "-deprecation", "-feature", "-Xlint", "-Dfile.encinding=UTF-8",
      "-language:implicitConversions", "-language:postfixOps", "-language:dynamics", "-language:higherKinds",
      "-language:reflectiveCalls",
      "-Ydead-code", "-Yclosure-elim", "-Yinline", "-Yinline-warnings") // "-explaintypes", "-Yconst-opt",

  def libraryOverrides = Set(slf4j, commonsLang3, commonsCodec, javassist, guava, httpcore, httpclient, mongoJavaDriver,
    "xml-apis" % "xml-apis" % "1.3.04",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3") ++
    springFrameworkAll

  val scala210 = "2.10.5"
  val scala211 = "2.11.7"

  def commonSettings = Seq(
    organization := "debop4s",
    version := "0.4.0-SNAPSHOT",
    scalaVersion in ThisBuild := scala211,
    ivyScala := ivyScala.value map {
      _.copy(overrideScalaVersion = true)
    },
    libraryDependencies ++= commonDependencies,
    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-library" % _),
    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _),
    dependencyOverrides ++= libraryOverrides,
    addCompilerPlugin(miniboxing),
    javacOptions ++= Seq("-source", jdkVersion, "-target", jdkVersion),
    scalacOptions ++= scalaBuildOptions,
    scalacOptions in(Compile, doc) ++= Seq("-groups", "-implicits"),
    resolvers ++= CustomResolvers.customResolvers,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    compileOrder := CompileOrder.Mixed,
    parallelExecution in Test := false,
    crossScalaVersions := Seq(scala210, scala211),
    //scala version 별로 코드가 달라질 경우 /scala, /scala_2.11 등으로 구성하게 한다.
    unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile, scalaBinaryVersion) {
                              (s, v) => s / ("scala_" + v)
                            },
    // publishM2 : local maven repository 에 publish
    // publishLocal : local ivy2 repository에 publish
    // publishTo := Some(Resolver.mavenLocal) // > + publish 를 수행하면 ~/.m2/reposioty 에 publish 됩니다.
    publishTo <<= version { v: String => getMavenRepository(v) },

    scalacOptions in Test ++= Seq("-Yrangepos"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra :=
      <url>https://github.com/debop/debop4s</url>
      <scm>
        <url>git@github.com:debop/debop4s.git</url>
        <connection>scm:git:git@github.com:debop/debop4s.git</connection>
      </scm>
        <developers>
          <developer>
            <id>debop</id>
            <name>Sunghyouk Bae</name>
            <url>http://debop.tumblr.com</url>
          </developer>
        </developers>
  )

  def getMavenRepository(v: String): Some[MavenRepository] = {
    val nexus = "http://nexus.kesti.co.kr/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("Kesti Snapshots" at nexus + "content/repositories/snapshots/")
    else
      Some("Kesti Releases" at nexus + "content/repositories/releases/")
  }
}

object Debop4sBuild extends Build with BuildSettings with Dependencies {

  lazy val debop4s = Project("debop4s", file("."))
                     .settings(commonSettings)
                     .aggregate(
                       debop4s_config,
                       debop4s_core,
                       debop4s_timeperiod,
                       debop4s_mongo,
                       hibernate_rediscala,
                       debop4s_redis,
                       debop4s_rediscala,
                       debop4s_data_common,
                       debop4s_data_slick2,
                       debop4s_data_slick2_northwind,
                       debop4s_data_slick3,
                       debop4s_data_slick3_northwind,
                       //debop4s_data_orm,
                       debop4s_shiro,
                       debop4s_web_spring,
                       debop4s_web_scalatra,
                       debop4s_reactive,
                       debop4s_benchmark
                     )


  val debop4s_config = Project("debop4s-config", file("debop4s-config"))
                       .settings(commonSettings ++ Seq(libraryDependencies += typesafeConfig))

  val debop4s_core = Project("debop4s-core", file("debop4s-core"))
                     .settings(commonSettings ++ Seq(libraryDependencies ++= coreDependencies))

  val debop4s_timeperiod = Project(id = "debop4s-timeperiod", base = file("debop4s-timeperiod"))
                           .settings(commonSettings ++ Seq(libraryDependencies ++= Seq(jodaTime, jodaConvert, junitBenchmarks)))
                           .dependsOn(debop4s_core)

  val debop4s_mongo = Project("debop4s-mongo", file("debop4s-mongo"))
                      .settings(commonSettings ++ Seq(libraryDependencies ++= Seq(springDataMongodb, mongoJavaDriver, querydslMongodb)))
                      .dependsOn(debop4s_core, debop4s_timeperiod)

  val hibernate_rediscala = Project("hibernate-rediscala", file("hibernate-rediscala"))
                            .settings(commonSettings ++
                                      Seq(libraryDependencies ++=
                                          hibernateAll ++
                                          Seq(akkaActor, scalaAsync) ++
                                          Seq(rediscala, typesafeConfig, hikaricp) ++
                                          Seq(fst, snappy, lz4) ++
                                          databaseDriverAllTest ++
                                          Seq(springContext % "test", springOrm % "test", springDataJpa % "test")))

  val debop4s_redis = Project("debop4s-redis", file("debop4s-redis"))
                      .settings(commonSettings ++
                                Seq(libraryDependencies ++=
                                    Seq(jedis, springContext) ++
                                    Seq(springOrm % "test", springDataJpa % "test")))
                      .dependsOn(debop4s_core, hibernate_rediscala)

  val debop4s_rediscala = Project("debop4s-rediscala", file("debop4s-rediscala"))
                          .settings(commonSettings ++
                                    Seq(libraryDependencies ++= Seq(rediscala, typesafeConfig, springContext)))
                          .dependsOn(debop4s_config, debop4s_core)

  val debop4s_data_common = Project("debop4s-data-common", file("debop4s-data-common"))
                            .settings(commonSettings ++ Seq(libraryDependencies ++=
                                                            Seq(hikaricp, bonecp, tomcatJdbc) ++
                                                            databaseDriverAllTest))
                            .dependsOn(debop4s_config)

  val debop4s_data_slick2 = Project("debop4s-data-slick", file("debop4s-data-slick"))
                            .settings(commonSettings ++
                                      Seq(libraryDependencies ++= slick2All ++ databaseDriverAllTest,
                                        libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _), // for slick
                                        scalacOptions ++= Seq("-nowarn")))
                            .dependsOn(debop4s_config, debop4s_core, debop4s_timeperiod, debop4s_data_common)

  val debop4s_data_slick2_northwind =
    Project("debop4s-data-slick-northwind", file("debop4s-data-slick-northwind"))
    .settings(commonSettings ++ Seq(libraryDependencies ++= slick2All ++ databaseDriverAllTest))
    .dependsOn(debop4s_data_slick2)

  val debop4s_data_slick3 = Project("debop4s-data-slick3", file("debop4s-data-slick3"))
                            .settings(commonSettings ++
                                      Seq(libraryDependencies ++= slick3All ++ databaseDriverAllTest ++ shapeless.value,
                                        // libraryDependencies <++= scalaBinaryVersion(sv => shapeless(sv)),
                                        libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _), // for slick
                                        scalacOptions ++= Seq("-nowarn")))
                            .dependsOn(debop4s_config, debop4s_core, debop4s_timeperiod, debop4s_data_common)

  val debop4s_data_slick3_northwind =
    Project("debop4s-data-slick3-northwind", file("debop4s-data-slick3-northwind"))
    .settings(commonSettings ++
              Seq(libraryDependencies ++= slick3All ++ databaseDriverAllTest ++ shapeless.value,
                // libraryDependencies <++= scalaBinaryVersion(sv => shapeless(sv)),
                scalacOptions ++= Seq("-nowarn")))
    .dependsOn(debop4s_data_slick3)

  // debop4s-data-orm 은 QueryDSL 을 사용해야 해서 sbt가 아닌 maven 으로 빌드해야 합니다.
  val debop4s_data_orm = Project("debop4s-data-orm", file("debop4s-data-orm"))
                         .settings(commonSettings ++
                                   Seq(libraryDependencies ++=
                                       Seq(springOrm, springDataJpa, hikaricp, bonecp, tomcatJdbc) ++
                                       hibernateAll ++
                                       Seq(querydslJpa, querydslJpaCodegen) ++
                                       databaseDriverAllTest))
                         .dependsOn(debop4s_config, debop4s_core, debop4s_timeperiod, debop4s_data_common, debop4s_rediscala, hibernate_rediscala)

  val debop4s_shiro = Project("debop4s-shiro", file("debop4s-shiro"))
                      .settings(commonSettings ++ Seq(libraryDependencies ++= Seq(shiroCore, rediscala)))
                      .dependsOn(debop4s_rediscala)

  val debop4s_web_spring = Project("debop4s-web-spring", file("debop4s-web-spring"))
                           .settings(commonSettings ++
                                     Seq(libraryDependencies ++=
                                         Seq(springWeb, springWebMvc, aspectjweaver) ++
                                         tomcatEmbedAll))
                           .dependsOn(debop4s_core, debop4s_rediscala)

  val debop4s_web_scalatra = Project("debop4s-web-scalatra", file("debop4s-web-scalatra"))
                             .settings(commonSettings ++
                                       Seq(libraryDependencies ++=
                                           scalatraAll ++
                                           Seq(scalate, springContext, javaxServletApi, jetty) ++
                                           tomcatEmbedAll))

  val debop4s_reactive = Project("debop4s-reactive", file("debop4s-reactive"))
                         .settings(commonSettings ++
                                   Seq(libraryDependencies ++= Seq(rxScala)))


  val debop4s_benchmark = Project("debop4s-benchmark", file("debop4s-benchmark"))
                          .settings(commonSettings ++
                                    Seq(libraryDependencies ++= Seq(scalaAsync, miniboxing, scalaxy_stream, fst, kryo),
                                      libraryDependencies <++= scalaBinaryVersion(sv => scalaBlitz(sv))))
                          .dependsOn(debop4s_core)
}