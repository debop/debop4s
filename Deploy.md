# Nexus에 라이브러리 배포하기


## 1. Nexus 설치

회사의 nexus 에 배포하기 위해서는 nexus를 다운 받아 설치해야 한다.
 
mac 에서는 `brew install nexus` 로 간단히 설치할 수 있고, Linux 에서는 [Sonatype Nexus 설치 및 설정](http://lesstif.com/pages/viewpage.action?pageId=13631508) 을 참고하면 된다.

### 1.1 Nexus 환경 설정 

##### 참고 : [Sonatype Nexus 설정 및 maven 연결하기](http://stove99.tistory.com/75)

1. Nexus 설치 후 브라우저에서 [http://127.0.0.1:8081/nexus](http://127.0.0.1:8081/nexus) 에 들어가서 admin 으로 로그인 한다. (id: admin, pwd: admin123) ** 보안을 위해 비밀번호를 변경하는 것을 추천합니다 **
2. 좌측 메뉴으ㅔ서 Repositories 를 선택 -> 메인화면 상단의 "Public Repositories" 선택 -> 하단의 Configuration Tab 선택 -> Ordered Group Repositories 에 "Central", "Releases", "Snapshots" 이 있도록 선택한다.
3. 상단 "Cental" Repository 를 선택한 후, 하단 "Configuration" 탭을 선택한다. 아래 설정 중 "Download Remote Indexes" 를 False 에서 True 로 변경한 후 저장한다.
4. 좌측 메뉴 "Administration" > "Scheduled Tasks" 를 선택하면, Remote Index 를 다운받는 Task 가 실행되고 있을 것이다. (완료까지 10여분이 걸린다.)
5. Repositories 중 Public Repositories 를 선택한 후 하단 "Browse Index" 탭을 선택하고, Refresh를 해보면, Remote 서버에 있는 파일들의 index 들을 보여준다. (실제 파일들을 다운받는 것은 아니다) 


## 2. Nexus Repository 사용하기 

설치된 Repository 를 사용하면, Remote에 접근할 필요없이 빠르게 사용할 수 있다.

### 2.1 Maven에서 사용하기 

maven 프로젝트의 pom.xml 에 다음과 같이 repositories 를 추가한다.

```
<repositories>
    <repository>
      <id>shbae repo</id>
      <url>http://127.0.0.1:8081/nexus/content/groups/public</url>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
</repositories>
```

local repository (.m2/repository) 에 다운받지 않은 라이브러리인 경우 nexus 에서 다운받아 제공하게 된다.
Nuxus 웹에서 "Public Repositories" 를 선택하고, "Browse Storage" 탭을 선택하면, 새롭게 다운받은 라이브러리를 볼 수 있다.

### 2.2 sbt에서 사용하기

sbt 에는 resolvers 에 로컬 nexus 를 추가하면 됩니다.

```
val localNexus = "Local Nexus" at "http://127.0.0.1:8081/nexus/content/groups/public"
val customResolvers = Seq(
    localNexus,
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
)

resolvers ++= CustomResolvers.customResolvers

```



## 3 배포

다음은 자신이 만든 프로젝트의 결과물을 회사 Nexus 에 배포하는 방법에 대해 설명합니다. 배포할 때에는 계정정보 (Credentials)가 필요합니다. Nexus admin 계정을 사용할 수도 있지만 되도록 deploy 전용 계정을 사용하는 것이 좋습니다.

### 3.1 Maven으로 배포하기

maven에서 배포하기 위해서는 pom.xml 에 distributionManagement element 를 project 바로 밑에 정의합니다.

```
<project>
	...
  <distributionManagement>
    <repository>
      <id>Local.Repo</id>
      <name>Local Release Repository</name>
      <url>http://127.0.0.1:8081/nexus/content/repositories/releases</url>
    </repository>
    <snapshotRepository>
      <id>Local.Repo.Snapshot</id>
      <name>Local Snapshot Repository</name>
      <url>http://127.0.0.1:8081/nexus/content/repositories/snapshots</url>
    </snapshotRepository>
  </distributionManagement>
</project>
```

다음으로는 project/build/plugins 에 배포를 위한 plugin 을 정의합니다.

```
<project>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-deploy-plugin</artifactId>
        <inherited>false</inherited>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```
마지막으로 ~/.m2/ 폴더에 nuxus 로그인 계정을 정의해야 합니다. ~/.m2/settings.xml 파일을 만들고, 로그인 정보를 작성합니다.

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>Local.Repo</id>
      <username>deployer</username>
      <password>deployer_password</password>
    </server>
    <server>
      <id>Local.Repo.Snapshot</id>
      <username>deployer</username>
      <password>deployer_password</password>
    </server>
  </servers>
</settings>
```

pom의 distributionManagement repository 의 Id와 settings.xml의 server Id 가 일치해야 제대로 배포가 됩니다. 

설정이 다 되었으면 `mvn deploy` 를 실해하면, 버전에 `SNAPSHOT`이 있으면 snapshots 에 배포되고, 없으면 `releases`에 배포됩니다.

배포 되었는지 확인을 위해, Nexus 웹에서 `Snapshots` Repository를 선택하고, 하단의 `Browse Storage` 탭을 선택하여 Refresh를 수행하고, 목록을 보시기 바랍니다. 제대로 배포가 되었다면 관련 jar 등이 배포가 되었을 것입니다.


### 3.2 sbt 로 배포하기

sbt로 배포하기 위해서는 [sbt 배포 (sbt 공식사이트)](http://www.scala-sbt.org/0.13/docs/Publishing.html)를 참고하면 쉽습니다.
다만 보안과 관련된 부분에 대해서만 조금 조심하면 됩니다.

##### PublishTo 정의 

우선 sbt 에서 배포하기 위해서는 settings 항목에 publishTo 를 지정해 주어야 합니다. 프로젝트의 버전에 SNAPSHOT이 붙어있으면, snapshots repository로 아니면 release repository 로 지정하도록 합니다.

```
  publishTo <<= version { v: String => getMavenRepository(v) }


  def getMavenRepository(v: String): Some[MavenRepository] = {
    val nexus = "http://127.0.0.1:8081/nexus/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("Local Snapshots" at nexus + "content/repositories/snapshots/")
    else
      Some("Local Releases" at nexus + "content/repositories/releases/")
  }
```

##### Credentials

nexus 에 접근하기 위한 계정 정보를 정의해야 하는데, 프로젝트 내에 정의하게 되면 보안 유출 문제가 발생할 소지가 있습니다.
이를 방지하기 위해 ~/.sbt/0.13/sonatype.sbt 파일을 만들고, 계정 정보를 입력하면, sbt 가 publish 시에 자동으로 이 파일을 읽어들여 사용합니다.

`~/.sbt/0.13/sonatype.sbt` 파일 내용

```
credentials += Credentials("Sonatype Nexus Repository Manager", "127.0.0.1", username, password)
```
로 정의하면 됩니다. **이때 `"Sonatype Nexus Repository Manager"` 명칭은 변경하면 안되고, Nexus 설치 서버도 port 명 없이 IP만 지정해야 합니다.**

##### Modify the generated POM

sbt 명령어 중 `publishM2` 를 이용하여 local maven repository 에 배포할 수 있고, `publishLocal`을 통해 local ivy2 repository 애 배포하게 됩니다. 위의 nexus 서버에 배포하기 위해서는 `publish` 나 `+publish` 를 수행하면 됩니다.

여기서 maven style 로 pom 을 생성하기 위해서는 sbt의 settings 에 다음과 같이 정보를 추가로 설정하면 됩니다.

```
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
```

pomExtra 에 프로젝트와 관련된 부가 정보를 지정해주면 됩니다.