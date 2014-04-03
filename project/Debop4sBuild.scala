import sbt.Keys._
import sbt._

object Debop4sBuild extends Build {

    val debop4sVersion = "0.3.0-SNAPSHOT"

    val akkaVersion = "2.2.3"
    val sprayVersion = "1.3.1"
    val springFrameworkVersion = "4.0.3.RELEASE"
    val hibernateVersion = "4.3.4.Final"
    val queryDslVersion = "3.3.2"

    // core dependencies
    val slf4j = "org.slf4j" % "slf4j-api" % "1.7.6"
    val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.1"
    val javasist = "org.javasist" % "javassist" % "3.18.1-GA"
    val commonLogging = "commons-logging" % "commons-logging" % "1.1.3"
    val jodaTime = "joda-time" % "joda-time" % "2.3+"
    val jodaConvert = "org.joda" % "joda-convert" % "1.6+"
    val guava = "com.google.guava" % "guava" % "16.0"
    val annotations = "com.intellij" % "annotations" % "12.0"

    // akka
    val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion exclude("scala-lang", "scala-compiler")
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test" exclude("scala-lang", "scala-compiler")

    // scala utils
    val scalaUtils = "org.scalautils" %% "scalautils" % "2.1.2" exclude("scala-lang", "scala-compiler")
    val scalaArm = "com.jsuereth" %% "scala-arm" % "1.3" exclude("scala-lang", "scala-compiler")
    val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.0-M4" exclude("scala-lang", "scala-compiler")
    val scalaPickling = "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT" exclude("scala-lang", "scala-compiler")
    val scalaUtilSet = Seq(scalaUtils, scalaArm, scalaAsync, scalaPickling)

    // apache commons
    val commonsCodec = "commons-codec" % "commons-codec" % "1.8"
    val commonsIO = "commons-io" % "commons-io" % "2.4"
    val commonsPool2 = "org.apache.commons" % "commons-pool2" % "2.1"
    val commonsLang3 = "org.apache.commons" % "commons-lang3" % "3.1"

    // http component
    val httpclient = "org.apache.httpcomponents" % "httpclient" % "4.3"
    val httpfluent = "org.apache.httpcomponents" % "fluent-hc" % "4.3.1"
    val httpasyncclient = "org.apache.httpcomponents" % "httpasyncclient" % "4.0" exclude("org.apache.httpcomponent", "httpcore")
    val asynchttpclient = "com.ning" % "async-http-client" % "1.7.20"

    // mail
    val mail = "javax.mail" % "mail" % "1.4"

    // serializer
    val snappy = "org.xerial.snappy" % "snappy-java" % "1.1.0.1"
    val fst = "de.ruedigermoeller" % "fst" % "1.53"

    // json
    val jacksonVersion = "2.3.1"
    val jackson = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
    val jacksonAnnotations = "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion
    val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
    val jacksonJoda = "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % jacksonVersion
    val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
    val jacksons = Seq(jackson, jacksonAnnotations, jacksonDatabind, jacksonJoda, jacksonScala)

    val sprayJson = "io.spray" %% "spray-json" % "1.2.5"

    // json4s
    val json4sValue = "3.2.8"
    val json4sNative = "org.json4s" %% "json4s-native" % json4sValue
    val json4sSet = Seq(json4sNative)

    // asm
    val cglib = "cglib" % "cglib" % "3.1"
    val ow2Asm = "org.ow2.asm" % "asm-all" % "4.2"

    // model mapper
    val modelmapper = "org.modelmapper" % "modelmapper" % "0.6.3"

    // jasypt
    val jasypt = "org.jasypt" % "jasypt" % "1.9.2"


    // spring framework
    val springContext = "org.springframework" % "spring-context" % springFrameworkVersion
    val springContextSupport = "org.springframework" % "spring-context-support" % springFrameworkVersion
    val springTest = "org.springframework" % "spring-test" % springFrameworkVersion % "test"
    val springContextSeq = Seq(springContext, springContextSupport, springTest)

