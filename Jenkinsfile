@Library('pipelib@master') _ 
 node { 
 
 
   stage("Clean") { 
     deleteDir() 
     sh "pwd" 
     sh "ls -al" 
   } 
 
 
   stage("Build/Push geopackage-wkb") { 
       git url: "https://github.com/venicegeo/geopackage-wkb-java.git" 
       withEnv(["PATH+MAVEN=${tool "M3"}/bin"]) { 
         
         sh "mvn clean install" 
         sh "pwd" 
         sh "ls -al target/" 
         mavenPush { 
         mavenArtifact = 'target/wkb-1.2.1-SF-SNAPSHOT.jar ' 
           app = 'geopackage-wkb-java' 
           packaging = '.jar' 
           mavenRepoHost= 'nexus.devops.geointservices.io'
           mavenRepo = 'venicegeo'
           mavenRepoId = 'nexus'
           mavenOrg = 'venice'
           mavenOrgType = 'org'
           mavenProject = 'beachfront'
           mavenSettingsFileId = 'maven_pipelib'
           mavenPath = '/content/repositories'
           mavenProtocol = 'https'
        } 
       } 
     } 
   
   stage("Build/Push geopackage-core") {
      git url: "https://github.com/venicegeo/geopackage-core-java.git"
      withEnv(["PATH+MAVEN=${tool "M3"}/bin"]) {
        sh "mvn clean install"
        sh "pwd"
        sh "ls -al target/"
         mavenPush { 
         mavenArtifact = 'target/geopackage-core-1.2.2-SF-SNAPSHOT.jar ' 
           app = 'geopackage-core-java' 
           packaging = '.jar' 
           mavenRepoHost= 'nexus.devops.geointservices.io'
           mavenRepo = 'venicegeo'
           mavenRepoId = 'nexus'
           mavenOrg = 'venice'
           mavenOrgType = 'org'
           mavenProject = 'beachfront'
           mavenSettingsFileId = 'maven_pipelib'
           mavenPath = '/content/repositories'
           mavenProtocol = 'https'
        } 
     }
    }
   
   stage("Build/Deploye geopackage-java") {
     git url: "https://github.com/venicegeo/geopackage-java.git"
        withEnv(["PATH+MAVEN=${tool "M3"}/bin"]) {
       
          sh "mvn clean install"
          sh "pwd"
          sh "ls -al target/"
         mavenPush { 
         mavenArtifact = 'target/geopackage-1.2.3-SF-SNAPSHOT.jar' 
           app = 'geopackage' 
           packaging = '.jar' 
           mavenRepoHost= 'nexus.devops.geointservices.io'
           mavenRepo = 'venicegeo'
           mavenRepoId = 'nexus'
           mavenOrg = 'venice'
           mavenOrgType = 'org'
           mavenProject = 'beachfront'
           mavenSettingsFileId = 'maven_pipelib'
           mavenPath = '/content/repositories'
           mavenProtocol = 'https'
        }   
     } 
    }
   
   stage("Build GGPKG Converter") {
      git url: "https://github.com/venicegeo/bf-geojson-geopkg-converter",
      branch: "master"
      withEnv(["PATH+MAVEN=${tool "M3"}/bin"]) {
      sh "mvn clean install"
      sh "pwd"
      sh "ls -al target/"
       mavenPush { 
         mavenArtifact = 'target/geojsongpkgconverter-1.0-SNAPSHOT.jar' 
           app = 'ggpkg-converter' 
           packaging = '.jar' 
           mavenRepoHost= 'nexus.devops.geointservices.io'
           mavenRepo = 'venicegeo'
           mavenRepoId = 'nexus'
           mavenOrg = 'venice'
           mavenOrgType = 'org'
           mavenProject = 'beachfront'
           mavenSettingsFileId = 'maven_pipelib'
           mavenPath = '/content/repositories'
           mavenProtocol = 'https'
        }    
          
      }
   }
       
}

