pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/salmannsharif/User-Profile-Manager.git'
            }
        }

        stage('Build') {
            steps {
                bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }
        }

        stage('Test Results & Artifacts') {
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
}