    val springAop = "org.springframework" % "spring-aop" % springFrameworkVersion
    val springExpression = "org.springframework" % "spring-expression" % springFrameworkVersion
    val springJdbc = "org.springframework" % "spring-jdbc" % springFrameworkVersion
    val springOrm = "org.springframework" % "spring-orm" % springFrameworkVersion
    val springTx = "org.springframework" % "spring-tx" % springFrameworkVersion
    val springOrmSeq = Seq(springOrm, springTx, springJdbc)

    // spring mvc
    val springWeb = "org.springframework" % "spring-web" % springFrameworkVersion
    val springWebMvc = "org.springframework" % "spring-webmvc" % springFrameworkVersion
    val springBoot = "org.springframework.boot" % "spring-boot-starter-web" % "1.0.0.RC5"

    // spring data
    val springDataJpa = "org.springframework.data" % "spring-data-jpa" % "1.5.1.RELEASE"
    val springDataMongo = "org.springframework.data" % "spring-data-mongodb" % "1.4.1.RELEASE"

    // spring scala
    val springScala = "org.springframework.scala" % "spring-scala" % "1.0.0.M2" exclude("scala-lang", "scala-compiler")

    // hibernate
    val hibernateCore = "org.hibernate" % "hibernate-core" % hibernateVersion
    val hibernateEntityManager = "org.hibernate" % "hibernate-entitymanager" % hibernateVersion
    val hibernateTest = "org.hibernate" % "hibernate-testing" % hibernateVersion % "test"
    val hibernateValidator = "org.hibernate" % "hibernate-validator" % "5.1.0.Final" exclude("org.jboss.logging", "jboss-logging")

    // Query DSL
    val queryDslApt = "com.mysema.querydsl" % "querydsl-apt" % queryDslVersion
    val queryDslJpa = "com.mysema.querydsl" % "querydsl-jpa" % queryDslVersion
    val queryDslScala = "com.mysema.querydsl" % "querydsl-scala" % queryDslVersion
    val queryDslMongo = "com.mysema.querydsl" % "querydsl-mongodb" % queryDslVersion

    // activate-framework
    val activateFrameworkVersion = "1.4.1"
    val activateCore = "net.fwbrasil" %% "activate-core" % activateFrameworkVersion
    val activateJdbc = "net.fwbrasil" %% "activate-jdbc" % activateFrameworkVersion
    val activateJdbcAsync = "net.fwbrasil" %% "activate-jdbc-async" % activateFrameworkVersion
    val activateMongo = "net.fwbrasil" %% "activate-mongo" % activateFrameworkVersion
    val activateMongoAsync = "net.fwbrasil" %% "activate-mongo-async" % activateFrameworkVersion

    // MongoDB
    val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % "0.10.0"
    val mongoDriver = "org.mongodb" % "mongo-java-driver" % "2.11.4"

    // Redis
    val rediscala = "com.etaty.rediscala" %% "rediscala" % "1.3"

    // Database Connection Pool
    val boneCP = "com.jolbox" % "bonecp" % "0.8.0.RELEASE"
    val hikariCP = "com.zaxxer" % "HikariCP" % "1.3.3"

    // Database Drivers
    val hsqldb = "org.hsqldb" % "hsqldb" % "2.3.2" % "test"
    val h2 = "com.h2database" % "h2" % "1.3.175" % "test"
    val mysql = "mysql" % "mysql-connector-java" % "5.1.29" % "test"
    val postgresql = "postgresql" % "postgresql" % "9.1-901.jdbc4" % "test"
    val databaseDrivers = Seq(hsqldb, h2, mysql, postgresql)

    // test
    val junit = "junit" % "junit" % "4.11" % "test"
    val festAssert = "org.easytesting" % "fest-assert" % "1.4" % "test"
    val scalaTest = "org.scalatest" %% "scalatest" % "2.1.2" % "test"
    val scalaMeter = "com.github.axel22" %% "scalameter" % "0.4" % "test"

    val testUnits = Seq(junit, festAssert, scalaTest, scalaMeter)


    val customResolvers = Seq(
        Resolver.mavenLocal,
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots"),
        Resolver.typesafeRepo("releases"),
        "springSource" at "http://repo.springsource.org/release",
        "springSourceMileston" at "http://repo.springsource.org/milestone",
        "jboss" at "http://repository.jboss.org/nexus/content/groups/public",
        "spray" at "http://repo.spray.io",
        "fwbrasil.net" at "http://fwbrasil.net/maven/",
        // travis can't access 'https'
        // "rediscala" at "https://github.com/etaty/rediscala-mvn/tree/master/releases",
        "rediscala" at "http://pk11-scratch.googlecode.com/svn/trunk/"
    )

