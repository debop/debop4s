import sbt.Keys._
import sbt._

object Debop4sBuild extends Build {

  import Dependencies._

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
                         debop4s_data_orm,
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
                                          Seq(springOrm % "test", springDataJpa % "test")))

  val debop4s_redis = Project("debop4s-redis", file("debop4s-redis"))
                      .settings(commonSettings ++
                                Seq(libraryDependencies ++=
                                    Seq(jedis, springContext) ++
                                    Seq(springOrm % "test", springDataJpa % "test")))
                      .dependsOn(debop4s_core, hibernate_rediscala)

  val debop4s_rediscala = Project("debop4s-rediscala", file("debop4s-rediscala"))
                          .settings(commonSettings ++
                                    Seq(libraryDependencies ++=
                                        Seq(rediscala, typesafeConfig, springContext) ++
                                        Seq(springOrm % "test", springDataJpa % "test")))
                          .dependsOn(debop4s_config, debop4s_core)

  val debop4s_data_common = Project("debop4s-data-common", file("debop4s-data-common"))
                            .settings(commonSettings ++ Seq(libraryDependencies ++=
                                                            Seq(hikaricp, bonecp, tomcatJdbc) ++
                                                            databaseDriverAllTest))
                            .dependsOn(debop4s_config)

  val debop4s_data_slick2 = Project("debop4s-data-slick", file("debop4s-data-slick"))
                            .settings(commonSettings ++ Seq(libraryDependencies ++= slick2All ++ databaseDriverAllTest))
                            .dependsOn(debop4s_config, debop4s_core, debop4s_timeperiod, debop4s_data_common)

  val debop4s_data_slick2_northwind =
    Project("debop4s-data-slick-northwind", file("debop4s-data-slick-northwind"))
    .settings(commonSettings ++ Seq(libraryDependencies ++= slick2All ++ databaseDriverAllTest))
    .dependsOn(debop4s_data_slick2)

  val debop4s_data_slick3 = Project("debop4s-data-slick3", file("debop4s-data-slick3"))
                            .settings(commonSettings ++
                                      Seq(libraryDependencies ++= slick3All ++ databaseDriverAllTest,
                                           libraryDependencies <++= scalaBinaryVersion(sv => shapeless(sv)),
                                           scalacOptions ++= Seq("-nowarn")))
                            .dependsOn(debop4s_config, debop4s_core, debop4s_timeperiod, debop4s_data_common)

  val debop4s_data_slick3_northwind =
    Project("debop4s-data-slick3-northwind", file("debop4s-data-slick3-northwind"))
    .settings(commonSettings ++ Seq(libraryDependencies ++= slick3All ++ databaseDriverAllTest,
                                     scalacOptions ++= Seq("-nowarn")))
    .dependsOn(debop4s_data_slick3)

  val debop4s_data_orm = Project("debop4s-data-orm", file("debop4s-data-orm"))
                         .settings(commonSettings ++
                                   Seq(libraryDependencies ++=
                                       Seq(springOrm, springDataJpa, hikaricp, bonecp, tomcatJdbc) ++
                                       hibernateAll ++
                                       Seq(querydslJpa, querydslJpaCodegen) ++
                                       databaseDriverAllTest))
                         .dependsOn(debop4s_config, debop4s_core, debop4s_timeperiod,
                             debop4s_data_common, debop4s_rediscala, hibernate_rediscala)

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


  /** Common Setting */
  val scalaBuildOptions = Seq("-unchecked", "-deprecation", "-feature", "-explaintypes", "-Dfile.encinding=UTF-8",
                               "-language:implicitConversions", "-language:postfixOps", "-language:dynamics", "-language:higherKinds",
                               "-language:reflectiveCalls",
                               "-Yconst-opt", "-Ydead-code", "-Yclosure-elim", "-Yinline", "-Yinline-warnings")


  def commonSettings: Seq[Def.Setting[_]] =
    Defaults.coreDefaultSettings ++ Seq(
                                         organization := "debop4s",
                                         version := "0.4.0-SNAPSHOT",
                                         scalaVersion := "2.11.6",
                                         ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
                                         libraryDependencies ++= commonDependencies,
                                         javacOptions ++= Seq("-source", jdkVersion, "-target", jdkVersion),
                                         scalacOptions ++= scalaBuildOptions,
                                         resolvers ++= CustomResolvers.customResolvers,
                                         publishMavenStyle := true,
                                         publishArtifact in Test := false,
                                         pomIncludeRepository := { x => false },
                                         compileOrder := CompileOrder.Mixed,
                                         parallelExecution in Test := true,
                                         crossScalaVersions := Seq("2.10.5", "2.11.6"),
                                         unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile, scalaBinaryVersion) {
                                           (s, v) => s / ("scala_" + v)
                                         }
                                       )
}