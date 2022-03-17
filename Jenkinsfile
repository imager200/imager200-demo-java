node {

    try {
                stage('checkout') {
                   checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'robot', url: 'git@github.com:imager200/imager200-demo-java.git']]])
                   def skipBuild = sh (script: "git log -1 | grep 'No Build'", returnStatus: true)

                    if (skipBuild == 0) {
                      echo ("[No Build] flag spotted in git commit. Aborting.")
                      env.SKIP_BUILD = "true"
                    }
                }

                if (env.SKIP_BUILD == "true") {
                    currentBuild.result = 'NOT_BUILT'
                    return
                }

                stage('build and push to registry') {
                    withMaven(
                        maven: 'mvn-3.6',
                        options: [
                           artifactsPublisher(disabled: true),
                           junitPublisher(disabled: true)],
                    ) {
                        sh '''
                        mvn package
                        echo $GH_TOKEN | docker login ghcr.io -u zak905 --password-stdin
                        docker build -f src/main/docker/Dockerfile.jvm -t ghcr.io/imager200/imager200-demo-java:latest .
                        docker push ghcr.io/imager200/imager200-demo-java:latest
                        '''
                    }  
                }

                stage('deploy') {


                }

                stage('cleanup') {
                   sh 'docker rmi ghcr.io/imager200/imager200-demo-java:latest'
                   cleanWs()
                }
    } catch (err) {
         currentBuild.result = 'FAILURE'
         echo 'some error prevented the build to finish, cleaning up workspace' + err.toString()
         cleanWs()
    }
}