pipeline {
    agent any

    environment {
        EC2_USER = credentials('ec2-user')
        EC2_HOST = credentials('ec2-host')
    }

    stages {
        stage("Add Properties") {
            steps {
                script{
                    withCredentials([file(credentialsId: 'app-aws', variable: 'awsProperties'),
                                     file(credentialsId: 'app-client', variable: 'clientProperties'),
                                     file(credentialsId: 'app-gcp', variable: 'gcpProperties'),
                                     file(credentialsId: 'app-jwt', variable: 'jwtProperties'),
                                     file(credentialsId: 'app-oauth', variable: 'oauthProperties'),
                                     file(credentialsId: 'app-payment', variable: 'paymentProperties'),
                                     file(credentialsId: 'gcp-credential', variable: 'gcpCredential'),
                                     file(credentialsId: 'app-env', variable: 'env')]) {

                        sh '''
                            cp -f ${awsProperties} src/main/resources/application-aws.yml
                            cp -f ${clientProperties} src/main/resources/application-client.yml
                            cp -f ${gcpProperties} src/main/resources/application-gcp.yml
                            cp -f ${jwtProperties} src/main/resources/application-jwt.yml
                            cp -f ${oauthProperties} src/main/resources/application-oauth.yml
                            cp -f ${paymentProperties} src/main/resources/application-payment.yml
                            cp -f ${gcpCredential} src/main/resources/gcp_credential.json
                            cp -f ${env} .env
                        '''
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(credentials: ['aws-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} uptime
                        ssh -t ${EC2_USER}@${EC2_HOST} ./app/deploy.sh
                    '''
                }
            }
        }
    }
}