import sbt.Keys._
import sbt._

trait Dependencies {
  self: Build =>
  val jdkVersion = "1.7"

  val akkaVersion = "2.3.12"
  val akkaActor   = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

  val slick3Version = "3.0.0"
  val slick3        = "com.typesafe.slick" %% "slick" % slick3Version
  val slick3CodeGen = "com.typesafe.slick" %% "slick-codegen" % slick3Version
  val slick3Testkit = "com.typesafe.slick" %% "slick-testkit" % slick3Version % "test"

  val slick3All = Seq(slick3, slick3CodeGen, slick3Testkit)

  val slick2Version = "2.1.0"
  val slick2        = "com.typesafe.slick" %% "slick" % slick2Version
  val slick2CodeGen = "com.typesafe.slick" %% "slick-codegen" % slick2Version
  val slick2Testkit = "com.typesafe.slick" %% "slick-testkit" % slick2Version % "test"

  val slick2All = Seq(slick2, slick2CodeGen, slick2Testkit)

  val typesafeConfig = "com.typesafe" % "config" % "1.3.0"
  // 1.3.0은 JDK 8용이다.
  val scalaAsync     = "org.scala-lang.modules" %% "scala-async" % "0.9.5"
  val scalaPickling  = "org.scala-lang" %% "scala-pickling" % "0.9.1"

  def scalaBlitz(scalaVersion: String) = scalaVersion match {
    case "2.10" => Seq("com.github.scala-blitz" %% "scala-blitz" % "1.0-M2")
    case _ => Seq("com.github.scala-blitz" %% "scala-blitz" % "1.2")
  }
  //val scalaBlitz     = "com.github.scala-blitz" %% "scala-blitz" % "1.2"

  val scalactic      = "org.scalactic" %% "scalactic" % "2.2.5"
  val miniboxing     = "org.scala-miniboxing.plugins" %% "miniboxing-runtime" % "0.4-M4"
  val scalaxy_stream = "com.nativelibs4java" % "scalaxy-streams_2.11" % "0.3.4"

