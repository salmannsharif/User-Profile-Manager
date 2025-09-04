pipeline {
    agent any

    tools {
        maven "M3"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/salmannsharif/User-Profile-Manager.git'
            }
        }

        stage('Build') {
            steps {
                bat "mvn clean package -DskipTests"
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying Application...'

                // Kill old process running on 8081 (if any)
                bat '''
                for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /PID %%a /F
                '''

                // Start new process in background
                bat 'start java -jar target/UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081'
            }
        }
    }
}
