pipeline {
    agent any

    tools {
        // Use the Maven installation configured in Jenkins (Manage Jenkins → Global Tool Configuration)
        maven "M3"
    }

    stages {
        stage('Checkout') {
            steps {
                // Clone your repo
                git branch: 'master', url: 'https://github.com/salmannsharif/User-Profile-Manager.git'
            }
        }

        stage('Build') {
            steps {
                // Run Maven on Windows
                bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }
        }

        stage('Test Results & Artifacts') {
            steps {
                // Collect JUnit test reports
                junit '**/target/surefire-reports/TEST-*.xml'
                // Archive the built JAR file
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
}
