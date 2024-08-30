pipeline {
    agent any

    stages {
        stage('Git Clone') {
            steps {
                git branch: 'develop', url: 'https://github.com/davidy87/give-me-con'
            }
        }
        stage('Build') {
            steps {
                sh "./gradlew clean build"
            }
        }
    }
}