  // scala version 에 따라 다른 dependency를 준다.
  // libraryDependencies ++= shapeless.value
  val shapeless = Def setting (
    CrossVersion partialVersion scalaVersion.value match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        Seq("com.chuusai" %% "shapeless" % "2.1.0")

      case Some((2, 10)) =>
        Seq("com.chuusai" % "shapeless_2.10.5" % "2.1.0",
          compilerPlugin("org.scalamacros" % "paradise_2.10.5" % "2.0.1"))
    }
    )

  val rxScala = "io.reactivex" %% "rxscala" % "0.25.0"

  val scalatraVersion   = "2.3.1"
  val scalatra          = "org.scalatra" %% "scalatra" % scalatraVersion
  val scalatra_scalate  = "org.scalatra" %% "scalatra-scalate" % scalatraVersion
  val scalatraJson      = "org.scalatra" %% "scalatra-json" % scalatraVersion
  val scalatraJetty     = "org.scalatra" %% "scalatra-jetty" % scalatraVersion
  val scalatraScalaTest = "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test"

  val scalatraAll = Seq(scalatra, scalatra_scalate, scalatraJson, scalatraJetty, scalatraScalaTest)

  val scalate = "org.scalatra.scalate" %% "scalate-core" % "1.7.0"

  val scaldiVersion = "0.5.5"
  val scaldi        = "org.scaldi" %% "scaldi" % scaldiVersion
  val scaldiAkka    = "org.scaldi" %% "scaldi-akka" % scaldiVersion
  val scaldiPlay    = "org.scaldi" %% "scaldi-play" % scaldiVersion

  val commonsCompress    = "org.apache.commons" % "commons-compress" % "1.9"
  val commonsPool2       = "org.apache.commons" % "commons-pool2" % "2.3"
  val commonsLang3       = "org.apache.commons" % "commons-lang3" % "3.4"
  val commonsCodec       = "commons-codec" % "commons-codec" % "1.10"
  val commonsCollections = "commons-collections" % "commons-collections" % "3.2.1"
  val commonsIO          = "commons-io" % "commons-io" % "2.4"
  val commonsValidator   = "commons-validator" % "commons-validator" % "1.4.0"

  val apacheCommons = Seq(commonsCompress, commonsPool2, commonsCollections, commonsLang3, commonsIO, commonsValidator)

  val gsCollectionsVersion   = "6.2.0"
  val gsCollections          = "com.goldmansachs" % "gs-collections" % gsCollectionsVersion
  val gsCollectionsApi       = "com.goldmansachs" % "gs-collections-api" % gsCollectionsVersion
  val gsCollectionsTestutils = "com.goldmansachs" % "gs-collections-testutils" % gsCollectionsVersion % "test"
  val gsCollectionsForkjoin  = "com.goldmansachs" % "gs-collections-forkjoin" % gsCollectionsVersion

  val gsCollectionsAll = Seq(
    gsCollections,
    gsCollectionsApi,
    gsCollectionsForkjoin,
    gsCollectionsTestutils
  )

  val javaxMail = "javax.mail" % "javax.mail-api" % "1.5.4"

  val apacheHttpVersion = "4.4.1"
  val httpcore          = "org.apache.httpcomponents" % "httpcore" % apacheHttpVersion
  val httpclient        = "org.apache.httpcomponents" % "httpclient" % apacheHttpVersion exclude("commons-logging", "commons-logging")
  val httpcoreNIO       = "org.apache.httpcomponents" % "httpcore-nio" % apacheHttpVersion
  val httpasyncclient   = "org.apache.httpcomponents" % "httpasyncclient" % "4.1"

  val apacheHttpComponentAll = Seq(httpcore, httpclient, httpcoreNIO, httpasyncclient)

  val asyncHttpClient = "com.ning" % "async-http-client" % "1.9.22"

  val javassist = "org.javassist" % "javassist" % "3.19.0-GA"

  val javaxInject     = "javax.inject" % "javax.inject" % "1"
  val jta             = "javax.transaction" % "jta" % "1.1"
  val javaxValidation = "javax.validation" % "validation-api" % "1.1.0.Final"
  val jsr305          = "com.google.code.findbugs" % "jsr305" % "3.0.+"
  val jcipAnnotations = "net.jcip" % "jcip-annotations" % "1.0"

  val guava = "com.google.guava" % "guava" % "18.0"
  val guice = "com.google.inject" % "guice" % "4.0"

  val snappy = "org.xerial.snappy" % "snappy-java" % "1.1.1.7"
  val lz4    = "net.jpountz.lz4" % "lz4" % "1.3.0"

  val fst = "de.ruedigermoeller" % "fst" % "2.29"

  val kryo                    = "com.esotericsoftware.kryo" % "kryo" % "2.24.0"
  val akka_kryo_serialization = "com.github.romix.akka" %% "akka-kryo-serialization" % "0.3.2"

  val gson = "com.google.code.gson" % "gson" % "2.3.1"

  val jacksonVersion = "2.6.0"

  val jacksonCore        = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
  val jacksonAnnotations = "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion
  val jacksonDatabind    = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion

  val jacksonDatatypeJoda       = "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % jacksonVersion
  val jacksonDatatypeHibernate4 = "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate4" % jacksonVersion

  val jacksonModuleScala       = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.2"
  val jacksonModuleAfterburner = "com.fasterxml.jackson.module" % "jackson-module-afterburner" % jacksonVersion

  val jacksonCoreAll     = Seq(jacksonCore, jacksonAnnotations, jacksonDatabind)
  val jacksonDatatypeAll = Seq(jacksonDatatypeJoda, jacksonDatatypeHibernate4)
  val jacksonModuleAll   = Seq(jacksonModuleScala, jacksonModuleAfterburner)

  val json4sVersion = "3.2.11"
  val json4sCore    = "org.json4s" %% "json4s-core" % json4sVersion
  val json4sJackson = "org.json4s" %% "json4s-jackson" % json4sVersion

  val asm    = "asm" % "asm" % "3.3.1" % "runtime"
  val ow2Asm = "org.ow2.asm" % "asm" % "5.0.3"

  val modelmapper = "org.modelmapper" % "modelmapper" % "0.7.4"

  // Http Component for scala
  val dispatch = "net.databinder.dispatch" %% "dispatch-core" % "0.11.2" exclude("org.scala-lang.modules", "scala-xml")

  // 암호화
  val jasypt = "org.jasypt" % "jasypt" % "1.9.2"

  val jodaTime    = "joda-time" % "joda-time" % "2.8"
  val jodaMoney   = "joda-money" % "joda-money" % "0.10.0"
  val jodaConvert = "org.joda" % "joda-convert" % "1.7"

  val javaTuples = "org.javatuples" % "javatuples" % "1.2"

  val joddVersion = "3.4.1"
  val joddCore    = "org.jodd" % "jodd-core" % joddVersion
  val joddBean    = "org.jodd" % "jodd-bean" % joddVersion
  val joddProps   = "org.jodd" % "jodd-props" % joddVersion
  val joddAll     = Seq(joddCore, joddBean, joddProps)

  val logback = "ch.qos.logback" % "logback-classic" % "1.1.3"

  val slf4jVersion = "1.7.12"
  val slf4j        = "org.slf4j" % "slf4j-api" % slf4jVersion
  val slf4jLog4j   = "org.slf4j" % "slf4j-log4j12" % slf4jVersion
  val slf4jJcl     = "org.slf4j" % "jcl-over-slf4j" % slf4jVersion

  val slf4jSeq = Seq(slf4j, logback)

  val commonLogging = "commons-logging" % "commons-logging" % "1.2"

  val lombok = "org.projectlombok" % "lombok" % "1.16.4"

  // Spring Framework
  val springFrameworkVersion = "4.2.0.RELEASE"
  val springCore             = "org.springframework" % "spring-core" % springFrameworkVersion
  val springAop              = "org.springframework" % "spring-aop" % springFrameworkVersion
  val springBeans            = "org.springframework" % "spring-beans" % springFrameworkVersion
  val springContext          = "org.springframework" % "spring-context" % springFrameworkVersion
  val springExpression       = "org.springframework" % "spring-expression" % springFrameworkVersion
  val springAspects          = "org.springframework" % "spring-aspects" % springFrameworkVersion
  val springTx               = "org.springframework" % "spring-tx" % springFrameworkVersion
  val springJdbc             = "org.springframework" % "spring-jdbc" % springFrameworkVersion
  val springOrm              = "org.springframework" % "spring-orm" % springFrameworkVersion
  val springTest             = "org.springframework" % "spring-test" % springFrameworkVersion % "test"

  val springFrameworkMinimum = Seq(springCore, springContext, springBeans)
  val springFrameworkAll     = springFrameworkMinimum ++ Seq(springAop, springExpression, springAspects, springTx, springJdbc, springOrm, springTest)

  // Spring MVC
  val springWeb    = "org.springframework" % "spring-web" % springFrameworkVersion
  val springWebMvc = "org.springframework" % "spring-webmvc" % springFrameworkVersion

  // Spring Data
  val springDataJpa     = "org.springframework.data" % "spring-data-jpa" % "1.8.0.RELEASE" excludeAll(
    ExclusionRule("org.springframework", "spring-orm"),
    ExclusionRule("org.springframework", "spring-context"),
    ExclusionRule("org.springframework", "spring-tx"),
    ExclusionRule("org.springframework", "spring-aop")
    )
  val springDataMongodb = "org.springframework.data" % "spring-data-mongodb" % "1.7.0.RELEASE"

  // Spring Security
  val springSecurityVersion = "4.0.1"
  val springSecurityCore    = "org.springframework.security" % "spring-security-core" % springSecurityVersion
  val springSecurityCrypto  = "org.springframework.security" % "spring-security-crypto" % springSecurityVersion
  val springSecurityWeb     = "org.springframework.security" % "spring-security-web" % springSecurityVersion

  // AspectJ
  val aspectjrt     = "org.aspectj" % "aspectjrt" % "1.8.1"
  val aspectjweaver = "org.aspectj" % "aspectjweaver" % "1.8.1"

  // Hibernate 4
  val hibernateVersion       = "4.3.10.Final"
  val hibernateCore          = "org.hibernate" % "hibernate-core" % hibernateVersion
  val hibernateEntityManager = "org.hibernate" % "hibernate-entitymanager" % hibernateVersion
  val hibernateTesting       = "org.hibernate" % "hibernate-testing" % hibernateVersion % "test"
  val hibernateJpa21Api      = "org.hibernate.javax.persistence" % "hibernate-jpa-2.1-api" % "1.0.0.Final"
  val hibernateAll           = Seq(hibernateCore, hibernateEntityManager, hibernateTesting, hibernateJpa21Api)

  val hibernateValidatorVersion = "5.1.3.Final"

  // QueryDSL
  val queryslVersion      = "3.6.3"
  val querydslCollections = "com.mysema.querydsl" % "querydsl-collections" % queryslVersion
  val querydslapt         = "com.mysema.querydsl" % "querydsl-apt" % queryslVersion
  val querydslJpa         = "com.mysema.querydsl" % "querydsl-jpa" % queryslVersion
  val querydslJpaCodegen  = "com.mysema.querydsl" % "querydsl-jpa-codegen" % queryslVersion
  val querydslScala       = "com.mysema.querydsl" % "querydsl-scala" % queryslVersion
  val querydslMongodb     = "com.mysema.querydsl" % "querydsl-mongodb" % queryslVersion
  val querydslSql         = "com.mysema.querydsl" % "querydsl-sql" % queryslVersion

  // MongoDB
  val mongoJavaDriver = "org.mongodb" % "mongo-java-driver" % "3.0.1"
  val casbar          = "org.mongodb" % "casbar" % "2.8.1"
  val reactiveMongo   = "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23"

  // Redis
  val jedis     = "redis.clients" % "jedis" % "2.7.2"
  val rediscala = "com.etaty.rediscala" %% "rediscala" % "1.4.2"

  // Connection Pool
  val hikaricp      = "com.zaxxer" % "HikariCP-java6" % "2.3.7"
  val hikaricpJava8 = "com.zaxxer" % "HikariCP" % "2.3.7"
  val tomcatJdbc    = "org.apache.tomcat" % "tomcat-jdbc" % "8.0.22"
  val bonecp        = "com.jolbox" % "bonecp" % "0.8.0.RELEASE"

  // Database drivers
  val hsqldb     = "org.hsqldb" % "hsqldb" % "2.3.2"
  val h2         = "com.h2database" % "h2" % "1.4.187"
  val mysql      = "mysql" % "mysql-connector-java" % "5.1.35"
  val postgresql = "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
  val mariadb    = "org.mariadb.jdbc" % "mariadb-java-client" % "1.1.8"

  val databaseDriverAll     = Seq(hsqldb, h2, mysql, postgresql, mariadb)
  val databaseDriverAllTest = Seq(hsqldb % "test", h2 % "test", mysql % "test", postgresql % "test", mariadb % "test")


  // Jetty
  val jetty = "org.eclipse.jetty" % "jetty-webapp" % "9.2.10.v20150310"

  // Tomcat
  val tomcatEmbedVersion     = "8.0.22"
  val tomcatEmbedCore        = "org.apache.tomcat.embed" % "tomcat-embed-core" % tomcatEmbedVersion
  val tomcatEmbedLoggingJuli = "org.apache.tomcat.embed" % "tomcat-embed-logging-juli" % tomcatEmbedVersion
  val tomcatEmbedJasper      = "org.apache.tomcat.embed" % "tomcat-embed-jasper" % tomcatEmbedVersion

  val tomcatEmbedAll = Seq(tomcatEmbedCore, tomcatEmbedLoggingJuli, tomcatEmbedJasper)

  // Servlet
  val javaeeApi       = "javax" % "javaee-api" % "7.0"
  val javaeeWebApi    = "javax" % "javaee-web-api" % "7.0"
  val javaxServletApi = "javax.servlet" % "javax.servlet-api" % "3.1.0"

  // Apache Shiro
  val shiroVersion = "1.2.3"
  val shiroCore    = "org.apache.shiro" % "shiro-core" % shiroVersion
  val shiroWeb     = "org.apache.shiro" % "shiro-web" % shiroVersion

  // Xml
  val xmlApis = "xml-apis" % "xml-apis" % "2.0.2"

  // Excel
  val apachePoi = "org.apache.poi" % "poi" % "3.11"
  val jxl       = "net.sourceforge.jexcelapi" % "jxl" % "2.6.12"


  // Testing for Java
  val junit           = "junit" % "junit" % "4.12" % "test"
  val junitBenchmarks = "com.carrotsearch" % "junit-benchmarks" % "0.7.2" % "test"
  val festAssert      = "org.easytesting" % "fest-assert" % "1.4" % "test"
  val mockito         = "org.mockito" % "mockito-all" % "1.10.19" % "test"

  val testingJavaSeq = Seq(junit, junitBenchmarks, festAssert, mockito)

  // Testing for Scala
  val scalatest  = "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  val spec2      = "org.specs2" %% "specs2" % "3.3.1" % "test"
  val scalameter = "com.storm-enroute" %% "scalameter" % "0.6" % "test"

  val testingScalaSeq = Seq(scalatest, scalameter)

  // Scala Check (automated property-based testing of Scala or Java)
  val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"

  val scalaStyle = Def setting (
    CrossVersion partialVersion scalaVersion.value match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq()
      case _ => Seq("org.scalastyle" %% "scalastyle" % "0.4.0" % "test")
    }
    )

  val commonDependencies = Seq(lombok, miniboxing, springTest) ++ slf4jSeq ++ testingJavaSeq ++ testingScalaSeq

  val coreDependencies = apacheCommons ++
                         apacheHttpComponentAll ++
                         joddAll ++
                         jacksonCoreAll ++
                         jacksonDatatypeAll ++
                         jacksonModuleAll ++
                         Seq(akkaActor, scalaPickling, scalaAsync, scalactic, springContext, springTest,
                           javaxMail, guava, jasypt, jodaTime, jodaConvert, javaTuples,
                           snappy, lz4, fst, gson,
                           modelmapper, dispatch, httpclient, asyncHttpClient,
                           ow2Asm, jsr305, jcipAnnotations, jta, javaxInject)
}
