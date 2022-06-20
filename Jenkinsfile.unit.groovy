pipeline {
    agent {
        label 'docker'
    }
    stages {
        //FASE UTILIZADA PARA PRE CONSTRUIR EL CORREO
        stage('PreBuild-Email') {
           steps {
               script {
                   def mailRecipients = 'rcardenas@cntcloud.com'
                   def jobName = currentBuild.fullDisplayName
                   //emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                   emailext body: '''ESTIMADO USUARIO''',
                       mimeTye: 'text/html',
                       subject: "[Jenkins] ha empezado ${jobName}",
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
        }
        success {
            /*Como las PRUEBAS QUE SE VALIDA EN EL PIPELINE SON POSITIVAS
            el nombre de las variables (nombre del proyecto, número de construccíon y URL de construcción)
            son verificales con el estado existoso del pipeline*/
            echo   "emailext body: Compruebe la salida de la consola en:${env.BUILD_URL} para ver los resultados \n para: rcardenas@cntcloud.com \n asunto: La construcción falló en Jenkins: ${currentBuild.fullDisplayName}"
        }
        /*CODIGO A UTILIZAR EN CASO DE QUE EL PIPELINE FALLE*/
        /*failure {
            emailext body: 'Check console output at $BUILD_URL to view the results. \n\n ${CHANGES} \n\n -------------------------------------------------- \n${BUILD_LOG, maxLines=100, escapeHtml=false}', 
                    to: "${EMAIL_TO}", 
                    subject: 'La Construcción Fallo en  Jenkins: $PROJECT_NAME - #$BUILD_NUMBER'
        }*/
    }
}