    def commonSettings =
        Defaults.defaultSettings ++ Seq(
            organization := "com.github.debop4s",
            version := "0.3.0-SNAPSHOT",
            scalaVersion := "2.10.4",
            javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.7", "-target", "1.7"),
            javaOptions ++= Seq("-ea", "-server", "-Xms512M", "-Xmx4G", "-XX:MaxPermSize=512M", "-XX:+UseConcMarkSweepGC", "-XX:+CMSClassUnloadingEnabled", "-XX:-UseGCOverheadLimit"),
            scalacOptions ++= Seq("-encoding", "UTF-8", "-target:jvm-1.7"),
            compileOrder := CompileOrder.JavaThenScala,
            fork in run := true,
            fork in Test := true,
            parallelExecution in Test := false,
            publishMavenStyle := true,
            resolvers ++= customResolvers,
            publishArtifact in Test := false,
            libraryDependencies ++= Seq(
                annotations, logbackClassic, slf4j,
                jodaTime, jodaConvert,
                springTest
            ) ++ testUnits
        )

    lazy val debop4s = Project(
        id = "debop4s",
        base = file("."),
        settings = commonSettings,
        aggregate = Seq(
            debop4sCore,
            debop4sTimeperiod,
            hibernateRediscala,
            debop4sRedis,
            debop4sMongo,
            debop4sData
        )
    )

    lazy val debop4sCore = Project(
        id = "debop4s-core",
        base = file("debop4s-core"),
        settings = commonSettings ++ Seq(
            libraryDependencies ++= Seq(
                commonsCodec, commonsIO, commonsPool2, mail, guava,
                snappy, fst, jasypt, modelmapper,
                httpclient, httpfluent, httpasyncclient, asynchttpclient,
                ow2Asm,
                springScala % "test"
            ) ++ scalaUtilSet ++ jacksons ++ json4sSet ++ springContextSeq
        )
    )

    lazy val debop4sTimeperiod = Project(
        id = "debop4s-timeperiod",
        base = file("debop4s-timeperiod"),
        dependencies = Seq(debop4sCore),
        settings = commonSettings
    )

    lazy val hibernateRediscala = Project(
        id = "hibernate-rediscala",
        base = file("hibernate-rediscala"),
        settings = commonSettings ++ Seq(
            libraryDependencies ++= Seq(
                rediscala, snappy, fst,
                akka, akkaTestkit,
                hibernateEntityManager, hibernateTest,
                boneCP % "test",
                hikariCP % "test",
                springContext % "test",
                springOrm % "test",
                springDataJpa % "test"
            ) ++ databaseDrivers
        )
    )

    lazy val debop4sRedis = Project(
        id = "debop4s-redis",
        base = file("debop4s-redis"),
        dependencies = Seq(debop4sCore),
        settings = commonSettings ++ Seq(
            libraryDependencies ++= Seq(
                rediscala, snappy, fst,
                akka, akkaTestkit,
                springOrm % "test",
                springDataJpa % "test"
            )
        )
    )

    lazy val debop4sMongo = Project(
        id = "debop4s-mongo",
        base = file("debop4s-mongo"),
        dependencies = Seq(debop4sCore, debop4sTimeperiod),
        settings = commonSettings ++ Seq(
            libraryDependencies ++= Seq(
                springDataMongo, queryDslMongo, reactiveMongo
            )
        )
    )

    lazy val debop4sData = Project(
        id = "debop4s-data",
        base = file("debop4s-data"),
        dependencies = Seq(debop4sCore, debop4sTimeperiod, debop4sRedis, hibernateRediscala),
        settings = commonSettings ++ Seq(
            libraryDependencies ++= Seq(
                springDataJpa, springScala,
                hibernateEntityManager, hibernateTest, hibernateValidator,
                queryDslJpa, queryDslScala,
                boneCP, hikariCP
            ) ++ databaseDrivers ++ springOrmSeq,
            testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-v")
        )
    )
}