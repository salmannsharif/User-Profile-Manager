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
        
                // Kill process on port 8081 if running
                bat '''
                for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /PID %%a /F
                exit 0
                '''
        
                // Start the Spring Boot app (detached)
                bat '''
                cd target
                start /B java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081
                '''
            }
        }

    }
}
