pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('PreBuild-Email') {
           steps {
               script {
                   def mailRecipients = 'rcardenas@cntcloud.com'
                   def jobName = currentBuild.fullDisplayName
                   def numEje = env.BUILD_NUMBER
                   //emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                       emailext body: '''Hello''',
                       mimeTye: 'text/html',
                       subject: "[Jenkins] Started ${jobName}",
                       to: "${mailRecipients}",
                       replyTo: "${mailRecipients}",
                       recipientProviders: [[$class: 'CulpritsRecipientProvider']]
                    }
                    }
                }
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('EndtoEnd tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }

    }
    post {
        always {
            junit 'results/*_result.xml'
            echo 'subject: 'Build Success in Jenkins: $PROJECT_NAME - #$BUILD_NUMBER''
        }
        
        success {
                       echo "subject: 'Build Success in Jenkins: $PROJECT_NAME - #$BUILD_NUMBER'"
        }
            /*emailext body: 'Check console output at $BUILD_URL to view the results. \n\n ${CHANGES} \n\n -------------------------------------------------- \n${BUILD_LOG, maxLines=100, escapeHtml=false}', 
                    to: "${EMAIL_TO}", 
                    subject: 'Build Success in Jenkins: $PROJECT_NAME - #$BUILD_NUMBER'
        }*/
        
        failure {
            /*emailext body: 'Check console output at $BUILD_URL to view the results. \n\n ${CHANGES} \n\n -------------------------------------------------- \n${BUILD_LOG, maxLines=100, escapeHtml=false}', 
                    to: "${EMAIL_TO}", 
                    subject: 'Build failed in Jenkins: $PROJECT_NAME - #$BUILD_NUMBER'*/
           /*echo "Check console output at $BUILD_URL to view the results. \n\n ${CHANGES} \n\n -------------------------------------------------- \n${BUILD_LOG, maxLines=100, escapeHtml=false}, 
                    to: "${EMAIL_TO}", */
            echo   "subject: Build failed in Jenkins: $PROJECT_NAME - #$BUILD_NUMBER"
        
        }
    }
}