pipeline {
    agent any

    stages {
        stage('Git Clone') {
            steps {
                git branch: 'develop', url: 'https://github.com/davidy87/give-me-con'
            }
        }

        stage("Add Properties") {
            steps {
                script{
                    withCredentials([file(credentialsId: 'application-aws', variable: 'application_aws'),
                                     file(credentialsId: 'application-client', variable: 'application_client'),
                                     file(credentialsId: 'application-gcp', variable: 'application_gcp'),
                                     file(credentialsId: 'application-jwt', variable: 'application_jwt'),
                                     file(credentialsId: 'application-oauth', variable: 'application_oauth'),
                                     file(credentialsId: 'application-payment', variable: 'application_payment'),
                                     file(credentialsId: 'gcp_credential', variable: 'gcp_credential'),
                                     file(credentialsId: 'app-env', variable: 'app_env')]) {
                       sh 'cp ${application_aws} src/main/resources/application-aws.yml'
                       sh 'cp ${application_client} src/main/resources/application-client.yml'
                       sh 'cp ${application_gcp} src/main/resources/application-gcp.yml'
                       sh 'cp ${application_jwt} src/main/resources/application-jwt.yml'
                       sh 'cp ${application_oauth} src/main/resources/application-oauth.yml'
                       sh 'cp ${application_payment} src/main/resources/application-payment.yml'
                       sh 'cp ${gcp_credential} src/main/resources/gcp_credential.json'
                       sh 'cp ${app_env} .env'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh "./gradlew clean build"
            }
        }
    }
}