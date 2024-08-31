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
                    withCredentials([file(credentialsId: 'application-aws', variable: 'awsProperties'),
                                     file(credentialsId: 'application-client', variable: 'clientProperties'),
                                     file(credentialsId: 'application-gcp', variable: 'gcpProperties'),
                                     file(credentialsId: 'application-jwt', variable: 'jwtProperties'),
                                     file(credentialsId: 'application-oauth', variable: 'oauthProperties'),
                                     file(credentialsId: 'application-payment', variable: 'paymentProperties'),
                                     file(credentialsId: 'gcp_credential', variable: 'gcpCredential'),
                                     file(credentialsId: 'app-env', variable: 'env')]) {
                       sh 'cp -f ${awsProperties} src/main/resources/application-aws.yml'
                       sh 'cp -f ${clientProperties} src/main/resources/application-client.yml'
                       sh 'cp -f ${gcpProperties} src/main/resources/application-gcp.yml'
                       sh 'cp -f ${jwtProperties} src/main/resources/application-jwt.yml'
                       sh 'cp -f ${oauthProperties} src/main/resources/application-oauth.yml'
                       sh 'cp -f ${paymentProperties} src/main/resources/application-payment.yml'
                       sh 'cp -f ${gcpCredential} src/main/resources/gcp_credential.json'
                       sh 'cp -f ${env} .env'
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