// resolvers += Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "3.0.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.code-troopers.play" % "play-querydsl" % "0.1.2")

// 이건 안되네...
// addSbtPlugin("com.code-troopers.play" % "play-querydsl" % "0.1.2")